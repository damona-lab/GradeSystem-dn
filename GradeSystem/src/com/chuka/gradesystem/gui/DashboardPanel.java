package com.chuka.gradesystem.gui;

import com.chuka.gradesystem.service.*;
import com.chuka.gradesystem.model.*;
import javax.swing.*;
import javax.swing.border.TitledBorder;

import java.awt.*;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

/**
 * DashboardPanel - Dashboard with Statistics
 *
 * Required Features:
 * - Display total number of students
 * - Display total number of courses
 * - Display total number of grades
 * - Show average GPA across all students
 * - Display honor roll students (GPA >= 3.5)
 * - Optional: Custom painting with Graphics2D for charts
 *
 * @author Charles David
 * @course ACSC 223 - Object Oriented Programming (Java 1)
 */
public class DashboardPanel extends JPanel {

    private DataManager dataManager;
    private GradeService gradeService;
    private JPanel statsGridPanel;
    private JTextArea honorRollArea;
    private ChartPanel chartPanel;
    
    // Color scheme
   private final Color[] STAT_CARD_COLORS = {
    new Color(255, 255, 255),   // White
    new Color(255, 255, 255),   // White
    new Color(255, 255, 255),   // White
    new Color(255, 255, 255),   // White
    new Color(255, 255, 255),   // White
    new Color(255, 255, 255)    // White
};


    public DashboardPanel(DataManager dataManager, GradeService gradeService) {
        this.dataManager = dataManager;
        this.gradeService = gradeService;
        initializeUi();
        refreshData();
    }

    private void initializeUi() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Create title
        JLabel titleLabel = new JLabel("Dashboard - Student Management System", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(0, 0, 139)); // Dark blue


        add(titleLabel, BorderLayout.NORTH);

        // Create main content panel
        JPanel contentPanel = new JPanel(new GridLayout(1, 2, 20, 10));
        
        // Left panel for statistics
        JPanel leftPanel = new JPanel(new BorderLayout());
        TitledBorder border = BorderFactory.createTitledBorder("System Statistics");
border.setTitleFont(new Font("Arial", Font.BOLD, 18)); // Bigger & bold font
leftPanel.setBorder(border);

        
        statsGridPanel = new JPanel(new GridLayout(2, 3, 15, 15));
        statsGridPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        leftPanel.add(statsGridPanel, BorderLayout.CENTER);
        
        // Right panel for honor roll and chart
        JPanel rightPanel = new JPanel(new BorderLayout(10, 10));
        
        // Honor Roll Panel
        JPanel honorRollPanel = new JPanel(new BorderLayout());
        honorRollPanel.setBorder(BorderFactory.createTitledBorder("Honor Roll Students (GPA ≥ 3.5)"));
        honorRollPanel.setForeground(getBackground());
        honorRollArea = new JTextArea(10, 30);
        honorRollArea.setEditable(false);
        honorRollArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        honorRollArea.setText("Loading honor roll data...");
        JScrollPane scrollPane = new JScrollPane(honorRollArea);
        scrollPane.setPreferredSize(new Dimension(400, 200));
        honorRollPanel.add(scrollPane, BorderLayout.CENTER);
        TitledBorder honorBorder = BorderFactory.createTitledBorder("Honor Roll Students (GPA ≥ 3.5)");
honorBorder.setTitleFont(new Font("Arial", Font.BOLD, 16));  // Bigger & bold
honorRollPanel.setBorder(honorBorder);

        // Chart Panel
        chartPanel = new ChartPanel();
        
        rightPanel.add(honorRollPanel, BorderLayout.NORTH);
        rightPanel.add(chartPanel, BorderLayout.CENTER);
        
        contentPanel.add(leftPanel);
        contentPanel.add(rightPanel);
        
        add(contentPanel, BorderLayout.CENTER);
        
