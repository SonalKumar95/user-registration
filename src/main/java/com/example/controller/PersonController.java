package com.example.controller;

import com.example.model.Address;
import com.example.model.Person;
import com.example.repo.PersistentPersonRepository;
import com.example.service.PersonService;
import com.example.util.PhoneValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/persons")
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.OPTIONS})
public class PersonController {
    private static final Logger logger = LoggerFactory.getLogger(PersonController.class);
    private static final PersonService service = new PersonService(new PersistentPersonRepository());

    @PostMapping
    public ResponseEntity<?> register(@RequestBody PersonRequest request) {
        try {
            logger.info("Registering person: {}", request.getName());
            
            if (request.getId() == null || request.getId().isEmpty()) {
                return ResponseEntity.badRequest().body(createErrorResponse("ID cannot be empty"));
            }
            if (request.getName() == null || request.getName().isEmpty()) {
                return ResponseEntity.badRequest().body(createErrorResponse("Name cannot be empty"));
            }
            if (request.getGender() == null || request.getGender().isEmpty()) {
                return ResponseEntity.badRequest().body(createErrorResponse("Gender cannot be empty"));
            }
            if (request.getBloodGroup() == null || request.getBloodGroup().isEmpty()) {
                return ResponseEntity.badRequest().body(createErrorResponse("Blood group cannot be empty"));
            }
            if (request.getPhone() == null || request.getPhone().isEmpty()) {
                return ResponseEntity.badRequest().body(createErrorResponse("Phone number cannot be empty"));
            }
            if (!PhoneValidator.isValidLength(request.getPhone())) {
                return ResponseEntity.badRequest().body(createErrorResponse("Phone number must contain exactly 10 digits"));
            }
            
            Person person = new Person(
                request.getId(),
                request.getName(),
                new Address(request.getStreet(), request.getCity(), request.getStreet()),
                request.getGender(),
                request.getBloodGroup(),
                request.getEmail(),
                request.getPhone()
            );
            service.register(person);
            logger.info("Successfully registered person: {}", person);
            return ResponseEntity.ok(person);
        } catch (Exception e) {
            logger.error("Error registering person", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse("Error: " + e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<List<Person>> list() {
        try {
            List<Person> persons = service.list();
            logger.info("Retrieved {} persons", persons.size());
            return ResponseEntity.ok(persons);
        } catch (Exception e) {
            logger.error("Error retrieving persons", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable String id) {
        try {
            logger.info("Deleting person with ID: {}", id);
            service.delete(id);
            logger.info("Successfully deleted person with ID: {}", id);
            return ResponseEntity.ok(createResponse("Person deleted successfully"));
        } catch (Exception e) {
            logger.error("Error deleting person", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse("Error: " + e.getMessage()));
        }
    }

    private Map<String, String> createErrorResponse(String message) {
        Map<String, String> response = new HashMap<>();
        response.put("error", message);
        return response;
    }

    private Map<String, String> createResponse(String message) {
        Map<String, String> response = new HashMap<>();
        response.put("message", message);
        return response;
    }

    public static class PersonRequest {
        private String id;
        private String name;
        private String street;
        private String city;
        private String gender;
        private String bloodGroup;
        private String email;
        private String phone;

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getStreet() { return street; }
        public void setStreet(String street) { this.street = street; }
        public String getCity() { return city; }
        public void setCity(String city) { this.city = city; }
        public String getGender() { return gender; }
        public void setGender(String gender) { this.gender = gender; }
        public String getBloodGroup() { return bloodGroup; }
        public void setBloodGroup(String bloodGroup) { this.bloodGroup = bloodGroup; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }
    }
}
