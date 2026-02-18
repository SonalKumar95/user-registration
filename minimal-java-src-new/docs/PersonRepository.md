# PersonRepository.java - Data Access Interface

## Overview
`PersonRepository.java` is the interface defining the contract for data persistence operations. It follows the Repository Pattern, allowing different storage implementations (file-based, in-memory, database) to be swapped transparently.

## File Location
```
src/main/java/com/example/repo/PersonRepository.java
```

## Interface Declaration
```java
public interface PersonRepository {
    Optional<Person> findById(String id);
    List<Person> findAll();
    void save(Person person);
    void delete(String id);
}
```

## Design Pattern

### Repository Pattern
**Purpose:** Abstract data access logic from business logic  
**Benefit:** Switch persistence mechanisms without changing service code

### Dependency Inversion Principle (SOLID)
- Service depends on PersonRepository interface
- Not on concrete implementations
- High-level modules independent of low-level modules

### Data Access Object (DAO) Pattern
- Encapsulates SQL/file operations
- Provides clean abstraction layer
- Centralizes data access logic

## Interface Methods

### 1. findById(String id)

#### Signature
```java
Optional<Person> findById(String id);
```

#### Parameters
| Parameter | Type | Description |
|-----------|------|-------------|
| id | String | Unique patient identifier |

#### Return Value
| Value | Type | Description |
|-------|------|-------------|
| Person wrapped in Optional | Optional<Person> | Present if found, empty if not |

#### Purpose
Retrieve a single patient by ID

#### Return Value Details

**If Patient Exists:**
```java
Optional<Person> result = repo.findById("P001");
if (result.isPresent()) {
    Person patient = result.get();  // Get the person
    System.out.println(patient.getName());
}
```

**If Patient Not Found:**
```java
Optional<Person> result = repo.findById("P999");
if (result.isEmpty()) {
    System.out.println("Patient not found");
}

// Alternative
result.ifPresent(person -> System.out.println(person.getName()));
```

#### Why Optional?
- Avoids null pointer exceptions
- Forces explicit handling of "not found" case
- Cleaner code compared to nullable return

#### Usage Examples

**Traditional null check:**
```java
Person p = repo.findById("P001");
if (p != null) {
    System.out.println(p.getName());
}
```

**Optional approach (Preferred):**
```java
repo.findById("P001").ifPresent(p -> System.out.println(p.getName()));
```

**Get with default:**
```java
Person p = repo.findById("P001")
    .orElse(new Person("UNKNOWN", ...));
```

#### Implementation in PersistentPersonRepository
```java
@Override
public Optional<Person> findById(String id) {
    return Optional.ofNullable(store.get(id));
    // Returns empty Optional if not in HashMap
    // Returns Optional with Person if found
}
```

#### Implementation in InMemoryPersonRepository
```java
@Override
public Optional<Person> findById(String id) {
    return Optional.ofNullable(store.get(id));
}
```

#### Search Time Complexity
- **HashMap lookup:** O(1) - Constant time
- **Best case:** ~1 microsecond
- **Average case:** ~1 microsecond
- **Worst case:** O(n) - Extremely rare

---

### 2. findAll()

#### Signature
```java
List<Person> findAll();
```

#### Return Value
| Value | Type | Description |
|-------|------|-------------|
| All patients | List<Person> | Empty list if no patients |

#### Purpose
Retrieve all patients from storage

#### Return Value Examples

**With Patients:**
```java
List<Person> patients = repo.findAll();
// Returns: [Person("P001"), Person("P002"), Person("P003")]

patients.size();  // 3
patients.isEmpty();  // false
```

**No Patients:**
```java
List<Person> patients = repo.findAll();
// Returns: []

patients.size();  // 0
patients.isEmpty();  // true
```

#### Usage Examples

**Iterate through list:**
```java
List<Person> patients = repo.findAll();
for (Person p : patients) {
    System.out.println(p.getName());
}
```

**Stream operations:**
```java
repo.findAll()
    .stream()
    .filter(p -> p.getGender().equals("Male"))
    .map(Person::getName)
    .forEach(System.out::println);
```

**Count patients:**
```java
int totalPatients = repo.findAll().size();
System.out.println("Total registered: " + totalPatients);
```

