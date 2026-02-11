# User Registration Project - Source Code Documentation

## Overview
This folder contains comprehensive documentation for every Java source file in the Hospital Patient Registration System. Each file has detailed explanations of architecture, design patterns, methods, and usage examples.

---

## Documentation Index

### Entry Point
- **[App.md](App.md)** - Spring Boot Application Entry Point
  - Main class and application initialization
  - Component scanning and auto-configuration
  - Server startup and configuration

### Data Models
- **[Person.md](Person.md)** - Patient Entity Model
  - Patient data structure with 7 fields
  - Immutable design pattern
  - JSON serialization with Jackson annotations
  - Medical information fields (gender, blood group)
  - Contact information (email, phone)

- **[Address.md](Address.md)** - Patient Address Entity
  - Address embedded in Person
  - Immutable value object
  - Dual property accessors for backward compatibility
  - Street address and city fields

### REST API Layer
- **[PersonController.md](PersonController.md)** - REST API Endpoints
  - 3 HTTP endpoints (POST, GET, DELETE)
  - Request validation and error handling
  - Response entity patterns
  - CORS configuration
  - Full API documentation with examples

### Business Logic Layer
- **[PersonService.md](PersonService.md)** - Business Logic Service
  - Service layer abstraction
  - Dependency injection pattern
  - 3 CRUD methods (register, list, delete)
  - Repository pattern integration

### Data Access Layer
- **[PersonRepository.md](PersonRepository.md)** - Data Access Interface
  - Repository pattern definition
  - 4 interface methods (findById, findAll, save, delete)
  - Optional return values
  - Search patterns and examples

- **[PersistentPersonRepository.md](PersistentPersonRepository.md)** - File-Based Persistence
  - JSON file storage (persons.json)
  - In-memory caching with HashMap
  - Thread-safety considerations
  - Performance characteristics
  - Error handling and recovery

- **[InMemoryPersonRepository.md](InMemoryPersonRepository.md)** - In-Memory Storage
  - Alternative non-persistent implementation
  - Testing and temporary caching
  - Thread-safety issues
  - Memory characteristics

---

## Architecture Overview

```
┌─────────────────────────────────────┐
│     App.java                        │
│  (Spring Boot Entry Point)          │
└────────────────┬────────────────────┘
                 │
┌────────────────▼─────────────────────┐
│  PersonController.java              │
│  (REST API: POST /register)          │
│             (GET /list)              │
│             (DELETE /delete)         │
└────────────────┬─────────────────────┘
                 │
┌────────────────▼─────────────────────┐
│  PersonService.java                 │
│  (Business Logic)                   │
└────────────────┬─────────────────────┘
                 │
┌────────────────▼─────────────────────┐
│  PersonRepository.java (Interface)  │
│                                     │
├──── PersistentPersonRepository       │
│     (File-based: persons.json)      │
│                                     │
└──── InMemoryPersonRepository         │
      (In-memory: HashMap)            │
```

---

## Class Responsibilities

| Class | Responsibility |
|-------|-----------------|
| **App** | Application bootstrap and component scanning |
| **PersonController** | HTTP request handling and validation |
| **PersonService** | Business logic and orchestration |
| **PersonRepository** | Define persistence contract |
| **PersistentPersonRepository** | Implement file-based storage |
| **InMemoryPersonRepository** | Implement in-memory storage |
| **Person** | Patient data model |
| **Address** | Embedded address entity |

---

## Design Patterns Used

### 1. **Repository Pattern**
- PersonRepository interface
- Multiple implementations (Persistent, InMemory)
- Service layer uses interface, not concrete class
- Easy to swap implementations

### 2. **Service Layer Pattern**
- PersonService encapsulates business logic
- Controllers delegate to service
- Reusable across endpoints

### 3. **Dependency Injection**
- Constructor-based injection
- PersonService receives PersonRepository
- Testable and loosely coupled

### 4. **Immutable Object Pattern**
- Person and Address are immutable
- All fields final
- No setters
- Thread-safe

### 5. **Data Access Object (DAO) Pattern**
- PersonRepository abstracts data access
- Changes to storage layer don't affect service

### 6. **Optional Pattern**
- findById returns Optional<Person>
- Null-safe
- Forces explicit handling of "not found" case

---

## HTTP Endpoints

| Method | URL | Description | Request | Response |
|--------|-----|-------------|---------|----------|
| POST | /api/persons | Register patient | PersonRequest JSON | Person JSON (200) |
| GET | /api/persons | List all patients | None | List<Person> JSON (200) |
| DELETE | /api/persons/{id} | Delete patient | ID in path | Success message (200) |

### Endpoint Details
- **Base URL:** http://localhost:8080
- **Port:** 8080 (configurable)
- **CORS:** Enabled for all origins
- **Content-Type:** application/json

---

## Data Flow Examples

### Registration Flow
```
HTML Form (index.html)
    ↓ (POST with JSON)
PersonController.register()
    ↓ (Validation)
PersonService.register()
    ↓ (Business logic)
PersonRepository.save()
    ↓ (Persistence)
HashMap → persons.json
    ↓ (Response)
Browser (Success message)
```

### Retrieval Flow
```
Browser (fetch /api/persons)
    ↓
PersonController.list()
    ↓
PersonService.list()
    ↓
PersonRepository.findAll()
    ↓
HashMap (from loaded persons.json)
    ↓
List<Person> (JSON serialized)
    ↓
Browser (Display list)
```

### Deletion Flow
```
Delete Button (JavaScript)
    ↓ (DELETE /api/persons/{id})
PersonController.delete()
    ↓
PersonService.delete()
    ↓
PersonRepository.delete()
    ↓
HashMap.remove() → persons.json updated
    ↓
Success response
    ↓
Browser (Update UI)
```

