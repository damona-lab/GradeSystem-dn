package com.chuka.gradesystem.service;
import com.chuka.gradesystem.model.*;
import java.util.List;
/**
 * GradeService - Implementation of GradeCalculator Interface
 *
 * This class demonstrates INTERFACE IMPLEMENTATION and POLYMORPHISM.
 *
 * Key OOP Concepts:
 * 1. INTERFACE IMPLEMENTATION - Implements all methods from GradeCalculator
 * 2. POLYMORPHISM - Can be used wherever GradeCalculator is expected
 * 3. ENCAPSULATION - Business logic encapsulated in methods
 *
 * @author Charles David
 * @course ACSC 223 - Object Oriented Programming (Java 1)
 * @institution Chuka University
 */
public class GradeService implements GradeCalculator {
 /**
 * Calculate GPA for a list of grades
 * Formula: GPA = Sum(Grade Point * Credits) / Total Credits
 *
 * @param grades List of grades
 * @return GPA value (0.0 - 4.0)
 */
 @Override
 public double calculateGPA(List<Grade> grades) {
 if (grades == null || grades.isEmpty()) {
 return 0.0;
 }
 double totalPoints = 0.0;
 int totalCredits = 0;
 for (Grade grade : grades) {
 Course course = grade.getCourse();
 if (course != null) {
 totalPoints += grade.getGradePoint() * course.getCredits();
 totalCredits += course.getCredits();
 }
 }
 return totalCredits > 0 ? totalPoints / totalCredits : 0.0;
 }
 /**
 * Calculate CGPA (Cumulative GPA) for a student
 * Uses all grades from the student's record
 *
 * @param student The student
 * @return CGPA value (0.0 - 4.0)
 */
 @Override
 public double calculateCGPA(Student student) {
 if (student == null) {
 return 0.0;
 }
 return calculateGPA(student.getGrades());
 }
 /**
 * Get letter grade based on numerical score
 * Grading Scale:
 * A: 70-100
 * B: 60-69
 * C: 50-59
 * D: 40-49
 * F: 0-39
 *
 * @param score Numerical score (0-100)
 * @return Letter grade
 */
 @Override
 public String getLetterGrade(double score) {
 if (score >= 70) {
 return "A";
 } else if (score >= 60) {
 return "B";
 } else if (score >= 50) {
 return "C";
 } else if (score >= 40) {
 return "D";
 } else {
 return "F";
 }
 }
 /**
 * Get grade point based on letter grade
 * Grade Points:
 * A: 4.0, B: 3.0, C: 2.0, D: 1.0, F: 0.0
 *
 * @param letterGrade Letter grade
 * @return Grade point
 */
 @Override
 public double getGradePoint(String letterGrade) {
 if (letterGrade == null) {
 return 0.0;
 }
 switch (letterGrade.toUpperCase()) {
 case "A":
 return 4.0;
 case "B":
 return 3.0;
 case "C":
 return 2.0;
 case "D":
 return 1.0;
 case "F":
 default:
 return 0.0;
 }
 }
 /**
 * Calculate total credits for a list of grades
 *
 * @param grades List of grades
 * @return Total credits
 */
 @Override
 public int calculateTotalCredits(List<Grade> grades) {
 if (grades == null || grades.isEmpty()) {
 return 0;
 }
 int total = 0;
 for (Grade grade : grades) {
 Course course = grade.getCourse();
 if (course != null) {
 total += course.getCredits();
 }
 }
 return total;
 }
 /**
 * Check if student is on honor roll
 * Honor Roll: GPA >= 3.5
 *
 * @param student The student
 * @return true if on honor roll, false otherwise
 */
 @Override
 public boolean isHonorRoll(Student student) {
 if (student == null) {
 return false;
 }
 double cgpa = calculateCGPA(student);
 return cgpa >= 3.5;
 }
 /**
 * Get class standing based on GPA
 * Classification:
 * First Class: GPA >= 3.5
 * Second Class Upper: GPA >= 3.0
 * Second Class Lower: GPA >= 2.5
 * Pass: GPA >= 2.0
 * Fail: GPA < 2.0
 *
 * @param gpa The GPA value
 * @return Class standing
 */
 @Override
 public String getClassStanding(double gpa) {
 if (gpa >= 3.5) {
 return "First Class";
 } else if (gpa >= 3.0) {
 return "Second Class Upper";
 } else if (gpa >= 2.5) {
 return "Second Class Lower";
 } else if (gpa >= 2.0) {
 return "Pass";
 } else {
 return "Fail";
 }
 }
 /**
 * Additional utility method: Get grade statistics
 * @param grades List of grades
 * @return Statistics string
 */
 public String getGradeStatistics(List<Grade> grades) {
 if (grades == null || grades.isEmpty()) {
 return "No grades available";
 }
 int countA = 0, countB = 0, countC = 0, countD = 0, countF = 0;
 double totalScore = 0.0;
 for (Grade grade : grades) {
 totalScore += grade.getScore();
 String letter = grade.getLetterGrade();
 switch (letter) {
 case "A": countA++; break;
 case "B": countB++; break;
 case "C": countC++; break;
 case "D": countD++; break;
 case "F": countF++; break;
 }
 }
 double averageScore = totalScore / grades.size();
 double gpa = calculateGPA(grades);
 StringBuilder stats = new StringBuilder();
 stats.append("=== GRADE STATISTICS ===\n");
 stats.append(String.format("Total Courses: %d\n", grades.size()));
 stats.append(String.format("Average Score: %.2f\n", averageScore));
 stats.append(String.format("GPA: %.2f\n", gpa));
 stats.append(String.format("Class Standing: %s\n", getClassStanding(gpa)));
 stats.append("\nGrade Distribution:\n");
 stats.append(String.format("A: %d\n", countA));
 stats.append(String.format("B: %d\n", countB));
 stats.append(String.format("C: %d\n", countC));
 stats.append(String.format("D: %d\n", countD));
 stats.append(String.format("F: %d\n", countF));
 return stats.toString();
 }
}