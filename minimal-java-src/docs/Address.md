# Address.java - Patient Address Entity

## Overview
`Address.java` is an immutable value object representing a patient's geographic location. It uses Jackson annotations for JSON serialization and is embedded within the Person model. The class provides backward compatibility with dual property accessors.

## File Location
```
src/main/java/com/example/model/Address.java
```

## Class Declaration
```java
public final class Address {
    private final String line1;    // Street address
    private final String city;     // City name
}
```

## Design Characteristics

### Final Class
- **Purpose:** Prevents subclassing
- **Benefit:** Guarantees immutability cannot be bypassed via inheritance
- **Pattern:** Value Object pattern

### Immutable Design
- All fields are `final`
- No setters provided
- Once created, cannot be modified
- Thread-safe without synchronization

## Fields

### 1. line1 (String)
**Type:** Final String  
**Purpose:** Street address / first address line  
**Constraints:** Cannot be null or empty  
**Examples:**
- "123 Main Street"
- "456 Oak Avenue, Apt 5B"
- "Hospital Wing A, Block 3"  
**JSON Property Names:** "line1" or "street" (see dual accessors)

### 2. city (String)
**Type:** Final String  
**Purpose:** City/municipal name  
**Constraints:** Cannot be null or empty  
**Examples:**
- "New York"
- "Kolkata"
- "Mumbai"  
**JSON Property Name:** "city"

## Constructor

### @JsonCreator Constructor
```java
@JsonCreator
public Address(
    @JsonProperty("line1") String line1,
    @JsonProperty("city") String city
)
```

**Purpose:** Deserialize JSON to Address object  

**Annotations:**
- `@JsonCreator` - Designates this constructor for Jackson JSON deserialization
- `@JsonProperty` - Maps JSON field names to constructor parameters

**Initialization:**
```java
this.line1 = line1;  // Store street address
this.city = city;    // Store city name
```

## Accessor Methods

### Primary Accessor (@JsonProperty)
```java
@JsonProperty("line1")
public String getLine1() { return line1; }

@JsonProperty("city")
public String getCity() { return city; }
```

**Purpose:** Serialize to JSON with specific property names

**JSON Output:**
```json
{
  "line1": "123 Main Street",
  "city": "Kolkata"
}
```

### Alternative Accessor (Backward Compatibility)
```java
@JsonProperty("street")
public String getStreet() { return line1; }
```

**Purpose:** Support legacy APIs that use "street" instead of "line1"  
**Benefit:** Provides flexibility for API versioning

**Dual JSON Mapping:**
```java
// Both serialize to valid JSON
@JsonProperty("line1")  public String getLine1() { return line1; }
@JsonProperty("street") public String getStreet() { return line1; }
```

### Records-Style Accessors
```java
public String line1() { return line1; }  // Functional style
public String city() { return city; }    // Shorter syntax
```

**Usage:**
```java
Address addr = new Address("123 Main St", "New York");
String street = addr.line1();    // Modern style
String cityName = addr.city();   // Modern style
String street2 = addr.getLine1(); // Traditional style
```

## Override Methods

### toString()
**Purpose:** Human-readable representation

**Format:**
```
{line1}, {city}
```

**Example:**
```
"123 Main Street, New York"
"Hospital Wing A, Mumbai"
```

**Output:**
```java
Address addr = new Address("123 Main St", "New York");
System.out.println(addr); // "123 Main St, New York"
```

### equals(Object)
**Purpose:** Content-based equality comparison

**Comparison Logic:**
```java
return Objects.equals(line1, a.line1) && Objects.equals(city, a.city);
```

**Characteristics:**
- Returns true only if both street and city are identical
- Case-sensitive comparison
- Handles null values gracefully
- Returns false for non-Address objects

**Example:**
```java
Address addr1 = new Address("123 Main St", "NYC");
Address addr2 = new Address("123 Main St", "NYC");
Address addr3 = new Address("456 Oak Ave", "NYC");

addr1.equals(addr2) // true - same values
addr1.equals(addr3) // false - different street
addr1.equals("123") // false - different type
```

### hashCode()
**Purpose:** Generate consistent hash for use in collections

**Algorithm:**
```java
return Objects.hash(line1, city);
```

**Properties:**
- Immutable (same object always produces same hash)
- Used for HashMap/HashSet lookups
- Contract: equal objects must have equal hash codes

**Example:**
```java
Map<Address, String> addressMap = new HashMap<>();
Address addr = new Address("123 Main St", "NYC");
addressMap.put(addr, "Hospital");
// hashCode() used for key lookup
```

## JSON Serialization

### Deserialization Examples

