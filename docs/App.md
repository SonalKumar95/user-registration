# App.java - Spring Boot Application Entry Point

## Overview
`App.java` is the main entry point for the Hospital Patient Registration System Spring Boot application. It initializes and starts the embedded Tomcat web server with all Spring configurations.

## File Location
```
src/main/java/com/example/App.java
```

## Class Declaration
```java
@SpringBootApplication
public class App {
    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }
}
```

## Annotations

### @SpringBootApplication
**Purpose:** Marks this class as a Spring Boot application entry point

**Functionality:**
- Enables component scanning for the `com.example` package and all sub-packages
- Enables auto-configuration of Spring components
- Enables Spring Boot's auto-configuration features
- Combines three annotations: `@Configuration`, `@ComponentScan`, and `@EnableAutoConfiguration`

**Auto-Configurations Applied:**
- Spring Web MVC setup with embedded Tomcat
- Jackson JSON processing
- Logging framework initialization
- Spring Data/Repository patterns
- Property management from `application.properties`

## Main Method

### Signature
```java
public static void main(String[] args)
```

### Execution Flow
1. **Startup:** JVM calls this method when application starts
2. **Spring Initialization:** `SpringApplication.run()` initializes Spring framework
3. **Component Scanning:** Scans for `@Component`, `@Controller`, `@Service`, `@Repository` classes
4. **Configuration Loading:** Loads settings from `application.properties`
5. **Bean Creation:** Instantiates beans and wires dependencies
6. **Server Start:** Starts embedded Tomcat server on configured port (8080)
7. **Ready State:** Application is ready to accept HTTP requests

### Parameters
- `args` (String[]): Command-line arguments passed to the application
  - Example: `java -jar app.jar --server.port=9090`
  - Allows runtime configuration override

## Component Scanning
The application automatically discovers and manages these components:

### Controllers
- `PersonController` - REST API endpoints for patient management

### Services
- `PersonService` - Business logic layer

### Models
- `Person` - Patient data entity
- `Address` - Embedded address information

### Repositories
- `PersistentPersonRepository` - File-based data persistence
- `InMemoryPersonRepository` - In-memory alternative (optional)

## Configuration Properties
Loaded from: `src/main/resources/application.properties`

```properties
server.port=8080                      # Tomcat port
logging.level.root=INFO               # Root log level
logging.level.com.example=DEBUG       # Package-specific log level
```

## Dependency Injection
Spring automatically injects dependencies using constructor injection:
- `PersonController` receives `PersonService`
- `PersonService` receives `PersonRepository`

## HTTP Server Configuration

### Embedded Tomcat
- **Server:** Apache Tomcat 10.1.17
- **Port:** 8080 (configurable)
- **Context Path:** "/" (root)
- **Max Threads:** 200 (default)
- **Connection Timeout:** 60000ms (default)

### Servlet Mapping
All REST endpoints are served under `/api/persons` base path

### CORS Configuration
- **Origins:** All (`*`)
- **Methods:** GET, POST, DELETE, OPTIONS
- **Headers:** All allowed

## Java Version Requirements
- **Minimum:** Java 17
- **Recommended:** Java 21 or latest LTS
- **Compilation Target:** Java 17

## Build and Execution

### Build
```bash
mvn clean package -DskipTests
```

### Run
```bash
java -jar target/minimal-java-mvn-1.0.0.jar
```

### Expected Startup Logs
```
Started App in 1.296 seconds (process running for 1.585)
Tomcat started on port 8080 (http) with context path ''
```

## Environment Variables
Can be overridden via environment or command-line:

```bash
# Override logging level
java -Dlogging.level.com.example=TRACE -jar app.jar

# Override port
java -Dserver.port=9090 -jar app.jar
```

## Class Responsibilities
1. **Entry Point:** Serves as application bootstrap
2. **Component Registry:** Enables Spring's component scanning
3. **Configuration Provider:** Loads application properties
4. **Server Lifecycle:** Manages embedded web server startup/shutdown

## Lifecycle Events
1. **PreStartup:** Property loading, component scanning
2. **Startup:** Spring context initialization
3. **ServerStart:** Tomcat initialization and binding to port
4. **Ready:** Application accepts HTTP requests
5. **Shutdown:** Clean resource cleanup on `Ctrl+C` or system signal

## Performance Characteristics
- **Startup Time:** ~1.3 seconds (on standard hardware)
- **Memory Usage:** ~100-200 MB (in-memory storage mode)
- **Request Handling:** Single-threaded blocking per request (synchronous)

## Integration Points
This class integrates with:
- Spring Boot Framework (3.2.1)
- Embedded Tomcat (10.1.17)
- Jackson ObjectMapper (2.15.3)
- SLF4J Logging Framework

## Related Files
- [PersonController.md](PersonController.md) - REST API endpoints
- [PersonService.md](PersonService.md) - Business logic
- [Person.md](Person.md) - Data model

## Notes
- No explicit bean configuration needed (Spring Boot auto-configures everything)
- The application uses functional constructor injection (no XML configuration)
- File-based persistence means data survives application restarts
- In production, consider migrating to database storage (MySQL, PostgreSQL)
