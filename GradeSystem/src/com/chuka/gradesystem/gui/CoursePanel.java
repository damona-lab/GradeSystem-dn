package com.chuka.gradesystem.gui;

import com.chuka.gradesystem.service.DataManager;
import com.chuka.gradesystem.model.Course;
import com.chuka.gradesystem.util.ValidationUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

/**
 * CoursePanel - Course Management Interface
 *
 * Required Features:
 * - JTable to display courses
 * - Form to add/edit courses (Course Code, Name, Credits, Instructor)
 * - Buttons: Add, Update, Delete, Clear
 * - Search functionality
 * - Input validation using ValidationUtil
 *
 * @author Charles David
 * @course ACSC 223 - Object Oriented Programming (Java 1)
 */
public class CoursePanel extends JPanel {
    private DataManager dataManager;
    
    // GUI Components
    private JTable courseTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JTextField codeField, nameField, creditsField, instructorField;
    private JButton addButton, updateButton, deleteButton, clearButton;
    private JLabel statusLabel;
    
    // Track original course code for updates
    private String originalCourseCode = "";
    
    public CoursePanel(DataManager dataManager) {
        this.dataManager = dataManager;
        setLayout(new BorderLayout(10, 10));
        initializeComponents();
        loadCourseData();
    }
    
    /**
     * Initialize all GUI components
     */
    private void initializeComponents() {
        // Create main panel with padding
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Add search panel at top
        mainPanel.add(createSearchPanel(), BorderLayout.NORTH);
        
        // Add table in center
        mainPanel.add(createTablePanel(), BorderLayout.CENTER);
        
        // Add form and buttons on right
        mainPanel.add(createFormPanel(), BorderLayout.EAST);
        
        // Add status bar at bottom
        mainPanel.add(createStatusBar(), BorderLayout.SOUTH);
        
        add(mainPanel);
    }
    
