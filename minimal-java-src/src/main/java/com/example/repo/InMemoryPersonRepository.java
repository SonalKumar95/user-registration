
package com.example.repo;

import com.example.model.Person;
import java.util.*;

public class InMemoryPersonRepository implements PersonRepository {
    private final Map<String, Person> store = new HashMap<>();

    @Override public Optional<Person> findById(String id) { return Optional.ofNullable(store.get(id)); }

    @Override public List<Person> findAll() { return new ArrayList<>(store.values()); }

    @Override public void save(Person person) { store.put(person.id(), person); }

    @Override public void delete(String id) { store.remove(id); }
}
