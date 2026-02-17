package com.example.util;

/**
 * Utility class for phone number validation.
 * Provides static methods to validate phone numbers in various formats.
 */
public class PhoneValidator {
    // Updated: Comprehensive phone number validation utility
    
    /**
     * Validates if a phone number contains exactly 10 digits.
     * 
     * @param phoneNumber the phone number to validate
     * @return true if the phone number contains exactly 10 digits, false otherwise
     */
    public static boolean isValidLength(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.isEmpty()) {
            return false;
        }
        String cleaned = phoneNumber.replaceAll("\\D", "");
        return cleaned.length() == 10;
    }

    /**
     * Validates if a phone number follows the pattern: (XXX) XXX-XXXX
     * 
     * @param phoneNumber the phone number to validate
     * @return true if the phone number matches the pattern, false otherwise
     */
    public static boolean isValidFormat(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.isEmpty()) {
            return false;
        }
        String pattern = "^\\(\\d{3}\\) \\d{3}-\\d{4}$";
        return phoneNumber.matches(pattern);
    }

    /**
     * Validates if a phone number starts with a valid US area code (0-9).
     * 
     * @param phoneNumber the phone number to validate
     * @return true if the first digit is between 0-9, false otherwise
     */
    public static boolean isValidAreaCode(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.isEmpty()) {
            return false;
        }
        String cleaned = phoneNumber.replaceAll("\\D", "");
        if (cleaned.isEmpty()) {
            return false;
        }
        int areaCode = Character.getNumericValue(cleaned.charAt(0));
        return areaCode >= 0 && areaCode <= 9;
    }

    /**
     * Formats a phone number string into the standard (XXX) XXX-XXXX format.
     * 
     * @param phoneNumber the raw phone number to format
     * @return the formatted phone number, or the original if formatting fails
     */
    public static String formatPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.isEmpty()) {
            return phoneNumber;
        }
        String cleaned = phoneNumber.replaceAll("\\D", "");
        if (cleaned.length() != 10) {
            return phoneNumber;
        }
        return String.format("(%s) %s-%s", 
            cleaned.substring(0, 3),
            cleaned.substring(3, 6),
            cleaned.substring(6, 10));
    }
}
