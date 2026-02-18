# InMemoryPersonRepository.java - In-Memory Data Storage

## Overview
`InMemoryPersonRepository.java` is an alternative implementation of the PersonRepository interface that stores patient data exclusively in memory using a HashMap. Data is volatile and lost on application restart, making it suitable for testing and temporary sessions.

## File Location
```
src/main/java/com/example/repo/InMemoryPersonRepository.java
```

## Class Declaration
```java
public class InMemoryPersonRepository implements PersonRepository {
    private final Map<String, Person> store = new HashMap<>();
}
```

## Design Pattern

### Strategy Pattern
- PersonRepository interface defines contract
- PersistentPersonRepository: File-based implementation
- InMemoryPersonRepository: Memory-based implementation
- Service uses same interface for both

### Trait: Non-Persistent
- Data exists only during runtime
- Lost on JVM shutdown
- No file I/O overhead

## Field

### store (HashMap)
```java
private final Map<String, Person> store = new HashMap<>();
```

**Type:** HashMap<String, Person>  
**Key:** Patient ID (String)  
**Value:** Person object  
**Purpose:** In-memory cache  
**Initialization:** Empty HashMap  
**Access:** O(1) average case  

## Methods

### 1. findById(String id)

#### Signature
```java
@Override 
public Optional<Person> findById(String id) { 
    return Optional.ofNullable(store.get(id)); 
}
```

#### Inline Implementation
- Single line method
- Delegates directly to HashMap
- Returns Optional wrapper

#### Example Usage
```java
InMemoryPersonRepository repo = new InMemoryPersonRepository();
repo.save(new Person("P001", "John", address, ...));

Optional<Person> patient = repo.findById("P001");
if (patient.isPresent()) {
    System.out.println(patient.get().getName());  // "John"
}
```

#### Performance
- **Time Complexity:** O(1)
- **Actual Speed:** <1 microsecond
- **Memory:** No additional allocation

---

### 2. findAll()

#### Signature
```java
@Override 
public List<Person> findAll() { 
    return new ArrayList<>(store.values()); 
}
```

#### Implementation
- Extracts values from HashMap
- Creates new ArrayList (defensive copy)
- Returns immutable reference

#### Example Usage
```java
InMemoryPersonRepository repo = new InMemoryPersonRepository();
repo.save(person1);
repo.save(person2);

List<Person> allPatients = repo.findAll();
System.out.println("Total: " + allPatients.size());  // 2
```

#### Data Loss Example
```java
List<Person> list1 = repo.findAll();
List<Person> list2 = repo.findAll();

// Different list objects (defensive copy)
list1.clear();  // Doesn't affect list2
list1.equals(list2);  // false (different objects)
```

#### Performance
- **Time Complexity:** O(n)
- **100 patients:** ~1ms
- **1000 patients:** ~10ms

---

### 3. save(Person person)

#### Signature
```java
@Override 
public void save(Person person) { 
    store.put(person.id(), person); 
}
```

#### Implementation
- Single line method
- Adds or overwrites in HashMap
- No file I/O

#### Example Usage
```java
InMemoryPersonRepository repo = new InMemoryPersonRepository();

Person patient = new Person("P001", "John Doe", address, "Male", "O+", "john@email.com", "9876543210");
repo.save(patient);  // Stored in memory only

// Data available immediately
Optional<Person> found = repo.findById("P001");
System.out.println(found.isPresent());  // true
```

#### Multiple Saves
```java
repo.save(person1);  // store: {P001: person1}
repo.save(person2);  // store: {P001: person1, P002: person2}
repo.save(person3);  // store: {P001: person1, P002: person2, P003: person3}
```

#### Overwrite Behavior
```java
Person oldP001 = new Person("P001", "John", address, ...);
repo.save(oldP001);

Person newP001 = new Person("P001", "Jane", address, ...);
repo.save(newP001);  // Overwrites oldP001

repo.findById("P001").get().getName();  // "Jane"
```

#### No Persistence
```
save(person)
    ↓
store.put(id, person)  // In-memory only
    ↓
Method returns immediately
    ↓
No file operations
    ↓
Application shutdown
    ↓
Data lost!
```

#### Performance
- **Time Complexity:** O(1)
- **Actual Speed:** ~1 microsecond
- **No I/O:** Extremely fast

---

### 4. delete(String id)

#### Signature
```java
@Override 
public void delete(String id) { 
    store.remove(id); 
}
```

#### Implementation
- Single line method
- Removes from HashMap
- Safe even if key not found

#### Example Usage
```java
InMemoryPersonRepository repo = new InMemoryPersonRepository();
repo.save(patient1);
repo.save(patient2);

repo.delete("P001");
List<Person> remaining = repo.findAll();
System.out.println(remaining.size());  // 1 (only P002)
```

#### Safe Deletion
```java
repo.delete("P999");  // P999 doesn't exist
// No error thrown
// store.remove("P999") returns null safely
```

