# Person.java - Patient Entity Model

## Overview
`Person.java` is an immutable data model representing a hospital patient with personal, medical, and contact information. It uses Jackson annotations for JSON serialization/deserialization and is stored persistently.

## File Location
```
src/main/java/com/example/model/Person.java
```

## Class Declaration
```java
public class Person {
    // Immutable fields
    private final String id;
    private final String name;
    private final Address address;
    private final String gender;
    private final String bloodGroup;
    private final String email;
    private final String phone;
}
```

## Design Pattern
**Immutable Object Pattern:** All fields are `final` and no setters are provided, ensuring thread-safety and preventing accidental modifications after creation.

## Fields

### 1. id (String)
**Type:** Final String  
**Purpose:** Unique patient identifier  
**Constraints:** Cannot be null or empty  
**Example:** "P001", "PATIENT_2024_001"  
**JSON:** `@JsonProperty("id")`

### 2. name (String)
**Type:** Final String  
**Purpose:** Full name of the patient  
**Constraints:** Cannot be null or empty  
**Example:** "John Doe", "Jane Smith"  
**JSON:** `@JsonProperty("name")`

### 3. address (Address)
**Type:** Final Address  
**Purpose:** Embedded residential/medical facility address  
**Constraints:** Cannot be null  
**Structure:** { line1: "street", city: "city name" }  
**JSON:** `@JsonProperty("address")`

### 4. gender (String)
**Type:** Final String  
**Purpose:** Patient's gender  
**Allowed Values:** "Male", "Female", "Other"  
**Example:** "Male"  
**JSON:** `@JsonProperty("gender")`

### 5. bloodGroup (String)
**Type:** Final String  
**Purpose:** Blood type for medical purposes  
**Allowed Values:** "A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"  
**Example:** "O+"  
**JSON:** `@JsonProperty("bloodGroup")`

### 6. email (String)
**Type:** Final String  
**Purpose:** Patient's email address for contact  
**Format:** Standard email format  
**Example:** "john@example.com"  
**JSON:** `@JsonProperty("email")`

### 7. phone (String)
**Type:** Final String  
**Purpose:** Patient's phone number  
**Format:** 10-digit number (no spaces/hyphens)  
**Example:** "9876543210"  
**JSON:** `@JsonProperty("phone")`

## Constructor

### @JsonCreator Constructor
```java
@JsonCreator
public Person(
    @JsonProperty("id") String id,
    @JsonProperty("name") String name,
    @JsonProperty("address") Address address,
    @JsonProperty("gender") String gender,
    @JsonProperty("bloodGroup") String bloodGroup,
    @JsonProperty("email") String email,
    @JsonProperty("phone") String phone
)
```

**Purpose:** Deserializes JSON to Person object  
**Annotations:**
- `@JsonCreator` - Marks this as Jackson's designated constructor for JSON deserialization
- `@JsonProperty` - Maps JSON field names to constructor parameters

**Usage in JSON:**
```json
{
  "id": "P001",
  "name": "John Doe",
  "address": {
    "line1": "123 Main St",
    "city": "New York"
  },
  "gender": "Male",
  "bloodGroup": "O+",
  "email": "john@example.com",
  "phone": "9876543210"
}
```

## Getter Methods

### @JsonProperty Getters
**Purpose:** Serialize object fields to JSON with proper field names

| Method | Returns | JSON Name | Use Case |
|--------|---------|-----------|----------|
| `getId()` | String | "id" | Get patient unique ID |
| `getName()` | String | "name" | Get patient's full name |
| `getAddress()` | Address | "address" | Get patient's address |
| `getGender()` | String | "gender" | Get patient's gender |
| `getBloodGroup()` | String | "bloodGroup" | Get patient's blood type |
| `getEmail()` | String | "email" | Get patient's email |
| `getPhone()` | String | "phone" | Get patient's phone number |

### Alternative Accessor Methods (Records-style)
These shorter methods provide functional programming style access:

```java
public String id()      // Alternative to getId()
public String name()    // Alternative to getName()
public Address address() // Alternative to getAddress()
```

**Usage:**
```java
Person p = new Person(...);
String patientId = p.id();      // Shorter syntax
String patientName = p.name();  // Preferred in modern code
```

## Override Methods

