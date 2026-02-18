# PersistentPersonRepository.java - File-Based Data Persistence

## Overview
`PersistentPersonRepository.java` is a concrete implementation of the PersonRepository interface that persists patient data to a JSON file (persons.json). It provides durable storage that survives application restarts while maintaining thread-safe in-memory access patterns.

## File Location
```
src/main/java/com/example/repo/PersistentPersonRepository.java
```

## Class Declaration
```java
public class PersistentPersonRepository implements PersonRepository {
    private static final String FILE_PATH = "persons.json";
    private final Map<String, Person> store = new HashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();
}
```

## Class Structure

### Implementation Details
- **Implements:** PersonRepository interface
- **Instantiation:** Singleton-like usage (created once in PersonController)
- **Thread-Safety:** Safe for concurrent reads, sequential writes

### Fields

#### 1. FILE_PATH (Constant)
```java
private static final String FILE_PATH = "persons.json";
```
**Type:** Static final String  
**Value:** "persons.json"  
**Location:** Project root directory  
**Purpose:** Specifies persistent storage file name

#### 2. store (In-Memory Cache)
```java
private final Map<String, Person> store = new HashMap<>();
```
**Type:** HashMap<String, Person>  
**Key:** Patient ID (String)  
**Value:** Person object  
**Purpose:** In-memory cache for fast access  
**Initialization:** Empty HashMap, loaded from file on startup  
**Access Pattern:** O(1) lookup by ID

#### 3. objectMapper (JSON Processing)
```java
private final ObjectMapper objectMapper = new ObjectMapper();
```
**Type:** Jackson ObjectMapper  
**Purpose:** JSON serialization/deserialization  
**Initialization:** New instance in constructor  
**Thread-Safety:** Thread-safe (default configuration)

## Constructor

### PersistentPersonRepository()
```java
public PersistentPersonRepository() {
    loadFromFile();
}
```

#### Execution Flow

1. **Create HashMap**
   ```java
   private final Map<String, Person> store = new HashMap<>();
   ```
   Empty in-memory storage

2. **Create ObjectMapper**
   ```java
   private final ObjectMapper objectMapper = new ObjectMapper();
   ```
   JSON processing utility

3. **Load From File**
   ```java
   loadFromFile();
   ```
   Read persons.json and populate HashMap

#### Startup Example
```
Application Start
    ↓
new PersistentPersonRepository()
    ↓
Constructor called
    ↓
loadFromFile()
    ↓
persons.json exists?
    ├─ YES: Read and deserialize to HashMap
    └─ NO: Keep HashMap empty
    ↓
Ready to serve requests
```

## Methods

### 1. findById(String id)

#### Signature
```java
@Override
public Optional<Person> findById(String id) {
    return Optional.ofNullable(store.get(id));
}
```

#### Implementation Details

**Direct HashMap Access:**
```java
store.get(id)  // O(1) operation
```
- Returns Person if found
- Returns null if not found
- Optional wraps result (null-safe)

#### Example Usage
```java
Optional<Person> patient = repo.findById("P001");

if (patient.isPresent()) {
    System.out.println("Found: " + patient.get().getName());
} else {
    System.out.println("Patient not found");
}

// Or modern style
patient.ifPresent(p -> System.out.println("Found: " + p.getName()));
```

#### Performance
- **Time Complexity:** O(1) - Constant time
- **Actual Speed:** ~1 microsecond
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

#### Implementation Details

**Create New List:**
```java
new ArrayList<>(store.values())
```
- Extracts all Person objects from HashMap
- Creates new ArrayList (defensive copy)
- Returns unmodifiable reference

**Defensive Copy:**
```java
// Changes to returned list don't affect store
List<Person> patients = repo.findAll();
patients.clear();  // Doesn't affect store
patients.add(null);  // Doesn't affect store
```

#### Example Usage
```java
List<Person> allPatients = repo.findAll();

for (Person p : allPatients) {
    System.out.println(p.getName());
}

// Stream operations
allPatients.stream()
    .filter(p -> p.getGender().equals("Male"))
    .forEach(p -> System.out.println(p.getName()));

// Count
System.out.println("Total: " + allPatients.size());
```