#### No Persistence
```
delete(id)
    ↓
store.remove(id)  // In-memory only
    ↓
Method returns
    ↓
No file operations
    ↓
Data removed from memory
```

#### Performance
- **Time Complexity:** O(1)
- **Actual Speed:** ~1 microsecond
- **No I/O:** Extremely fast

---

## Initialization and Lifecycle

### On Creation
```java
InMemoryPersonRepository repo = new InMemoryPersonRepository();
// store = new HashMap<>()
// store is now empty: {}
```

### During Runtime
```
Application Start
    ↓
new InMemoryPersonRepository()
    ↓
store = {}
    ↓
save(P001) → store = {P001: person1}
save(P002) → store = {P001: person1, P002: person2}
save(P003) → store = {P001: person1, P002: person2, P003: person3}
    ↓
list() → [person1, person2, person3]
delete(P001) → store = {P002: person2, P003: person3}
    ↓
Application Shutdown → MEMORY FREED, DATA LOST
```

### Data Loss
```java
// First run
repo.save(patient1);
// Application stops

// Second run - NEW PROCESS
InMemoryPersonRepository repo2 = new InMemoryPersonRepository();
repo2.findAll();  // Returns: [] (empty)
// patient1 is gone forever!
```

---

## Use Cases

### Use Case 1: Unit Testing
```java
@Test
public void testPersonRegistration() {
    PersonRepository repo = new InMemoryPersonRepository();
    PersonService service = new PersonService(repo);
    
    Person patient = new Person("P001", "John", address, ...);
    service.register(patient);
    
    assertEquals(1, service.list().size());
}
```

**Why:**
- Each test gets fresh empty repository
- No file pollution
- Tests run fast
- No test data cleanup needed

### Use Case 2: Integration Testing
```java
@SpringBootTest
public class PersonControllerTest {
    @Bean
    PersonRepository inMemoryRepo() {
        return new InMemoryPersonRepository();  // Use in-memory for tests
    }
}
```

### Use Case 3: Temporary Caching
```java
// Load from persistent storage
List<Person> all = persistentRepo.findAll();

// Cache in memory
InMemoryPersonRepository cache = new InMemoryPersonRepository();
for (Person p : all) {
    cache.save(p);
}
```

### Use Case 4: Stateless Services
```java
// Each request gets fresh repository
@GetMapping
public List<Person> list() {
    InMemoryPersonRepository temp = new InMemoryPersonRepository();
    // Fresh data only for this request
    return temp.findAll();
}
```

---

## Comparison: In-Memory vs Persistent

| Aspect | In-Memory | Persistent |
|--------|-----------|------------|
| **Storage** | HashMap | persons.json file |
| **Durability** | Lost on restart | Survives restart |
| **Speed** | ~1ms operations | ~50ms with file I/O |
| **Use Case** | Testing | Production |
| **Data Safety** | Low | High |
| **Scalability** | <10k items | >100k items (with DB) |
| **Thread-Safe** | No (HashMap) | Partially |
| **Concurrent Access** | No | Yes |

---

## Thread Safety Issues

### HashMap NOT Thread-Safe
```java
// Concurrent access will corrupt data
Thread 1: save(P001)
Thread 2: save(P002)
Thread 3: delete(P001)

// Result: Undefined behavior
```

### Race Condition Example
```java
// Thread 1
store.put(id, person);

// Thread 2 (simultaneously)
store.remove(id);

// Result: Corrupted internal structure
```

### Solution (Not Implemented)
```java
private final Map<String, Person> store = 
    Collections.synchronizedMap(new HashMap<>());
```

### For Current Code
- **Single-threaded testing:** Safe
- **Multi-threaded environment:** NOT safe
- **Production use:** NOT recommended

---

## Memory Characteristics

### Memory Usage
| Item | Bytes |
|------|-------|
| HashMap object | ~48 |
| Per entry | ~48 (overhead) |
| Person object | ~400 |
| Address object | ~100 |
| **Per patient total** | ~548 |

### Example Calculations
```
100 patients:  100 × 548 = 54.8 KB
1000 patients: 1000 × 548 = 548 KB
10000 patients: 10000 × 548 = 5.48 MB
```

### Memory Leaks
```java
// If PersonService not garbage collected
PersonService service = new PersonService(inMemoryRepo);

// And service holds reference to 10,000 patients
// All patient objects kept in memory indefinitely
```

---

## Testing Examples

### Test 1: Basic CRUD
```java
@Test
public void testBasicCrud() {
    PersonRepository repo = new InMemoryPersonRepository();
    
    // Create
    Person patient = new Person("P001", "John", address, "Male", "O+", "john@email.com", "9876543210");
    repo.save(patient);
    
    // Read
    assertEquals("P001", repo.findById("P001").get().getId());
    
    // List
    assertEquals(1, repo.findAll().size());
    
    // Delete
    repo.delete("P001");
    assertEquals(0, repo.findAll().size());
}
```

