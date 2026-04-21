package com.chuka.gradesystem.model;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
/**
 * Student - Concrete Class extending Person
 *
 * This class demonstrates INHERITANCE and POLYMORPHISM in OOP.
 *
 * Key OOP Concepts:
 * 1. INHERITANCE - Student extends Person (IS-A relationship)
 * 2. POLYMORPHISM - Overrides the abstract getDetails() method
 * 3. ENCAPSULATION - Additional private fields with getters/setters
 *
 * @author Charles David
 * @course ACSC 223 - Object Oriented Programming (Java 1)
 * @institution Chuka University
 */
public class Student extends Person implements Serializable {
 // Private fields specific to Student
 private String studentId;
 private String department;
 private int yearOfStudy;
 private List<Course> enrolledCourses;
 private List<Grade> grades;
 /**
 * Default Constructor
 */
 public Student() {
 super(); // Call parent class constructor
 this.enrolledCourses = new ArrayList<>();
 this.grades = new ArrayList<>();
 }
 /**
 * Parameterized Constructor
 * @param id Person ID
 * @param name Student name
 * @param email Student email
 * @param phone Student phone
 * @param studentId University student ID
 * @param department Department name
 * @param yearOfStudy Current year of study
 */
 public Student(String id, String name, String email, String phone,
 String studentId, String department, int yearOfStudy) {
 // Call parent class constructor using super()
 super(id, name, email, phone);
 this.studentId = studentId;
 this.department = department;
 this.yearOfStudy = yearOfStudy;
 this.enrolledCourses = new ArrayList<>();
 this.grades = new ArrayList<>();
 }
 // GETTERS
 public String getStudentId() {
 return studentId;
 }
 public String getDepartment() {
 return department;
 }
 public int getYearOfStudy() {
 return yearOfStudy;
 }
 public List<Course> getEnrolledCourses() {
 return enrolledCourses;
 }
 public List<Grade> getGrades() {
 return grades;
 }
 // SETTERS
 public void setStudentId(String studentId) {
 this.studentId = studentId;
 }
 public void setDepartment(String department) {
 this.department = department;
 }
 public void setYearOfStudy(int yearOfStudy) {
 this.yearOfStudy = yearOfStudy;
 }
 public void setEnrolledCourses(List<Course> enrolledCourses) {
 this.enrolledCourses = enrolledCourses;
 }
 public void setGrades(List<Grade> grades) {
 this.grades = grades;
 }
 // BUSINESS METHODS
 /**
 * Enroll student in a course
 * @param course Course to enroll in
 */
 public void enrollInCourse(Course course) {
 if (!enrolledCourses.contains(course)) {
 enrolledCourses.add(course);
 }
 }
 /**
 * Add a grade for this student
 * @param grade Grade to add
 */
 public void addGrade(Grade grade) {
 grades.add(grade);
 }
 /**
 * Calculate the student's GPA
 * @return GPA value
 */
 public double calculateGPA() {
 if (grades.isEmpty()) {
 return 0.0;
 }
 double totalPoints = 0.0;
 int totalCredits = 0;
 for (Grade grade : grades) {
 Course course = grade.getCourse();
 totalPoints += grade.getGradePoint() * course.getCredits();
 totalCredits += course.getCredits();
 }
 return totalCredits > 0 ? totalPoints / totalCredits : 0.0;
 }
 /**
 * Get total credits earned
 * @return Total credits
 */
 public int getTotalCredits() {
 int total = 0;
 for (Course course : enrolledCourses) {
 total += course.getCredits();
 }
 return total;
 }
 /**
 * POLYMORPHISM - Method Overriding
 *
 * This method overrides the abstract method from Person class.
 * This is RUNTIME POLYMORPHISM - the method to be called is determined at runtime.
 *
 * @return Detailed information about the student
 */
 @Override
public String getDetails() {
    StringBuilder details = new StringBuilder();
    details.append("=== STUDENT DETAILS ===\n");
    details.append("Student ID: ").append(studentId).append("\n");
    details.append("Name: ").append(getName()).append("\n");
    details.append("Email: ").append(getEmail()).append("\n");
    details.append("Phone: ").append(getPhone()).append("\n");
    details.append("Department: ").append(department).append("\n");
    details.append("Year of Study: ").append(yearOfStudy).append("\n");
    details.append("Enrolled Courses: ").append(enrolledCourses.size()).append("\n");
    details.append("Current GPA: ").append(String.format("%.2f", calculateGPA())).append("\n");
    return details.toString();
}
 /**
 * toString method for string representation
 * @return String representation of Student
 */
 @Override
 public String toString() {
 return "Student{" +
 "studentId='" + studentId + '\'' +
 ", name='" + getName() + '\'' +
 ", department='" + department + '\'' +
 ", year=" + yearOfStudy +
 ", GPA=" + String.format("%.2f", calculateGPA()) +
 '}';
 }
}