#### JSON Serialization
```java
// List automatically serialized to JSON
[
  {
    "id": "P001",
    "name": "John Doe",
    "address": {...},
    ...
  },
  {
    "id": "P002",
    "name": "Jane Smith",
    ...
  }
]
```

#### Performance
- **Time Complexity:** O(n) - Linear to number of patients
- **Space Complexity:** O(n) - Creates new list
- **100 patients:** ~5ms
- **1000 patients:** ~50ms

---

### 3. save(Person person)

#### Signature
```java
@Override
public void save(Person person) {
    store.put(person.id(), person);
    saveToFile();
}
```

#### Implementation Details

**Step 1: Add to HashMap**
```java
store.put(person.id(), person);
```
- Adds new person or overwrites existing
- O(1) operation (~1 microsecond)
- Immediate in-memory availability

**Step 2: Persist to File**
```java
saveToFile();
```
- Serializes entire HashMap to persons.json
- Makes change durable
- File becomes source of truth

#### Save Flow
```
save(Person)
    ↓
store.put(id, person)  ← In-memory update (fast)
    ↓
saveToFile()           ← File I/O (slow)
    ↓
persons.json updated
```

#### Example Usage
```java
Person newPatient = new Person(
    "P001",
    "John Doe",
    new Address("123 Main St", "NYC"),
    "Male",
    "O+",
    "john@example.com",
    "9876543210"
);

repo.save(newPatient);  // Persisted to file
```

#### Persistence Details
```json
// persons.json after first save
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

#### Multiple Saves
```
Save P001 → persons.json: [P001]
Save P002 → persons.json: [P001, P002]
Save P003 → persons.json: [P001, P002, P003]
```

#### Overwrite Behavior
```java
Person oldP001 = new Person("P001", "John", ...);
repo.save(oldP001);  // persons.json: [P001: "John"]

Person newP001 = new Person("P001", "Jane", ...);
repo.save(newP001);  // persons.json: [P001: "Jane"]
// ID is same, so overwrites
```

#### Performance
- **HashMap put:** O(1) - ~1 microsecond
- **File write:** O(n) - ~50ms for 100 patients
- **Total:** ~50ms (dominated by file I/O)

---

### 4. delete(String id)

#### Signature
```java
@Override
public void delete(String id) {
    store.remove(id);
    saveToFile();
}
```

#### Implementation Details

**Step 1: Remove From HashMap**
```java
store.remove(id);
```
- Removes person from in-memory store
- O(1) operation (~1 microsecond)
- Safely handles non-existent keys (returns null, no error)

**Step 2: Persist to File**
```java
saveToFile();
```
- Serializes updated HashMap (without deleted person)
- Writes to persons.json
- File updated immediately

#### Delete Flow
```
delete("P001")
    ↓
store.remove("P001")  ← Remove from memory
    ↓
saveToFile()          ← Update file
    ↓
persons.json updated (P001 removed)
```

#### Example Usage
```java
repo.delete("P001");  // Safe even if P001 doesn't exist
```

#### Deletion Details
```json
// Before delete("P001")
[
  { "id": "P001", "name": "John Doe", ... },
  { "id": "P002", "name": "Jane Smith", ... }
]

// After delete("P001")
[
  { "id": "P002", "name": "Jane Smith", ... }
]
```

#### Safe Deletion
```java
repo.delete("P999");  // P999 doesn't exist
// No error thrown
// File unchanged