### Test 2: Multiple Patients
```java
@Test
public void testMultiplePatients() {
    PersonRepository repo = new InMemoryPersonRepository();
    
    repo.save(new Person("P001", "John", addr1, "Male", "O+", "j@email.com", "1111111111"));
    repo.save(new Person("P002", "Jane", addr2, "Female", "A+", "j@email.com", "2222222222"));
    repo.save(new Person("P003", "Bob", addr3, "Male", "B+", "b@email.com", "3333333333"));
    
    assertEquals(3, repo.findAll().size());
}
```

### Test 3: Non-Existent Patient
```java
@Test
public void testNonExistentPatient() {
    PersonRepository repo = new InMemoryPersonRepository();
    
    Optional<Person> patient = repo.findById("P999");
    assertFalse(patient.isPresent());
    
    repo.delete("P999");  // No error
}
```

### Test 4: Overwrite
```java
@Test
public void testOverwrite() {
    PersonRepository repo = new InMemoryPersonRepository();
    
    Person p1 = new Person("P001", "John", addr1, "Male", "O+", "j@email.com", "1111111111");
    repo.save(p1);
    
    Person p2 = new Person("P001", "Jane", addr2, "Female", "A+", "j@email.com", "2222222222");
    repo.save(p2);  // Overwrites p1
    
    String name = repo.findById("P001").get().getName();
    assertEquals("Jane", name);  // Not "John"
}
```

---

## Integration with PersonService

### Example: Testing with Mock Repository
```java
// Production
PersonService service = new PersonService(new PersistentPersonRepository());

// Testing
PersonService serviceTest = new PersonService(new InMemoryPersonRepository());
```

### Usage in Tests
```java
public class PersonServiceTest {
    private PersonService service;
    private InMemoryPersonRepository repo;
    
    @Before
    public void setup() {
        repo = new InMemoryPersonRepository();
        service = new PersonService(repo);
    }
    
    @Test
    public void testRegister() {
        Person patient = new Person(...);
        service.register(patient);
        
        assertEquals(1, service.list().size());
    }
}
```

---

## Limitations

### 1. No Persistence
```java
repo.save(patient);
// JVM crashes
// Patient data lost forever
```

### 2. Not Thread-Safe
```java
// Concurrent access corrupts data
ExecutorService executor = Executors.newFixedThreadPool(10);
for (int i = 0; i < 1000; i++) {
    executor.submit(() -> repo.save(patient));
}
// Potential data corruption
```

### 3. No Query Filtering
```java
// Must load all patients to filter
List<Person> allPatients = repo.findAll();
allPatients.stream()
    .filter(p -> p.getGender().equals("Male"))
    .collect(Collectors.toList());
// Inefficient for large datasets
```

### 4. Limited Scalability
```java
// If 100,000 patients in memory
List<Person> all = repo.findAll();  // Returns 100k objects
// Memory intensive
// List operations slow
```

### 5. No Indexes
```java
// O(n) to find by name
repo.findAll().stream()
    .filter(p -> p.getName().equals("John"))
    .findFirst();
// Must scan all patients
```

---

## Advantages Over Persistent Storage

### 1. Speed
```
In-Memory: ~1 microsecond
File-based: ~50 milliseconds
Database: ~5 milliseconds

In-memory is 50,000x faster!
```

### 2. Simplicity
```java
// No file paths, no I/O exceptions
store.put(id, person);  // Done

// Compare to persistent:
objectMapper.writeValue(file, list);  // Throws IOException
```

### 3. Testing
```java
// Fresh repository for each test
repo = new InMemoryPersonRepository();

// No test data cleanup needed
// No file pollution
// Tests run in parallel safely
```

### 4. Development
```java
// Can modify data without persistence
repo.save(person);
// Disappears on restart - good for development
```

---

## Realistic Deployment Scenarios

### Scenario 1: Development
```
Start application
    ↓
Create InMemoryPersonRepository
    ↓
Test manually in browser
    ↓
Stop application
    ↓
Data lost (OK for development)
```

### Scenario 2: Unit Testing
```
@Test
    ↓
Create InMemoryPersonRepository
    ↓
Test service logic
    ↓
Assert results
    ↓
Cleanup (automatic)
    ↓
@Test passes/fails
```

### Scenario 3: Temporary Cache
```
Load from DB
    ↓
Cache in InMemoryPersonRepository
    ↓
Serve requests from cache
    ↓
Application restarts
    ↓
Reload cache from DB
```

---

## Related Classes
- [PersonRepository.md](PersonRepository.md) - Interface definition
- [PersistentPersonRepository.md](PersistentPersonRepository.md) - File-based implementation
- [PersonService.md](PersonService.md) - Uses this repository
- [Person.md](Person.md) - Data model stored