**Example 1: Standard JSON**
```json
{
  "line1": "123 Main Street",
  "city": "Kolkata"
}
```
```java
Address addr = mapper.readValue(json, Address.class);
// addr.getLine1() = "123 Main Street"
// addr.getCity() = "Kolkata"
```

**Example 2: Legacy "street" field**
```json
{
  "street": "456 Oak Avenue",
  "city": "Mumbai"
}
```
```java
// Jackson finds @JsonProperty("street") on getStreet()
// But constructor expects @JsonProperty("line1")
// Requires special handling for backward compatibility
```

### Serialization Example
```java
Address addr = new Address("123 Main St", "New York");
ObjectMapper mapper = new ObjectMapper();
String json = mapper.writeValueAsString(addr);

// Output:
{
  "line1": "123 Main Street",
  "street": "123 Main Street",  // Both mapped
  "city": "New York"
}
```

## Embedded Address in Person

### JSON Structure
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

### Java Structure
```java
Address address = new Address("123 Main Street", "Kolkata");
Person person = new Person("P001", "John Doe", address, "Male", "O+", "john@example.com", "9876543210");
```

## Usage in Application

### Controller (PersonController)
```java
Person person = new Person(
    request.getId(),
    request.getName(),
    new Address(request.getStreet(), request.getCity()),  // Creates Address
    request.getGender(),
    request.getBloodGroup(),
    request.getEmail(),
    request.getPhone()
);
```

### Repository (PersistentPersonRepository)
```java
// Persists to JSON with nested address
persons.json:
[
  {
    "address": {
      "line1": "123 Main Street",
      "city": "Kolkata"
    },
    ...
  }
]
```

### Frontend Form
```html
<input type="text" id="street" placeholder="Street Address" required />
<input type="text" id="city" placeholder="City" required />
```

## Validation Rules

### Frontend Validation
```javascript
if (!street || street.trim() === '') {
    alert('Street address is required');
}
if (!city || city.trim() === '') {
    alert('City is required');
}
```

### Backend Validation
```java
if (request.getStreet() == null || request.getStreet().isEmpty()) {
    return ResponseEntity.badRequest().body("Street cannot be empty");
}
if (request.getCity() == null || request.getCity().isEmpty()) {
    return ResponseEntity.badRequest().body("City cannot be empty");
}
```

## Memory Characteristics
- Typical Address object: ~100-150 bytes
- String overhead: ~40 bytes per String
- Minimal memory footprint suitable for large datasets

## Comparison with Database Address Model

| Aspect | Current (File-based) | Database |
|--------|----------------------|----------|
| **Storage** | JSON nested object | Separate table with FK |
| **Normalization** | Denormalized | Normalized (3NF) |
| **Duplicate Cities** | Repeated in every patient | Single record, referenced |
| **Query Cities** | Array filter | SQL JOIN query |
| **Performance** | Good for <10k patients | Better for >100k patients |

## Future Enhancements

### Proposal 1: Geocoding
```java
public class Address {
    private final Double latitude;
    private final Double longitude;
    // For hospital location mapping
}
```

### Proposal 2: Postal Code
```java
public class Address {
    private final String postalCode;
    // For logistics and mail sorting
}
```

### Proposal 3: State/Province
```java
public class Address {
    private final String state;
    // For multi-country support
}
```

### Proposal 4: Database Normalization
```java
// Move to JPA entity with @Entity, @Table
@Entity
@Table(name = "addresses")
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "line1")
    private String line1;
    
    @Column(name = "city")
    private String city;
}
```

## Related Classes
- [Person.md](Person.md) - Contains Address as embedded object
- [PersonController.md](PersonController.md) - Receives address from frontend

## Testing Examples

### Unit Test
```java
@Test
public void testAddressEquality() {
    Address addr1 = new Address("123 Main St", "NYC");
    Address addr2 = new Address("123 Main St", "NYC");
    Address addr3 = new Address("456 Oak Ave", "NYC");
    
    assertEquals(addr1, addr2);        // Pass
    assertNotEquals(addr1, addr3);     // Pass
    assertEquals(addr1.hashCode(), addr2.hashCode()); // Pass
}

@Test
public void testAddressToString() {
    Address addr = new Address("123 Main St", "NYC");
    assertEquals("123 Main St, NYC", addr.toString());
}
```

### JSON Test
```java
@Test
public void testAddressSerialization() throws Exception {
    Address addr = new Address("123 Main St", "NYC");
    ObjectMapper mapper = new ObjectMapper();
    String json = mapper.writeValueAsString(addr);
    
    Address deserialized = mapper.readValue(json, Address.class);
    assertEquals(addr, deserialized);
}
```

## Notes
- Always validate address fields are non-empty before creating
- Address is embedded in Person (one-to-one relationship)
- Consider database migration for large-scale deployments
- Dual property accessors support API versioning and backward compatibility