repo.delete("P001");  // P001 exists
// Person removed
// File updated
```

#### Performance
- **HashMap remove:** O(1) - ~1 microsecond
- **File write:** O(n) - ~50ms for 100 patients
- **Total:** ~50ms

---

## Private Helper Methods

### saveToFile()

#### Signature
```java
private void saveToFile() {
    try {
        objectMapper.writerWithDefaultPrettyPrinter()
            .writeValue(new File(FILE_PATH), new ArrayList<>(store.values()));
    } catch (IOException e) {
        System.err.println("Error saving persons to file: " + e.getMessage());
    }
}
```

#### Purpose
Serialize HashMap to formatted JSON file

#### Implementation Details

**1. Get ObjectMapper Writer**
```java
objectMapper.writerWithDefaultPrettyPrinter()
```
- Creates writer with indentation
- Output file is human-readable
- Slightly larger file size

**2. Serialize to File**
```java
.writeValue(new File(FILE_PATH), new ArrayList<>(store.values()))
```
- Converts ArrayList<Person> to JSON
- Writes to persons.json
- Overwrites existing file

**3. Error Handling**
```java
catch (IOException e) {
    System.err.println("Error saving persons to file: " + e.getMessage());
}
```
- Catches file I/O errors
- Prints to stderr
- Doesn't throw exception (silent fail)

#### Output Format
```json
[ {
  "id" : "P001",
  "name" : "John Doe",
  "address" : {
    "line1" : "123 Main Street",
    "city" : "Kolkata"
  },
  "gender" : "Male",
  "bloodGroup" : "O+",
  "email" : "john@example.com",
  "phone" : "9876543210"
}, {
  "id" : "P002",
  ...
} ]
```

#### File Operations
```
String → ObjectMapper → JSON → File
```

#### Performance
- **Single patient:** ~5ms
- **100 patients:** ~50ms
- **1000 patients:** ~500ms (slow!)

#### Issues with Current Approach
1. **Full rewrite:** Entire file written each save
2. **No atomicity:** Partial write if crash occurs
3. **No concurrency control:** Multiple writes could corrupt

---

### loadFromFile()

#### Signature
```java
private void loadFromFile() {
    try {
        File file = new File(FILE_PATH);
        if (file.exists()) {
            List<Person> loadedPersons = objectMapper.readValue(
                file, 
                new TypeReference<List<Person>>() {}
            );
            for (Person p : loadedPersons) {
                store.put(p.id(), p);
            }
            System.out.println("Loaded " + loadedPersons.size() + " persons from file");
        }
    } catch (IOException e) {
        System.err.println("Error loading persons from file: " + e.getMessage());
    }
}
```

#### Purpose
Load persisted data from JSON file on startup

#### Implementation Details

**1. Check File Exists**
```java
File file = new File(FILE_PATH);
if (file.exists()) {
    // Only load if file exists
}
```
- First startup: file doesn't exist (empty list)
- Subsequent startups: file exists (load data)

**2. Deserialize From JSON**
```java
List<Person> loadedPersons = objectMapper.readValue(
    file, 
    new TypeReference<List<Person>>() {}
);
```
- Jackson converts JSON to List<Person>
- Uses @JsonCreator annotation
- Handles nested Address objects

**3. Populate HashMap**
```java
for (Person p : loadedPersons) {
    store.put(p.id(), p);
}
```
- Store as key-value map for O(1) access
- ID is unique key
- Overwrites if duplicate ID (last one wins)

**4. Log Success**
```java
System.out.println("Loaded " + loadedPersons.size() + " persons from file");
```
- Confirms successful load
- Shows data recovery on startup

**5. Error Handling**
```java
catch (IOException e) {
    System.err.println("Error loading persons from file: " + e.getMessage());
}
```
- File corrupted: Caught and logged
- Application continues with empty store
- Data loss occurs (no recovery)

#### Startup Scenarios

**Scenario 1: Fresh Install**
```
Application start
    ↓
new PersistentPersonRepository()
    ↓
loadFromFile()
    ↓
persons.json exists? NO
    ↓
store remains empty
    ↓
Ready to accept first patient
```

**Scenario 2: After Data Registration**
```
Application start
    ↓
new PersistentPersonRepository()
    ↓
loadFromFile()
    ↓
persons.json exists? YES
    ↓
Read: [ {P001}, {P002}, {P003} ]
    ↓
Populate store: {P001→person1, P002→person2, P003→person3}
    ↓
System: "Loaded 3 persons from file"
    ↓
All patients available in memory
```

**Scenario 3: Corrupted File**
```
Application start
    ↓
