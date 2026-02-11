package com.example.repo;

import com.example.model.Person;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class PersistentPersonRepository implements PersonRepository {
    private static final String FILE_PATH = "persons.json";
    private final Map<String, Person> store = new HashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public PersistentPersonRepository() {
        loadFromFile();
    }

    @Override
    public Optional<Person> findById(String id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public List<Person> findAll() {
        return new ArrayList<>(store.values());
    }

    @Override
    public void save(Person person) {
        store.put(person.id(), person);
        saveToFile();
    }

    @Override
    public void delete(String id) {
        store.remove(id);
        saveToFile();
    }

    private void saveToFile() {
        try {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File(FILE_PATH), new ArrayList<>(store.values()));
        } catch (IOException e) {
            System.err.println("Error saving persons to file: " + e.getMessage());
        }
    }

    private void loadFromFile() {
        try {
            File file = new File(FILE_PATH);
            if (file.exists()) {
                List<Person> loadedPersons = objectMapper.readValue(file, new TypeReference<List<Person>>() {});
                for (Person p : loadedPersons) {
                    store.put(p.id(), p);
                }
                System.out.println("Loaded " + loadedPersons.size() + " persons from file");
            }
        } catch (IOException e) {
            System.err.println("Error loading persons from file: " + e.getMessage());
        }
    }
}
