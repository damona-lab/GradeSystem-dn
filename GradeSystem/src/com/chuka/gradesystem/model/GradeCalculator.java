package com.chuka.gradesystem.model;
import java.util.List;
/**
 * GradeCalculator - Interface
 *
 * This interface demonstrates INTERFACE and ABSTRACTION in OOP.
 *
 * Key OOP Concepts:
 * 1. INTERFACE - Contract that defines what methods must be implemented
 * 2. ABSTRACTION - Defines WHAT to do, not HOW to do it
 * 3. MULTIPLE INHERITANCE - A class can implement multiple interfaces
 *
 * An interface is a completely abstract class that contains only abstract methods.
 * Any class that implements this interface MUST provide implementation for all methods.
 *
 * @author Charles David
 * @course ACSC 223 - Object Oriented Programming (Java 1)
 * @institution Chuka University
 */
public interface GradeCalculator {
 /**
 * Calculate GPA (Grade Point Average) for a list of grades
 * GPA = (Sum of (Grade Point * Credits)) / Total Credits
 *
 * @param grades List of grades
 * @return GPA value (0.0 - 4.0)
 */
 double calculateGPA(List<Grade> grades);
 /**
 * Calculate CGPA (Cumulative Grade Point Average)
 * CGPA is the overall GPA across all semesters
 *
 * @param student The student
 * @return CGPA value (0.0 - 4.0)
 */
 double calculateCGPA(Student student);
 /**
 * Get letter grade based on score
 *
 * @param score Numerical score (0-100)
 * @return Letter grade (A, B, C, D, F)
 */
 String getLetterGrade(double score);
 /**
 * Get grade point based on letter grade
 *
 * @param letterGrade Letter grade
 * @return Grade point (0.0 - 4.0)
 */
 double getGradePoint(String letterGrade);
 /**
 * Calculate total credits for a list of grades
 *
 * @param grades List of grades
 * @return Total credits
 */
 int calculateTotalCredits(List<Grade> grades);
 /**
 * Check if student is on honor roll (GPA >= 3.5)
 *
 * @param student The student
 * @return true if on honor roll, false otherwise
 */
 boolean isHonorRoll(Student student);
 /**
 * Get class standing based on GPA
 * First Class: GPA >= 3.5
 * Second Class Upper: GPA >= 3.0
 * Second Class Lower: GPA >= 2.5
 * Pass: GPA >= 2.0
 * Fail: GPA < 2.0
 *
 * @param gpa The GPA value
 * @return Class standing
 */
 String getClassStanding(double gpa);
}