# 🎯 Ribbion Doubt Forum — API Documentation

> **Base URL:** `http://localhost:8080`
>
> **Auth:** JWT Bearer Token — pass `Authorization: Bearer <token>` in headers for protected endpoints.
>
> **All responses** are wrapped in:
> ```json
> {
>   "success": true/false,
>   "message": "optional message",
>   "data": { ... },
>   "timestamp": "2026-03-14T20:00:00"
> }
> ```

---

## 1. 🔐 Authentication

### 1.1 Register a New User

```
POST /api/auth/register
```

**Auth Required:** ❌ No

**When to use:** When a new user wants to create an account on the forum.

**Request Body:**
```json
{
  "username": "johndoe",
  "email": "john@example.com",
  "password": "MyPass123",
  "displayName": "John Doe"
}
```

| Field | Type | Required | Constraints |
|-------|------|----------|-------------|
| `username` | string | ✅ | 3–30 characters, must be unique |
| `email` | string | ✅ | Valid email format, must be unique |
| `password` | string | ✅ | 6–100 characters |
| `displayName` | string | ❌ | Max 50 chars. Defaults to username if omitted |

**Success Response (201):**
```json
{
  "success": true,
  "message": "Registration successful",
  "data": {
    "accessToken": "eyJhbGciOiJIUzUxMiJ9...",
    "tokenType": "Bearer",
    "user": {
      "id": 1,
      "username": "johndoe",
      "email": "john@example.com",
      "displayName": "John Doe",
      "reputation": 0,
      "role": "USER",
      "createdAt": "2026-03-14T20:00:00"
    }
  }
}
```

**Error Responses:**
| Status | Cause |
|--------|-------|
| 400 | Validation failed (missing fields, too short, invalid email) |
| 409 | Username or email already taken |

---

### 1.2 Login

```
POST /api/auth/login
```

**Auth Required:** ❌ No

**When to use:** When an existing user wants to log in and get a JWT token.

**Request Body:**
```json
{
  "usernameOrEmail": "johndoe",
  "password": "MyPass123"
}
```

| Field | Type | Required | Notes |
|-------|------|----------|-------|
| `usernameOrEmail` | string | ✅ | Accepts either username or email |
| `password` | string | ✅ | |

**Success Response (200):** Same structure as register response with `accessToken`.

**Error Responses:**
| Status | Cause |
|--------|-------|
| 401 | Invalid username/email or password |

---

## 2. 👤 Users

### 2.1 Get Current User Profile

```
GET /api/users/me
```

**Auth Required:** ✅ Yes

**When to use:** After login, to fetch the logged-in user's full profile (for dashboard, settings page, etc.).

**Response (200):**
```json
{
  "data": {
    "id": 1,
    "username": "johndoe",
    "email": "john@example.com",
    "displayName": "John Doe",
    "avatarUrl": null,
    "bio": null,
    "reputation": 10,
    "role": "USER",
    "createdAt": "2026-03-14T20:00:00"
  }
}
```

---

### 2.2 Update Current User Profile

```
PUT /api/users/me
```

**Auth Required:** ✅ Yes

**When to use:** When a user wants to update their display name, avatar, or bio from settings.

**Request Body:**
```json
{
  "displayName": "John D.",
  "avatarUrl": "https://example.com/avatar.png",
  "bio": "Spring Boot developer and open source enthusiast"
}
```

| Field | Type | Required | Constraints |
|-------|------|----------|-------------|
| `displayName` | string | ❌ | Max 50 chars |
| `avatarUrl` | string | ❌ | URL to avatar image |
| `bio` | string | ❌ | Max 500 chars |

> **Note:** Only pass the fields you want to update. `null` fields are ignored.

---

### 2.3 View Any User's Profile

```
GET /api/users/{id}
```

**Auth Required:** ❌ No

**When to use:** To view another user's public profile page.

| Path Param | Type | Description |
|------------|------|-------------|
| `id` | Long | User's ID |

---

### 2.4 Get a User's Questions

```
GET /api/users/{id}/questions?page=0&size=10
```

**Auth Required:** ❌ No

**When to use:** On a user's profile page, to show all questions they've asked.

| Param | Type | Default | Description |
|-------|------|---------|-------------|
| `id` | Long (path) | — | User's ID |
| `page` | int (query) | 0 | Page number (0-indexed) |
| `size` | int (query) | 10 | Results per page |

---

### 2.5 Get a User's Answers

```
GET /api/users/{id}/answers?page=0&size=10
```

