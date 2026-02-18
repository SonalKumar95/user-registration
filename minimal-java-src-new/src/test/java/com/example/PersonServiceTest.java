
package com.example;

import com.example.model.Address;
import com.example.model.Person;
import com.example.repo.PersonRepository;
import com.example.service.PersonService;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;

class PersonServiceTest {
    static class MockPersonRepository implements PersonRepository {
        private final Map<String, Person> store = new HashMap<>();
        
        @Override public Optional<Person> findById(String id) { return Optional.ofNullable(store.get(id)); }
        @Override public List<Person> findAll() { return new ArrayList<>(store.values()); }
        @Override public void save(Person person) { store.put(person.id(), person); }
        @Override public void delete(String id) { store.remove(id); }
    }

    @Test void registersAndLists() {
        var service = new PersonService(new MockPersonRepository());
        service.register(new Person("42", "Sonal", new Address("EM Bypass", "Kolkata"), "Female", "O+", "sonal@example.com", "9876543210"));
        assertEquals(1, service.list().size());
    }
}