        // Refresh button panel
      JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

JButton refreshButton = new JButton("Refresh Dashboard");
refreshButton.setFont(new Font("Arial", Font.BOLD, 14));

// Set button background & text color
refreshButton.setBackground(new Color(0, 0, 139)); // Dark blue
refreshButton.setForeground(Color.BLACK);          // White text

refreshButton.addActionListener(e -> refreshData());

buttonPanel.add(refreshButton);
add(buttonPanel, BorderLayout.SOUTH);

    }

    public void refreshData() {
        // Clear previous content
        statsGridPanel.removeAll();
        honorRollArea.setText("");
        
        try {
            // Fetch data
            List<Student> students = dataManager.loadAllStudents();
            List<Course> courses = dataManager.getAllCourses();
            List<Grade> grades = dataManager.loadAllGrades();
            
            int totalStudents = students.size();
            int totalCourses = courses.size();
            int totalGrades = grades.size();
            double averageGPA = calculateAverageGPA(students);
            
            // Calculate honor roll students
            List<Student> honorRollStudents = getHonorRollStudents(students);
            int honorRollCount = honorRollStudents.size();
            
            // Calculate course performance for chart
            Map<String, Double> courseAverages = calculateCourseAverages(grades);
            
            // Create stat cards
            statsGridPanel.add(createStatCard("Total Students", 
                                             String.valueOf(totalStudents), 
                                             STAT_CARD_COLORS[0]));
            
            statsGridPanel.add(createStatCard("Total Courses", 
                                             String.valueOf(totalCourses), 
                                             STAT_CARD_COLORS[1]));
            
            statsGridPanel.add(createStatCard("Total Grades", 
                                             String.valueOf(totalGrades), 
                                             STAT_CARD_COLORS[2]));
            
            statsGridPanel.add(createStatCard("Average GPA", 
                                             String.format("%.2f", averageGPA), 
                                             STAT_CARD_COLORS[3]));
            
            statsGridPanel.add(createStatCard("Honor Roll", 
                                             String.valueOf(honorRollCount), 
                                             STAT_CARD_COLORS[4]));
            
            statsGridPanel.add(createStatCard("Active Courses", 
                                             String.valueOf(courseAverages.size()), 
                                             STAT_CARD_COLORS[5]));
            
            // Update honor roll display
            updateHonorRollDisplay(honorRollStudents);
            
            // Update chart
            chartPanel.updateChartData(courseAverages);
            
            // Revalidate and repaint
            statsGridPanel.revalidate();
            statsGridPanel.repaint();
            chartPanel.repaint();
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error loading dashboard data: " + e.getMessage(),
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private JPanel createStatCard(String title, String value, Color color) {
        JPanel card = new JPanel(new BorderLayout(5, 5));
        card.setPreferredSize(new Dimension(180, 120));
        
        // Create gradient background
        JPanel gradientPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                GradientPaint gradient = new GradientPaint(
                    0, 0, color.brighter(),
                    getWidth(), getHeight(), color.darker()
                );
                g2d.setPaint(gradient);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                
                // Draw border
                g2d.setColor(color.darker().darker());
                g2d.setStroke(new BasicStroke(2));
                g2d.drawRoundRect(1, 1, getWidth()-2, getHeight()-2, 15, 15);
            }
        };
        gradientPanel.setLayout(new BorderLayout());
        gradientPanel.setOpaque(false);
        
        // Title label
        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        titleLabel.setForeground(Color.black);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(15, 10, 5, 10));
        
        // Value label
        JLabel valueLabel = new JLabel(value, SwingConstants.CENTER);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 36));
        valueLabel.setForeground(Color.black);
        valueLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 15, 10));
        
        gradientPanel.add(titleLabel, BorderLayout.NORTH);
        gradientPanel.add(valueLabel, BorderLayout.CENTER);
        
        card.add(gradientPanel, BorderLayout.CENTER);
        card.setOpaque(false);
        
        return card;
    }
    
    private double calculateAverageGPA(List<Student> students) {
        if (students.isEmpty()) {
            return 0.0;
        }
        
        double totalGPA = 0.0;
        int count = 0;
        
        for (Student student : students) {
            try {
                double gpa = gradeService.calculateCGPA(student);
                if (gpa >= 0) { // Valid GPA
                    totalGPA += gpa;
                    count++;
                }
            } catch (Exception e) {
                // Skip students with calculation errors
            }
        }
        
        return count > 0 ? totalGPA / count : 0.0;
    }
    
    private List<Student> getHonorRollStudents(List<Student> students) {
        List<Student> honorRoll = new java.util.ArrayList<>();
        
        for (Student student : students) {
            try {
                double gpa = gradeService.calculateCGPA(student);
                if (gpa >= 3.5) {
                    honorRoll.add(student);
                }
            } catch (Exception e) {
                // Skip students with calculation errors
            }
        }
        
        return honorRoll;
    }
    
    private Map<String, Double> calculateCourseAverages(List<Grade> grades) {
        Map<String, CourseStats> stats = new HashMap<>();
        
        for (Grade grade : grades) {
            String courseCode = grade.getCourse().getCourseCode();
            CourseStats courseStat = stats.getOrDefault(courseCode, new CourseStats());
            courseStat.totalScore += grade.getScore();
            courseStat.count++;
            stats.put(courseCode, courseStat);
        }
        
        Map<String, Double> averages = new HashMap<>();
        for (Map.Entry<String, CourseStats> entry : stats.entrySet()) {
            averages.put(entry.getKey(), entry.getValue().getAverage());
        }
        
        return averages;
    }
    
    private void updateHonorRollDisplay(List<Student> honorRollStudents) {
        if (honorRollStudents.isEmpty()) {
            honorRollArea.setText("No honor roll students found.\n(GPA ≥ 3.5 required)");
            return;
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append("Honor Roll Students:\n");
        sb.append("═══════════════════════════════════════\n\n");
        
        for (int i = 0; i < honorRollStudents.size(); i++) {
            Student student = honorRollStudents.get(i);
            try {
                double gpa = gradeService.calculateCGPA(student);
                sb.append(String.format("%2d. %-15s %-20s GPA: %.2f\n", 
                    i + 1, 
                    student.getStudentId(),
                    student.getName(),
                    gpa));
            } catch (Exception e) {
                sb.append(String.format("%2d. %-15s %-20s GPA: Error\n", 
                    i + 1, 
                    student.getStudentId(),
                    student.getName()));
            }
        }
        
        honorRollArea.setText(sb.toString());
    }
    
    // Helper class for course statistics
    private static class CourseStats {
        double totalScore;
        int count;
        
        double getAverage() {
            return count == 0 ? 0 : totalScore / count;
        }
    }
    
    /**
     * Custom chart panel using Graphics2D
     */
    private class ChartPanel extends JPanel {
        private Map<String, Double> courseAverages;
        private final Color[] CHART_COLORS = {
            new Color(0, 133, 244),   // Blue
            new Color(52, 168, 83),    // Green
            new Color(251, 188, 5),    // Yellow
            new Color(234, 67, 53),    // Red
            new Color(171, 71, 188),   // Purple
            new Color(255, 112, 67),   // Orange
            new Color(0, 172, 193),    // Cyan
            new Color(158, 158, 158)   // Gray
        };
        
        public ChartPanel() {
            setBorder(BorderFactory.createTitledBorder("Course Performance Chart"));
            setPreferredSize(new Dimension(400, 250));
            courseAverages = new HashMap<>();
        }
        
        public void updateChartData(Map<String, Double> data) {
            this.courseAverages = data;
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            if (courseAverages.isEmpty()) {
                drawNoDataMessage(g2d);
                return;
            }
            
            drawBarChart(g2d);
        }
        
        private void drawNoDataMessage(Graphics2D g2d) {
            g2d.setColor(Color.GRAY);
            g2d.setFont(new Font("Arial", Font.ITALIC, 14));
            String message = "No grade data available for chart";
            FontMetrics fm = g2d.getFontMetrics();
            int x = (getWidth() - fm.stringWidth(message)) / 2;
            int y = getHeight() / 2;
            g2d.drawString(message, x, y);
        }
        
        private void drawBarChart(Graphics2D g2d) {
            int width = getWidth();
            int height = getHeight();
            
            // Chart dimensions
            int margin = 60;
            int chartWidth = width - 2 * margin;
            int chartHeight = height - 2 * margin;
            int chartBottom = height - margin;
            int chartTop = margin;
            
            // Get course codes and values
            List<String> courseCodes = new java.util.ArrayList<>(courseAverages.keySet());
            int barCount = courseCodes.size();
            
            if (barCount == 0) return;
            
            int barWidth = Math.min(40, chartWidth / barCount - 10);
            int barSpacing = (chartWidth - (barCount * barWidth)) / (barCount + 1);
            
            // Find maximum value for scaling
            double maxValue = 0;
            for (Double value : courseAverages.values()) {
                if (value > maxValue) maxValue = value;
            }
            if (maxValue == 0) maxValue = 100; // Default scale
            
            // Draw bars
            int x = margin + barSpacing;
            for (int i = 0; i < barCount; i++) {
                String courseCode = courseCodes.get(i);
                double value = courseAverages.get(courseCode);
                int barHeight = (int) ((value / maxValue) * chartHeight);
                
                // Draw bar with gradient
                Color barColor = CHART_COLORS[i % CHART_COLORS.length];
                GradientPaint gradient = new GradientPaint(
                    x, chartBottom - barHeight, barColor,
                    x, chartBottom, barColor.darker()
                );
                g2d.setPaint(gradient);
                g2d.fillRect(x, chartBottom - barHeight, barWidth, barHeight);
                
                // Draw bar border
                g2d.setColor(Color.BLACK);
                g2d.drawRect(x, chartBottom - barHeight, barWidth, barHeight);
                
                // Draw value label
                g2d.setColor(Color.BLACK);
                String valueStr = String.format("%.1f", value);
                FontMetrics fm = g2d.getFontMetrics();
                int labelWidth = fm.stringWidth(valueStr);
                g2d.drawString(valueStr, x + (barWidth - labelWidth) / 2, 
                              chartBottom - barHeight - 5);
                
                // Draw course code
                String displayCode = courseCode.length() > 6 ? 
                    courseCode.substring(0, 6) + ".." : courseCode;
                int codeWidth = fm.stringWidth(displayCode);
                g2d.drawString(displayCode, x + (barWidth - codeWidth) / 2, 
                              chartBottom + 15);
                
                x += barWidth + barSpacing;
            }
            
            // Draw axes
            g2d.setColor(Color.BLACK);
            g2d.setStroke(new BasicStroke(2));
            g2d.drawLine(margin, chartTop, margin, chartBottom);
            g2d.drawLine(margin, chartBottom, width - margin, chartBottom);
            
            // Draw Y-axis labels
            g2d.setFont(new Font("Arial", Font.PLAIN, 10));
            for (int i = 0; i <= 5; i++) {
                int y = chartBottom - (i * chartHeight / 5);
                g2d.drawLine(margin - 5, y, margin, y);
                String label = String.format("%.0f", (maxValue * i / 5));
                FontMetrics fm = g2d.getFontMetrics();
                int labelWidth = fm.stringWidth(label);
                g2d.drawString(label, margin - labelWidth - 8, y + 4);
            }
            
            // Draw title
            g2d.setFont(new Font("Arial", Font.BOLD, 12));
            String title = "Average Scores by Course";
            FontMetrics fm = g2d.getFontMetrics();
            int titleWidth = fm.stringWidth(title);
            g2d.drawString(title, (width - titleWidth) / 2, margin / 2);
            
            // Draw legend
            drawLegend(g2d, courseCodes);
        }
        
        private void drawLegend(Graphics2D g2d, List<String> courseCodes) {
            int x = getWidth() - 120;
            int y = 40;
            
            g2d.setFont(new Font("Arial", Font.BOLD, 11));
            g2d.drawString("Courses:", x, y);
            
            // Show up to 5 courses in legend
            int maxItems = Math.min(courseCodes.size(), 5);
            for (int i = 0; i < maxItems; i++) {
                String courseCode = courseCodes.get(i);
                if (courseCode.length() > 10) {
                    courseCode = courseCode.substring(0, 10) + "...";
                }
                
                g2d.setColor(CHART_COLORS[i % CHART_COLORS.length]);
                g2d.fillRect(x, y + 5 + (i * 18), 12, 12);
                g2d.setColor(Color.BLACK);
                g2d.drawRect(x, y + 5 + (i * 18), 12, 12);
                g2d.drawString(courseCode, x + 18, y + 16 + (i * 18));
            }
        }
    }
}