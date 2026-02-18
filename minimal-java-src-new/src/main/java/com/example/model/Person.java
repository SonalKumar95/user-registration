
package com.example.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;

public class Person {
    private final String id;
    private final String name;
    private final Address address;
    private final String gender;
    private final String bloodGroup;
    private final String email;
    private final String phone;

    @JsonCreator
    public Person(
        @JsonProperty("id") String id,
        @JsonProperty("name") String name,
        @JsonProperty("address") Address address,
        @JsonProperty("gender") String gender,
        @JsonProperty("bloodGroup") String bloodGroup,
        @JsonProperty("email") String email,
        @JsonProperty("phone") String phone
    ) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.gender = gender;
        this.bloodGroup = bloodGroup;
        this.email = email;
        this.phone = phone;
    }

    @JsonProperty("id")
    public String getId() { return id; }
    
    @JsonProperty("name")
    public String getName() { return name; }
    
    @JsonProperty("address")
    public Address getAddress() { return address; }
    
    @JsonProperty("gender")
    public String getGender() { return gender; }
    
    @JsonProperty("bloodGroup")
    public String getBloodGroup() { return bloodGroup; }
    
    @JsonProperty("email")
    public String getEmail() { return email; }
    
    @JsonProperty("phone")
    public String getPhone() { return phone; }
    
    public String id() { return id; }
    public String name() { return name; }
    public Address address() { return address; }

    @Override public String toString() { return name + " (" + id + ") @ " + address; }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Person)) return false;
        Person p = (Person) o;
        return Objects.equals(id, p.id) && Objects.equals(name, p.name) && Objects.equals(address, p.address);
    }

    @Override public int hashCode() { return Objects.hash(id, name, address); }
}