**Auth Required:** ❌ No

**When to use:** On a user's profile page, to show all answers they've posted.

---

## 3. ❓ Questions

### 3.1 Create a Question

```
POST /api/questions
```

**Auth Required:** ✅ Yes

**When to use:** When a user wants to ask a new doubt/question on the forum.

**Request Body:**
```json
{
  "title": "How does Spring Security filter chain work?",
  "body": "I'm trying to understand how Spring Security processes requests through its filter chain. Can someone explain the order of filters and how custom filters interact with the default ones?",
  "tagNames": ["spring", "security", "java"]
}
```

| Field | Type | Required | Constraints |
|-------|------|----------|-------------|
| `title` | string | ✅ | 10–300 characters |
| `body` | string | ✅ | Min 20 characters |
| `tagNames` | string[] | ❌ | Tags auto-created if they don't exist. Stored lowercase. |

**Success Response (201):**
```json
{
  "data": {
    "id": 1,
    "title": "How does Spring Security filter chain work?",
    "body": "I'm trying to understand...",
    "author": {
      "id": 1,
      "username": "johndoe",
      "displayName": "John Doe",
      "reputation": 0
    },
    "tags": [
      { "id": 1, "name": "spring", "questionCount": 1 },
      { "id": 2, "name": "security", "questionCount": 1 }
    ],
    "voteCount": 0,
    "viewCount": 0,
    "answerCount": 0,
    "status": "OPEN",
    "createdAt": "2026-03-14T20:10:00"
  }
}
```

---

### 3.2 List Questions (Home Page)

```
GET /api/questions?sort=VOTES&page=0&size=10
```

**Auth Required:** ❌ No

**When to use:** The main home page feed. Use sort options to power different tabs.

| Param | Type | Default | Options |
|-------|------|---------|---------|
| `sort` | string | `NEWEST` | `NEWEST` — latest first |
|        |        |          | `VOTES` — highest voted first (trending) |
|        |        |          | `VIEWS` — most viewed first (popular) |
|        |        |          | `UNANSWERED` — questions with 0 answers |
| `page` | int | 0 | Page number (0-indexed) |
| `size` | int | 10 | Results per page |

**Response (200):** Paginated list of question summaries (without body for efficiency):
```json
{
  "data": {
    "content": [
      {
        "id": 1,
        "title": "How does Spring Security filter chain work?",
        "author": { "id": 1, "username": "johndoe", "reputation": 10 },
        "tags": [{ "name": "spring" }],
        "voteCount": 15,
        "viewCount": 102,
        "answerCount": 3,
        "status": "SOLVED",
        "createdAt": "2026-03-14T20:10:00"
      }
    ],
    "page": 0,
    "size": 10,
    "totalElements": 42,
    "totalPages": 5,
    "last": false
  }
}
```

> **Tip:** Use `sort=VOTES` for the default home page to show the highest-voted (most useful) questions on top, as per your requirement.

---

### 3.3 Get a Single Question (Detail Page)

```
GET /api/questions/{id}
```

**Auth Required:** ❌ No

**When to use:** When a user clicks on a question to see the full detail page. Includes the body.

> **Side effect:** Each call increments the question's `viewCount` by 1.

---

### 3.4 Edit a Question

```
PUT /api/questions/{id}
```

**Auth Required:** ✅ Yes (author only)

**When to use:** When the question author wants to edit title, body, or tags.

**Request Body:** Same as create question.

---

### 3.5 Delete a Question

```
DELETE /api/questions/{id}
```

**Auth Required:** ✅ Yes (author or admin)

**When to use:** When the author wants to remove their question, or an admin is cleaning up.

**Response (200):**
```json
{ "success": true, "message": "Question deleted" }
```

---

### 3.6 Search Questions

```
GET /api/questions/search?q=spring+boot&page=0&size=10
```

**Auth Required:** ❌ No

**When to use:** In the search bar — searches through question titles and bodies.

| Param | Type | Required |
|-------|------|----------|
| `q` | string | ✅ |
| `page` | int | ❌ (default 0) |
| `size` | int | ❌ (default 10) |

---

### 3.7 Filter Questions by Tag

```
GET /api/questions/tagged/{tag}?page=0&size=10
```

**Auth Required:** ❌ No

**When to use:** When a user clicks on a tag to see all questions with that tag.

| Param | Type | Example |
|-------|------|---------|
| `tag` | string (path) | `spring`, `java`, `docker` |

---

## 4. 💬 Answers

