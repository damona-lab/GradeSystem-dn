package com.chuka.gradesystem.gui;

import com.chuka.gradesystem.model.*;
import com.chuka.gradesystem.service.*;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.text.DecimalFormat;
import java.util.List;

/**
 * ReportPanel - Report Generation Interface
 *
 * Required Features:
 * - JComboBox to select student
 * - JButton to generate transcript
 * - JTextArea to display the report
 * - JButton to export report to file
 * - Show: Student details, all courses, grades, GPA, class standing
 *
 * @author Charles David
 * @course ACSC 223 - Object Oriented Programming (Java 1)
 */
public class ReportPanel extends JPanel {
    private DataManager dataManager;
    private GradeService gradeService;
    
    // UI Components
    private JComboBox<Student> studentComboBox;
    private JButton generateButton;
    private JButton exportButton;
    private JTextArea reportTextArea;
    private JScrollPane reportScrollPane;
    private JLabel statusLabel;
    
    // Current student and report
    private Student currentStudent;
    private String currentReport;
    
    public ReportPanel(DataManager dataManager, GradeService gradeService) {
        this.dataManager = dataManager;
        this.gradeService = gradeService;
        this.currentReport = "";
        
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        initializeComponents();
        buildUI();
        loadStudentData();
    }
    
    private void initializeComponents() {
        // Student selection combobox
        studentComboBox = new JComboBox<>();
        studentComboBox.setPreferredSize(new Dimension(300, 30));
        studentComboBox.addActionListener(e -> {
            currentStudent = (Student) studentComboBox.getSelectedItem();
            if (currentStudent != null) {
                statusLabel.setText("Selected: " + currentStudent.getName());
            }
        });
        
        // Generate button
        generateButton = new JButton("Generate Transcript");
        generateButton.addActionListener(e -> generateTranscript());
        
        // Export button
        exportButton = new JButton("Export to File");
        exportButton.addActionListener(e -> exportReportToFile());
        exportButton.setEnabled(false); // Disabled until report is generated
        
        // Report text area
        reportTextArea = new JTextArea();
        reportTextArea.setEditable(false);
        reportTextArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        reportTextArea.setLineWrap(true);
        reportTextArea.setWrapStyleWord(true);
        reportTextArea.setMargin(new Insets(10, 10, 10, 10));
        
        reportScrollPane = new JScrollPane(reportTextArea);
        reportScrollPane.setPreferredSize(new Dimension(600, 400));
        reportScrollPane.setBorder(BorderFactory.createTitledBorder("Transcript Report"));
        
        // Status label
        statusLabel = new JLabel("Select a student and generate transcript");
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        
        // Style buttons
        Color buttonColor = new Color(70, 130, 180);
        Color buttonTextColor = Color.black;
        
        generateButton.setBackground(buttonColor);
        generateButton.setForeground(buttonTextColor);
        generateButton.setFocusPainted(false);
        
        exportButton.setBackground(new Color(34, 139, 34)); // Green for export
        exportButton.setForeground(buttonTextColor);
        exportButton.setFocusPainted(false);
    }
    
