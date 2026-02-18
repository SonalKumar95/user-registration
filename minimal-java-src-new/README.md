# Hospital Patient Registration System

A modern web application for hospital patient registration and management with persistent data storage. Built with Spring Boot backend and responsive HTML/CSS/JavaScript frontend.

## 📋 Table of Contents

- [Repository Structure](#repository-structure)
- [System Requirements](#system-requirements)
- [Prerequisites](#prerequisites)
- [Installation & Setup](#installation--setup)
- [Running the Application](#running-the-application)
- [Features](#features)
- [API Endpoints](#api-endpoints)
- [Project Architecture](#project-architecture)
- [Data Persistence](#data-persistence)

---

## 📁 Repository Structure

```
minimal-java-src/
├── pom.xml                          # Maven configuration file
├── README.md                         # This file
├── persons.json                      # Persisted patient data (auto-generated)
│
├── src/
│   ├── main/
│   │   ├── java/com/example/
│   │   │   ├── App.java             # Spring Boot entry point
│   │   │   ├── controller/
│   │   │   │   └── PersonController.java    # REST API endpoints
│   │   │   ├── model/
│   │   │   │   ├── Person.java      # Patient model with @JsonCreator
│   │   │   │   └── Address.java     # Address model
│   │   │   ├── repo/
│   │   │   │   ├── PersonRepository.java             # Repository interface
│   │   │   │   ├── InMemoryPersonRepository.java     # In-memory implementation
│   │   │   │   └── PersistentPersonRepository.java   # File-based implementation
│   │   │   └── service/
│   │   │       └── PersonService.java     # Business logic service
│   │   │
│   │   └── resources/
│   │       ├── application.properties     # Spring Boot configuration
│   │       └── static/
│   │           └── index.html            # Frontend UI (two-column layout)
│   │
│   └── test/
│       └── java/com/example/
│           └── PersonServiceTest.java    # Unit tests
│
├── target/                          # Maven build output
│   ├── classes/                     # Compiled Java classes
│   ├── minimal-java-mvn-1.0.0.jar  # Executable JAR file
│   └── ...
│
└── .gitignore

```

---

## 💻 System Requirements

### Minimum Requirements
- **Operating System:** Windows, macOS, or Linux
- **RAM:** 2GB minimum, 4GB recommended
- **Disk Space:** 500MB for dependencies and application
- **Network:** Internet connection for Maven dependency download

### Software Requirements
- **Java Development Kit (JDK):** Version 17 or higher
  - Download from: [oracle.com/java](https://www.oracle.com/java/technologies/downloads/)
- **Maven:** Version 3.6.0 or higher
  - Download from: [maven.apache.org](https://maven.apache.org/download.cgi)
- **Web Browser:** Modern browser (Chrome, Firefox, Safari, Edge)

### Verify Installation
```bash
java -version
mvn -version
```

---

## ✅ Prerequisites

Before running the application, ensure you have:

1. ✓ JDK 17+ installed and added to PATH
2. ✓ Maven 3.6.0+ installed and added to PATH
3. ✓ Port 8080 available on your system
4. ✓ Git (optional, for cloning the repository)

---

## 🚀 Installation & Setup

### Option 1: Using Git Clone

```bash
# Clone the repository
git clone <repository-url>
cd user-registration/minimal-java-src

# Verify Maven is working
mvn --version
```

### Option 2: Direct Folder Access

```bash
# Navigate to the project directory
cd /path/to/minimal-java-src

# Verify all required files exist
ls -la
```

### Dependencies Installation

Maven will automatically download all dependencies on first build. Required dependencies:
- **Spring Boot:** Web and starter dependencies
- **Jackson:** JSON serialization/deserialization
- **JUnit 5:** Testing framework
- **Tomcat:** Embedded web server

---

## ▶️ Running the Application

### Step 1: Clean Build (First Time or After Major Changes)

```bash
cd /path/to/minimal-java-src
mvn clean package -DskipTests
```

**Output:** Build success message + JAR file created in `target/` directory

### Step 2: Run the Application

#### Using Maven (Development)
```bash
mvn spring-boot:run
```

#### Using JAR File (Production)
```bash
java -jar target/minimal-java-mvn-1.0.0.jar
```

**Expected Output:**
```
  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 :: Spring Boot ::                (v3.2.1)

Tomcat started on port 8080 (http) with context path ''
Started App in X.XXX seconds
```

### Step 3: Access the Application

Open your web browser and navigate to:
```
http://localhost:8080
```

Or use the Simple Browser in VS Code.

### Step 4: Stop the Application

Press `Ctrl+C` in the terminal where the application is running.

---

## ✨ Features

### Patient Registration
- **Patient ID:** Unique identifier for each patient
- **Personal Information:**
  - Full Name
  - Gender (Male, Female, Other)
  - Blood Group (A+, A-, B+, B-, AB+, AB-, O+, O-)
  - Email Address
  - Phone Number
- **Address Information:**
  - Street Address
  - City

### Patient Management
- ✅ **Create:** Register new patients via the form
- ✅ **Read:** View all registered patients in real-time
- ✅ **Delete:** Remove patients with confirmation dialog
- ✅ **Persist:** Data saved to `persons.json` file

### User Interface
- **Two-Column Layout:**
  - Left: Registration form (sticky, responsive)
  - Right: Patient list (scrollable, real-time updates)
- **Responsive Design:** Works on desktop, tablet, and mobile
- **Real-time Updates:** List refreshes every 2 seconds
- **Visual Feedback:** Success/error messages for user actions

---

## 🔌 API Endpoints

### Base URL
```
http://localhost:8080/api/persons
```

### Endpoints

#### 1. Register a Patient (CREATE)
```http
POST /api/persons
Content-Type: application/json

{
  "id": "P001",
  "name": "John Doe",
  "gender": "Male",
  "bloodGroup": "O+",
  "email": "john@example.com",
  "phone": "9876543210",
  "street": "123 Main Street",
  "city": "Kolkata"
}

Response (200 OK):
{
  "id": "P001",
  "name": "John Doe",
  "gender": "Male",
  "bloodGroup": "O+",
  "email": "john@example.com",
  "phone": "9876543210",
  "address": {
    "line1": "123 Main Street",
    "city": "Kolkata"
  }
}
```

#### 2. Get All Patients (READ)
```http
GET /api/persons

Response (200 OK):
[
  {
    "id": "P001",
    "name": "John Doe",
    ...
  },
  {
    "id": "P002",
    "name": "Jane Smith",
    ...
  }
]
```

#### 3. Delete a Patient (DELETE)
```http
DELETE /api/persons/{id}

Example: DELETE /api/persons/P001

Response (200 OK):
{
  "message": "Person deleted successfully"
}
```

---

## 🏗️ Project Architecture

### Technology Stack

| Layer | Technology |
|-------|-----------|
| **Backend Framework** | Spring Boot 3.2.1 |
| **Web Server** | Apache Tomcat 10.1.17 |
| **Java Version** | 17+ |
| **Build Tool** | Maven 3.x |
| **JSON Processing** | Jackson Databind 2.15.3 |
| **Testing** | JUnit Jupiter 5.10.1 |
| **Frontend** | HTML5, CSS3, JavaScript (Vanilla) |

### Application Layers

```
┌─────────────────────────────────────┐
│     Frontend (HTML/CSS/JS)          │
│  - Registration Form                │
│  - Patient List Display             │
│  - Delete Functionality             │
└──────────────┬──────────────────────┘
               │
┌──────────────▼──────────────────────┐
│     REST API (PersonController)     │
│  - POST /api/persons                │
│  - GET /api/persons                 │
│  - DELETE /api/persons/{id}         │
└──────────────┬──────────────────────┘
               │
┌──────────────▼──────────────────────┐
│     Service Layer (PersonService)   │
│  - register()                       │
│  - list()                           │
│  - delete()                         │
└──────────────┬──────────────────────┘
               │
┌──────────────▼──────────────────────┐
│  Repository (PersonRepository)      │
│  - InMemoryPersonRepository         │
│  - PersistentPersonRepository       │
└──────────────┬──────────────────────┘
               │
┌──────────────▼──────────────────────┐
│     Data Storage                    │
│  - persons.json (File-based)        │
└─────────────────────────────────────┘
```

### Class Hierarchy

**Models:**
- `Person.java` - Patient entity with Jackson annotations
- `Address.java` - Address embedded in Person

**Repository Pattern:**
- `PersonRepository` (Interface)
  - `InMemoryPersonRepository` - In-memory storage
  - `PersistentPersonRepository` - File-based JSON storage

**Service Layer:**
- `PersonService` - Business logic for CRUD operations

**Controller:**
- `PersonController` - REST endpoints and request handling

---

## 💾 Data Persistence

### Storage Method
- **Format:** JSON
- **Location:** `persons.json` in project root
- **Auto-created:** Yes, on first patient registration

### Persistence Features

1. **Automatic Save**
   - Data saves automatically when a patient is registered or deleted
   - File is updated in real-time

2. **Auto-Load on Startup**
   - Application loads existing data from `persons.json` on startup
   - Empty list if file doesn't exist

3. **Example Data File**
```json
[
  {
    "id": "P001",
    "name": "John Doe",
    "gender": "Male",
    "bloodGroup": "O+",
    "email": "john@example.com",
    "phone": "9876543210",
    "address": {
      "line1": "123 Main Street",
      "city": "Kolkata"
    }
  }
]
```

### Data Reset

To start fresh with no patients:
```bash
# Linux/macOS
rm persons.json

# Windows
del persons.json
```

---

## 🧪 Running Tests

### Run All Tests
```bash
mvn test
```

### Run Specific Test Class
```bash
mvn test -Dtest=PersonServiceTest
```

### Run Tests with Coverage
```bash
mvn test jacoco:report
```

---

## 🐛 Troubleshooting

### Port 8080 Already in Use
**Error:** `Port 8080 was already in use`

**Solution:**
```bash
# Linux/macOS - Find and kill process using port 8080
lsof -i :8080
kill -9 <PID>

# Or kill all Java processes running JAR files
pkill -f "java -jar"

# Windows
netstat -ano | findstr :8080
taskkill /PID <PID> /F
```

### JSON Deserialization Error
**Error:** `Unrecognized field "street" (class com.example.model.Address), not marked as ignorable`

**Cause:** Old `persons.json` file has incompatible schema after code changes

**Solution:**
```bash
# Delete the old data file to start fresh
cd /path/to/minimal-java-src
rm persons.json

# On Windows
del persons.json

# Restart the application
java -jar target/minimal-java-mvn-1.0.0.jar
```

### Maven Build Fails
**Error:** `[ERROR] COMPILATION ERROR`

**Solution:**
```bash
# Clear Maven cache and rebuild
mvn clean

# Force update dependencies
mvn clean install -U

# Full clean rebuild (skipping tests for faster build)
mvn clean package -DskipTests
```

### Application Won't Start
**Symptoms:** Application fails to start or crashes immediately

**Checks:**
- ✓ Java version: `java -version` (must be 17+)
- ✓ Maven version: `mvn -version` (must be 3.6+)
- ✓ Disk space: At least 500MB required
- ✓ Port 8080: Must be available (not in use)
- ✓ Target JAR exists: `ls target/minimal-java-mvn-1.0.0.jar`

**Solution:**
```bash
# Do a complete fresh build and restart
cd /path/to/minimal-java-src
rm persons.json                                    # Clear old data
pkill -f "java -jar"                              # Kill any running instances
mvn clean package -DskipTests                     # Fresh build
java -jar target/minimal-java-mvn-1.0.0.jar      # Start application
```

### Frontend Not Loading
**Symptom:** Browser shows blank page or cannot connect to localhost:8080

**Solutions:**
1. Verify application is running: Check terminal for startup messages
2. Check browser console: Open DevTools (F12) → Console tab for JavaScript errors
3. Clear browser cache: Ctrl+Shift+Delete (or Cmd+Shift+Delete on macOS)
4. Try different browser: Chrome, Firefox, Safari, Edge
5. Check firewall: Ensure port 8080 is not blocked

### Patient Registration Returns Error
**Error:** `Failed to register person` (from frontend notification)

**Causes & Solutions:**
1. **Missing required fields:** Ensure all form fields are filled
2. **Invalid phone format:** Use only digits (e.g., 9876543210)
3. **Duplicate ID:** Patient ID must be unique - use different ID
4. **Server error:** Check browser console (F12) for detailed error message
5. **Backend not running:** Verify application started successfully

### Patient List Not Updating
**Symptom:** New patients don't appear in the list after registration

**Solutions:**
1. Wait 2-3 seconds (list auto-refreshes every 2 seconds)
2. Refresh browser: Press F5 or Ctrl+R
3. Check browser console for JavaScript errors (F12)
4. Verify registration was successful (should see success message)
5. Check `persons.json` file exists and has data:
   ```bash
   cat persons.json
   ```

### Delete Button Not Working
**Symptom:** Clicking delete does nothing or shows error

**Solutions:**
1. Confirm patient card is visible in the list
2. Check browser console for errors (F12)
3. Verify backend is still running
4. Try refreshing page and deleting again
5. Check server logs for error messages

### Slow Performance / Lag
**Symptom:** Page is slow, registration takes long time

**Solutions:**
1. Check system resources: RAM usage, CPU usage
2. Close other browser tabs to free memory
3. Clear browser cache: Ctrl+Shift+Delete
4. Restart application:
   ```bash
   pkill -f "java -jar"
   java -jar target/minimal-java-mvn-1.0.0.jar
   ```
5. For large datasets (100+ patients): Consider upgrading to database storage

### Data Loss After Restart
**Symptom:** Registered patients disappear after stopping and restarting application

**Causes:**
1. `persons.json` was deleted
2. File permissions issue - application cannot write to file
3. Disk full - file save failed silently

**Solutions:**
```bash
# Check if persons.json exists
ls -la persons.json

# Check file permissions (must be readable/writable)
chmod 644 persons.json

# Check disk space
df -h

# If file corrupted, delete and restart fresh
rm persons.json
java -jar target/minimal-java-mvn-1.0.0.jar
```

### Complete Fresh Start (Nuclear Option)
**Use this if nothing else works:**

```bash
cd /path/to/minimal-java-src

# Kill running processes
pkill -f "java -jar"

# Clean everything
rm -f persons.json
mvn clean

# Full rebuild
mvn clean package -DskipTests

# Start fresh
java -jar target/minimal-java-mvn-1.0.0.jar
```

---

## 📝 Development Notes

### Adding New Features

1. **New Patient Field:**
   - Update `Person.java` model
   - Update `PersonRequest` class in controller
   - Update frontend form and display

2. **New API Endpoint:**
   - Add method to `PersonRepository` interface
   - Implement in repository classes
   - Add service method
   - Add controller endpoint with proper HTTP method

3. **UI Changes:**
   - Edit `index.html` in `src/main/resources/static/`
   - Rebuild with `mvn clean package`
   - Restart application

### Code Structure Best Practices
- Follow Spring Boot conventions
- Keep business logic in service layer
- Use repository pattern for data access
- Implement proper error handling
- Add Jackson annotations for JSON serialization

---

## 📞 Support

For issues or questions:
1. Check the troubleshooting section
2. Review API endpoint documentation
3. Check application logs on startup
4. Verify all prerequisites are installed

---

## 📄 License

This project is provided as-is for educational and hospital management purposes.

---

## 🔄 Version History

| Version | Date | Changes |
|---------|------|---------|
| 1.0.0 | 2026-01-28 | Initial release with patient registration, deletion, and persistence |

---

**Last Updated:** January 28, 2026
