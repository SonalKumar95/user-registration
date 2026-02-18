package com.example.validator;

import java.util.regex.Pattern;

public class PersonValidator {
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );
    private static final Pattern PHONE_PATTERN = Pattern.compile("^[0-9]{10}$");
    private static final Pattern GENDER_PATTERN = Pattern.compile("^(Male|Female|Other)$");
    private static final Pattern BLOOD_GROUP_PATTERN = Pattern.compile("^(O\\+|O\\-|A\\+|A\\-|B\\+|B\\-|AB\\+|AB\\-)$");

    public static ValidationResult validate(String id, String name, String street, String city, 
                                            String gender, String bloodGroup, String email, String phone) {
        if (id == null || id.isEmpty()) {
            return ValidationResult.invalid("ID cannot be empty");
        }
        if (name == null || name.isEmpty()) {
            return ValidationResult.invalid("Name cannot be empty");
        }
        if (street == null || street.isEmpty()) {
            return ValidationResult.invalid("Street cannot be empty");
        }
        if (city == null || city.isEmpty()) {
            return ValidationResult.invalid("City cannot be empty");
        }
        if (gender == null || gender.isEmpty()) {
            return ValidationResult.invalid("Gender cannot be empty");
        }
        if (!GENDER_PATTERN.matcher(gender).matches()) {
            return ValidationResult.invalid("Gender must be Male, Female, or Other");
        }
        if (bloodGroup == null || bloodGroup.isEmpty()) {
            return ValidationResult.invalid("Blood group cannot be empty");
        }
        if (!BLOOD_GROUP_PATTERN.matcher(bloodGroup).matches()) {
            return ValidationResult.invalid("Invalid blood group format");
        }
        if (email != null && !email.isEmpty() && !EMAIL_PATTERN.matcher(email).matches()) {
            return ValidationResult.invalid("Invalid email format");
        }
        if (phone != null && !phone.isEmpty() && !PHONE_PATTERN.matcher(phone).matches()) {
            return ValidationResult.invalid("Phone number must be 10 digits");
        }
        return ValidationResult.valid();
    }

    public static class ValidationResult {
        private final boolean valid;
        private final String message;

        private ValidationResult(boolean valid, String message) {
            this.valid = valid;
            this.message = message;
        }

        public static ValidationResult valid() {
            return new ValidationResult(true, null);
        }

        public static ValidationResult invalid(String message) {
            return new ValidationResult(false, message);
        }

        public boolean isValid() {
            return valid;
        }

        public String getMessage() {
            return message;
        }
    }
}
