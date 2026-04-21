package com.chuka.gradesystem.util;
import java.util.regex.Pattern;
/**
 * ValidationUtil - Utility Class for Input Validation
 *
 * This class demonstrates UTILITY CLASS pattern with STATIC METHODS.
 *
 * Key Concepts:
 * 1. STATIC METHODS - Can be called without creating an instance
 * 2. UTILITY CLASS - Provides helper methods for common tasks
 * 3. REGULAR EXPRESSIONS - Pattern matching for validation
 *
 * @author Charles David
 * @course ACSC 223 - Object Oriented Programming (Java 1)
 * @institution Chuka University
 */
public class ValidationUtil {
 // Regular expression patterns
 private static final Pattern EMAIL_PATTERN =
 Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
 private static final Pattern PHONE_PATTERN =
 Pattern.compile("^[0-9]{10}$|^\\+[0-9]{12}$");
 private static final Pattern STUDENT_ID_PATTERN =
 Pattern.compile("^[A-Z]{2,4}[0-9]{3,6}$");
 private static final Pattern COURSE_CODE_PATTERN =
 Pattern.compile("^[A-Z]{3,4}\\s?[0-9]{3}$");
 /**
 * Private constructor to prevent instantiation
 * Utility classes should not be instantiated
 */
 private ValidationUtil() {
 throw new UnsupportedOperationException("Utility class cannot be instantiated");
 }
 /**
 * Validate email address
 * @param email Email to validate
 * @return true if valid, false otherwise
 */
 public static boolean isValidEmail(String email) {
 if (email == null || email.trim().isEmpty()) {
 return false;
 }
 return EMAIL_PATTERN.matcher(email.trim()).matches();
 }
 /**
 * Validate phone number
 * Accepts: 0712345678 or +254712345678
 * @param phone Phone number to validate
 * @return true if valid, false otherwise
 */
 public static boolean isValidPhone(String phone) {
 if (phone == null || phone.trim().isEmpty()) {
 return false;
 }
 return PHONE_PATTERN.matcher(phone.trim()).matches();
 }
 /**
 * Validate student ID
 * Format: 2-4 uppercase letters followed by 3-6 digits
 * Examples: CS12345, BCOM001, IT2023
 * @param studentId Student ID to validate
 * @return true if valid, false otherwise
 */
 public static boolean isValidStudentId(String studentId) {
 if (studentId == null || studentId.trim().isEmpty()) {
 return false;
 }
 return STUDENT_ID_PATTERN.matcher(studentId.trim()).matches();
 }
 /**
 * Validate course code
 * Format: 3-4 uppercase letters followed by 3 digits
 * Examples: ACSC 223, CS101, MATH 201
 * @param courseCode Course code to validate
 * @return true if valid, false otherwise
 */
 public static boolean isValidCourseCode(String courseCode) {
 if (courseCode == null || courseCode.trim().isEmpty()) {
 return false;
 }
 return COURSE_CODE_PATTERN.matcher(courseCode.trim()).matches();
 }
 /**
 * Validate score
 * Score must be between 0 and 100
 * @param score Score to validate
 * @return true if valid, false otherwise
 */
 public static boolean isValidScore(double score) {
 return score >= 0 && score <= 100;
 }
 /**
 * Validate credits
 * Credits must be positive
 * @param credits Credits to validate
 * @return true if valid, false otherwise
 */
 public static boolean isValidCredits(int credits) {
 return credits > 0 && credits <= 10;
 }
 /**
 * Validate year of study
 * Year must be between 1 and 6
 * @param year Year to validate
 * @return true if valid, false otherwise
 */
 public static boolean isValidYearOfStudy(int year) {
 return year >= 1 && year <= 6;
 }
 /**
 * Validate name
 * Name must not be empty and should contain only letters and spaces
 * @param name Name to validate
 * @return true if valid, false otherwise
 */
 public static boolean isValidName(String name) {
 if (name == null || name.trim().isEmpty()) {
 return false;
 }
 return name.trim().matches("^[a-zA-Z\\s]+$");
 }
 /**
 * Sanitize string input
 * Removes leading/trailing whitespace
 * @param input Input string
 * @return Sanitized string
 */
 public static String sanitize(String input) {
 if (input == null) {
 return "";
 }
 return input.trim();
 }
 /**
 * Check if string is empty or null
 * @param str String to check
 * @return true if empty or null, false otherwise
 */
 public static boolean isEmpty(String str) {
 return str == null || str.trim().isEmpty();
 }
}