### 4.1 Post an Answer

```
POST /api/questions/{questionId}/answers
```

**Auth Required:** ✅ Yes

**When to use:** When a user wants to answer a question.

**Request Body:**
```json
{
  "body": "Spring Security uses a chain of servlet filters. The key filters in order are: SecurityContextPersistenceFilter, UsernamePasswordAuthenticationFilter, and FilterSecurityInterceptor..."
}
```

| Field | Type | Required | Constraints |
|-------|------|----------|-------------|
| `body` | string | ✅ | Min 10 characters |

> **Side effect:** Increments the question's `answerCount` by 1.

---

### 4.2 Get Answers for a Question

```
GET /api/questions/{questionId}/answers?page=0&size=20
```

**Auth Required:** ❌ No

**When to use:** On the question detail page, to load all answers.

> **Sorting:** Accepted answer always comes first, then by highest `voteCount`, then by oldest first. This is automatic — no sort parameter needed.

**Response (200):**
```json
{
  "data": {
    "content": [
      {
        "id": 1,
        "body": "Spring Security uses...",
        "questionId": 1,
        "author": { "id": 2, "username": "janedoe", "reputation": 150 },
        "voteCount": 12,
        "accepted": true,
        "createdAt": "2026-03-14T20:30:00"
      },
      {
        "id": 2,
        "body": "Another approach is...",
        "questionId": 1,
        "author": { "id": 3, "username": "bob", "reputation": 45 },
        "voteCount": 5,
        "accepted": false,
        "createdAt": "2026-03-14T21:00:00"
      }
    ]
  }
}
```

---

### 4.3 Edit an Answer

```
PUT /api/answers/{id}
```

**Auth Required:** ✅ Yes (author only)

**Request Body:** Same as create answer.

---

### 4.4 Delete an Answer

```
DELETE /api/answers/{id}
```

**Auth Required:** ✅ Yes (author or admin)

> **Side effect:** Decrements the question's `answerCount`. If the deleted answer was accepted, the question status reverts to `OPEN`.

---

### 4.5 Accept an Answer ⭐

```
PUT /api/answers/{id}/accept
```

**Auth Required:** ✅ Yes (question author only)

**When to use:** When the person who asked the question finds a satisfactory answer and marks it as accepted.

**What it does:**
1. Marks the answer as `accepted: true`
2. If another answer was previously accepted, un-accepts it
3. Sets the question status to `SOLVED`
4. Gives the answer author **+15 reputation**

**Response (200):**
```json
{
  "data": {
    "id": 1,
    "body": "Spring Security uses...",
    "voteCount": 12,
    "accepted": true
  }
}
```

| Error | Cause |
|-------|-------|
| 403 | You're not the question author |
| 400 | Answer is already accepted |

---

## 5. 🗳️ Voting

### 5.1 Cast a Vote (Upvote / Downvote)

```
POST /api/votes
```

**Auth Required:** ✅ Yes

**When to use:** When a user clicks the upvote/downvote button on a question or answer.

**Request Body:**
```json
{
  "targetType": "QUESTION",
  "targetId": 1,
  "value": 1
}
```

| Field | Type | Required | Values |
|-------|------|----------|--------|
| `targetType` | enum | ✅ | `QUESTION` or `ANSWER` |
| `targetId` | Long | ✅ | ID of the question or answer |
| `value` | int | ✅ | `1` (upvote) or `-1` (downvote) |

**Toggle Behavior:**

| Current State | Action | Result |
|---------------|--------|--------|
| No vote | Send `+1` | Upvoted (+1 to count) |
| No vote | Send `-1` | Downvoted (-1 to count) |
| Already upvoted (+1) | Send `+1` again | **Vote removed** (toggle off, -1 to count) |
| Already upvoted (+1) | Send `-1` | **Flipped to downvote** (-2 to count) |
| Already downvoted (-1) | Send `-1` again | **Vote removed** (toggle off, +1 to count) |
| Already downvoted (-1) | Send `+1` | **Flipped to upvote** (+2 to count) |

**Response (200):**
```json
{
  "data": {
    "targetType": "QUESTION",
    "targetId": 1,
    "currentVoteCount": 16,
    "userVote": 1
  }
}
```

| Field | Meaning |
|-------|---------|
| `currentVoteCount` | Updated total vote count on the target |
| `userVote` | Your current vote: `1`, `-1`, or `0` (no active vote) |

**Side effects:**
- Updates the `voteCount` on the target question/answer
- Updates the `reputation` of the content author (up/down by vote change)
- A user **cannot vote on their own content** (returns 400)

