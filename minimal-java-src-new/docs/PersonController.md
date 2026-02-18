# PersonController.java - REST API Endpoints

## Overview
`PersonController.java` is the REST API controller handling HTTP requests for patient registration, retrieval, and deletion. It serves as the bridge between the frontend and backend business logic with comprehensive validation and error handling.

## File Location
```
src/main/java/com/example/controller/PersonController.java
```

## Class Declaration
```java
@RestController
@RequestMapping("/api/persons")
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {...})
public class PersonController {
    private static final Logger logger = LoggerFactory.getLogger(PersonController.class);
    private static final PersonService service = new PersonService(new PersistentPersonRepository());
}
```

## Class-Level Annotations

### @RestController
**Purpose:** Marks class as REST API controller  
**Behavior:**
- Combines `@Controller` + `@ResponseBody`
- All methods return JSON by default
- No view resolution needed

### @RequestMapping("/api/persons")
**Purpose:** Base URL path for all endpoints  
**Path:** All endpoints use this prefix
- POST /api/persons → register()
- GET /api/persons → list()
- DELETE /api/persons/{id} → delete()

### @CrossOrigin
**Purpose:** Enable cross-origin requests from browsers  
**Configuration:**
```java
@CrossOrigin(
    origins = "*",              // Allow all origins
    allowedHeaders = "*",       // Allow all headers
    methods = {                 // Allowed HTTP methods
        RequestMethod.GET,
        RequestMethod.POST,
        RequestMethod.OPTIONS
    }
)
```

**Use Cases:**
- Frontend on different port (localhost:3000 → localhost:8080)
- Frontend on different domain (app.example.com → api.example.com)
- CORS preflight requests (OPTIONS)

## Instance Variables

### Logger
```java
private static final Logger logger = LoggerFactory.getLogger(PersonController.class);
```
**Purpose:** Log request/response information for debugging  
**Type:** SLF4J Logger  
**Usage:**
```java
logger.info("Registering person: {}", request.getName());
logger.error("Error registering person", e);
```

### PersonService
```java
private static final PersonService service = new PersonService(new PersistentPersonRepository());
```
**Purpose:** Dependency for business logic operations  
**Initialization:** File-based repository for persistent storage  
**Operations:**
- `service.register(person)` - Save patient
- `service.list()` - Get all patients
- `service.delete(id)` - Remove patient

## Endpoints

### 1. POST /api/persons - Register Patient

#### Method Signature
```java
@PostMapping
public ResponseEntity<?> register(@RequestBody PersonRequest request)
```

#### HTTP Details
| Aspect | Value |
|--------|-------|
| **Method** | POST |
| **URL** | http://localhost:8080/api/persons |
| **Content-Type** | application/json |
| **Response Type** | application/json |

#### Request Body (PersonRequest)
```json
{
  "id": "P001",
  "name": "John Doe",
  "street": "123 Main Street",
  "city": "Kolkata",
  "gender": "Male",
  "bloodGroup": "O+",
  "email": "john@example.com",
  "phone": "9876543210"
}
```

#### Request Validation

**Step 1: ID Validation**
```java
if (request.getId() == null || request.getId().isEmpty()) {
    return ResponseEntity.badRequest().body(
        createErrorResponse("ID cannot be empty")
    );
}
```
**Error Response:** 400 Bad Request

**Step 2: Name Validation**
```java
if (request.getName() == null || request.getName().isEmpty()) {
    return ResponseEntity.badRequest().body(
        createErrorResponse("Name cannot be empty")
    );
}
```

**Step 3: Gender Validation**
```java
if (request.getGender() == null || request.getGender().isEmpty()) {
    return ResponseEntity.badRequest().body(
        createErrorResponse("Gender cannot be empty")
    );
}
```

**Step 4: Blood Group Validation**
```java
if (request.getBloodGroup() == null || request.getBloodGroup().isEmpty()) {
    return ResponseEntity.badRequest().body(
        createErrorResponse("Blood group cannot be empty")
    );
}
```

#### Processing Steps

1. **Log Request**
   ```java
   logger.info("Registering person: {}", request.getName());
   ```

2. **Validate All Fields**
   - Check for null/empty values
   - Return 400 if validation fails

3. **Create Person Object**
   ```java
   Person person = new Person(
       request.getId(),
       request.getName(),
       new Address(request.getStreet(), request.getCity()),
       request.getGender(),
       request.getBloodGroup(),
       request.getEmail(),
       request.getPhone()
   );
   ```

4. **Persist Data**
   ```java
   service.register(person);
   ```

5. **Log Success & Return**
   ```java
   logger.info("Successfully registered person: {}", person);
   return ResponseEntity.ok(person);
   ```

