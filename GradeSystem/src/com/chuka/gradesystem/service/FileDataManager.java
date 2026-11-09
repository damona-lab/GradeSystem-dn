package com.chuka.gradesystem.service;

import com.chuka.gradesystem.model.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * FileDataManager - File-based Implementation of DataManager Interface
 *
 * This class demonstrates INTERFACE IMPLEMENTATION and FILE I/O.
 *
 * Key OOP Concepts:
 * 1. INTERFACE IMPLEMENTATION - Implements all DataManager methods
 * 2. POLYMORPHISM - Can be used wherever DataManager is expected
 * 3. ENCAPSULATION - File operations encapsulated in private methods
 * 4. SERIALIZATION - Objects are saved to files using Java serialization
 *
 * @author Charles David
 * @course ACSC 223 - Object Oriented Programming (Java 1)
 * @institution Chuka University
 */
public class FileDataManager implements DataManager {
    // File paths for data storage
    private static final String DATA_DIR = resolveDataDir();
    private static final String STUDENTS_FILE = DATA_DIR + "students.dat";
    private static final String COURSES_FILE = DATA_DIR + "courses.dat";
    private static final String GRADES_FILE = DATA_DIR + "grades.dat";

    private static String resolveDataDir() {
        File localData = new File("data");
        File gsData = new File("GradeSystem/data");
        if (!localData.exists() && gsData.exists()) {
            return "GradeSystem/data/";
        }
        return "data/";
    }
    
    // In-memory storage (loaded from files)
    private List<Student> students;
    private List<Course> courses;
    private List<Grade> grades;
    
    /**
     * Constructor - Initialize data structures and load data
     */
    public FileDataManager() {
        students = new ArrayList<>();
        courses = new ArrayList<>();
        grades = new ArrayList<>();
        
        // Create data directory if it doesn't exist
        File dataDir = new File(DATA_DIR);
        if (!dataDir.exists()) {
            dataDir.mkdirs();
        }
        
        // Load existing data
        loadAllData();
        
        // After loading, sync student grades
        syncStudentGrades();
    }
    
    /**
     * Sync grades to students after loading data
     */
    private void syncStudentGrades() {
        // Clear all student grades first
        for (Student student : students) {
            student.getGrades().clear();
        }
        
        // Link actual instances and add grades to respective students
        for (Grade grade : grades) {
            if (grade.getStudent() != null) {
                Student actualStudent = findStudentById(grade.getStudent().getStudentId());
                if (actualStudent != null) {
                    grade.setStudent(actualStudent);
                }
            }
            if (grade.getCourse() != null) {
                Course actualCourse = findCourseByCode(grade.getCourse().getCourseCode());
                if (actualCourse != null) {
                    grade.setCourse(actualCourse);
                }
            }
            if (grade.getStudent() != null) {
                grade.getStudent().addGrade(grade);
            }
        }
    }
    
    // ========== STUDENT OPERATIONS ==========
    @Override
    public boolean saveStudent(Student student) {
        if (student == null) {
            return false;
        }
        
        // Check if student already exists
        Student existing = findStudentById(student.getStudentId());
        if (existing != null) {
            // Update existing student
            return updateStudent(student);
        }
        
        students.add(student);
        return saveStudentsToFile();
    }
    
    @Override
    public List<Student> loadAllStudents() {
        return new ArrayList<>(students);
    }
    
    @Override
    public Student findStudentById(String studentId) {
        for (Student student : students) {
            if (student.getStudentId().equals(studentId)) {
                return student;
            }
        }
        return null;
    }
    
    @Override
    public boolean updateStudent(Student student) {
        if (student == null) {
            return false;
        }
        
        for (int i = 0; i < students.size(); i++) {
            if (students.get(i).getStudentId().equals(student.getStudentId())) {
                students.set(i, student);
                return saveStudentsToFile();
            }
        }
        return false;
    }
    
    @Override
    public boolean deleteStudent(String studentId) {
        boolean removed = students.removeIf(s -> s.getStudentId().equals(studentId));
        if (removed) {
            // Also remove all grades for this student
            grades.removeIf(g -> g.getStudent() != null && studentId.equals(g.getStudent().getStudentId()));
            saveGradesToFile();
            return saveStudentsToFile();
        }
        return false;
    }
    
    // ========== COURSE OPERATIONS ==========
    @Override
    public boolean saveCourse(Course course) {
        if (course == null) {
            return false;
        }
        
        Course existing = findCourseByCode(course.getCourseCode());
        if (existing != null) {
            // Course with this code already exists - update it
            return updateCourse(course);
        }
        
        courses.add(course);
        return saveCoursesToFile();
    }
    
    @Override
    public List<Course> getAllCourses() {
        return new ArrayList<>(courses);
    }
    
    @Override
    public Course findCourseByCode(String courseCode) {
        for (Course course : courses) {
            if (course.getCourseCode().equals(courseCode)) {
                return course;
            }
        }
        return null;
    }
    
