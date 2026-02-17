# lkmobile

LkMobile
Overview
LkMobile is a project consisting of:

Python Flask Backend API (main.py) - Runs on port 5000, provides REST API for user management, status tracking, and invitations.
Android App (Kotlin/Jetpack Compose in src/) - Mobile client that connects to the Flask backend. Cannot be built in Replit.
Project Architecture
main.py - Flask backend with in-memory user storage, endpoints for auth, registration, user listing, status updates, invites, and health checks.
build.gradle.kts - Android app build config (Kotlin, Jetpack Compose, Retrofit for API calls).
src/ - Android app source code (Kotlin).
Running
The Flask backend runs via python main.py on port 5000.
Production uses gunicorn: gunicorn --bind=0.0.0.0:5000 --reuse-port main:app
API Endpoints
POST /api/auth/verify - Generate new user ID
POST /api/users/register - Register or auto-login user
GET /api/users - List users in default group
PUT/POST /api/users/status - Update user status
GET /api/users/<user_id> - Get user by ID
POST /api/invite - Send invite
GET /api/health - Health check
Recent Changes
2026-02-17: Initial setup in Replit environment. Installed Python 3.11, Flask, flask-cors, gunicorn. Configured workflow and deployment.