### toString()
**Purpose:** Human-readable string representation

**Output Format:**
```
John Doe (P001) @ 123 Main St, New York
```

**Pattern:** `{name} ({id}) @ {address}`

**Usage:**
```java
System.out.println(person); // Calls toString()
logger.info("Patient: {}", person);
```

### equals(Object)
**Purpose:** Compare two Person objects by content, not by reference

**Comparison Logic:**
- Compares `id`, `name`, and `address` fields
- Ignores email, phone, gender, bloodGroup

**Usage:**
```java
Person p1 = new Person("P001", "John", addr1, ...);
Person p2 = new Person("P001", "John", addr1, ...);
boolean same = p1.equals(p2);  // true - same content
```

**Note:** Returns false if compared with non-Person objects

### hashCode()
**Purpose:** Generates hash code based on id, name, address

**Usage:** For use in HashMap, HashSet collections
```java
Set<Person> uniquePatients = new HashSet<>();
uniquePatients.add(person1);
uniquePatients.add(person2); // Will use hashCode() for equality
```

**Contract:** If two objects are equal, they must have the same hash code

## JSON Serialization Examples

### Serialization (Java → JSON)
```java
Person person = new Person("P001", "John Doe", address, "Male", "O+", "john@email.com", "9876543210");
ObjectMapper mapper = new ObjectMapper();
String json = mapper.writeValueAsString(person);

// Output:
{
  "id": "P001",
  "name": "John Doe",
  "address": {
    "line1": "123 Main St",
    "city": "New York"
  },
  "gender": "Male",
  "bloodGroup": "O+",
  "email": "john@email.com",
  "phone": "9876543210"
}
```

### Deserialization (JSON → Java)
```java
String json = "{\"id\":\"P001\", \"name\":\"John Doe\", ...}";
ObjectMapper mapper = new ObjectMapper();
Person person = mapper.readValue(json, Person.class);
// Uses @JsonCreator constructor
```

## Data Validation

### Frontend Validation (HTML Form)
```html
<input type="text" id="id" required />
<input type="text" id="name" required />
<input type="email" id="email" />
<input type="tel" id="phone" />
<select id="gender" required>
  <option>Male</option>
  <option>Female</option>
  <option>Other</option>
</select>
<select id="bloodGroup" required>
  <option>A+</option><option>A-</option>
  <option>B+</option><option>B-</option>
  <option>AB+</option><option>AB-</option>
  <option>O+</option><option>O-</option>
</select>
```

### Backend Validation (PersonController)
```java
if (request.getId() == null || request.getId().isEmpty()) {
    return ResponseEntity.badRequest().body("ID cannot be empty");
}
if (request.getName() == null || request.getName().isEmpty()) {
    return ResponseEntity.badRequest().body("Name cannot be empty");
}
// ... more validations
```

## Immutability Benefits

1. **Thread-Safety:** Can be safely shared between threads without synchronization
2. **Predictability:** Values cannot be accidentally modified after creation
3. **Hashable:** Safe to use in HashMap, HashSet without issues
4. **Debugging:** Easier to trace state changes (only at creation)

## Storage Format (persons.json)
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

## Memory Footprint
- Typical Person object: ~400-500 bytes
- With 1000 patients: ~400-500 KB
- Suitable for in-memory storage up to 10,000 patients

## Related Classes
- [Address.md](Address.md) - Nested address entity
- [PersonController.md](PersonController.md) - REST API for Person CRUD
- [PersonService.md](PersonService.md) - Business logic for Person operations
- [PersistentPersonRepository.md](PersistentPersonRepository.md) - Data persistence

## Use Cases in Application

### Registration Flow
```
User Form → PersonRequest → Person Object → Repository.save() → persons.json
```

### Retrieval Flow
```
persons.json → ObjectMapper → List<Person> → REST API → JSON Response
```

### Deletion Flow
```
DELETE /api/persons/{id} → Repository.delete(id) → persons.json updated
```

## Future Enhancements
1. Add `@NotNull`, `@NotBlank` JSR-303 validation annotations
2. Add timestamp fields: `createdAt`, `updatedAt`
3. Add `patientHistory` field for medical records
4. Move to database persistence (JPA/Hibernate)
5. Add encryption for sensitive fields (email, phone)
