# PersonService.java - Business Logic Service Layer

## Overview
`PersonService.java` is the service layer implementing the business logic for patient management operations. It acts as a facade between the REST controller and the data persistence layer using the dependency injection pattern.

## File Location
```
src/main/java/com/example/service/PersonService.java
```

## Class Declaration
```java
public class PersonService {
    private final PersonRepository repo;
    
    public PersonService(PersonRepository repo) {
        this.repo = Objects.requireNonNull(repo);
    }
}
```

## Design Patterns

### Service Layer Pattern
**Purpose:** Encapsulate business logic separate from controllers  
**Benefit:** Testability, reusability, and maintainability

### Dependency Injection
**Pattern:** Constructor injection  
**Implementation:**
```java
public PersonService(PersonRepository repo) {
    this.repo = Objects.requireNonNull(repo);  // Null check
}
```

**Benefit:** Allows swapping implementations (InMemory vs Persistent)

### Repository Pattern
**Abstraction:** PersonRepository interface  
**Implementations:**
- PersistentPersonRepository (File-based)
- InMemoryPersonRepository (In-memory)

**Benefit:** Loose coupling from data storage mechanism

## Fields

### repo (PersonRepository)
```java
private final PersonRepository repo;
```

**Type:** Final PersonRepository  
**Purpose:** Data persistence abstraction  
**Initialization:** Constructor parameter  
**Usage:** All CRUD operations delegated to repository

**Implementations:**
1. **PersistentPersonRepository** (Current)
   - Stores data in persons.json file
   - Survives application restarts
   - Thread-safe with CopyOnWriteArrayList

2. **InMemoryPersonRepository** (Alternative)
   - Stores in HashMap
   - Loses data on restart
   - Faster for testing

## Methods

### 1. register(Person p)

#### Signature
```java
public Person register(Person p) {
    repo.save(p);
    return p;
}
```

#### Parameters
| Parameter | Type | Description |
|-----------|------|-------------|
| p | Person | Patient object to register |

#### Return Value
| Value | Type | Description |
|-------|------|-------------|
| Person | Person | Same person object passed in |

#### Processing Steps

1. **Validate Input**
   - PersonService itself doesn't validate
   - Validation done in PersonController
   - Person object is assumed valid

2. **Persist Data**
   ```java
   repo.save(p);  // Delegates to repository
   ```
   - File-based: Writes to persons.json
   - In-memory: Stores in HashMap
   - Returns after save completes

3. **Return Person**
   ```java
   return p;  // Same object, now persisted
   ```

#### Usage Example
```java
// In PersonController
Person person = new Person(
    "P001", 
    "John Doe", 
    address,
    "Male",
    "O+",
    "john@example.com",
    "9876543210"
);

Person registered = service.register(person);
// Now available in persons.json
```

#### Flow Diagram
```
register(person)
    ↓
repo.save(person)
    ↓
PersistentPersonRepository.save()
    ↓
store.put(id, person)  // Add to HashMap
    ↓
saveToFile()  // Write to persons.json
    ↓
return person
```

#### Error Handling
```java
try {
    service.register(person);
} catch (Exception e) {
    logger.error("Failed to register", e);
    return ResponseEntity.status(500).body(error);
}
```

#### Data Persistence
```json
// persons.json after registration
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

---

### 2. list()

#### Signature
```java
public List<Person> list() {
    return repo.findAll();
}
```

#### Return Value
| Value | Type | Description |
|-------|------|-------------|
| List of all patients | List<Person> | Can be empty if no patients |

#### Processing Steps

1. **Fetch From Repository**
   ```java
   return repo.findAll();
   ```
   - File-based: Loads from persons.json
   - In-memory: Returns HashMap values as ArrayList

2. **Return List**
   - Returns unmodifiable view (safe from external changes)
   - Returns empty list if no patients exist

#### Usage Example
```java
// In PersonController
List<Person> patients = service.list();