---

## Configuration

### Server Configuration
- **File:** src/main/resources/application.properties
- **Port:** 8080
- **Context Path:** /
- **Logging Level:** DEBUG for com.example

### Build Configuration
- **File:** pom.xml
- **Framework:** Spring Boot 3.2.1
- **Java Version:** 17+
- **Build Tool:** Maven 3.6+

---

## Key Technologies

| Technology | Version | Purpose |
|-----------|---------|---------|
| Spring Boot | 3.2.1 | Web framework |
| Apache Tomcat | 10.1.17 | Web server |
| Jackson | 2.15.3 | JSON processing |
| JUnit | 5.10.1 | Testing |
| Java | 17+ | Language |
| SLF4J | Latest | Logging |

---

## File-Based Persistence

### Data File
- **Location:** persons.json (project root)
- **Format:** JSON with 2-space indentation
- **Auto-created:** On first patient registration
- **Human-readable:** Yes

### Example Content
```json
[
  {
    "id": "P001",
    "name": "John Doe",
    "address": {
      "line1": "123 Main Street",
      "city": "Kolkata"
    },
    "gender": "Male",
    "bloodGroup": "O+",
    "email": "john@example.com",
    "phone": "9876543210"
  }
]
```

### File Size Estimates
- **1 patient:** ~300 bytes
- **100 patients:** ~30 KB
- **1000 patients:** ~300 KB
- **Practical limit:** ~10,000 patients

---

## Error Handling

### Validation Errors (400 Bad Request)
- Missing required fields (ID, name, gender, blood group)
- Empty string values
- Invalid data types

### Server Errors (500 Internal Server Error)
- File I/O exceptions
- JSON serialization failures
- Unexpected exceptions

### Safe Operations
- Delete non-existent patient: Returns success (no error)
- List when empty: Returns empty array

---

## Testing

### Unit Test Approach
```java
// Use InMemoryPersonRepository for tests
PersonRepository repo = new InMemoryPersonRepository();
PersonService service = new PersonService(repo);

// Test business logic without persistence
Person patient = new Person(...);
service.register(patient);
assertEquals(1, service.list().size());
```

### Integration Test Approach
```java
// Use actual controller with test repository
@SpringBootTest
public class PersonControllerTest {
    @MockBean
    PersonRepository repo = new InMemoryPersonRepository();
}
```

---

## Performance Characteristics

### Operation Times
| Operation | Time | Complexity |
|-----------|------|-----------|
| find by ID | <1ms | O(1) |
| list all | ~5ms (100 patients) | O(n) |
| save | ~50ms | O(n) file write |
| delete | ~50ms | O(n) file write |

### Scalability
- **Recommended limit:** 10,000 patients
- **Hard limit:** ~100,000 (memory constraints)
- **Recommended upgrade:** Migrate to database at >10k patients

---

## Security Considerations

### Current Implementation
- ⚠️ No authentication/authorization
- ⚠️ CORS allows all origins
- ⚠️ No input sanitization
- ⚠️ No rate limiting

### Recommended Improvements
1. Add Spring Security
2. Implement JWT token authentication
3. Restrict CORS to specific domains
4. Add input validation with JSR-303
5. Implement rate limiting
6. Add HTTPS/TLS

---

## Future Enhancements

### Phase 1: Database Migration
- Replace file-based with SQL database
- Add Spring Data JPA
- Implement transaction support
- Add database indexing

### Phase 2: Authentication
- Add user login
- Implement JWT tokens
- Add role-based access control
- Audit logging

### Phase 3: Advanced Features
- Patient search and filtering
- Medical records integration
- Hospital referral system
- Appointment scheduling
- Document upload

### Phase 4: DevOps
- Docker containerization
- CI/CD pipeline
- Cloud deployment (AWS/GCP)
- Load balancing
- Database replication

---

## Quick Links

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Jackson JSON Documentation](https://github.com/FasterXML/jackson)
- [Maven Documentation](https://maven.apache.org/)
- [JUnit 5 Documentation](https://junit.org/junit5/)

---

## Document Status

| Document | Status | Last Updated |
|----------|--------|--------------|
| App.md | ✅ Complete | 2026-01-30 |
| Person.md | ✅ Complete | 2026-01-30 |
| Address.md | ✅ Complete | 2026-01-30 |
| PersonController.md | ✅ Complete | 2026-01-30 |
| PersonService.md | ✅ Complete | 2026-01-30 |
| PersonRepository.md | ✅ Complete | 2026-01-30 |
| PersistentPersonRepository.md | ✅ Complete | 2026-01-30 |
| InMemoryPersonRepository.md | ✅ Complete | 2026-01-30 |

---

## Documentation Standards

Each document includes:
- **Overview:** Purpose and responsibility
- **Design Patterns:** Architectural patterns used
- **Fields/Methods:** Complete reference documentation
- **Examples:** Real usage examples with code
- **Performance:** Time/space complexity
- **Related Classes:** Links to related documentation
- **Future Enhancements:** Improvement suggestions

---

## How to Use This Documentation

1. **Start with [App.md](App.md)** for high-level overview
2. **Review [PersonController.md](PersonController.md)** for API endpoints
3. **Study [PersonService.md](PersonService.md)** for business logic
4. **Understand [PersonRepository.md](PersonRepository.md)** for data access patterns
5. **Deep dive into [PersistentPersonRepository.md](PersistentPersonRepository.md)** for persistence details
6. **Refer to [Person.md](Person.md) and [Address.md](Address.md)** for data models

---

**Documentation Version:** 1.0  
**Last Updated:** January 30, 2026  
**Applicable to:** Hospital Patient Registration System v1.0.0
