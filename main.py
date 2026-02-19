from flask import Flask, request, jsonify
from flask_cors import CORS
from flask_socketio import SocketIO, emit, join_room
import time
import uuid
import random
import threading
import sqlite3
import logging
from contextlib import contextmanager
from queue import Queue

# ─────────────────────────────────────────────────────────────
# CONFIGURATION & LOGGING
# ─────────────────────────────────────────────────────────────
app = Flask(__name__)
app.config['SECRET_KEY'] = 'secret-key-change-in-prod'
CORS(app, resources={r"/api/*": {"origins": "*"}})
# Optimized ping for faster disconnect detection
socketio = SocketIO(app, cors_allowed_origins="*", async_mode='eventlet', ping_timeout=10, ping_interval=5)

# Настройки
DB_PATH = 'server.db'
PORT = 5000
HOST = '185.105.90.127'
DEBUG_MODE = False

logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s - %(levelname)s - %(message)s'
)
logger = logging.getLogger(__name__)

# ─────────────────────────────────────────────────────────────
# DATABASE MANAGER (Optimized with Pooling)
# ─────────────────────────────────────────────────────────────
db_queue = Queue(maxsize=10)

def get_db_connection():
    conn = sqlite3.connect(DB_PATH, check_same_thread=False, timeout=5.0)
    conn.row_factory = sqlite3.Row
    conn.execute("PRAGMA journal_mode=WAL")
    return conn

for _ in range(5):
    db_queue.put(get_db_connection())

@contextmanager
def get_db_cursor():
    conn = db_queue.get(timeout=5.0)
    try:
        yield conn
    finally:
        db_queue.put(conn)

def init_db():
    with get_db_cursor() as conn:
        cursor = conn.cursor()
        cursor.execute('''
        CREATE TABLE IF NOT EXISTS users (
            id TEXT PRIMARY KEY,
            name TEXT UNIQUE NOT NULL,
            access_id TEXT DEFAULT 'default_group',
            is_playing BOOLEAN DEFAULT 0,
            is_online BOOLEAN DEFAULT 0,
            last_seen INTEGER DEFAULT 0,
            avatar_color INTEGER DEFAULT 7104255
        )
        ''')
        cursor.execute('''
        CREATE TABLE IF NOT EXISTS invites (
            id TEXT PRIMARY KEY,
            to_user_id TEXT NOT NULL,
            from_name TEXT NOT NULL,
            time INTEGER NOT NULL
        )
        ''')
        conn.commit()
    logger.info("✅ Database initialized (WAL mode enabled).")

def load_users_to_memory():
    global users, username_to_id, access_groups, invites
    with get_db_cursor() as conn:
        cursor = conn.cursor()
        cursor.execute("SELECT * FROM users")
        for row in cursor.fetchall():
            uid = row['id']
            user_data = {
                "id": uid,
                "name": row['name'],
                "accessId": row['access_id'],
                "isPlaying": bool(row['is_playing']),
                "isOnline": bool(row['is_online']),
                "lastSeen": row['last_seen'],
                "avatarColor": row['avatar_color']
            }
            users[uid] = user_data
            username_to_id[row['name']] = uid

            aid = row['access_id']
            if aid not in access_groups:
                access_groups[aid] = set()
            access_groups[aid].add(uid)

        cursor.execute("SELECT * FROM invites")
        for row in cursor.fetchall():
            tid = row['to_user_id']
            if tid not in invites:
                invites[tid] = []
            invites[tid].append({
                "id": row['id'],
                "fromName": row['from_name'],
                "time": row['time']
            })
    logger.info(f"📥 Loaded {len(users)} users.")

def save_user_to_db(user_data):
    with get_db_cursor() as conn:
        cursor = conn.cursor()
        cursor.execute('''
        INSERT OR REPLACE INTO users
        (id, name, access_id, is_playing, is_online, last_seen, avatar_color)
        VALUES (?, ?, ?, ?, ?, ?, ?)
        ''', (
            user_data['id'], user_data['name'], user_data['accessId'],
            1 if user_data['isPlaying'] else 0,
            1 if user_data['isOnline'] else 0,
            user_data['lastSeen'], user_data['avatarColor']
        ))
        conn.commit()

def update_user_status_in_db(user_id, is_online=None, is_playing=None, last_seen=None):
    with get_db_cursor() as conn:
        cursor = conn.cursor()
        updates, values = [], []
        if is_online is not None:
            updates.append("is_online = ?"); values.append(1 if is_online else 0)
        if is_playing is not None:
            updates.append("is_playing = ?"); values.append(1 if is_playing else 0)
        if last_seen is not None:
            updates.append("last_seen = ?"); values.append(last_seen)

        if updates:
            values.append(user_id)
            cursor.execute(f"UPDATE users SET {', '.join(updates)} WHERE id = ?", values)
            conn.commit()

# ─────────────────────────────────────────────────────────────
# IN-MEMORY STORAGE
# ─────────────────────────────────────────────────────────────
users = {}
username_to_id = {}
access_groups = {}
invites = {}
lobbies = {}
messages = []
private_messages = {}

# ─────────────────────────────────────────────────────────────
# BACKGROUND TASKS (Optimized)
# ─────────────────────────────────────────────────────────────
avatar_colors = [0x6C63FF, 0x00D9FF, 0xFF5252, 0x00E676, 0xFFAB40]

def cleanup_dead_users():
    while True:
        now = int(time.time() * 1000)
        dead_users = []
        for uid, user in list(users.items()):
            if user.get('isOnline'):
                # Faster cleanup: 45 seconds inactivity means offline
                if now - user.get('lastSeen', 0) > 45_000:
                    user['isOnline'] = False
                    user['isPlaying'] = False
                    update_user_status_in_db(uid, is_online=False, is_playing=False, last_seen=now)
                    dead_users.append(user['name'])
                    socketio.emit('status_update', user)

        if dead_users:
            logger.warning(f"🔴 Cleaned up: {', '.join(dead_users)}")

        time.sleep(10)