if (patients.isEmpty()) {
    System.out.println("No patients registered");
} else {
    System.out.println("Total patients: " + patients.size());
    for (Person p : patients) {
        System.out.println(p.getName());
    }
}
```

#### Return Values

**With Patients:**
```json
[
  {
    "id": "P001",
    "name": "John Doe",
    "address": {...},
    "gender": "Male",
    "bloodGroup": "O+",
    "email": "john@example.com",
    "phone": "9876543210"
  },
  {
    "id": "P002",
    "name": "Jane Smith",
    ...
  }
]
```

**No Patients:**
```json
[]
```

#### Flow Diagram
```
list()
    ↓
repo.findAll()
    ↓
PersistentPersonRepository.findAll()
    ↓
return new ArrayList<>(store.values())
    ↓
return to frontend
    ↓
JSON serialization
```

#### Frontend Auto-Refresh
```javascript
// Frontend code
setInterval(async () => {
    const response = await fetch('/api/persons');
    const patients = await response.json();
    
    // Update patient list display
    displayPatients(patients);
}, 2000);  // Every 2 seconds
```

#### Performance Impact
- **1 patient:** ~1ms to list
- **100 patients:** ~5ms to list
- **1000 patients:** ~50ms to list
- **10000 patients:** ~500ms to list (consider database migration)

---

### 3. delete(String id)

#### Signature
```java
public void delete(String id) {
    repo.delete(id);
}
```

#### Parameters
| Parameter | Type | Description |
|-----------|------|-------------|
| id | String | Unique patient identifier |

#### Return Value
| Value | Type | Description |
|-------|------|-------------|
| None | void | No return value |

#### Processing Steps

1. **Validate Input**
   - ID is assumed non-null (checked in controller)
   - No existence check (safe to delete non-existent)

2. **Delete From Repository**
   ```java
   repo.delete(id);
   ```
   - File-based: Removes from HashMap, updates persons.json
   - In-memory: Removes from HashMap only

3. **Confirm Deletion**
   - Returns when delete completes
   - No exception if ID not found (safe operation)

#### Usage Example
```java
// In PersonController
String patientId = "P001";
service.delete(patientId);
// Patient removed from system
```

#### Flow Diagram
```
DELETE /api/persons/{id}
    ↓
delete(id)
    ↓
repo.delete(id)
    ↓
PersistentPersonRepository.delete()
    ↓
store.remove(id)
    ↓
saveToFile()  // Update persons.json
    ↓
return success response
```

#### Data After Deletion
```json
// persons.json before deletion
[
  {"id": "P001", "name": "John Doe", ...},
  {"id": "P002", "name": "Jane Smith", ...}
]

// After delete("P001")
[
  {"id": "P002", "name": "Jane Smith", ...}
]
```

#### Error Handling
```java
try {
    service.delete(id);
    return ResponseEntity.ok("Person deleted successfully");
} catch (Exception e) {
    logger.error("Failed to delete", e);
    return ResponseEntity.status(500).body(error);
}
```

#### Safe Deletion
- Deleting non-existent ID returns success (no error)
- Prevents "404 Not Found" confusing UI

#### Frontend Deletion
```javascript
async function deletePatient(id) {
    if (!confirm("Delete patient?")) return;
    
    const response = await fetch(`/api/persons/${id}`, {
        method: 'DELETE'
    });
    
    if (response.ok) {
        // Remove from UI
        document.getElementById(id).remove();
    }
}
```

---

## Constructor

### PersonService Constructor
```java
public PersonService(PersonRepository repo) {
    this.repo = Objects.requireNonNull(repo);
}
```

#### Parameters
| Parameter | Type | Required | Example |
|-----------|------|----------|---------|
| repo | PersonRepository | Yes | new PersistentPersonRepository() |

#### Null Check
```java
Objects.requireNonNull(repo)
```
**Purpose:** Throw NullPointerException if repo is null  
**Behavior:** Fails fast at construction time  
**Benefit:** Prevents null pointer errors later

#### Initialization
```java
// In PersonController
private static final PersonService service = 
    new PersonService(new PersistentPersonRepository());

