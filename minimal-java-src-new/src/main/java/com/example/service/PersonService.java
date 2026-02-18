
package com.example.service;

import com.example.model.Person;
import com.example.repo.PersonRepository;
import java.util.List;
import java.util.Objects;

public class PersonService {
    private final PersonRepository repo;

    public PersonService(PersonRepository repo) {
        this.repo = Objects.requireNonNull(repo);
    }

    public Person register(Person p) {
        repo.save(p);
        return p;
    }

    public Person update(Person p) {
        repo.save(p);
        return p;
    }

    public List<Person> list() { return repo.findAll(); }

    public void delete(String id) { repo.delete(id); }
}
