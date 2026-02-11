
package com.example;

import com.example.model.Address;
import com.example.model.Person;
import com.example.repo.InMemoryPersonRepository;
import com.example.service.PersonService;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PersonServiceTest {
    @Test void registersAndLists() {
        var service = new PersonService(new InMemoryPersonRepository());
        service.register(new Person("42", "Sonal", new Address("EM Bypass", "Kolkata"), "Female", "O+", "sonal@example.com", "9876543210"));
        assertEquals(1, service.list().size());
    }
}
