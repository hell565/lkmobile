from flask import Flask, request, jsonify
from flask_cors import CORS
import time
import uuid
import random
import threading

# ✅ Исправлено: было Flask(name)
app = Flask(__name__)
CORS(app)

# In-memory storage
users = {}  # userId: user_data
access_groups = {}  # accessId: set of userIds
username_to_id = {}  # name: userId
messages = []  # List of {id, from, text, time, color}
invites = {}  # toUserId: [list of invites]

# ✅ Исправлено: убран 0xFF (альфа-канал), чтобы Android не падал с NumberFormatException
avatar_colors = [
    0x6C63FF, 0x00D9FF, 0xFF5252,
    0x00E676, 0xFFAB40, 0x7C4DFF,
    0x448AFF, 0x00E676, 0x18FFFF
]

# ✅ Фоновая очистка "зависших" пользователей (энергоэффективно)
def cleanup_dead_users():
    """
    Запускается в фоне. Проверяет каждые 2 минуты:
    Если пользователь не обновлял lastSeen > 180 секунд → помечает офлайн
    """
    while True:
        now = int(time.time() * 1000)
        dead_users = []
        
        for uid, user in users.items():
            if user.get('isOnline'):
                time_since_seen = now - user.get('lastSeen', 0)
                if time_since_seen > 180_000:  # 3 минуты
                    user['isOnline'] = False
                    user['isPlaying'] = False
                    dead_users.append(user['name'])
        
        if dead_users:
            print(f"🔴 Cleaned up inactive users: {', '.join(dead_users)}")
        
        time.sleep(120)  # Проверка раз в 2 минуты (минимальная нагрузка на CPU)

# Запуск фоновой задачи при старте сервера
threading.Thread(target=cleanup_dead_users, daemon=True).start()

# ─────────────────────────────────────────────────────────────
# API ENDPOINTS
# ─────────────────────────────────────────────────────────────

@app.route('/api/auth/verify', methods=['POST'])
def verify_id():
    """
    Верификация пользователя (возвращает новый userId)
    Используется при первом запуске приложения
    """
    user_id = str(uuid.uuid4())
    return jsonify({
        "success": True,
        "message": "Verified",
        "userId": user_id
    })

@app.route('/api/users/register', methods=['POST'])
def register_user():
    """
    Регистрация нового пользователя или вход по имени
    Если пользователь с таким именем уже есть — возвращаем его данные (auto-login)
    """
    data = request.json
    name = data.get('name', '').strip()
    user_id = data.get('userId')
    access_id = "default_group"
    
    if not name:
        return jsonify({
            "success": False,
            "message": "Name is required"
        }), 400

    # Auto-login if user exists by name
    if name in username_to_id:
        existing_id = username_to_id[name]
        # Обновляем lastSeen при входе
        users[existing_id]['lastSeen'] = int(time.time() * 1000)
        users[existing_id]['isOnline'] = True
        return jsonify({
            "success": True,
            "user": users[existing_id]
        })

    # Create new user
    uid = user_id or str(uuid.uuid4())
    user = {
        "id": uid,
        "name": name,
        "accessId": access_id,
        "isPlaying": False,
        "isOnline": True,
        "lastSeen": int(time.time() * 1000),
        "avatarColor": random.choice(avatar_colors)
    }

    users[uid] = user
    username_to_id[name] = uid

    if access_id not in access_groups:
        access_groups[access_id] = set()
    # ✅ Исправлено: было ad d(uid)
    access_groups[access_id].add(uid)

    return jsonify({
        "success": True,
        "user": user
    })

@app.route('/api/users', methods=['GET'])
def get_users():
    """
    Получить список всех пользователей в группе
    Используется для отображения списка игроков в приложении
    """
    access_id = "default_group"
    group_user_ids = access_groups.get(access_id, set())
    group_users = [users[uid] for uid in group_user_ids if uid in users]
    
    return jsonify({
        "users": group_users
    })

@app.route('/api/users/status', methods=['PUT', 'POST'])
def update_status():
    """
    Обновить статус пользователя (online/offline, playing/not playing)
    Поддерживает поиск по userId ИЛИ по name (для удобства Android)
    
    JSON body:
    {
        "userId": "uuid-или-null",
        "name": "nickname-или-null",
        "isOnline": true/false,
        "isPlaying": true/false
    }
    """
    data = request.json
    user_id = data.get('userId')
    name = data.get('name')  # ✅ Добавлено: поддержка поиска по имени
    
    # Ищем пользователя по имени, если не передан ID
    if name and name in username_to_id:
        user_id = username_to_id[name]
    
    if not user_id or user_id not in users:
        return jsonify({
            "success": False,
            "message": "User not found"
        }), 404
    
    # Обновляем только те поля, которые пришли в запросе
    if 'isPlaying' in data:
        users[user_id]['isPlaying'] = data['isPlaying']
    if 'isOnline' in data:
        users[user_id]['isOnline'] = data['isOnline']
    
    # ✅ Важно: обновляем lastSeen для работы таймаута
    users[user_id]['lastSeen'] = int(time.time() * 1000)
    
    return jsonify({
        "success": True
    })

@app.route('/api/users/<user_id>', methods=['GET'])
def get_user(user_id):
    """
    Получить данные конкретного пользователя по ID
    """
    if user_id not in users:
        return jsonify({
            "success": False,
            "message": "User not found"
        }), 404
    
    return jsonify({
        "success": True,
        "user": users[user_id]
    })

@app.route('/api/chat', methods=['GET'])
def get_messages():
    return jsonify({"messages": messages[-50:]})  # Return last 50 messages

@app.route('/api/chat', methods=['POST'])
def send_message():
    data = request.json
    msg = {
        "id": str(uuid.uuid4()),
        "from": data.get('from'),
        "text": data.get('text'),
        "time": int(time.time() * 1000),
        "color": data.get('color', 0x6C63FF)
    }
    messages.append(msg)
    if len(messages) > 200: messages.pop(0)
    return jsonify({"success": True, "message": msg})

@app.route('/api/invites/<user_id>', methods=['GET'])
def get_invites(user_id):
    user_invites = invites.get(user_id, [])
    invites[user_id] = [] # Clear after reading
    return jsonify({"invites": user_invites})

@app.route('/api/invite', methods=['POST'])
def send_invite():
    """
    Отправить приглашение пользователю
    """
    data = request.json
    from_name = data.get('fromUserName', 'Someone')
    to_user_id = data.get('toUserId')
    
    if to_user_id not in invites:
        invites[to_user_id] = []
    
    invite_data = {
        "id": str(uuid.uuid4()),
        "fromName": from_name,
        "time": int(time.time() * 1000)
    }
    invites[to_user_id].append(invite_data)
    
    return jsonify({
        "success": True,
        "message": f"Invite sent from {from_name}"
    })

@app.route('/api/health', methods=['GET'])
def health_check():
    """
    Проверка работоспособности сервера
    """
    return jsonify({
        "status": "ok",
        "users_online": sum(1 for u in users.values() if u.get('isOnline')),
        "users_playing": sum(1 for u in users.values() if u.get('isPlaying'))
    })

# ✅ Исправлено: было if name == 'main':
if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000, debug=False)