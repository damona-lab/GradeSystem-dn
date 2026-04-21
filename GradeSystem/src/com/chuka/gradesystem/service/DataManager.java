package com.chuka.gradesystem.service;
import com.chuka.gradesystem.model.*;
import java.util.List;
/**
 * DataManager - Interface for Data Persistence
 *
 * This interface demonstrates INTERFACE and ABSTRACTION for data management.
 *
 * Key OOP Concepts:
 * 1. INTERFACE - Defines contract for data operations
 * 2. ABSTRACTION - Hides implementation details
 * 3. POLYMORPHISM - Different implementations can be used interchangeably
 *
 * This interface can have multiple implementations:
 * - FileDataManager (file-based storage)
 * - DatabaseDataManager (database storage)
 * - CloudDataManager (cloud storage)
 *
 * @author Charles David
 * @course ACSC 223 - Object Oriented Programming (Java 1)
 * @institution Chuka University
 */
public interface DataManager {
 // ========== STUDENT OPERATIONS ==========
 /**
 * Save a student to storage
 * @param student Student to save
 * @return true if successful, false otherwise
 */
 boolean saveStudent(Student student);
 /**
 * Load all students from storage
 * @return List of all students
 */
 List<Student> loadAllStudents();
 /**
 * Find a student by ID
 * @param studentId Student ID
 * @return Student object or null if not found
 */
 Student findStudentById(String studentId);
 /**
 * Update an existing student
 * @param student Student with updated information
 * @return true if successful, false otherwise
 */
 boolean updateStudent(Student student);
 /**
 * Delete a student
 * @param studentId Student ID
 * @return true if successful, false otherwise
 */
 boolean deleteStudent(String studentId);
 // ========== COURSE OPERATIONS ==========
 /**
 * Save a course to storage
 * @param course Course to save
 * @return true if successful, false otherwise
 */
 boolean saveCourse(Course course);
 /**
 * Load all courses from storage
 * @return List of all courses
 */
 List<Course> getAllCourses();
 /**
 * Find a course by code
 * @param courseCode Course code
 * @return Course object or null if not found
 */
 Course findCourseByCode(String courseCode);
 /**
 * Update an existing course
 * @param course Course with updated information
 * @return true if successful, false otherwise
 */
 boolean updateCourse(Course course);
 /**
 * Delete a course
 * @param courseCode Course code
 * @return true if successful, false otherwise
 */
 boolean deleteCourse(String courseCode);
 // ========== GRADE OPERATIONS ==========
 /**
 * Save a grade to storage
 * @param grade Grade to save
 * @return true if successful, false otherwise
 */
 boolean saveGrade(Grade grade);
 /**
 * Load all grades from storage
 * @return List of all grades
 */
 List<Grade> loadAllGrades();
 /**
 * Get all grades for a specific student
 * @param studentId Student ID
 * @return List of grades for the student
 */
 List<Grade> getGradesByStudent(String studentId);
 /**
 * Get all grades for a specific course
 * @param courseCode Course code
 * @return List of grades for the course
 */
 List<Grade> getGradesByCourse(String courseCode);
 /**
 * Delete a grade
 * @param student Student
 * @param course Course
 * @return true if successful, false otherwise
 */
 boolean deleteGrade(Student student, Course course);
 // ========== UTILITY OPERATIONS ==========
 /**
 * Save all data to storage
 * @return true if successful, false otherwise
 */
 boolean saveAllData();
 /**
 * Load all data from storage
 * @return true if successful, false otherwise
 */
 boolean loadAllData();
 /**
 * Clear all data (use with caution!)
 * @return true if successful, false otherwise
 */
 boolean clearAllData();
/**
 * Add a new course to storage
 * @param course Course to save
 * @return true if successful, false otherwise
 */

/**
 * Retrieve all courses from storage
 * @return List of all courses
 */



}