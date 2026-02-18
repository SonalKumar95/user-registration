
package com.example.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;

public final class Address {
    private final String line1;
    private final String city;

    @JsonCreator
    public Address(
        @JsonProperty("line1") String line1,
        @JsonProperty("city") String city
    ) {
        this.line1 = line1;
        this.city = city;
    }

    @JsonProperty("street")
    public String getStreet() { return line1; }
    
    @JsonProperty("line1")
    public String getLine1() { return line1; }
    
    @JsonProperty("city")
    public String getCity() { return city; }
    
    public String line1() { return line1; }
    public String city() { return city; }

    @Override public String toString() { return line1 + ", " + city; }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Address)) return false;
        Address a = (Address) o;
        return Objects.equals(line1, a.line1) && Objects.equals(city, a.city);
    }

    @Override public int hashCode() { return Objects.hash(line1, city); }
}