#### Success Response (200 OK)
```json
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
```

#### Error Responses

**400 Bad Request - Empty ID**
```json
{
  "error": "ID cannot be empty"
}
```

**400 Bad Request - Empty Name**
```json
{
  "error": "Name cannot be empty"
}
```

**500 Internal Server Error**
```json
{
  "error": "Error: IOException occurred"
}
```

#### Frontend Example
```javascript
async function registerPatient() {
    const formData = {
        id: document.getElementById('id').value,
        name: document.getElementById('name').value,
        street: document.getElementById('street').value,
        city: document.getElementById('city').value,
        gender: document.getElementById('gender').value,
        bloodGroup: document.getElementById('bloodGroup').value,
        email: document.getElementById('email').value,
        phone: document.getElementById('phone').value
    };
    
    const response = await fetch('/api/persons', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(formData)
    });
    
    if (response.ok) {
        const patient = await response.json();
        console.log('Registered:', patient);
    } else {
        const error = await response.json();
        console.error('Error:', error.error);
    }
}
```

#### cURL Example
```bash
curl -X POST http://localhost:8080/api/persons \
  -H "Content-Type: application/json" \
  -d '{
    "id": "P001",
    "name": "John Doe",
    "street": "123 Main St",
    "city": "New York",
    "gender": "Male",
    "bloodGroup": "O+",
    "email": "john@example.com",
    "phone": "9876543210"
  }'
```

---

### 2. GET /api/persons - List All Patients

#### Method Signature
```java
@GetMapping
public ResponseEntity<List<Person>> list()
```

#### HTTP Details
| Aspect | Value |
|--------|-------|
| **Method** | GET |
| **URL** | http://localhost:8080/api/persons |
| **Accept** | application/json |
| **Query Parameters** | None |

#### Processing Steps

1. **Fetch From Repository**
   ```java
   List<Person> persons = service.list();
   ```

2. **Log Retrieved Count**
   ```java
   logger.info("Retrieved {} persons", persons.size());
   ```

3. **Return All Patients**
   ```java
   return ResponseEntity.ok(persons);
   ```

#### Success Response (200 OK)
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
  },
  {
    "id": "P002",
    "name": "Jane Smith",
    "address": {
      "line1": "456 Oak Avenue",
      "city": "Mumbai"
    },
    "gender": "Female",
    "bloodGroup": "A+",
    "email": "jane@example.com",
    "phone": "9876543211"
  }
]
```

#### Empty List Response
```json
[]
```

#### Error Response (500 Internal Server Error)
```
500 Internal Server Error
(No body, logged on server)
```

#### Frontend Example
```javascript
async function loadPatients() {
    const response = await fetch('/api/persons');
    const patients = await response.json();
    
    patients.forEach(patient => {
        console.log(`${patient.name} (${patient.id})`);
    });
}

