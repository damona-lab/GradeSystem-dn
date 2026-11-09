package com.chuka.gradesystem.model;
import java.io.Serializable;
import java.util.Objects;
/**
 * Course - Model Class
 *
 * This class demonstrates ENCAPSULATION in OOP.
 *
 * Key OOP Concepts:
 * 1. ENCAPSULATION - All fields are private with public getters/setters
 * 2. DATA HIDING - Internal state is protected from external modification
 *
 * @author Charles David
 * @course ACSC 223 - Object Oriented Programming (Java 1)
 * @institution Chuka University
 */
public class Course implements Serializable {
    private static final long serialVersionUID = 6251509704247163033L;
 // Private fields - ENCAPSULATION
 private String courseCode;
 private String courseName;
 private int credits;
 private String instructor;
 private String description;
 /**
 * Default Constructor
 */
 public Course() {
 }
 /**
 * Parameterized Constructor
 * @param courseCode Unique course code (e.g., ACSC 223)
 * @param courseName Name of the course
 * @param credits Number of credit hours
 * @param instructor Instructor name
 */
 public Course(String courseCode, String courseName, int credits, String instructor) {
 this.courseCode = courseCode;
 this.courseName = courseName;
 this.credits = credits;
 this.instructor = instructor;
 }
 /**
 * Full Parameterized Constructor
 * @param courseCode Unique course code
 * @param courseName Name of the course
 * @param credits Number of credit hours
 * @param instructor Instructor name
 * @param description Course description
 */
 public Course(String courseCode, String courseName, int credits,
 String instructor, String description) {
 this.courseCode = courseCode;
 this.courseName = courseName;
 this.credits = credits;
 this.instructor = instructor;
 this.description = description;
 }
 // GETTERS - Provide READ access to private fields
 public String getCourseCode() {
 return courseCode;
 }
 public String getCourseName() {
 return courseName;
 }
 public int getCredits() {
 return credits;
 }
 public String getInstructor() {
 return instructor;
 }
 public String getDescription() {
 return description;
 }
 // SETTERS - Provide WRITE access to private fields with validation
 public void setCourseCode(String courseCode) {
 this.courseCode = courseCode;
 }
 public void setCourseName(String courseName) {
 this.courseName = courseName;
 }
 /**
 * Set credits with validation
 * Demonstrates encapsulation - we can add business logic in setters
 * @param credits Number of credit hours
 */
 public void setCredits(int credits) {
 if (credits < 0) {
 throw new IllegalArgumentException("Credits cannot be negative");
 }
 this.credits = credits;
 }
 public void setInstructor(String instructor) {
 this.instructor = instructor;
 }
 public void setDescription(String description) {
 this.description = description;
 }
 /**
 * Get full course information
 * @return Formatted course details
 */
 public String getFullDetails() {
 return String.format("%s - %s (%d credits)\nInstructor: %s",
 courseCode, courseName, credits, instructor);
 }
 /**
 * equals method for comparing Course objects
 * Two courses are equal if they have the same course code
 * @param o Object to compare
 * @return true if equal, false otherwise
 */
 @Override
 public boolean equals(Object o) {
 if (this == o) return true;
 if (o == null || getClass() != o.getClass()) return false;
 Course course = (Course) o;
 return Objects.equals(courseCode, course.courseCode);
 }
 /**
 * hashCode method for hash-based collections
 * @return hash code
 */
 @Override
 public int hashCode() {
 return Objects.hash(courseCode);
 }
 /**
 * toString method for string representation
 * @return String representation of Course
 */
 @Override
 public String toString() {
 return courseCode + " - " + courseName;
 }
}