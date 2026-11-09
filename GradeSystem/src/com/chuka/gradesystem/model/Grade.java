package com.chuka.gradesystem.model;
import java.io.Serializable;
import java.util.Objects;
/**
 * Grade - Model Class
 *
 * This class represents a grade for a student in a specific course.
 * Demonstrates ENCAPSULATION and BUSINESS LOGIC implementation.
 *
 * Key OOP Concepts:
 * 1. ENCAPSULATION - Private fields with controlled access
 * 2. COMPOSITION - Grade HAS-A Student and HAS-A Course
 * 3. BUSINESS LOGIC - Grade calculation methods
 *
 * @author Charles David
 * @course ACSC 223 - Object Oriented Programming (Java 1)
 * @institution Chuka University
 */
public class Grade implements Serializable {
    private static final long serialVersionUID = -6687771529431291272L;
 // Private fields
 private Student student;
 private Course course;
 private double score; // Numerical score (0-100)
 private String letterGrade; // Letter grade (A, B, C, D, F)
 private double gradePoint; // Grade point (0.0-4.0)
 private String semester;
 /**
 * Default Constructor
 */
 public Grade() {
 }
 /**
 * Parameterized Constructor
 * @param student The student
 * @param course The course
 * @param score The numerical score
 * @param semester The semester
 */
 public Grade(Student student, Course course, double score, String semester) {
 this.student = student;
 this.course = course;
 this.score = score;
 this.semester = semester;
 // Automatically calculate letter grade and grade point
 calculateLetterGrade();
 calculateGradePoint();
 }
 // GETTERS
 public Student getStudent() {
 return student;
 }
 public Course getCourse() {
 return course;
 }
 public double getScore() {
 return score;
 }
 public String getLetterGrade() {
 return letterGrade;
 }
 public double getGradePoint() {
 return gradePoint;
 }
 public String getSemester() {
 return semester;
 }
 // SETTERS
 public void setStudent(Student student) {
 this.student = student;
 }
 public void setCourse(Course course) {
 this.course = course;
 }
 /**
 * Set score and automatically recalculate letter grade and grade point
 * Demonstrates encapsulation - internal state is automatically updated
 * @param score Numerical score (0-100)
 */
 public void setScore(double score) {
 if (score < 0 || score > 100) {
 throw new IllegalArgumentException("Score must be between 0 and 100");
 }
 this.score = score;
 calculateLetterGrade();
 calculateGradePoint();
 }
 public void setSemester(String semester) {
 this.semester = semester;
 }
 // BUSINESS LOGIC METHODS
 /**
 * Calculate letter grade based on score
 * Grading Scale:
 * A: 70-100
 * B: 60-69
 * C: 50-59
 * D: 40-49
 * F: 0-39
 */
 private void calculateLetterGrade() {
 if (score >= 70) {
 letterGrade = "A";
 } else if (score >= 60) {
 letterGrade = "B";
 } else if (score >= 50) {
 letterGrade = "C";
 } else if (score >= 40) {
 letterGrade = "D";
 } else {
 letterGrade = "F";
 }
 }
 /**
 * Calculate grade point based on letter grade
 * Grade Points:
 * A: 4.0
 * B: 3.0
 * C: 2.0
 * D: 1.0
 * F: 0.0
 */
 private void calculateGradePoint() {
 switch (letterGrade) {
 case "A":
 gradePoint = 4.0;
 break;
 case "B":
 gradePoint = 3.0;
 break;
 case "C":
 gradePoint = 2.0;
 break;
 case "D":
 gradePoint = 1.0;
 break;
 case "F":
 gradePoint = 0.0;
 break;
 default:
 gradePoint = 0.0;
 }
 }
 /**
 * Check if the grade is passing
 * @return true if passing (D or above), false otherwise
 */
 public boolean isPassing() {
 return score >= 40;
 }
 /**
 * Get grade status
 * @return Status string (Excellent, Good, Average, Poor, Fail)
 */
 public String getGradeStatus() {
 if (score >= 70) {
 return "Excellent";
 } else if (score >= 60) {
 return "Good";
 } else if (score >= 50) {
 return "Average";
 } else if (score >= 40) {
 return "Poor";
 } else {
 return "Fail";
 }
 }
 /**
 * Get full grade details
 * @return Formatted grade information
 */
 public String getDetails() {
     String courseName = course != null ? course.getCourseName() : "N/A";
     return String.format("Course: %s\nScore: %.2f\nLetter Grade: %s\nGrade Point: %.1f\nStatus: %s\nSemester: %s",
         courseName, score, letterGrade, gradePoint, getGradeStatus(), semester != null ? semester : "N/A");
 }

 @Override
 public boolean equals(Object o) {
     if (this == o) return true;
     if (o == null || getClass() != o.getClass()) return false;
     Grade grade = (Grade) o;
     String thisStudentId = student != null ? student.getStudentId() : null;
     String otherStudentId = grade.student != null ? grade.student.getStudentId() : null;
     String thisCourseCode = course != null ? course.getCourseCode() : null;
     String otherCourseCode = grade.course != null ? grade.course.getCourseCode() : null;
     return Objects.equals(thisStudentId, otherStudentId) &&
            Objects.equals(thisCourseCode, otherCourseCode);
 }

 @Override
 public int hashCode() {
     String thisStudentId = student != null ? student.getStudentId() : null;
     String thisCourseCode = course != null ? course.getCourseCode() : null;
     return Objects.hash(thisStudentId, thisCourseCode);
 }

 /**
 * toString method for string representation
 * @return String representation of Grade
 */
 @Override
 public String toString() {
     return String.format("%s: %.2f (%s)",
         course != null ? course.getCourseCode() : "N/A", score, letterGrade);
 }
}