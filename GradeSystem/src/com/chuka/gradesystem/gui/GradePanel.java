package com.chuka.gradesystem.gui;

import com.chuka.gradesystem.model.*;
import com.chuka.gradesystem.service.*;
import com.chuka.gradesystem.util.Constants;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.*;
import java.text.DecimalFormat;
import java.util.List;

/**
 * GradePanel - Grade Management Interface
 *
 * Required Features:
 * - JTable to display grades
 * - Form with: Student (JComboBox), Course (JComboBox), Score (JTextField), Semester (JComboBox)
 * - Automatically calculate and display Letter Grade and Grade Point
 * - Buttons: Add Grade, Update, Delete, Calculate GPA
 * - Display student's current GPA
 *
 * @author Charles David
 * @course ACSC 223 - Object Oriented Programming (Java 1)
 */
public class GradePanel extends JPanel {
    private DataManager dataManager;
    private GradeService gradeService;
    
    // Table components
    private JTable gradeTable;
    private DefaultTableModel tableModel;
    private JScrollPane tableScrollPane;
    
    // Form components
    private JComboBox<Student> studentComboBox;
    private JComboBox<Course> courseComboBox;
    private JTextField scoreField;
    private JComboBox<String> semesterComboBox;
    private JLabel letterGradeLabel;
    private JLabel gradePointLabel;
    private JLabel gpaLabel;
    
    // Buttons
    private JButton addButton;
    private JButton updateButton;
    private JButton deleteButton;
    private JButton calculateGPAButton;
    private JButton clearButton;
    
    // Current selection
    private int selectedRow = -1;
    
    public GradePanel(DataManager dataManager, GradeService gradeService) {
        this.dataManager = dataManager;
        this.gradeService = gradeService;
        
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Initialize components
        initializeComponents();
        
        // Build UI
        buildUI();
        
        // Load initial data
        loadData();
        
        // Refresh table
        refreshTable();
    }
    
    private void initializeComponents() {
        // Create table model with columns
        String[] columns = {"Student", "Course", "Score", "Letter Grade", 
                           "Grade Point", "Semester", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table non-editable
            }
        };
        