// Alternative: Could use InMemoryPersonRepository
// private static final PersonService service = 
//     new PersonService(new InMemoryPersonRepository());
```

#### Dependency Injection Benefits

| Benefit | Description |
|---------|-------------|
| **Testability** | Can inject mock repository for unit tests |
| **Flexibility** | Swap implementation without changing service code |
| **Loose Coupling** | Service depends on interface, not concrete class |
| **Maintainability** | Easy to change persistence layer |

---

## Design Considerations

### Current Architecture
```
PersonController
    ↓
PersonService (Single instance)
    ↓
PersistentPersonRepository (File-based)
    ↓
persons.json (File storage)
```

### Potential Issues

1. **Single PersonService Instance**
   - Thread-safe for file-based repo
   - Could be bottleneck for high concurrency
   - No transaction management

2. **File-Based Persistence**
   - Slow for large datasets (>10k patients)
   - No concurrent access control
   - Loss of data if file corrupted

3. **No Validation in Service**
   - Assumes controller validates
   - Could accept invalid data from other sources

### Future Improvements

**Proposal 1: Add Validation**
```java
public class PersonService {
    public Person register(Person p) {
        if (p == null || p.getName().isEmpty()) {
            throw new IllegalArgumentException("Invalid person");
        }
        repo.save(p);
        return p;
    }
}
```

**Proposal 2: Database Migration**
```java
@Service
public class PersonService {
    @Autowired
    private PersonRepository personRepo;  // Spring JPA
    
    public Person register(Person p) {
        return personRepo.save(p);  // JPA save
    }
}
```

**Proposal 3: Transaction Management**
```java
@Service
public class PersonService {
    @Transactional
    public Person register(Person p) {
        // Atomic operation
        repo.save(p);
        return p;
    }
}
```

**Proposal 4: Caching**
```java
@Service
@EnableCaching
public class PersonService {
    @Cacheable("patients")
    public List<Person> list() {
        return repo.findAll();
    }
    
    @CacheEvict("patients")
    public void register(Person p) {
        repo.save(p);
    }
}
```

---

## Testing

### Unit Test Example
```java
@Test
public void testRegisterPatient() {
    PersonRepository mockRepo = new InMemoryPersonRepository();
    PersonService service = new PersonService(mockRepo);
    
    Person patient = new Person("P001", "John", address, "Male", "O+", "john@email.com", "9876543210");
    Person registered = service.register(patient);
    
    assertEquals("P001", registered.getId());
    assertEquals("John", registered.getName());
}

@Test
public void testListPatients() {
    PersonRepository mockRepo = new InMemoryPersonRepository();
    PersonService service = new PersonService(mockRepo);
    
    service.register(patient1);
    service.register(patient2);
    
    List<Person> patients = service.list();
    assertEquals(2, patients.size());
}

@Test
public void testDeletePatient() {
    PersonRepository mockRepo = new InMemoryPersonRepository();
    PersonService service = new PersonService(mockRepo);
    
    service.register(patient1);
    service.delete("P001");
    
    List<Person> patients = service.list();
    assertEquals(0, patients.size());
}
```

---

## Integration with Other Components

### From Controller
```java
// PersonController uses PersonService
private static final PersonService service = 
    new PersonService(new PersistentPersonRepository());

@PostMapping
public ResponseEntity<?> register(@RequestBody PersonRequest request) {
    Person person = new Person(...);
    service.register(person);  // Calls service
    return ResponseEntity.ok(person);
}

@GetMapping
public ResponseEntity<List<Person>> list() {
    return ResponseEntity.ok(service.list());  // Calls service
}

@DeleteMapping("/{id}")
public ResponseEntity<?> delete(@PathVariable String id) {
    service.delete(id);  // Calls service
    return ResponseEntity.ok("Deleted");
}
```

---

## Related Classes
- [PersonController.md](PersonController.md) - Uses PersonService
- [PersonRepository.md](PersonRepository.md) - Interface for persistence
- [PersistentPersonRepository.md](PersistentPersonRepository.md) - Concrete implementation
- [Person.md](Person.md) - Data model
