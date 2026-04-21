<p align="center">
  <h1 align="center">🎯 Ribbion — Doubt Forum Backend</h1>
  <p align="center">
    A full-featured Q&A forum backend built with Spring Boot 4 — think Stack Overflow, but for your community.
  </p>
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Spring%20Boot-4.0.3-6DB33F?style=for-the-badge&logo=spring-boot" />
  <img src="https://img.shields.io/badge/Java-23-ED8B00?style=for-the-badge&logo=openjdk" />
  <img src="https://img.shields.io/badge/MySQL-8.0-4479A1?style=for-the-badge&logo=mysql&logoColor=white" />
  <img src="https://img.shields.io/badge/JWT-Auth-000000?style=for-the-badge&logo=jsonwebtokens" />
  <img src="https://img.shields.io/badge/Docker-Ready-2496ED?style=for-the-badge&logo=docker&logoColor=white" />
</p>

---

## 📖 About

**Ribbion** is a production-ready REST API backend for Doubt/Q&A Forum where users can:

- 📝 **Ask questions** with tags and rich text
- 💬 **Answer questions** posted by others
- ⬆️ **Upvote / Downvote** questions and answers — the best floats to the top
- ✅ **Accept answers** — marks the question as solved and rewards the answerer
- 💭 **Comment** on questions and answers for clarifications
- 🏷️ **Tag system** — auto-created, searchable, with popular tags
- 🔍 **Search & Filter** — full-text search, filter by tag, sort by votes/views/newest
- 👤 **User profiles** with reputation system

---

## 🏗️ Tech Stack

| Layer | Technology |
|-------|-----------|
| **Framework** | Spring Boot 4.0.3 |
| **Language** | Java 23 |
| **Database** | MySQL 8.0 (Docker) |
| **ORM** | Spring Data JPA / Hibernate |
| **Auth** | JWT (JJWT 0.12.6) + Spring Security |
| **Validation** | Jakarta Bean Validation |
| **Build** | Maven |
| **Other** | Lombok, BCrypt |

---

## 📂 Project Structure

```
src/main/java/com/mpj/ribbion/
│
├── controller/          # REST API endpoints (7 controllers, 25+ endpoints)
│   ├── AuthController         → Register & Login
│   ├── UserController         → Profile management
│   ├── QuestionController     → Question CRUD, search, filter
│   ├── AnswerController       → Answer CRUD, accept answer
│   ├── VoteController         → Upvote / Downvote
│   ├── TagController          → Browse & search tags
│   └── CommentController      → Comments on questions & answers
│
├── service/             # Business logic layer
├── repository/          # JPA repositories with custom queries
├── entity/              # JPA entities (User, Question, Answer, Vote, Tag, Comment)
├── dto/                 # Request / Response DTOs with validation
├── security/            # JWT auth (token provider, filter, config)
└── exception/           # Global exception handler + custom exceptions
```

---

## 🚀 Getting Started

### Prerequisites

- **Java 23** (or compatible JDK)
- **Docker** (for MySQL)
- **Maven** (or use the included `mvnw` wrapper)

### 1. Clone the repo

```bash
git clone https://github.com/your-username/ribbion.git
cd ribbion
```

### 2. Start MySQL with Docker

```bash
docker-compose up -d
```

This spins up a MySQL 8.0 container with:
- **Database:** `ribbion_db`
- **User:** `ribbion` / **Password:** `ribbion123`
- **Port:** `3306`
- **Persistent volume:** Data survives container restarts

### 3. Run the application

```bash
# Windows
.\mvnw.cmd spring-boot:run

# Linux / Mac
./mvnw spring-boot:run
```

The server starts at **http://localhost:8080**

### 4. Test it out

```bash
# Register
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","email":"test@test.com","password":"Test1234","displayName":"Test User"}'

# Login (copy the accessToken from response)
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"usernameOrEmail":"testuser","password":"Test1234"}'

# Create a question (replace TOKEN)
curl -X POST http://localhost:8080/api/questions \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer TOKEN" \
  -d '{"title":"How does Spring Boot auto-configuration work?","body":"I want to understand the magic behind @SpringBootApplication and how it auto-configures beans.","tagNames":["spring","java"]}'

# Browse questions
curl http://localhost:8080/api/questions?sort=VOTES&page=0&size=10
```

---

## 🔑 API Endpoints Overview

| Category | Endpoints | Auth |
|----------|-----------|------|
| **Auth** | `POST /api/auth/register`, `/login` | ❌ Public |
| **Users** | `GET/PUT /api/users/me`, `GET /api/users/{id}` | Mixed |
| **Questions** | CRUD + search + filter by tag | GET: Public, Write: 🔒 |
| **Answers** | CRUD + accept answer | GET: Public, Write: 🔒 |
| **Votes** | `POST /api/votes`, `GET /api/votes/status` | 🔒 Protected |
| **Tags** | List, popular, search | ❌ Public |
| **Comments** | CRUD on questions & answers | GET: Public, Write: 🔒 |

> 📄 **Full API Documentation:** See [API_DOCUMENTATION.md](API_DOCUMENTATION.md) for complete details with request/response examples, validation rules, and user flow guides.

---

## ⚙️ Key Features in Detail

### 🗳️ Smart Voting System
- **Toggle behavior:** Click upvote again to remove your vote, or click downvote to flip
- **Self-vote prevention:** Users cannot vote on their own content
- **Reputation tracking:** Votes affect the content author's reputation score

### ✅ Accept Answer
- Only the **question author** can accept an answer
- Accepting marks the question as **SOLVED**
- The answerer receives **+15 reputation**
- Accepted answer always appears **first** in the list

### 🏷️ Auto Tag System
- Tags are **auto-created** when you use them in a question
- Stored as **lowercase**, duplicates prevented
- Tag `questionCount` is maintained automatically
- Search and popular tags endpoints for discovery

### 🔒 Security
- **Stateless JWT** authentication — no server-side sessions
- **BCrypt** password hashing
- **Role-based** access (USER / ADMIN)
- **Owner-only** operations — users can only edit/delete their own content

### 📄 Pagination
- All list endpoints support `page` and `size` query parameters
- Response includes `totalElements`, `totalPages`, and `last` flag

---

## 🗄️ Database Schema

```
┌──────────┐       ┌──────────────┐       ┌──────────┐
│  users   │──1:N──│  questions   │──N:M──│   tags   │
└──────────┘       └──────────────┘       └──────────┘
     │                    │
     │ 1:N           1:N  │
     ▼                    ▼
┌──────────┐       ┌──────────────┐
│  votes   │       │   answers    │
└──────────┘       └──────────────┘
     ▲                    │
     │               1:N  │
     │                    ▼
     │             ┌──────────────┐
     └─────────────│   comments   │
                   └──────────────┘
```

---

## 📝 Configuration

Key settings in `src/main/resources/application.properties`:

| Property | Default | Description |
|----------|---------|-------------|
| `server.port` | 8080 | Server port |
| `spring.datasource.url` | `jdbc:mysql://localhost:3306/ribbion_db` | MySQL connection |
| `app.jwt.secret` | (configured) | JWT signing key |
| `app.jwt.expiration-ms` | 86400000 | Token expiry (24 hours) |
| `spring.jpa.hibernate.ddl-auto` | update | Auto-creates/updates tables |

---

## 🤝 Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

---