# ─────────────────────────────────────────────────────────────
# SOCKET.IO EVENTS (Real-time Chat)
# ─────────────────────────────────────────────────────────────
@socketio.on('connect')
def handle_connect():
    logger.info(f"Client connected: {request.sid}")

@socketio.on('disconnect')
def handle_disconnect():
    logger.info(f"Client disconnected: {request.sid}")

@socketio.on('send_message')
def handle_socket_message(data):
    """Мгновенная отправка сообщений через WebSocket"""
    lobby_id = data.get('lobbyId')
    msg = {
        "id": str(uuid.uuid4()),
        "from": data.get('from'),
        "text": data.get('text'),
        "time": int(time.time() * 1000),
        "color": data.get('color', 0x6C63FF)
    }

    if lobby_id and lobby_id in lobbies:
        lobbies[lobby_id]['messages'].append(msg)
        if len(lobbies[lobby_id]['messages']) > 100:
            lobbies[lobby_id]['messages'].pop(0)
        socketio.emit('new_message', msg, to=lobby_id)
    else:
        messages.append(msg)
        if len(messages) > 200:
            messages.pop(0)
        socketio.emit('new_global_message', msg)

@socketio.on('join_lobby')
def handle_join_lobby(data):
    lobby_id = data.get('lobbyId')
    user_id = data.get('userId')
    if lobby_id and user_id:
        join_room(lobby_id)
        if lobby_id in lobbies:
            if user_id not in lobbies[lobby_id]['members']:
                lobbies[lobby_id]['members'].append(user_id)
        socketio.emit('lobby_update', lobbies.get(lobby_id, {}), to=lobby_id)

# ─────────────────────────────────────────────────────────────
# API ENDPOINTS
# ─────────────────────────────────────────────────────────────

@app.route('/api/auth/verify', methods=['POST'])
def verify_id():
    return jsonify({"success": True, "userId": str(uuid.uuid4())})

@app.route('/api/users/register', methods=['POST'])
def register_user():
    data = request.json
    name = data.get('name', '').strip()
    user_id = data.get('userId')

    if not name:
        return jsonify({"success": False, "message": "Name required"}), 400

    if name in username_to_id:
        existing_id = username_to_id[name]
        if existing_id in users:
            users[existing_id]['lastSeen'] = int(time.time() * 1000)
            users[existing_id]['isOnline'] = True
            update_user_status_in_db(existing_id, is_online=True, last_seen=users[existing_id]['lastSeen'])
            socketio.emit('status_update', users[existing_id])
            return jsonify({"success": True, "user": users[existing_id]})

    uid = user_id or str(uuid.uuid4())

    with get_db_cursor() as conn:
        if conn.execute("SELECT id FROM users WHERE name = ?", (name,)).fetchone():
            return jsonify({"success": False, "message": "Username taken"}), 409

    user = {
        "id": uid, "name": name, "accessId": "default_group",
        "isPlaying": False, "isOnline": True,
        "lastSeen": int(time.time() * 1000),
        "avatarColor": random.choice(avatar_colors)
    }

    users[uid] = user
    username_to_id[name] = uid
    access_groups.setdefault("default_group", set()).add(uid)
    save_user_to_db(user)
    socketio.emit('status_update', user)

    return jsonify({"success": True, "user": user})

@app.route('/api/users/status', methods=['POST'])
def update_status():
    data = request.json
    user_id = data.get('userId')
    if not user_id or user_id not in users:
        return jsonify({"success": False}), 404

    if 'isPlaying' in data:
        users[user_id]['isPlaying'] = data['isPlaying']
    if 'isOnline' in data:
        users[user_id]['isOnline'] = data['isOnline']

    last_seen = int(time.time() * 1000)
    users[user_id]['lastSeen'] = last_seen
    update_user_status_in_db(user_id, is_online=users[user_id]['isOnline'], 
                             is_playing=users[user_id]['isPlaying'], last_seen=last_seen)
    socketio.emit('status_update', users[user_id])
    return jsonify({"success": True})

@app.route('/api/lobbies', methods=['GET'])
def get_lobbies():
    return jsonify({"lobbies": [{"id": k, **v} for k, v in lobbies.items()]})

@app.route('/api/lobbies', methods=['POST'])
def create_lobby():
    data = request.json
    lobby_id = str(uuid.uuid4())
    lobbies[lobby_id] = {
        "name": data.get('name', 'Lobby'),
        "creator": data.get('userName'),
        "members": [data.get('userId')],
        "messages": []
    }
    return jsonify({"success": True, "lobbyId": lobby_id})

@app.route('/api/chat', methods=['GET'])
def get_chat():
    return jsonify({"messages": messages})

@app.route('/api/users', methods=['GET'])
def get_users():
    return jsonify({"users": list(users.values())})

@app.route('/api/health', methods=['GET'])
def health_check():
    return jsonify({
        "status": "ok",
        "users_online": sum(1 for u in users.values() if u.get('isOnline')),
        "users_playing": sum(1 for u in users.values() if u.get('isPlaying'))
    })

if __name__ == '__main__':
    init_db()
    load_users_to_memory()
    threading.Thread(target=cleanup_dead_users, daemon=True).start()
    logger.info(f"🚀 Server starting on http://{HOST}:{PORT}")
    socketio.run(app, host='0.0.0.0', port=PORT, debug=DEBUG_MODE, allow_unsafe_werkzeug=True)