        // Create table
        gradeTable = new JTable(tableModel);
        gradeTable.setRowHeight(25);
        gradeTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        gradeTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                handleTableSelection();
            }
        });
        
        // Style table header
        JTableHeader header = gradeTable.getTableHeader();
        header.setFont(new Font("Arial", Font.BOLD, 12));
        header.setBackground(new Color(70, 130, 180));
        header.setForeground(Color.black);
        
        // Create scroll pane for table
        tableScrollPane = new JScrollPane(gradeTable);
        tableScrollPane.setPreferredSize(new Dimension(800, 300));
        
        // Create form components
        studentComboBox = new JComboBox<>();
        courseComboBox = new JComboBox<>();
        scoreField = new JTextField(10);
        
        // Initialize semester combobox
        semesterComboBox = new JComboBox<>(Constants.SEMESTERS);
        
        // Labels for calculated values
        letterGradeLabel = new JLabel("-");
        letterGradeLabel.setFont(new Font("Arial", Font.BOLD, 14));
        letterGradeLabel.setForeground(new Color(0, 0, 0));
        
        gradePointLabel = new JLabel("-");
        gradePointLabel.setFont(new Font("Arial", Font.BOLD, 14));
        gradePointLabel.setForeground(new Color(0, 0, 0));
        
        gpaLabel = new JLabel("Select a student to calculate GPA");
        gpaLabel.setFont(new Font("Arial", Font.BOLD, 14));
        gpaLabel.setForeground(new Color(0, 0, 139));
        
        // Create buttons
        addButton = new JButton("Add Grade");
        updateButton = new JButton("Update");
        deleteButton = new JButton("Delete");
        calculateGPAButton = new JButton("Calculate GPA");
        clearButton = new JButton("Clear");
        
        // Style buttons
        Color buttonColor = new Color(70, 130, 180);
        Color buttonTextColor = Color.black;
        JButton[] buttons = {addButton, updateButton, deleteButton, calculateGPAButton, clearButton};
        for (JButton btn : buttons) {
            btn.setBackground(buttonColor);
            btn.setForeground(buttonTextColor);
            btn.setFocusPainted(false);
        }
        
        // Add action listeners
        addButton.addActionListener(e -> addGrade());
        updateButton.addActionListener(e -> updateGrade());
        deleteButton.addActionListener(e -> deleteGrade());
        calculateGPAButton.addActionListener(e -> calculateGPA());
        clearButton.addActionListener(e -> clearForm());
        
        // Add key listener to score field for auto-calculation
        scoreField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                calculateGradeFromScore();
            }
        });
        
        // Add listener to student combobox to auto-calculate GPA
        studentComboBox.addActionListener(e -> {
            Student selected = (Student) studentComboBox.getSelectedItem();
            if (selected != null) {
                updateGpaLabel(selected);
            }
        });
    }
    
    private void buildUI() {
        // Create main panel with BorderLayout
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        
        // Top panel for GPA display
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        topPanel.add(gpaLabel);
        mainPanel.add(topPanel, BorderLayout.NORTH);
        
        // Center panel for table
        mainPanel.add(tableScrollPane, BorderLayout.CENTER);
        
        // Bottom panel for form and buttons
        JPanel bottomPanel = new JPanel(new BorderLayout(10, 10));
        
        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Grade Entry Form"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Row 1: Student
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Student:"), gbc);
        gbc.gridx = 1;
        formPanel.add(studentComboBox, gbc);
        
        // Row 2: Course
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Course:"), gbc);
        gbc.gridx = 1;
        formPanel.add(courseComboBox, gbc);
        
        // Row 3: Score
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Score (0-100):"), gbc);
        gbc.gridx = 1;
        formPanel.add(scoreField, gbc);
        
        // Row 4: Semester
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Semester:"), gbc);
        gbc.gridx = 1;
        formPanel.add(semesterComboBox, gbc);
        
        // Row 5: Letter Grade
        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(new JLabel("Letter Grade:"), gbc);
        gbc.gridx = 1;
        formPanel.add(letterGradeLabel, gbc);
        
        // Row 6: Grade Point
        gbc.gridx = 0; gbc.gridy = 5;
        formPanel.add(new JLabel("Grade Point:"), gbc);
        gbc.gridx = 1;
        formPanel.add(gradePointLabel, gbc);
        
        bottomPanel.add(formPanel, BorderLayout.CENTER);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(calculateGPAButton);
        buttonPanel.add(clearButton);
        
        bottomPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
    }
    
    private void loadData() {
        // Load students into combobox using DataManager interface
        List<Student> students = dataManager.loadAllStudents();
        studentComboBox.removeAllItems();
        for (Student student : students) {
            studentComboBox.addItem(student);
        }
        
        // Load courses into combobox using DataManager interface
        List<Course> courses = dataManager.getAllCourses();
        courseComboBox.removeAllItems();
        for (Course course : courses) {
            courseComboBox.addItem(course);
        }
    }
    
    private void calculateGradeFromScore() {
        try {
            String scoreText = scoreField.getText().trim();
            if (!scoreText.isEmpty()) {
                double score = Double.parseDouble(scoreText);
                if (score >= 0 && score <= 100) {
                    String letterGrade = gradeService.getLetterGrade(score);
                    double gradePoint = gradeService.getGradePoint(letterGrade);
                    
                    letterGradeLabel.setText(letterGrade);
                    gradePointLabel.setText(String.format("%.1f", gradePoint));
                } else {
                    letterGradeLabel.setText("Invalid");
                    gradePointLabel.setText("Invalid");
                }
            }
        } catch (NumberFormatException e) {
            letterGradeLabel.setText("Invalid");
            gradePointLabel.setText("Invalid");
        }
    }
    
    private void addGrade() {
        try {
            // Validate selection
            Student student = (Student) studentComboBox.getSelectedItem();
            Course course = (Course) courseComboBox.getSelectedItem();
            String semester = (String) semesterComboBox.getSelectedItem();
            
            if (student == null || course == null) {
                JOptionPane.showMessageDialog(this, "Please select both student and course", 
                    "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Validate score
            double score;
            try {
                score = Double.parseDouble(scoreField.getText().trim());
                if (score < 0 || score > 100) {
                    throw new NumberFormatException();
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Please enter a valid score between 0 and 100", 
                    "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Create new grade
            Grade newGrade = new Grade(student, course, score, semester);
            
            // Save grade using DataManager interface
            boolean success = dataManager.saveGrade(newGrade);
            
            if (success) {
                // Save all data to persist changes
                dataManager.saveAllData();
                
                // Refresh table
                refreshTable();
                
                // Clear form
                clearForm();
                
                // Update GPA label
                updateGpaLabel(student);
                
                JOptionPane.showMessageDialog(this, "Grade added successfully!", 
                    "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Failed to add grade. Please try again.", 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error adding grade: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void updateGrade() {
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a grade to update from the table", 
                "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            Student student = (Student) studentComboBox.getSelectedItem();
            Course course = (Course) courseComboBox.getSelectedItem();
            String semester = (String) semesterComboBox.getSelectedItem();
            
            if (student == null || course == null) {
                JOptionPane.showMessageDialog(this, "Please select both student and course", 
                    "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Validate score
            double score;
            try {
                score = Double.parseDouble(scoreField.getText().trim());
                if (score < 0 || score > 100) {
                    throw new NumberFormatException();
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Please enter a valid score between 0 and 100", 
                    "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Get selected grade information from table
            String studentName = (String) tableModel.getValueAt(selectedRow, 0);
            String courseCode = (String) tableModel.getValueAt(selectedRow, 1);
            
            // Find the student and course objects
            Student oldStudent = null;
            Course oldCourse = null;
            
            // Find the original student
            List<Student> allStudents = dataManager.loadAllStudents();
            for (Student s : allStudents) {
                if (s.getName().equals(studentName)) {
                    oldStudent = s;
                    break;
                }
            }
            
            // Find the original course
            List<Course> allCourses = dataManager.getAllCourses();
            for (Course c : allCourses) {
                if (c.getCourseCode().equals(courseCode)) {
                    oldCourse = c;
                    break;
                }
            }
            
            if (oldStudent == null || oldCourse == null) {
                JOptionPane.showMessageDialog(this, "Could not find the original grade to update", 
                    "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Delete the old grade using DataManager interface
            boolean deleteSuccess = dataManager.deleteGrade(oldStudent, oldCourse);
            
            if (deleteSuccess) {
                // Create new grade with updated information
                Grade updatedGrade = new Grade(student, course, score, semester);
                
                // Save the updated grade using DataManager interface
                boolean saveSuccess = dataManager.saveGrade(updatedGrade);
                
                if (saveSuccess) {
                    // Save all data to persist changes
                    dataManager.saveAllData();
                    
                    // Refresh table
                    refreshTable();
                    
                    // Clear selection and form
                    gradeTable.clearSelection();
                    selectedRow = -1;
                    clearForm();
                    
                    // Update GPA label
                    updateGpaLabel(student);
                    
                    JOptionPane.showMessageDialog(this, "Grade updated successfully!", 
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to save updated grade", 
                        "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete old grade", 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error updating grade: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void deleteGrade() {
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a grade to delete from the table", 
                "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to delete this grade?", 
            "Confirm Delete", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                // Get selected grade information from table
                String studentName = (String) tableModel.getValueAt(selectedRow, 0);
                String courseCode = (String) tableModel.getValueAt(selectedRow, 1);
                
                // Find the student and course objects
                Student studentToDelete = null;
                Course courseToDelete = null;
                
                // Find the student
                List<Student> allStudents = dataManager.loadAllStudents();
                for (Student s : allStudents) {
                    if (s.getName().equals(studentName)) {
                        studentToDelete = s;
                        break;
                    }
                }
                
                // Find the course
                List<Course> allCourses = dataManager.getAllCourses();
                for (Course c : allCourses) {
                    if (c.getCourseCode().equals(courseCode)) {
                        courseToDelete = c;
                        break;
                    }
                }
                
                if (studentToDelete == null || courseToDelete == null) {
                    JOptionPane.showMessageDialog(this, "Could not find the grade to delete", 
                        "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // Delete grade using DataManager interface
                boolean success = dataManager.deleteGrade(studentToDelete, courseToDelete);
                
                if (success) {
                    // Save all data to persist changes
                    dataManager.saveAllData();
                    
                    // Refresh table
                    refreshTable();
                    
                    // Clear selection and form
                    gradeTable.clearSelection();
                    selectedRow = -1;
                    clearForm();
                    
                    // Update GPA label for current student
                    Student currentStudent = (Student) studentComboBox.getSelectedItem();
                    if (currentStudent != null) {
                        updateGpaLabel(currentStudent);
                    }
                    
                    JOptionPane.showMessageDialog(this, "Grade deleted successfully!", 
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to delete grade. Please try again.", 
                        "Error", JOptionPane.ERROR_MESSAGE);
                }
                
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error deleting grade: " + e.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void calculateGPA() {
        Student student = (Student) studentComboBox.getSelectedItem();
        if (student == null) {
            JOptionPane.showMessageDialog(this, "Please select a student", 
                "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            // Get student's grades using DataManager interface
            List<Grade> studentGrades = dataManager.getGradesByStudent(student.getStudentId());
            
            if (studentGrades == null || studentGrades.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No grades found for this student", 
                    "Information", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            
            // Calculate GPA
            double gpa = gradeService.calculateGPA(studentGrades);
            DecimalFormat df = new DecimalFormat("0.00");
            
            // Update GPA label
            gpaLabel.setText(String.format("GPA for %s: %s", student.getName(), df.format(gpa)));
            
            // Show detailed GPA info
            String message = String.format("Student: %s\nGPA: %.2f\nClass Standing: %s\nTotal Credits: %d",
                student.getName(), gpa, gradeService.getClassStanding(gpa),
                gradeService.calculateTotalCredits(studentGrades));
            
            if (gpa >= Constants.HONOR_ROLL_MIN) {
                message += "\n\n🎉 Congratulations! This student is on the Honor Roll!";
            }
            
            JOptionPane.showMessageDialog(this, message, 
                "GPA Calculation", JOptionPane.INFORMATION_MESSAGE);
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error calculating GPA: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void updateGpaLabel(Student student) {
        if (student == null) {
            gpaLabel.setText("Select a student to calculate GPA");
            return;
        }
        
        try {
            // Get student's grades using DataManager interface
            List<Grade> studentGrades = dataManager.getGradesByStudent(student.getStudentId());
            
            if (studentGrades == null || studentGrades.isEmpty()) {
                gpaLabel.setText("GPA for " + student.getName() + ": No grades yet");
                return;
            }
            
            // Calculate GPA
            double gpa = gradeService.calculateGPA(studentGrades);
            DecimalFormat df = new DecimalFormat("0.00");
            
            gpaLabel.setText(String.format("GPA for %s: %s (Class: %s)", 
                student.getName(), df.format(gpa), gradeService.getClassStanding(gpa)));
            
        } catch (Exception e) {
            gpaLabel.setText("Error calculating GPA for " + student.getName());
        }
    }
    
    private void handleTableSelection() {
        selectedRow = gradeTable.getSelectedRow();
        if (selectedRow != -1) {
            try {
                // Get values from selected row
                String studentName = (String) tableModel.getValueAt(selectedRow, 0);
                String courseCode = (String) tableModel.getValueAt(selectedRow, 1);
                double score = Double.parseDouble(tableModel.getValueAt(selectedRow, 2).toString());
                String semester = (String) tableModel.getValueAt(selectedRow, 5);
                
                // Find student in combobox
                for (int i = 0; i < studentComboBox.getItemCount(); i++) {
                    Student student = studentComboBox.getItemAt(i);
                    if (student != null && (student.getName().equals(studentName) || studentName.equals(student.getStudentId()))) {
                        studentComboBox.setSelectedIndex(i);
                        break;
                    }
                }
                
                // Find course in combobox
                for (int i = 0; i < courseComboBox.getItemCount(); i++) {
                    Course course = courseComboBox.getItemAt(i);
                    if (course.getCourseCode().equals(courseCode)) {
                        courseComboBox.setSelectedIndex(i);
                        break;
                    }
                }
                
                // Set other fields
                scoreField.setText(String.format("%.2f", score));
                semesterComboBox.setSelectedItem(semester);
                
                // Calculate and display grades
                calculateGradeFromScore();
                
            } catch (Exception e) {
                // Handle any errors in selection
                System.err.println("Error handling table selection: " + e.getMessage());
            }
        }
    }
    
    private void clearForm() {
        if (studentComboBox.getItemCount() > 0) {
            studentComboBox.setSelectedIndex(0);
        }
        if (courseComboBox.getItemCount() > 0) {
            courseComboBox.setSelectedIndex(0);
        }
        scoreField.setText("");
        if (semesterComboBox.getItemCount() > 0) {
            semesterComboBox.setSelectedIndex(0);
        }
        letterGradeLabel.setText("-");
        gradePointLabel.setText("-");
        gradeTable.clearSelection();
        selectedRow = -1;
    }
    
    public void refreshData() {
        loadData();
        refreshTable();
        clearForm();
        
        // Update GPA label if a student is selected
        Student selectedStudent = (Student) studentComboBox.getSelectedItem();
        if (selectedStudent != null) {
            updateGpaLabel(selectedStudent);
        }
    }
    
    private void refreshTable() {
        // Clear existing rows
        tableModel.setRowCount(0);
        
        // Get grades from DataManager interface
        List<Grade> grades = dataManager.loadAllGrades();
        
        if (grades == null || grades.isEmpty()) {
            return;
        }
        
        // Add grades to table
        for (Grade grade : grades) {
            Student student = grade.getStudent();
            Course course = grade.getCourse();
            
            String status = grade.getScore() >= 40 ? "PASS" : "FAIL";
            
            Object[] row = {
                student != null ? student.getName() : "Unknown",
                course != null ? course.getCourseCode() : "Unknown",
                grade.getScore(),
                grade.getLetterGrade(),
                grade.getGradePoint(),
                grade.getSemester(),
                status
            };
            
            tableModel.addRow(row);
        }
        
        // Apply custom renderer for status column
        gradeTable.getColumnModel().getColumn(6).setCellRenderer(new StatusCellRenderer());
    }
    
    // Custom cell renderer for status column
    private class StatusCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            
            if (column == 6) { // Status column
                String status = (String) value;
                if ("PASS".equals(status)) {
                    c.setForeground(new Color(0, 100, 0)); // Dark green
                    c.setFont(c.getFont().deriveFont(Font.BOLD));
                } else if ("FAIL".equals(status)) {
                    c.setForeground(Color.RED);
                    c.setFont(c.getFont().deriveFont(Font.BOLD));
                }
            }
            
            return c;
        }
    }
}