    /**
     * Create search panel
     */
    private JPanel createSearchPanel() {
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setBorder(BorderFactory.createTitledBorder("Search Courses"));
        
        JLabel searchLabel = new JLabel("Search:");
        searchField = new JTextField(20);
        
        // Add key listener for real-time search
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                filterCourses(searchField.getText());
            }
        });
        
        JButton searchButton = new JButton("Search");
        searchButton.addActionListener(e -> filterCourses(searchField.getText()));
        
        JButton clearSearchButton = new JButton("Clear");
        clearSearchButton.addActionListener(e -> {
            searchField.setText("");
            loadCourseData();
        });
        
        searchPanel.add(searchLabel);
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        searchPanel.add(clearSearchButton);
        
        return searchPanel;
    }
    
    /**
     * Create table panel
     */
    private JPanel createTablePanel() {
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createTitledBorder("Courses List"));
        
        // Create table model with columns
        String[] columns = {"Course Code", "Course Name", "Credits", "Instructor"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table non-editable
            }
        };
        
        courseTable = new JTable(tableModel);
        courseTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        courseTable.getTableHeader().setReorderingAllowed(false);
        
        // Add selection listener to populate form when row is selected
        courseTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && courseTable.getSelectedRow() != -1) {
                populateFormFromSelectedRow();
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(courseTable);
        scrollPane.setPreferredSize(new Dimension(600, 300));
        
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        
        return tablePanel;
    }
    
    /**
     * Create form panel
     */
    private JPanel createFormPanel() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Course Details"));
        formPanel.setPreferredSize(new Dimension(300, 0));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        
        // Course Code
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Course Code*:"), gbc);
        gbc.gridx = 1;
        codeField = new JTextField(15);
        formPanel.add(codeField, gbc);
        
        // Course Name
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Course Name*:"), gbc);
        gbc.gridx = 1;
        nameField = new JTextField(15);
        formPanel.add(nameField, gbc);
        
        // Credits
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Credits*:"), gbc);
        gbc.gridx = 1;
        creditsField = new JTextField(15);
        formPanel.add(creditsField, gbc);
        
        // Instructor
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Instructor*:"), gbc);
        gbc.gridx = 1;
        instructorField = new JTextField(15);
        formPanel.add(instructorField, gbc);
        
        // Buttons panel
        gbc.gridx = 0; gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        
        JPanel buttonPanel = new JPanel(new FlowLayout());
        addButton = new JButton("Add");
        updateButton = new JButton("Update");
        deleteButton = new JButton("Delete");
        clearButton = new JButton("Clear");
        
        // Set initial button states
        updateButton.setEnabled(false);
        deleteButton.setEnabled(false);
        
        // Add action listeners
        addButton.addActionListener(e -> addCourse());
        updateButton.addActionListener(e -> updateCourse());
        deleteButton.addActionListener(e -> deleteCourse());
        clearButton.addActionListener(e -> clearForm());
        
        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(clearButton);
        
        formPanel.add(buttonPanel, gbc);
        
        return formPanel;
    }
    
    /**
     * Create status bar
     */
    private JPanel createStatusBar() {
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusLabel = new JLabel("Ready");
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        statusPanel.add(statusLabel);
        return statusPanel;
    }
    
    /**
     * Add new course
     */
    private void addCourse() {
        if (!validateForm()) {
            return;
        }
        
        try {
            String courseCode = codeField.getText().trim();
            String courseName = nameField.getText().trim();
            int credits = Integer.parseInt(creditsField.getText().trim());
            String instructor = instructorField.getText().trim();
            
            // Check if course code already exists
            if (isCourseCodeExists(courseCode)) {
                JOptionPane.showMessageDialog(this, 
                    "Course code '" + courseCode + "' already exists!", "Error", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            Course course = new Course(courseCode, courseName, credits, instructor);
            boolean success = dataManager.saveCourse(course);
            
            if (success) {
                // Save all data to persist changes
                dataManager.saveAllData();
                loadCourseData();
                clearForm();
                statusLabel.setText("Course '" + courseCode + "' added successfully!");
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Failed to save course. Please try again.", "Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, 
                "Please enter valid numeric value for credits!", "Error", 
                JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error adding course: " + e.getMessage(), "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Update selected course
     */
    private void updateCourse() {
        if (originalCourseCode.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "No course selected for update!", "Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (!validateForm()) {
            return;
        }
        
        try {
            String newCourseCode = codeField.getText().trim();
            String courseName = nameField.getText().trim();
            int credits = Integer.parseInt(creditsField.getText().trim());
            String instructor = instructorField.getText().trim();
            
            // Check if course code has changed
            if (!originalCourseCode.equals(newCourseCode)) {
                // Don't allow course code changes if grades exist for this course
                if (hasGradesForCourse(originalCourseCode)) {
                    JOptionPane.showMessageDialog(this,
                        "Cannot change course code for '" + originalCourseCode + "'!\n" +
                        "This course has grades assigned to it.\n" +
                        "Changing the course code would delete all grades and affect GPA calculations.\n\n" +
                        "Please keep the same course code or create a new course instead.",
                        "Course Code Change Not Allowed",
                        JOptionPane.ERROR_MESSAGE);
                    codeField.setText(originalCourseCode); // Reset to original code
                    codeField.requestFocus();
                    return;
                }
                
                // Check if new course code already exists
                if (isCourseCodeExists(newCourseCode)) {
                    JOptionPane.showMessageDialog(this, 
                        "Course code '" + newCourseCode + "' already exists!", "Error", 
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
            
            // Create updated course
            Course updatedCourse = new Course(newCourseCode, courseName, credits, instructor);
            
            // If course code hasn't changed or has no grades, use regular update
            boolean success = dataManager.updateCourse(updatedCourse);
            
            if (success) {
                dataManager.saveAllData();
                loadCourseData();
                clearForm();
                statusLabel.setText("Course '" + newCourseCode + "' updated successfully!");
            } else {
                JOptionPane.showMessageDialog(this,
                    "Failed to update course. Please try again.", "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, 
                "Please enter valid numeric value for credits!", "Error", 
                JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error updating course: " + e.getMessage(), "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Check if a course has any grades assigned to it
     */
    private boolean hasGradesForCourse(String courseCode) {
        List<com.chuka.gradesystem.model.Grade> grades = dataManager.getGradesByCourse(courseCode);
        return grades != null && !grades.isEmpty();
    }
    
    /**
     * Delete selected course
     */
    private void deleteCourse() {
        if (originalCourseCode.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Please select a course to delete!", "Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Get course name for confirmation message
        String courseName = nameField.getText().trim();
        
        // Check if course has grades
        boolean hasGrades = hasGradesForCourse(originalCourseCode);
        
        String warningMessage;
        if (hasGrades) {
            warningMessage = "Are you sure you want to delete course:\n" + 
                originalCourseCode + " - " + courseName + "\n\n" +
                "⚠️ WARNING: This course has grades assigned to it!\n" +
                "Deleting this course will also delete all grades for it,\n" +
                "which will affect GPA calculations!\n\n" +
                "Are you absolutely sure you want to proceed?";
        } else {
            warningMessage = "Are you sure you want to delete course:\n" + 
                originalCourseCode + " - " + courseName + "?";
        }
        
        int confirm = JOptionPane.showConfirmDialog(this,
            warningMessage,
            "Confirm Delete", 
            JOptionPane.YES_NO_OPTION, 
            hasGrades ? JOptionPane.WARNING_MESSAGE : JOptionPane.QUESTION_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                boolean success = dataManager.deleteCourse(originalCourseCode);
                
                if (success) {
                    dataManager.saveAllData();
                    loadCourseData();
                    clearForm();
                    statusLabel.setText("Course '" + originalCourseCode + "' deleted successfully!");
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "Failed to delete course. Please try again.", "Error", 
                        JOptionPane.ERROR_MESSAGE);
                }
                
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, 
                    "Error deleting course: " + e.getMessage(), "Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    /**
     * Clear form fields
     */
    private void clearForm() {
        codeField.setText("");
        nameField.setText("");
        creditsField.setText("");
        instructorField.setText("");
        originalCourseCode = "";
        
        courseTable.clearSelection();
        updateButton.setEnabled(false);
        deleteButton.setEnabled(false);
        addButton.setEnabled(true);
        
        statusLabel.setText("Ready");
        codeField.requestFocus();
    }
    
    /**
     * Populate form from selected table row
     */
    private void populateFormFromSelectedRow() {
        int selectedRow = courseTable.getSelectedRow();
        if (selectedRow != -1) {
            originalCourseCode = (String) tableModel.getValueAt(selectedRow, 0);
            codeField.setText(originalCourseCode);
            nameField.setText((String) tableModel.getValueAt(selectedRow, 1));
            creditsField.setText(tableModel.getValueAt(selectedRow, 2).toString());
            instructorField.setText((String) tableModel.getValueAt(selectedRow, 3));
            
            updateButton.setEnabled(true);
            deleteButton.setEnabled(true);
            addButton.setEnabled(false);
            
            // Disable course code field if course has grades (to protect GPA calculations)
            boolean hasGrades = hasGradesForCourse(originalCourseCode);
            codeField.setEditable(!hasGrades);
            if (hasGrades) {
                codeField.setToolTipText("Course code cannot be changed because this course has grades assigned");
                codeField.setBackground(new Color(255, 255, 240)); // Light yellow background
            } else {
                codeField.setToolTipText(null);
                codeField.setBackground(UIManager.getColor("TextField.background"));
            }
        }
    }
    
    /**
     * Filter courses based on search text
     */
    private void filterCourses(String searchText) {
        if (searchText == null || searchText.trim().isEmpty()) {
            loadCourseData();
            return;
        }
        
        String searchLower = searchText.toLowerCase();
        List<Course> allCourses = dataManager.getAllCourses();
        tableModel.setRowCount(0);
        
        for (Course course : allCourses) {
            if (course.getCourseCode().toLowerCase().contains(searchLower) ||
                course.getCourseName().toLowerCase().contains(searchLower) ||
                course.getInstructor().toLowerCase().contains(searchLower)) {
                
                tableModel.addRow(new Object[]{
                    course.getCourseCode(),
                    course.getCourseName(),
                    course.getCredits(),
                    course.getInstructor()
                });
            }
        }
        
        statusLabel.setText("Found " + tableModel.getRowCount() + " courses matching: " + searchText);
    }
    
    /**
     * Load course data into table
     */
    private void loadCourseData() {
        tableModel.setRowCount(0);
        List<Course> courses = dataManager.getAllCourses();
        
        for (Course course : courses) {
            tableModel.addRow(new Object[]{
                course.getCourseCode(),
                course.getCourseName(),
                course.getCredits(),
                course.getInstructor()
            });
        }
        
        statusLabel.setText("Loaded " + courses.size() + " courses");
    }
    
    /**
     * Validate form inputs
     */
    private boolean validateForm() {
        String courseCode = codeField.getText().trim();
        String courseName = nameField.getText().trim();
        String creditsText = creditsField.getText().trim();
        String instructor = instructorField.getText().trim();
        
        // Validate course code
        if (courseCode.isEmpty()) {
            showValidationError("Course code is required!");
            codeField.requestFocus();
            return false;
        }
        
        if (!ValidationUtil.isValidCourseCode(courseCode)) {
            showValidationError("Invalid course code format! (e.g., CS101)");
            codeField.requestFocus();
            return false;
        }
        
        // Validate course name
        if (courseName.isEmpty()) {
            showValidationError("Course name is required!");
            nameField.requestFocus();
            return false;
        }
        
        // Validate credits
        if (creditsText.isEmpty()) {
            showValidationError("Credits are required!");
            creditsField.requestFocus();
            return false;
        }
        
        try {
            int credits = Integer.parseInt(creditsText);
            if (!ValidationUtil.isValidCredits(credits)) {
                showValidationError("Credits must be between 1 and 6!");
                creditsField.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            showValidationError("Credits must be a valid number!");
            creditsField.requestFocus();
            return false;
        }
        
        // Validate instructor
        if (instructor.isEmpty()) {
            showValidationError("Instructor name is required!");
            instructorField.requestFocus();
            return false;
        }
        
        return true;
    }
    
    /**
     * Show validation error message
     */
    private void showValidationError(String message) {
        JOptionPane.showMessageDialog(this, message, "Validation Error", 
            JOptionPane.ERROR_MESSAGE);
        statusLabel.setText("Validation Error: " + message);
    }
    
    /**
     * Check if course code already exists
     */
    private boolean isCourseCodeExists(String courseCode) {
        Course existing = dataManager.findCourseByCode(courseCode);
        return existing != null;
    }
    
    /**
     * Refresh data - called when switching tabs
     */
    public void refreshData() {
        loadCourseData();
        clearForm();
    }
}