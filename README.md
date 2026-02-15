# Resume Builder

Simple Spring Boot application to create, view, update, and delete resumes with a web UI and REST API.

## Features

- Create and manage resumes in memory (no database required).
- Live resume form UI at the root path (`/`).
- REST endpoints for CRUD operations under `/api/resumes`.
- Input validation with clear error responses.

## Tech Stack

- Java 21
- Spring Boot 4.0.0
- Maven Wrapper (`mvnw`, `mvnw.cmd`)
- Lombok

## Project Structure

- `src/main/java/com/resume/builder/resume_builder/controller` - REST controller
- `src/main/java/com/resume/builder/resume_builder/service` - business logic + in-memory store
- `src/main/java/com/resume/builder/resume_builder/dto` - request DTOs + validation
- `src/main/resources/static` - frontend (`index.html`, `app.js`, `style.css`)
- `src/test/java` - unit and web layer tests

## Prerequisites

- JDK 21 installed

## Run Locally

### Windows (PowerShell)

```powershell
.\mvnw.cmd spring-boot:run
```

### macOS/Linux

```bash
./mvnw spring-boot:run
```

Application URLs:

- UI: `http://localhost:8080`
- API base: `http://localhost:8080/api/resumes`

## Run Tests

### Windows (PowerShell)

```powershell
.\mvnw.cmd test
```

### macOS/Linux

```bash
./mvnw test
```

## API Endpoints

- `POST /api/resumes` - create resume
- `GET /api/resumes` - list all resumes
- `GET /api/resumes/{id}` - get one resume
- `PUT /api/resumes/{id}` - update resume
- `DELETE /api/resumes/{id}` - delete resume

### Sample Create Request

```bash
curl -X POST http://localhost:8080/api/resumes \
  -H "Content-Type: application/json" \
  -d '{
    "fullName": "Jane Doe",
    "headline": "Java Developer",
    "email": "jane@example.com",
    "phone": "+1 555 0100",
    "summary": "Backend engineer focused on Spring Boot APIs.",
    "skills": ["Java", "Spring Boot", "REST"],
    "educations": [
      {
        "school": "State University",
        "degree": "B.Tech Computer Science",
        "startDate": "2019",
        "endDate": "2023",
        "description": "Focused on software engineering."
      }
    ],
    "experiences": [
      {
        "company": "Acme Corp",
        "role": "Software Engineer",
        "startDate": "2023-07",
        "endDate": "Present",
        "description": "Built internal microservices."
      }
    ]
  }'
```

## Notes

- Data is stored in memory (`ConcurrentHashMap`) and is lost when the app stops.
- Validation errors return HTTP `400` with `errors` details.
- Missing records return HTTP `404`.
