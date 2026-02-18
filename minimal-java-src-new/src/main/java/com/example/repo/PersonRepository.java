
package com.example.repo;

import com.example.model.Person;
import java.util.List;
import java.util.Optional;

public interface PersonRepository {
    Optional<Person> findById(String id);
    List<Person> findAll();
    void save(Person person);
    void delete(String id);
}