#### Implementation in PersistentPersonRepository
```java
@Override
public List<Person> findAll() {
    return new ArrayList<>(store.values());
    // Returns new list of all stored persons
    // Changes to returned list don't affect internal store
}
```

#### Implementation in InMemoryPersonRepository
```java
@Override
public List<Person> findAll() {
    return new ArrayList<>(store.values());
}
```

#### Performance Characteristics
| Metric | Value |
|--------|-------|
| **Time Complexity** | O(n) - Linear |
| **Space Complexity** | O(n) - Creates new list |
| **1 patient** | ~1ms |
| **100 patients** | ~5ms |
| **1000 patients** | ~50ms |
| **10000 patients** | ~500ms |

#### Frontend Usage
```javascript
// Auto-refresh patient list every 2 seconds
setInterval(async () => {
    const response = await fetch('/api/persons');
    const allPatients = await response.json();
    
    // Update UI with all patients
    displayPatientList(allPatients);
}, 2000);
```

---

### 3. save(Person person)

#### Signature
```java
void save(Person person);
```

#### Parameters
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| person | Person | Yes | Patient object to persist |

#### Return Value
| Value | Type | Description |
|-------|------|-------------|
| None | void | No return value |

#### Purpose
Create or update a patient record

#### Behavior Details

**Insert (Create):**
```java
Person newPatient = new Person("P001", "John Doe", address, ...);
repo.save(newPatient);
// Result: Patient added to system
```

**Update (Overwrite):**
```java
Person existingPatient = repo.findById("P001").get();
Person updated = new Person("P001", "John Smith", ...);  // Different name
repo.save(updated);
// Result: P001 now has new name "John Smith"
```

#### Implementation in PersistentPersonRepository
```java
@Override
public void save(Person person) {
    store.put(person.id(), person);  // Add to HashMap
    saveToFile();  // Persist to persons.json
}
```

**Details:**
1. Add person to in-memory HashMap
2. Write entire HashMap to JSON file
3. File becomes source of truth

#### Implementation in InMemoryPersonRepository
```java
@Override
public void save(Person person) {
    store.put(person.id(), person);
    // No file write - volatile storage
}
```

#### Data Persistence Flow
```
save(person)
    ↓
store.put(id, person)  // Update HashMap
    ↓
saveToFile()  // Serialize to JSON
    ↓
persons.json
{
  "id": "P001",
  "name": "John Doe",
  ...
}
```

#### Transaction Semantics
**Current:** Not ACID compliant
- No rollback on failure
- No transaction isolation
- Single-threaded safety only

**Example Issue:**
```java
// If saveToFile() fails after store.put():
service.register(person1);  // Fails during file write
// person1 is in memory but NOT in file
// On restart, person1 is lost
```

#### Performance Characteristics
| Storage | Operation | Time |
|---------|-----------|------|
| **In-Memory** | save | <1ms |
| **File-Based** | save | 5-50ms |
| **Database** | save | 2-10ms (with indexing) |

#### Thread Safety
```java
// PersistentPersonRepository is thread-safe
CopyOnWriteArrayList helps ensure thread safety

// InMemoryPersonRepository is NOT thread-safe
// Multiple threads writing simultaneously could corrupt data
```

#### Error Handling
```java
try {
    repo.save(person);
} catch (IOException e) {
    // File I/O failed
    logger.error("Failed to persist patient", e);
}
```

---

### 4. delete(String id)

#### Signature
```java
void delete(String id);
```

#### Parameters
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| id | String | Yes | Patient ID to delete |

#### Return Value
| Value | Type | Description |
|-------|------|-------------|
| None | void | No return value |

#### Purpose
Remove a patient record from system

#### Behavior Details

**Delete Existing Patient:**
```java
// Before
persons.json: [P001: John, P002: Jane]

repo.delete("P001");

// After
persons.json: [P002: Jane]
```

**Delete Non-Existent Patient:**
```java
repo.delete("P999");  // P999 doesn't exist
// No error thrown - safe deletion

// Result: No change to storage
```

#### Implementation in PersistentPersonRepository
```java
@Override
public void delete(String id) {
    store.remove(id);  // Remove from HashMap
    saveToFile();  // Persist to persons.json
}
```