// Auto-refresh every 2 seconds
setInterval(loadPatients, 2000);
```

#### cURL Example
```bash
curl -X GET http://localhost:8080/api/persons
```

---

### 3. DELETE /api/persons/{id} - Delete Patient

#### Method Signature
```java
@DeleteMapping("/{id}")
public ResponseEntity<?> delete(@PathVariable String id)
```

#### HTTP Details
| Aspect | Value |
|--------|-------|
| **Method** | DELETE |
| **URL** | http://localhost:8080/api/persons/{id} |
| **Path Parameter** | id - Patient ID |

#### Path Parameter

**id (String)**
- **Location:** URL path
- **Required:** Yes
- **Example:** "P001"
- **Extracted by:** @PathVariable String id

#### Processing Steps

1. **Extract Patient ID From URL**
   ```java
   // @PathVariable automatically extracts from URL
   // DELETE /api/persons/P001 → id = "P001"
   ```

2. **Log Deletion Request**
   ```java
   logger.info("Deleting person with ID: {}", id);
   ```

3. **Delete From Repository**
   ```java
   service.delete(id);
   ```

4. **Log Success**
   ```java
   logger.info("Successfully deleted person with ID: {}", id);
   ```

5. **Return Success Message**
   ```java
   return ResponseEntity.ok(createResponse("Person deleted successfully"));
   ```

#### Success Response (200 OK)
```json
{
  "message": "Person deleted successfully"
}
```

#### Error Response (500 Internal Server Error)
```json
{
  "error": "Error: IOException occurred"
}
```

#### Frontend Example
```javascript
async function deletePatient(patientId) {
    if (!confirm(`Delete patient ${patientId}?`)) {
        return;
    }
    
    const response = await fetch(`/api/persons/${patientId}`, {
        method: 'DELETE'
    });
    
    if (response.ok) {
        const result = await response.json();
        console.log(result.message);
        loadPatients(); // Refresh list
    } else {
        const error = await response.json();
        console.error('Error:', error.error);
    }
}
```

#### cURL Example
```bash
curl -X DELETE http://localhost:8080/api/persons/P001
```

---

## Helper Methods

### createErrorResponse(String message)
```java
private Map<String, String> createErrorResponse(String message) {
    Map<String, String> response = new HashMap<>();
    response.put("error", message);
    return response;
}
```

**Purpose:** Standardize error response format  
**Returns:** Map with "error" key  
**Example:**
```java
createErrorResponse("ID cannot be empty");
// Returns: {"error": "ID cannot be empty"}
```

### createResponse(String message)
```java
private Map<String, String> createResponse(String message) {
    Map<String, String> response = new HashMap<>();
    response.put("message", message);
    return response;
}
```

**Purpose:** Standardize success response format  
**Returns:** Map with "message" key  
**Example:**
```java
createResponse("Person deleted successfully");
// Returns: {"message": "Person deleted successfully"}
```

## Inner Class: PersonRequest

```java
public static class PersonRequest {
    private String id;
    private String name;
    private String street;
    private String city;
    private String gender;
    private String bloodGroup;
    private String email;
    private String phone;
    
    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    // ... more getters/setters
}
```

**Purpose:** DTO (Data Transfer Object) for receiving POST request data  
**Fields:**
- Matches HTML form field names
- Maps to Person object fields
- Used for validation and transformation

**JSON Mapping:**
```
JSON Field    → PersonRequest Field → Person Field
"id"          → id                   → id
"name"        → name                 → name
"street"      → street               → Address.line1
"city"        → city                 → Address.city
"gender"      → gender               → gender
"bloodGroup"  → bloodGroup           → bloodGroup
"email"       → email                → email
"phone"       → phone                → phone
```

## Exception Handling

### try-catch Pattern
```java
try {
    // Business logic
    logger.info("Processing request");
    service.register(person);
} catch (Exception e) {
    // Log error
    logger.error("Error registering person", e);
    // Return error response
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(createErrorResponse("Error: " + e.getMessage()));
}
```

### Exception Types Caught
- **IOException:** File write/read errors
- **NullPointerException:** Null field access
- **RuntimeException:** Unexpected errors

## Response Entity Pattern

### Success Response
```java
ResponseEntity.ok(person)                    // 200 OK
ResponseEntity.ok(persons)                   // 200 OK
ResponseEntity.ok(createResponse(msg))       // 200 OK
```

### Error Response
```java
ResponseEntity.badRequest()                  // 400 Bad Request
    .body(createErrorResponse(msg))

ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)  // 500
    .body(createErrorResponse(msg))
```

## Performance Considerations

### Request/Response Flow
```
HTTP Request
    ↓
@RequestBody deserialization (Jackson)
    ↓
Validation checks
    ↓
Business logic (service.register/delete/list)
    ↓
Persistence (file I/O)
    ↓
Response object creation
    ↓
@ResponseBody serialization (Jackson)
    ↓
HTTP Response
```

### Bottlenecks
1. **File I/O** - persons.json read/write (10-50ms)
2. **Validation** - Multiple null checks
3. **Serialization** - JSON conversion (1-5ms)

## Logging Output

### Registration Success
```
INFO  com.example.controller.PersonController - Registering person: John Doe
INFO  com.example.controller.PersonController - Successfully registered person: John Doe (P001) @ 123 Main Street, Kolkata
```

### Deletion Success
```
INFO  com.example.controller.PersonController - Deleting person with ID: P001
INFO  com.example.controller.PersonController - Successfully deleted person with ID: P001
```

### Error Case
```
ERROR com.example.controller.PersonController - Error registering person
java.io.IOException: Disk full
    at com.example.repo.PersistentPersonRepository.saveToFile(...)
```

## API Testing Checklist

- [ ] Register new patient with valid data
- [ ] Register with missing ID (should return 400)
- [ ] Register with missing name (should return 400)
- [ ] Register with missing gender (should return 400)
- [ ] Register with missing blood group (should return 400)
- [ ] List all patients (should return 200)
- [ ] List when empty (should return empty array)
- [ ] Delete existing patient (should return 200)
- [ ] Delete non-existent patient (should return 200)
- [ ] Verify patient data persists after restart

## Related Classes
- [PersonService.md](PersonService.md) - Business logic
- [Person.md](Person.md) - Data model
- [PersonRequest Documentation](#inner-class-personrequest)
- [PersistentPersonRepository.md](PersistentPersonRepository.md) - Data persistence
