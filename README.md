# Mini Ticket Tracker

A small support-ticket tracker: a Spring Boot REST API on PostgreSQL with a single-page React UI.

Take-home exercise by **Harrison Kiprono**. I aimed for a small, well-structured slice rather than a long feature list.

## Stack

| Layer    | Choice                                                         |
|----------|----------------------------------------------------------------|
| Backend  | Java 25, Spring Boot 4, Spring Data JPA, Bean Validation       |
| Database | PostgreSQL 16, schema managed by Flyway                        |
| API docs | springdoc-openapi (Swagger UI)                                 |
| Frontend | React 19, TypeScript, Vite                                     |
| Tests    | JUnit 5, MockMvc, Testcontainers (real Postgres)               |

## Prerequisites

- Docker (runs Postgres, and the integration tests use it through Testcontainers)
- Node.js 20+
- A JDK to launch Gradle. JDK 25 itself does not need to be installed: the Gradle toolchain downloads it on first build if it is missing.

## Running it

### 1. Database

```bash
docker compose up -d
```

Postgres comes up on `localhost:5432` (database `ticket_tracker`, user/password `ticket`/`ticket`). Flyway creates the schema when the backend starts.

### 2. Backend

```bash
cd backend
./gradlew bootRun
```

- API: <http://localhost:8080/api/v1>
- Swagger UI: <http://localhost:8080/swagger-ui.html>
- Health: <http://localhost:8080/actuator/health>

### 3. Frontend

```bash
cd frontend
cp .env.example .env
npm install
npm run dev
```

Open <http://localhost:5173>.

### If a port is already in use

Everything is driven by environment variables, so nothing needs editing:

```bash
# Postgres on 5433 and the API on 8081 instead of the defaults
DB_PORT=5433 docker compose up -d
cd backend && DB_PORT=5433 SERVER_PORT=8081 ./gradlew bootRun
# then in frontend/.env: VITE_API_BASE_URL=http://localhost:8081/api/v1
```

The backend also reads `DB_HOST`, `DB_NAME`, `DB_USER`, `DB_PASSWORD` and `CORS_ALLOWED_ORIGINS`; defaults are in `backend/src/main/resources/application.yml`.

## API

All routes are under `/api/v1`. Full request/response schemas are in Swagger UI.

| Method  | Path             | Description                                                          |
|---------|------------------|----------------------------------------------------------------------|
| `POST`  | `/tickets`       | Create a ticket. Starts as `open`; `priority` defaults to `medium`.  |
| `GET`   | `/tickets`       | List tickets, newest first. `?status=` filter, `?page=&limit=` paging. |
| `PATCH` | `/tickets/{id}`  | Update `status` and/or `priority`.                                   |
| `GET`   | `/tickets/stats` | Ticket count per status.                                             |

Enum values on the wire are lowercase: `open | in_progress | closed` and `low | medium | high`.

```bash
curl -X POST localhost:8080/api/v1/tickets \
  -H 'Content-Type: application/json' \
  -d '{"title": "Password reset email never arrives", "priority": "high"}'

curl 'localhost:8080/api/v1/tickets?status=open&page=1&limit=10'
```

A list response is wrapped in a page envelope:

```json
{ "content": [ ... ], "page": 1, "limit": 10, "totalElements": 42, "totalPages": 5 }
```

Errors always have the same shape, whatever the cause:

```json
{
  "timestamp": "2026-10-13T11:03:35Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Invalid status 'done'. Allowed values: open, in_progress, closed",
  "path": "/api/v1/tickets",
  "details": []
}
```

## Tests

```bash
cd backend
./gradlew test
```

The integration tests boot the whole application against a throwaway Postgres container, so they exercise the real Flyway schema and SQL. They cover create (defaults, validation, unknown enum values), list (status filter, pagination, ordering, out-of-range parameters), update (partial update, 404, empty body) and stats.

## Project layout

```
├── docker-compose.yml
├── backend/src/main/java/com/kiprono/tickettracker/
│   ├── controller/     HTTP layer + OpenAPI annotations
│   ├── service/        use cases, transactions, business defaults
│   ├── repository/     Spring Data JPA
│   ├── domain/         entity and enums
│   ├── dto/            request / response records
│   ├── mapper/         entity -> DTO
│   ├── exception/      exceptions + global error handler
│   ├── config/         typed properties, CORS, OpenAPI
│   ├── constant/       API paths, field limits, pagination contract
│   └── util/           pagination and enum helpers
└── frontend/src/
    ├── api/            fetch wrapper + ticket endpoints
    ├── hooks/          data loading (list, stats)
    ├── pages/          the one screen; owns data and mutations
    ├── components/
    │   ├── common/     Badge, SelectField, Pagination
    │   └── tickets/    form, list, row, stats bar
    ├── constants/      labels, limits, API routes
    ├── types/          API and domain types
    └── utils/          error and date formatting
```

## Decisions & Tradeoffs

**Assumptions I made where the brief was open**

- Routes are prefixed with `/api/v1`. The brief lists `/tickets`; I added a version prefix so a breaking change can ship next to the current contract.
- `page` is 1-based and `limit` defaults to 20 with a cap of 100 (both in YAML). Out-of-range values get a 400 instead of being silently clamped, so a client bug is visible.
- A new ticket is always `open`; the create endpoint does not accept a status. Any status can move to any other, because reopening a closed ticket is normal in support work.
- `PATCH` only touches status and priority, as specified. Title and description are immutable after creation.
- `id` is a `bigint` identity column. It is simpler to read and to index than a UUID, and nothing here needs ids generated outside the database.
- Stats always return all three statuses, with `0` for the empty ones, so clients never deal with a missing key.

**Shortcuts and what I would do with more time**

Flyway owns the schema and Hibernate only validates against it, which costs a little setup but means the schema is reviewed like code. Enums are stored as text with `CHECK` constraints rather than native Postgres enums, because adding a value is then a one-line migration. I did not add optimistic locking: two people changing the same ticket's status is last-write-wins, which I think is acceptable for a support queue but would not be for anything financial. There is no authentication, and no notion of who created or owns a ticket; that would be the first thing to add in a real system, together with an `updated_at` column and an audit trail of status changes. The list uses offset pagination, which is fine at this size, but I would switch to keyset pagination on `(created_at, id)` once the table is large, since deep offsets get slow and rows shift between pages while people are working. The stats endpoint runs a `GROUP BY` on every call; that is cheap with three statuses and an index, and I would only cache it if it showed up in profiling. On the frontend I used plain `fetch` and two small hooks instead of a data-fetching library, and after every write I re-fetch the list and the stats rather than updating local state optimistically: simpler and always correct, at the cost of an extra round trip. The UI has no automated tests; with more time I would add component tests for the form and the status change, plus a CI workflow that runs the backend tests and the frontend build on every push. Styling is a single hand-written stylesheet, which is enough for one page but would not scale to a larger app.