**Details:**
1. Remove person from HashMap (returns null if not found)
2. Write updated HashMap to JSON file
3. File reflects deletion immediately

#### Implementation in InMemoryPersonRepository
```java
@Override
public void delete(String id) {
    store.remove(id);
    // No file update - volatile
}
```

#### Data Persistence Flow
```
delete("P001")
    ↓
store.remove("P001")
    ↓
saveToFile()
    ↓
persons.json updated without P001
```

#### Safe Deletion Pattern
```java
// Safe to delete without checking existence first
repo.delete(id);  // No error if doesn't exist

// Compare to database:
// DELETE FROM persons WHERE id = 'P001';
// Returns: 1 row affected (safe even if 0 affected)
```

#### Performance Characteristics
| Operation | Time |
|-----------|------|
| **HashMap.remove()** | O(1) - ~1 microsecond |
| **saveToFile()** | O(n) - ~50ms for 100 patients |
| **Total delete** | ~50ms |

#### Cascading Deletes
**Current:** No cascading
```java
repo.delete("P001");
// Only deletes person record
// Address not separately stored (embedded)
```

**Future (Database):**
```sql
DELETE FROM persons WHERE id = 'P001'
CASCADE DELETE FROM addresses WHERE person_id = 'P001'
```

#### Soft Delete Alternative
```java
// Instead of actual deletion, mark as deleted
public class Person {
    private boolean deleted = false;
    
    // Later query only non-deleted patients
    if (!person.isDeleted()) { ... }
}
```

---

## Implementations

### Current Implementation: PersistentPersonRepository
**Storage:** File-based JSON (persons.json)  
**Durability:** Data survives application restart  
**Use Case:** Development, small deployments

### Alternative Implementation: InMemoryPersonRepository
**Storage:** HashMap in memory  
**Durability:** Lost on application restart  
**Use Case:** Unit testing, temporary sessions

### Future Implementation: DatabaseRepository
**Storage:** SQL Database  
**Technology:** Spring Data JPA, Hibernate  
**Durability:** Persistent, reliable  
**Use Case:** Production, multi-user

---

## Search Patterns

### By ID
```java
repo.findById("P001")
    .ifPresent(p -> System.out.println(p.getName()));
```

### By Name (Not in interface)
```java
repo.findAll()
    .stream()
    .filter(p -> p.getName().contains("John"))
    .collect(Collectors.toList());
```

### By Gender
```java
repo.findAll()
    .stream()
    .filter(p -> p.getGender().equals("Male"))
    .count();
```

### By Blood Group
```java
repo.findAll()
    .stream()
    .filter(p -> p.getBloodGroup().equals("O+"))
    .collect(Collectors.toList());
```

---

## Null Handling

### findById with null
```java
repo.findById(null);  // Should handle gracefully
// Returns Optional.empty() or throws exception
```

### save with null
```java
repo.save(null);  // Undefined behavior
// Could throw NullPointerException
// Or store null in HashMap (bad)
```

### delete with null
```java
repo.delete(null);  // Should handle gracefully
// HashMap.remove(null) is valid (removes null key)
```

---

## Transaction and Consistency

### Current Behavior (Eventual Consistency)
```
Request 1: save(P001) ───┐
Request 2: save(P002) ───┼─→ In-memory store updated
Request 3: list()        │   then file written
                         └─→ Potential inconsistency window
```

### Desired Behavior (ACID)
```
Atomicity:    All save/delete complete or none
Consistency:  System in valid state
Isolation:    Concurrent requests don't interfere
Durability:   Committed changes survive crashes
```

---

## Contract Summary

| Method | Input | Output | Side Effects |
|--------|-------|--------|--------------|
| findById | String id | Optional<Person> | None (read-only) |
| findAll | None | List<Person> | None (read-only) |
| save | Person | void | Persists to storage |
| delete | String id | void | Removes from storage |

---

## Related Classes
- [PersistentPersonRepository.md](PersistentPersonRepository.md) - File-based implementation
- [InMemoryPersonRepository.md](InMemoryPersonRepository.md) - In-memory implementation
- [PersonService.md](PersonService.md) - Uses this interface
- [Person.md](Person.md) - Data model being persisted