    private void buildUI() {
        // Top panel for controls
        JPanel controlPanel = new JPanel(new GridBagLayout());
        controlPanel.setBorder(BorderFactory.createTitledBorder("Report Controls"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Row 1: Student selection
        gbc.gridx = 0; gbc.gridy = 0;
        controlPanel.add(new JLabel("Select Student:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        controlPanel.add(studentComboBox, gbc);
        
        // Row 2: Buttons
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.weightx = 0;
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.add(generateButton);
        buttonPanel.add(exportButton);
        controlPanel.add(buttonPanel, gbc);
        
        // Row 3: Status
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.gridwidth = 2;
        controlPanel.add(statusLabel, gbc);
        
        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.add(controlPanel, BorderLayout.NORTH);
        mainPanel.add(reportScrollPane, BorderLayout.CENTER);
        
        // Add some statistics at the bottom
        JPanel statsPanel = createStatisticsPanel();
        mainPanel.add(statsPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
    }
    
    private JPanel createStatisticsPanel() {
        JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statsPanel.setBorder(BorderFactory.createTitledBorder("System Statistics"));
        
        // Load statistics
        List<Student> students = dataManager.loadAllStudents();
        List<Course> courses = dataManager.getAllCourses();
        List<Grade> grades = dataManager.loadAllGrades();
        
        JLabel statsLabel = new JLabel(
            String.format("Students: %d | Courses: %d | Grades Recorded: %d",
                students.size(), courses.size(), grades.size())
        );
        statsLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        
        statsPanel.add(statsLabel);
        return statsPanel;
    }
    
    private void loadStudentData() {
        // Load students from DataManager
        List<Student> students = dataManager.loadAllStudents();
        studentComboBox.removeAllItems();
        
        if (students.isEmpty()) {
            // Add a placeholder item instead of creating invalid Student object
            studentComboBox.addItem(new Student() {
                @Override
                public String toString() {
                    return "No Students Found";
                }
                
                @Override
                public String getName() {
                    return "No Students";
                }
                
                @Override
                public String getStudentId() {
                    return "N/A";
                }
                
                @Override
                public String getDepartment() {
                    return "N/A";
                }
            });
            generateButton.setEnabled(false);
            statusLabel.setText("No students available. Please add students first.");
        } else {
            for (Student student : students) {
                studentComboBox.addItem(student);
            }
            generateButton.setEnabled(true);
            
            // Select first student if available
            if (studentComboBox.getItemCount() > 0) {
                studentComboBox.setSelectedIndex(0);
                currentStudent = (Student) studentComboBox.getSelectedItem();
                statusLabel.setText("Ready - " + currentStudent.getName() + " selected");
            }
        }
    }
    
    private void generateTranscript() {
        if (currentStudent == null) {
            JOptionPane.showMessageDialog(this, "Please select a student first", 
                "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Check if this is the placeholder "No Students Found" item
        if ("No Students".equals(currentStudent.getName())) {
            JOptionPane.showMessageDialog(this, "No students available. Please add students first.",
                "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            // Get student's grades using DataManager
            List<Grade> studentGrades = dataManager.getGradesByStudent(currentStudent.getStudentId());
            
            if (studentGrades == null || studentGrades.isEmpty()) {
                reportTextArea.setText("No grades found for " + currentStudent.getName() + 
                    "\n\nStudent ID: " + currentStudent.getStudentId() + 
                    "\nDepartment: " + currentStudent.getDepartment() +
                    "\n\nThis student has no recorded grades yet.");
                exportButton.setEnabled(false);
                return;
            }
            
            // Generate report
            currentReport = generateReportContent(currentStudent, studentGrades);
            reportTextArea.setText(currentReport);
            
            // Enable export button
            exportButton.setEnabled(true);
            
            // Update status
            statusLabel.setText("Transcript generated for " + currentStudent.getName());
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error generating transcript: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
            statusLabel.setText("Error generating transcript");
        }
    }
    
    private String generateReportContent(Student student, List<Grade> grades) {
        StringBuilder report = new StringBuilder();
        DecimalFormat df = new DecimalFormat("0.00");
        
        // Calculate statistics
        double cgpa = gradeService.calculateCGPA(student);
        int totalCredits = gradeService.calculateTotalCredits(grades);
        boolean isHonorRoll = gradeService.isHonorRoll(student);
        String classStanding = gradeService.getClassStanding(cgpa);
        
        // Header
        report.append("=".repeat(60)).append("\n");
        report.append("                  ACADEMIC TRANSCRIPT\n");
        report.append("=".repeat(60)).append("\n\n");
        
        // Student Information
        report.append("STUDENT INFORMATION\n");
        report.append("-".repeat(40)).append("\n");
        report.append("Name: ").append(student.getName()).append("\n");
        report.append("ID: ").append(student.getStudentId()).append("\n");
        report.append("Department: ").append(student.getDepartment()).append("\n");
        report.append("\n");
        
        // Academic Summary
        report.append("ACADEMIC SUMMARY\n");
        report.append("-".repeat(40)).append("\n");
        report.append(String.format("Cumulative GPA (CGPA): %.2f\n", cgpa));
        report.append("Class Standing: ").append(classStanding).append("\n");
        report.append("Total Credits: ").append(totalCredits).append("\n");
        report.append("Honor Roll: ").append(isHonorRoll ? "YES" : "NO").append("\n");
        report.append("\n");
        
        // Course Grades
        report.append("COURSE GRADES\n");
        report.append("-".repeat(40)).append("\n");
        report.append(String.format("%-12s %-25s %-8s %-6s %-8s %-8s\n",
            "Code", "Course Name", "Credits", "Score", "Grade", "Point"));
        report.append("-".repeat(77)).append("\n");
        
        double totalPoints = 0;
        for (Grade grade : grades) {
            Course course = grade.getCourse();
            if (course != null) {
                report.append(String.format("%-12s %-25s %-8d %-6.1f %-8s %-8.1f\n",
                    course.getCourseCode(),
                    truncate(course.getCourseName(), 23),
                    course.getCredits(),
                    grade.getScore(),
                    grade.getLetterGrade(),
                    grade.getGradePoint()
                ));
                totalPoints += grade.getGradePoint() * course.getCredits();
            }
        }
        report.append("\n");
        
        // Grade Distribution
        report.append("GRADE DISTRIBUTION\n");
        report.append("-".repeat(40)).append("\n");
        
        int countA = 0, countB = 0, countC = 0, countD = 0, countF = 0;
        for (Grade grade : grades) {
            switch (grade.getLetterGrade()) {
                case "A": countA++; break;
                case "B": countB++; break;
                case "C": countC++; break;
                case "D": countD++; break;
                case "F": countF++; break;
            }
        }
        
        int totalCourses = grades.size();
        report.append(String.format("A: %d (%.1f%%)\n", countA, (countA * 100.0 / totalCourses)));
        report.append(String.format("B: %d (%.1f%%)\n", countB, (countB * 100.0 / totalCourses)));
        report.append(String.format("C: %d (%.1f%%)\n", countC, (countC * 100.0 / totalCourses)));
        report.append(String.format("D: %d (%.1f%%)\n", countD, (countD * 100.0 / totalCourses)));
        report.append(String.format("F: %d (%.1f%%)\n", countF, (countF * 100.0 / totalCourses)));
        report.append("\n");
        
        // Semester Breakdown (if available)
        report.append("SEMESTER BREAKDOWN\n");
        report.append("-".repeat(40)).append("\n");
        
        // Group grades by semester
        java.util.Map<String, java.util.List<Grade>> semesterGrades = new java.util.HashMap<>();
        for (Grade grade : grades) {
            String semester = grade.getSemester();
            semesterGrades.computeIfAbsent(semester, k -> new java.util.ArrayList<>()).add(grade);
        }
        
        for (String semester : semesterGrades.keySet()) {
            List<Grade> semesterGradeList = semesterGrades.get(semester);
            double semesterGPA = gradeService.calculateGPA(semesterGradeList);
            int semesterCredits = gradeService.calculateTotalCredits(semesterGradeList);
            
            report.append(String.format("%s: GPA %.2f, Credits: %d\n", 
                semester, semesterGPA, semesterCredits));
        }
        report.append("\n");
        
        // Footer
        report.append("=".repeat(60)).append("\n");
        report.append("Generated on: ").append(new java.util.Date()).append("\n");
        report.append("University Academic System\n");
        report.append("=".repeat(60)).append("\n");
        
        return report.toString();
    }
    
    private String truncate(String text, int maxLength) {
        if (text == null) return "";
        if (text.length() <= maxLength) {
            return text;
        }
        return text.substring(0, maxLength - 3) + "...";
    }
    
    private void exportReportToFile() {
        if (currentReport.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No report to export. Generate a transcript first.",
                "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Export Transcript Report");
        fileChooser.setSelectedFile(new File(currentStudent.getName() + "_Transcript.txt"));
        
        // Set file filter for text files
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
            "Text Files (*.txt)", "txt");
        fileChooser.setFileFilter(filter);
        
        // Show save dialog
        int userSelection = fileChooser.showSaveDialog(this);
        
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            
            // Ensure .txt extension
            if (!fileToSave.getName().toLowerCase().endsWith(".txt")) {
                fileToSave = new File(fileToSave.getAbsolutePath() + ".txt");
            }
            
            // Check if file exists and confirm overwrite
            if (fileToSave.exists()) {
                int confirm = JOptionPane.showConfirmDialog(this,
                    "File already exists. Overwrite?", "Confirm Overwrite",
                    JOptionPane.YES_NO_OPTION);
                if (confirm != JOptionPane.YES_OPTION) {
                    return;
                }
            }
            
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileToSave))) {
                writer.write(currentReport);
                writer.flush();
                
                JOptionPane.showMessageDialog(this,
                    "Report exported successfully to:\n" + fileToSave.getAbsolutePath(),
                    "Success", JOptionPane.INFORMATION_MESSAGE);
                
                statusLabel.setText("Report exported: " + fileToSave.getName());
                
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this,
                    "Error exporting report: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
                statusLabel.setText("Export failed");
            }
        }
    }
    
    public void refreshData() {
        loadStudentData();
        reportTextArea.setText("");
        currentReport = "";
        exportButton.setEnabled(false);
        statusLabel.setText("Data refreshed. Select a student to generate transcript.");
    }
}