    @Override
    public boolean updateCourse(Course course) {
        if (course == null) {
            return false;
        }
        
        // Find the course by its code
        for (int i = 0; i < courses.size(); i++) {
            if (courses.get(i).getCourseCode().equals(course.getCourseCode())) {
                courses.set(i, course);
                
                // Update the course reference in all grades
                for (Grade grade : grades) {
                    if (grade.getCourse().getCourseCode().equals(course.getCourseCode())) {
                        grade.setCourse(course);
                    }
                }
                
                saveGradesToFile();
                return saveCoursesToFile();
            }
        }
        
        // Course not found - add as new
        courses.add(course);
        return saveCoursesToFile();
    }
    
    /**
     * Helper method to update course when course code changes
     * This is not in the DataManager interface, but useful for GUI
     */
    public boolean updateCourseWithCodeChange(String oldCourseCode, Course newCourse) {
        if (newCourse == null || oldCourseCode == null) {
            return false;
        }
        
        // Check if new course code already exists (and it's not the old course)
        Course existingWithNewCode = findCourseByCode(newCourse.getCourseCode());
        if (existingWithNewCode != null && !existingWithNewCode.getCourseCode().equals(oldCourseCode)) {
            return false; // New course code already exists
        }
        
        // Find the course by old code
        for (int i = 0; i < courses.size(); i++) {
            if (courses.get(i).getCourseCode().equals(oldCourseCode)) {
                // Remove the old course
                courses.remove(i);
                // Add the new course
                courses.add(newCourse);
                
                // Update all grades that reference this course
                for (Grade grade : grades) {
                    if (grade.getCourse().getCourseCode().equals(oldCourseCode)) {
                        grade.setCourse(newCourse);
                    }
                }
                
                saveGradesToFile();
                return saveCoursesToFile();
            }
        }
        
        return false;
    }
    
    @Override
    public boolean deleteCourse(String courseCode) {
        boolean removed = courses.removeIf(c -> c.getCourseCode().equals(courseCode));
        if (removed) {
            // Also remove all grades for this course
            grades.removeIf(g -> g.getCourse() != null && courseCode.equals(g.getCourse().getCourseCode()));
            saveGradesToFile();
            return saveCoursesToFile();
        }
        return false;
    }
    
    // ========== GRADE OPERATIONS ==========
    @Override
    public boolean saveGrade(Grade grade) {
        if (grade == null || grade.getStudent() == null || grade.getCourse() == null) {
            return false;
        }
        
        // Check if student and course exist
        Student student = findStudentById(grade.getStudent().getStudentId());
        Course course = findCourseByCode(grade.getCourse().getCourseCode());
        
        if (student == null || course == null) {
            return false; // Can't save grade for non-existent student or course
        }
        
        // Remove existing grade for same student and course
        grades.removeIf(g ->
            g.getStudent() != null && g.getCourse() != null &&
            g.getStudent().getStudentId().equals(grade.getStudent().getStudentId()) &&
            g.getCourse().getCourseCode().equals(grade.getCourse().getCourseCode())
        );
        
        // Create a new grade with actual student and course objects (not the passed references)
        Grade newGrade = new Grade(student, course, grade.getScore(), grade.getSemester());
        grades.add(newGrade);
        
        // Also add the grade to the student's internal list for GPA calculations
        // First remove any existing grade for this course from student's list
        student.getGrades().removeIf(g -> 
            g.getCourse() != null && g.getCourse().getCourseCode().equals(course.getCourseCode())
        );
        // Add the new grade
        student.addGrade(newGrade);
        
        // Update the student to persist changes
        updateStudent(student);
        
        return saveGradesToFile();
    }
    
    @Override
    public List<Grade> loadAllGrades() {
        return new ArrayList<>(grades);
    }
    
    @Override
    public List<Grade> getGradesByStudent(String studentId) {
        if (studentId == null) {
            return new ArrayList<>();
        }
        
        return grades.stream()
            .filter(g -> g.getStudent() != null && studentId.equals(g.getStudent().getStudentId()))
            .collect(Collectors.toList());
    }
    
    /**
     * Get grades for student with proper student object references
     * This ensures the student object in the grade has all its data
     */
    public List<Grade> getSyncedGradesByStudent(String studentId) {
        Student student = findStudentById(studentId);
        if (student == null) {
            return new ArrayList<>();
        }
        
        return student.getGrades(); // This returns grades with proper student references
    }
    
    @Override
    public List<Grade> getGradesByCourse(String courseCode) {
        if (courseCode == null) {
            return new ArrayList<>();
        }
        
        return grades.stream()
            .filter(g -> g.getCourse() != null && courseCode.equals(g.getCourse().getCourseCode()))
            .collect(Collectors.toList());
    }
    
    @Override
    public boolean deleteGrade(Student student, Course course) {
        if (student == null || course == null) {
            return false;
        }
        
        boolean removed = grades.removeIf(g ->
            g.getStudent() != null && 
            g.getCourse() != null &&
            g.getStudent().getStudentId().equals(student.getStudentId()) &&
            g.getCourse().getCourseCode().equals(course.getCourseCode())
        );
        
        if (removed) {
            // Also remove the grade from the student's internal list
            Student actualStudent = findStudentById(student.getStudentId());
            if (actualStudent != null) {
                actualStudent.getGrades().removeIf(g -> 
                    g.getCourse().getCourseCode().equals(course.getCourseCode())
                );
                updateStudent(actualStudent);
            }
            
            return saveGradesToFile();
        }
        
        return false;
    }
    