---

### 5.2 Check Vote Status

```
GET /api/votes/status?targetType=QUESTION&targetId=1
```

**Auth Required:** ✅ Yes

**When to use:** When loading a question/answer detail, to highlight the vote button if the user already voted.

| Param | Type | Required |
|-------|------|----------|
| `targetType` | enum | ✅ (`QUESTION` or `ANSWER`) |
| `targetId` | Long | ✅ |

**Response:** Same as cast vote response.

---

## 6. 🏷️ Tags

### 6.1 List All Tags

```
GET /api/tags?page=0&size=20
```

**Auth Required:** ❌ No

**When to use:** On a "Browse Tags" page.

---

### 6.2 Get Popular Tags

```
GET /api/tags/popular?page=0&size=10
```

**Auth Required:** ❌ No

**When to use:** To show trending/popular tags in the sidebar or tag cloud. Sorted by `questionCount` descending.

**Response (200):**
```json
{
  "data": {
    "content": [
      { "id": 1, "name": "java", "description": null, "questionCount": 42 },
      { "id": 2, "name": "spring", "description": null, "questionCount": 35 },
      { "id": 3, "name": "docker", "description": null, "questionCount": 18 }
    ]
  }
}
```

---

### 6.3 Search Tags

```
GET /api/tags/search?q=spr&page=0&size=10
```

**Auth Required:** ❌ No

**When to use:** Autocomplete in the tag input field when creating a question.

---

## 7. 💭 Comments

### 7.1 Add Comment to a Question

```
POST /api/questions/{questionId}/comments
```

**Auth Required:** ✅ Yes

**When to use:** When a user wants to ask for clarification or add a remark on a question (not a full answer).

**Request Body:**
```json
{
  "body": "Can you share the error message you're getting?"
}
```

| Field | Type | Required | Constraints |
|-------|------|----------|-------------|
| `body` | string | ✅ | Max 1000 characters |

---

### 7.2 Get Comments on a Question

```
GET /api/questions/{questionId}/comments?page=0&size=20
```

**Auth Required:** ❌ No

**When to use:** On question detail page, below the question body.

---

### 7.3 Add Comment to an Answer

```
POST /api/answers/{answerId}/comments
```

**Auth Required:** ✅ Yes

**When to use:** When a user wants to comment on a specific answer.

**Request Body:** Same as question comment.

---

### 7.4 Get Comments on an Answer

```
GET /api/answers/{answerId}/comments?page=0&size=20
```

**Auth Required:** ❌ No

---

### 7.5 Delete a Comment

```
DELETE /api/comments/{id}
```

**Auth Required:** ✅ Yes (author or admin)

---

## 📋 Quick Reference — Typical User Flows

### Flow 1: New User Onboarding
```
POST /api/auth/register  →  save token  →  GET /api/questions?sort=VOTES
```

### Flow 2: Ask a Question
```
POST /api/auth/login  →  POST /api/questions  →  (wait for answers)
```

### Flow 3: Answer & Get Accepted
```
GET /api/questions/{id}  →  POST /api/questions/{id}/answers  →  (question author calls PUT /api/answers/{id}/accept)
```

### Flow 4: Browse & Vote
```
GET /api/questions?sort=NEWEST  →  GET /api/questions/{id}  →  POST /api/votes {targetType: QUESTION, value: 1}  →  GET /api/questions/{id}/answers  →  POST /api/votes {targetType: ANSWER, value: 1}
```

### Flow 5: Search & Filter
```
GET /api/questions/search?q=spring boot  →  GET /api/questions/tagged/java
```

---

## ⚠️ Error Codes Reference

| Status | Meaning | Common Causes |
|--------|---------|---------------|
| 400 | Bad Request | Validation failed, invalid vote value, self-voting |
| 401 | Unauthorized | Missing/invalid/expired JWT token |
| 403 | Forbidden | Trying to edit/delete another user's content |
| 404 | Not Found | Question/answer/user doesn't exist |
| 409 | Conflict | Duplicate username or email |
| 500 | Server Error | Unexpected error |

All error responses follow this format:
```json
{
  "success": false,
  "message": "Username is already taken",
  "timestamp": "2026-03-14T20:00:00"
}
```

Validation errors include field details:
```json
{
  "success": false,
  "message": "Validation failed",
  "data": {
    "title": "Title must be between 10 and 300 characters",
    "body": "Body must be at least 20 characters"
  }
}
```