new PersistentPersonRepository()
    ↓
loadFromFile()
    ↓
persons.json exists? YES
    ↓
Try to deserialize
    ↓
JSON parsing error
    ↓
catch (IOException e)
    ↓
System: "Error loading persons from file: JSONParseException"
    ↓
store remains empty
    ↓
All data lost! ⚠️
```

#### Performance
- **No patients:** <1ms
- **100 patients:** ~10ms
- **1000 patients:** ~100ms

#### Data Recovery
```
If persons.json corrupted:
1. Delete persons.json
2. Restart application
3. Reenter all patient data

(No backup/recovery mechanism currently)
```

---

## File Structure (persons.json)

### Example File
```json
[ {
  "id" : "P001",
  "name" : "John Doe",
  "address" : {
    "line1" : "123 Main Street",
    "city" : "Kolkata"
  },
  "gender" : "Male",
  "bloodGroup" : "O+",
  "email" : "john@example.com",
  "phone" : "9876543210"
}, {
  "id" : "P002",
  "name" : "Jane Smith",
  "address" : {
    "line1" : "456 Oak Avenue",
    "city" : "Mumbai"
  },
  "gender" : "Female",
  "bloodGroup" : "A+",
  "email" : "jane@example.com",
  "phone" : "9876543211"
} ]
```

### File Location
- **Path:** Project root directory
- **Filename:** persons.json
- **Encoding:** UTF-8
- **Format:** JSON with 2-space indentation

### File Size
| Patients | Size |
|----------|------|
| 1 | ~300 bytes |
| 10 | ~3 KB |
| 100 | ~30 KB |
| 1000 | ~300 KB |
| 10000 | ~3 MB |

---

## Thread Safety

### Current Implementation
```java
private final Map<String, Person> store = new HashMap<>();
```

**HashMap is NOT thread-safe:**
- Concurrent reads: Safe
- Concurrent writes: Can corrupt
- Read + write simultaneously: Can corrupt

### Scenario: Race Condition
```
Thread 1: save(P001)
Thread 2: delete(P001)
Thread 3: list()

Results unpredictable!
```

### Solution: Synchronization (Not Implemented)
```java
private final Map<String, Person> store = 
    Collections.synchronizedMap(new HashMap<>());
```

### Better Solution: CopyOnWriteArrayList
```java
private final List<Person> store = new CopyOnWriteArrayList<>();
```
- Thread-safe for concurrent reads
- Creates copy on each write
- Good for read-heavy workloads

---

## Limitations and Issues

### 1. No Transactions
```java
repo.save(P001);  // If fails halfway
// P001 in memory but not in file
```

### 2. Full File Rewrite
- Every save rewrites entire file
- Slow for large datasets
- Increases SSD wear

### 3. No Concurrency Control
- Multiple application instances corrupt data
- No file locking mechanism

### 4. No Backup/Recovery
- Corrupted file = complete data loss
- No version control

### 5. Scalability Limit
- ~10,000 patients max practical limit
- Performance degrades significantly

---

## Future Improvements

### Proposal 1: Database Migration
```java
@Repository
public class PersonJpaRepository extends JpaRepository<Person, String> {
    // Use Spring Data JPA
}
```

### Proposal 2: Add Transaction Support
```java
@Transactional
public void save(Person person) {
    repo.save(person);
    // Atomic operation
}
```

### Proposal 3: Backup System
```java
private void saveToFileWithBackup() {
    // Copy old file to backup
    Files.copy(new File(FILE_PATH), 
               new File(FILE_PATH + ".bak"));
    saveToFile();  // Write new version
}
```

### Proposal 4: Indexed File Format
- Use SQLite instead of JSON
- Better performance for queries
- Built-in transaction support

---

## Related Classes
- [PersonRepository.md](PersonRepository.md) - Interface definition
- [InMemoryPersonRepository.md](InMemoryPersonRepository.md) - Alternative implementation
- [PersonService.md](PersonService.md) - Uses this repository
- [Person.md](Person.md) - Data model being persisted