    // ========== UTILITY OPERATIONS ==========
    @Override
    public boolean saveAllData() {
        boolean success = true;
        success &= saveStudentsToFile();
        success &= saveCoursesToFile();
        success &= saveGradesToFile();
        return success;
    }
    
    @Override
    public boolean loadAllData() {
        boolean success = true;
        success &= loadStudentsFromFile();
        success &= loadCoursesFromFile();
        success &= loadGradesFromFile();
        
        // After loading, sync student grades
        syncStudentGrades();
        
        return success;
    }
    
    @Override
    public boolean clearAllData() {
        students.clear();
        courses.clear();
        grades.clear();
        return saveAllData();
    }
    
    // ========== PRIVATE FILE I/O METHODS ==========
    private void mirrorSave(String fileName, Object data) {
        if (!DATA_DIR.startsWith("GradeSystem") && new File("GradeSystem/data").isDirectory()) {
            try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(new File("GradeSystem/data", fileName)))) {
                oos.writeObject(data);
            } catch (IOException ignored) {}
        }
    }

    /**
     * Save students to file using serialization
     * @return true if successful
     */
    private boolean saveStudentsToFile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(
            new FileOutputStream(STUDENTS_FILE))) {
            oos.writeObject(students);
            mirrorSave("students.dat", students);
            return true;
        } catch (IOException e) {
            System.err.println("Error saving students: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Load students from file
     * @return true if successful
     */
    @SuppressWarnings("unchecked")
    private boolean loadStudentsFromFile() {
        File file = new File(STUDENTS_FILE);
        if (!file.exists()) {
            return true; // No file yet, not an error
        }
        
        try (ObjectInputStream ois = new ObjectInputStream(
            new FileInputStream(STUDENTS_FILE))) {
            Object obj = ois.readObject();
            if (obj instanceof List) {
                students = (List<Student>) obj;
            }
            return true;
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading students: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Save courses to file
     * @return true if successful
     */
    private boolean saveCoursesToFile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(
            new FileOutputStream(COURSES_FILE))) {
            oos.writeObject(courses);
            mirrorSave("courses.dat", courses);
            return true;
        } catch (IOException e) {
            System.err.println("Error saving courses: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Load courses from file
     * @return true if successful
     */
    @SuppressWarnings("unchecked")
    private boolean loadCoursesFromFile() {
        File file = new File(COURSES_FILE);
        if (!file.exists()) {
            return true;
        }
        
        try (ObjectInputStream ois = new ObjectInputStream(
            new FileInputStream(COURSES_FILE))) {
            Object obj = ois.readObject();
            if (obj instanceof List) {
                courses = (List<Course>) obj;
            }
            return true;
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading courses: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Save grades to file
     * @return true if successful
     */
    private boolean saveGradesToFile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(
            new FileOutputStream(GRADES_FILE))) {
            oos.writeObject(grades);
            mirrorSave("grades.dat", grades);
            return true;
        } catch (IOException e) {
            System.err.println("Error saving grades: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Load grades from file
     * @return true if successful
     */
    @SuppressWarnings("unchecked")
    private boolean loadGradesFromFile() {
        File file = new File(GRADES_FILE);
        if (!file.exists()) {
            return true;
        }
        
        try (ObjectInputStream ois = new ObjectInputStream(
            new FileInputStream(GRADES_FILE))) {
            Object obj = ois.readObject();
            if (obj instanceof List) {
                grades = (List<Grade>) obj;
            }
            return true;
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading grades: " + e.getMessage());
            return false;
        }
    }
    
    // ========== ADDITIONAL HELPER METHODS ==========
    
    /**
     * Validate if a grade can be saved (student and course must exist)
     */
    public boolean canSaveGrade(Grade grade) {
        if (grade == null || grade.getStudent() == null || grade.getCourse() == null) {
            return false;
        }
        
        Student student = findStudentById(grade.getStudent().getStudentId());
        Course course = findCourseByCode(grade.getCourse().getCourseCode());
        
        return student != null && course != null;
    }
    
    /**
     * Get statistics about the data
     */
    public String getDataStatistics() {
        StringBuilder stats = new StringBuilder();
        stats.append("=== DATA STATISTICS ===\n");
        stats.append(String.format("Students: %d\n", students.size()));
        stats.append(String.format("Courses: %d\n", courses.size()));
        stats.append(String.format("Grades: %d\n", grades.size()));
        
        // Calculate average grades per student
        if (!students.isEmpty()) {
            double avgGradesPerStudent = (double) grades.size() / students.size();
            stats.append(String.format("Average grades per student: %.1f\n", avgGradesPerStudent));
        }
        
        // Calculate average grades per course
        if (!courses.isEmpty()) {
            double avgGradesPerCourse = (double) grades.size() / courses.size();
            stats.append(String.format("Average grades per course: %.1f\n", avgGradesPerCourse));
        }
        
        return stats.toString();
    }
}