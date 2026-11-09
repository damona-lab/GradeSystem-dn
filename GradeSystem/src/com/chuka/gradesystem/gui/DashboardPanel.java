package com.chuka.gradesystem.gui;

import com.chuka.gradesystem.service.*;
import com.chuka.gradesystem.model.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

/**
 * DashboardPanel - Modern Glassmorphism Dashboard
 *
 * Features:
 * - Ambient multi-orb deep gradient canvas
 * - Translucent frosted glass stat cards with specular rim lighting and subtle glow
 * - Dynamic honor roll showcase with modern rank badges
 * - Custom Graphics2D course performance bar chart with dual-tone gradients
 * - Glassmorphic interactive action buttons
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
    private JLabel statusBadgeLabel;

    // Vibrant accent colors for glass cards
    private static final Color COLOR_STUDENTS = new Color(56, 189, 248);   // Sky Cyan
    private static final Color COLOR_COURSES  = new Color(52, 211, 153);   // Emerald Mint
    private static final Color COLOR_GRADES   = new Color(244, 114, 182);  // Rose Pink
    private static final Color COLOR_GPA      = new Color(167, 139, 250);  // Lavender Violet
    private static final Color COLOR_HONOR    = new Color(251, 191, 36);   // Star Gold
    private static final Color COLOR_ACTIVE   = new Color(45, 212, 191);   // Radiant Teal

    public DashboardPanel(DataManager dataManager, GradeService gradeService) {
        this.dataManager = dataManager;
        this.gradeService = gradeService;
        initializeUi();
        refreshData();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        int w = getWidth();
        int h = getHeight();

        // 1. Deep space midnight background
        GradientPaint bg = new GradientPaint(
            0, 0, new Color(11, 15, 25),
            w, h, new Color(20, 24, 48)
        );
        g2.setPaint(bg);
        g2.fillRect(0, 0, w, h);

        // 2. Ambient glowing orbs (to make glass panels frost against real color)
        // Orb 1: Electric Cyan (top-left)
        RadialGradientPaint orb1 = new RadialGradientPaint(
            new Point2D.Float(w * 0.12f, h * 0.18f),
            Math.max(w, h) * 0.42f,
            new float[]{0.0f, 0.5f, 1.0f},
            new Color[]{
                new Color(14, 165, 233, 65),
                new Color(14, 165, 233, 20),
                new Color(11, 15, 25, 0)
            }
        );
        g2.setPaint(orb1);
        g2.fillRect(0, 0, w, h);

        // Orb 2: Radiant Violet / Magenta (bottom-right)
        RadialGradientPaint orb2 = new RadialGradientPaint(
            new Point2D.Float(w * 0.88f, h * 0.72f),
            Math.max(w, h) * 0.48f,
            new float[]{0.0f, 0.55f, 1.0f},
            new Color[]{
                new Color(168, 85, 247, 70),
                new Color(236, 72, 153, 22),
                new Color(20, 24, 48, 0)
            }
        );
        g2.setPaint(orb2);
        g2.fillRect(0, 0, w, h);

        // Orb 3: Golden Emerald (center-bottom)
        RadialGradientPaint orb3 = new RadialGradientPaint(
            new Point2D.Float(w * 0.45f, h * 0.85f),
            Math.max(w, h) * 0.35f,
            new float[]{0.0f, 1.0f},
            new Color[]{
                new Color(16, 185, 129, 45),
                new Color(11, 15, 25, 0)
            }
        );
        g2.setPaint(orb3);
        g2.fillRect(0, 0, w, h);

        g2.dispose();
    }

    private void initializeUi() {
        setLayout(new BorderLayout(16, 16));
        setBorder(BorderFactory.createEmptyBorder(18, 22, 18, 22));
        setOpaque(false);

        // 1. Top Header Bar
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);

        // 2. Main Content Split Panel (Left: Stats Grid, Right: Honor Roll + Chart)
        JPanel contentPanel = new JPanel(new GridLayout(1, 2, 18, 10));
        contentPanel.setOpaque(false);

        // Left Container for 6 Stat Cards
        GlassPanel leftContainer = new GlassPanel(22, new Color(59, 130, 246));
        leftContainer.setLayout(new BorderLayout(10, 12));
        leftContainer.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        // Left header
        JPanel leftHeader = new JPanel(new BorderLayout(5, 2));
        leftHeader.setOpaque(false);
        JLabel statsTitle = new JLabel("⚡ Academic Metrics");
        statsTitle.setFont(new Font("Segoe UI", Font.BOLD, 17));
        statsTitle.setForeground(Color.WHITE);
        JLabel statsSubtitle = new JLabel("Real-time enrollment, curriculum, and assessment analytics");
        statsSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statsSubtitle.setForeground(new Color(148, 163, 184));
        leftHeader.add(statsTitle, BorderLayout.NORTH);
        leftHeader.add(statsSubtitle, BorderLayout.SOUTH);
        leftContainer.add(leftHeader, BorderLayout.NORTH);

        statsGridPanel = new JPanel(new GridLayout(2, 3, 12, 12));
        statsGridPanel.setOpaque(false);
        leftContainer.add(statsGridPanel, BorderLayout.CENTER);

        // Right Container (Honor Roll on top, Chart below)
        JPanel rightContainer = new JPanel(new GridLayout(2, 1, 0, 14));
        rightContainer.setOpaque(false);

        // Honor Roll Glass Card
        GlassPanel honorRollPanel = createHonorRollPanel();
        rightContainer.add(honorRollPanel);

        // Chart Glass Card
        chartPanel = new ChartPanel();
        rightContainer.add(chartPanel);

        contentPanel.add(leftContainer);
        contentPanel.add(rightContainer);
        add(contentPanel, BorderLayout.CENTER);

        // 3. Bottom Controls Panel
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        footerPanel.setOpaque(false);

        GlassButton refreshButton = new GlassButton("⟳  Refresh Dashboard Analytics");
        refreshButton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        refreshButton.addActionListener(e -> refreshData());

        footerPanel.add(refreshButton);
        add(footerPanel, BorderLayout.SOUTH);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout(10, 5));
        headerPanel.setOpaque(false);

        // Left titles
        JPanel titlesPanel = new JPanel(new BorderLayout(0, 2));
        titlesPanel.setOpaque(false);

        JLabel mainTitle = new JLabel("Student Grade Management System");
        mainTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        mainTitle.setForeground(Color.WHITE);

        JLabel subTitle = new JLabel("ACADEMIC ANALYTICS & INSTITUTIONAL PERFORMANCE DASHBOARD · CHUKA UNIVERSITY");
        subTitle.setFont(new Font("Segoe UI", Font.BOLD, 11));
        subTitle.setForeground(new Color(56, 189, 248)); // Vibrant sky cyan

        titlesPanel.add(mainTitle, BorderLayout.NORTH);
        titlesPanel.add(subTitle, BorderLayout.SOUTH);

        // Right glass status badge
        JPanel statusBadge = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(52, 211, 153, 30));
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 20, 20);
                g2.setColor(new Color(52, 211, 153, 100));
                g2.setStroke(new BasicStroke(1.2f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 20, 20);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        statusBadge.setOpaque(false);
        statusBadge.setBorder(BorderFactory.createEmptyBorder(6, 14, 6, 14));

        statusBadgeLabel = new JLabel("● Live System Active");
        statusBadgeLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        statusBadgeLabel.setForeground(new Color(52, 211, 153));
        statusBadge.add(statusBadgeLabel);

        headerPanel.add(titlesPanel, BorderLayout.WEST);
        headerPanel.add(statusBadge, BorderLayout.EAST);

        return headerPanel;
    }

    private GlassPanel createHonorRollPanel() {
        GlassPanel honorRollCard = new GlassPanel(22, COLOR_HONOR);
        honorRollCard.setLayout(new BorderLayout(8, 8));
        honorRollCard.setBorder(BorderFactory.createEmptyBorder(14, 16, 14, 16));

        // Header with title and GPA badge
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JLabel title = new JLabel("🏆 Honor Roll Students");
        title.setFont(new Font("Segoe UI", Font.BOLD, 15));
        title.setForeground(Color.WHITE);

        JLabel badge = new JLabel("GPA ≥ 3.50 Required");
        badge.setFont(new Font("Segoe UI", Font.BOLD, 11));
        badge.setForeground(new Color(251, 191, 36));
        badge.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(251, 191, 36, 120), 1, true),
            BorderFactory.createEmptyBorder(3, 8, 3, 8)
        ));

        header.add(title, BorderLayout.WEST);
        header.add(badge, BorderLayout.EAST);
        honorRollCard.add(header, BorderLayout.NORTH);

        // Content Area
        honorRollArea = new JTextArea();
        honorRollArea.setEditable(false);
        honorRollArea.setOpaque(false);
        honorRollArea.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        honorRollArea.setForeground(new Color(241, 245, 249));
        honorRollArea.setMargin(new Insets(6, 6, 6, 6));

        JScrollPane scrollPane = new JScrollPane(honorRollArea);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        honorRollCard.add(scrollPane, BorderLayout.CENTER);
        return honorRollCard;
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

            // Add the 6 glass cards with vibrant themes and icons
            statsGridPanel.add(createStatCard("Total Students", String.valueOf(totalStudents), "Active enrollment", "👥", COLOR_STUDENTS));
            statsGridPanel.add(createStatCard("Total Courses", String.valueOf(totalCourses), "Curriculum units", "📚", COLOR_COURSES));
            statsGridPanel.add(createStatCard("Total Grades", String.valueOf(totalGrades), "Recorded entries", "📝", COLOR_GRADES));
            statsGridPanel.add(createStatCard("Average GPA", String.format("%.2f", averageGPA), "Institutional score", "🎯", COLOR_GPA));
            statsGridPanel.add(createStatCard("Honor Roll", String.valueOf(honorRollCount), "Distinction level", "🏆", COLOR_HONOR));
            statsGridPanel.add(createStatCard("Graded Courses", String.valueOf(courseAverages.size()), "With assessments", "⚡", COLOR_ACTIVE));

            // Update honor roll display
            updateHonorRollDisplay(honorRollStudents);

            // Update chart
            chartPanel.updateChartData(courseAverages);

            // Update status badge
            if (statusBadgeLabel != null) {
                statusBadgeLabel.setText(String.format("● %d Students · %d Courses · %d Grades", totalStudents, totalCourses, totalGrades));
            }

            // Revalidate and repaint
            statsGridPanel.revalidate();
            statsGridPanel.repaint();
            chartPanel.repaint();
            revalidate();
            repaint();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error loading dashboard data: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Create an individual glassmorphism stat card
     */
    private JPanel createStatCard(String title, String value, String subtitle, String icon, Color accentColor) {
        GlassPanel card = new GlassPanel(18, accentColor);
        card.setLayout(new BorderLayout(6, 6));
        card.setBorder(BorderFactory.createEmptyBorder(12, 14, 12, 14));

        // Top Row: Title + Icon Badge
        JPanel topRow = new JPanel(new BorderLayout());
        topRow.setOpaque(false);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        titleLabel.setForeground(new Color(203, 213, 225)); // Slate 300

        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 15));
        iconLabel.setForeground(accentColor);

        topRow.add(titleLabel, BorderLayout.WEST);
        topRow.add(iconLabel, BorderLayout.EAST);
        card.add(topRow, BorderLayout.NORTH);

        // Center: Huge Stat Value
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 30));
        valueLabel.setForeground(Color.WHITE);
        card.add(valueLabel, BorderLayout.CENTER);

        // Bottom: Subtitle with subtle accent line
        JPanel bottomRow = new JPanel(new BorderLayout(4, 2));
        bottomRow.setOpaque(false);

        JLabel subLabel = new JLabel(subtitle);
        subLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        subLabel.setForeground(new Color(148, 163, 184)); // Slate 400

        // Little accent dot
        JLabel dotLabel = new JLabel("● ");
        dotLabel.setFont(new Font("Segoe UI", Font.PLAIN, 9));
        dotLabel.setForeground(accentColor);

        bottomRow.add(dotLabel, BorderLayout.WEST);
        bottomRow.add(subLabel, BorderLayout.CENTER);
        card.add(bottomRow, BorderLayout.SOUTH);

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
                if (gpa >= 0) {
                    totalGPA += gpa;
                    count++;
                }
            } catch (Exception ignored) {}
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
            } catch (Exception ignored) {}
        }

        // Sort descending by GPA
        honorRoll.sort((a, b) -> Double.compare(gradeService.calculateCGPA(b), gradeService.calculateCGPA(a)));

        return honorRoll;
    }

    private Map<String, Double> calculateCourseAverages(List<Grade> grades) {
        Map<String, CourseStats> stats = new HashMap<>();

        for (Grade grade : grades) {
            if (grade != null && grade.getCourse() != null) {
                String courseCode = grade.getCourse().getCourseCode();
                CourseStats courseStat = stats.getOrDefault(courseCode, new CourseStats());
                courseStat.totalScore += grade.getScore();
                courseStat.count++;
                stats.put(courseCode, courseStat);
            }
        }

        Map<String, Double> averages = new HashMap<>();
        for (Map.Entry<String, CourseStats> entry : stats.entrySet()) {
            averages.put(entry.getKey(), entry.getValue().getAverage());
        }

        return averages;
    }

    private void updateHonorRollDisplay(List<Student> honorRollStudents) {
        if (honorRollStudents.isEmpty()) {
            honorRollArea.setText("\n  No honor roll students recorded yet.\n  (Students achieving GPA ≥ 3.50 will be celebrated here)");
            return;
        }

        StringBuilder sb = new StringBuilder();
        String[] medals = {"🥇", "🥈", "🥉"};

        for (int i = 0; i < honorRollStudents.size(); i++) {
            Student s = honorRollStudents.get(i);
            String medal = (i < medals.length) ? medals[i] : "⭐";
            try {
                double gpa = gradeService.calculateCGPA(s);
                sb.append(String.format(" %s  #%d  %-10s  %-22s  GPA: %.2f  (%s)\n",
                    medal,
                    i + 1,
                    s.getStudentId(),
                    s.getName(),
                    gpa,
                    s.getDepartment() != null ? s.getDepartment() : "General"));
            } catch (Exception e) {
                sb.append(String.format(" %s  #%d  %-10s  %-22s  GPA: N/A\n",
                    medal, i + 1, s.getStudentId(), s.getName()));
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
     * Reusable Frosted Glass Panel with specular rim and ambient glow
     */
    public static class GlassPanel extends JPanel {
        private int cornerRadius;
        private Color accentColor;

        public GlassPanel(int radius, Color accent) {
            this.cornerRadius = radius;
            this.accentColor = accent;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();

            // 1. Semi-transparent frosted glass body
            GradientPaint glassGradient = new GradientPaint(
                0, 0, new Color(255, 255, 255, 24),
                w, h, new Color(255, 255, 255, 9)
            );
            g2.setPaint(glassGradient);
            g2.fillRoundRect(0, 0, w - 1, h - 1, cornerRadius, cornerRadius);

            // 2. Corner glow from accent color
            if (accentColor != null) {
                RadialGradientPaint cornerGlow = new RadialGradientPaint(
                    new Point2D.Float(w * 0.15f, h * 0.15f),
                    Math.max(w, h) * 0.75f,
                    new float[]{0.0f, 1.0f},
                    new Color[]{
                        new Color(accentColor.getRed(), accentColor.getGreen(), accentColor.getBlue(), 42),
                        new Color(accentColor.getRed(), accentColor.getGreen(), accentColor.getBlue(), 0)
                    }
                );
                g2.setPaint(cornerGlow);
                g2.fillRoundRect(0, 0, w - 1, h - 1, cornerRadius, cornerRadius);
            }

            // 3. Specular glass rim highlight (top edge is brightest)
            GradientPaint borderGradient = new GradientPaint(
                0, 0, new Color(255, 255, 255, 95),
                w, h, new Color(255, 255, 255, 25)
            );
            g2.setPaint(borderGradient);
            g2.setStroke(new BasicStroke(1.2f));
            g2.drawRoundRect(0, 0, w - 1, h - 1, cornerRadius, cornerRadius);

            g2.dispose();
            super.paintComponent(g);
        }
    }

    /**
     * Interactive Glass Pill Button
     */
    public static class GlassButton extends JButton {
        private boolean isHovered = false;
        private boolean isPressed = false;

        public GlassButton(String text) {
            super(text);
            setOpaque(false);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setForeground(Color.WHITE);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setBorder(BorderFactory.createEmptyBorder(10, 24, 10, 24));

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    isHovered = true;
                    repaint();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    isHovered = false;
                    repaint();
                }

                @Override
                public void mousePressed(MouseEvent e) {
                    isPressed = true;
                    repaint();
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    isPressed = false;
                    repaint();
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();

            Color c1 = isPressed ? new Color(67, 56, 202, 230) :
                       isHovered ? new Color(129, 140, 248, 220) :
                                   new Color(99, 102, 241, 180);

            Color c2 = isPressed ? new Color(49, 46, 129, 240) :
                       isHovered ? new Color(99, 102, 241, 220) :
                                   new Color(67, 56, 202, 190);

            GradientPaint gp = new GradientPaint(0, 0, c1, w, h, c2);
            g2.setPaint(gp);
            g2.fillRoundRect(0, 0, w - 1, h - 1, 24, 24);

            // Specular border
            Color borderCol = isHovered ? new Color(255, 255, 255, 200) : new Color(255, 255, 255, 120);
            g2.setColor(borderCol);
            g2.setStroke(new BasicStroke(1.3f));
            g2.drawRoundRect(0, 0, w - 1, h - 1, 24, 24);

            g2.dispose();
            super.paintComponent(g);
        }
    }

    /**
     * Modern Glassmorphism Bar Chart
     */
    private class ChartPanel extends JPanel {
        private Map<String, Double> courseAverages;

        // Dual-tone bar gradient colors
        private final Color[][] BAR_PALETTES = {
            { new Color(56, 189, 248),  new Color(2, 132, 199) },   // Sky Cyan
            { new Color(52, 211, 153),  new Color(5, 150, 105) },   // Mint Emerald
            { new Color(167, 139, 250), new Color(124, 58, 237) },  // Violet
            { new Color(251, 146, 60),  new Color(234, 88, 12) },   // Sunset Orange
            { new Color(244, 114, 182), new Color(219, 39, 119) },  // Rose
            { new Color(251, 191, 36),  new Color(217, 119, 6) },   // Amber
            { new Color(45, 212, 191),  new Color(13, 148, 136) }   // Teal
        };

        public ChartPanel() {
            setOpaque(false);
            courseAverages = new HashMap<>();
        }

        public void updateChartData(Map<String, Double> data) {
            this.courseAverages = data;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();

            // 1. Frosted glass panel background
            GradientPaint glassGradient = new GradientPaint(
                0, 0, new Color(255, 255, 255, 24),
                w, h, new Color(255, 255, 255, 9)
            );
            g2.setPaint(glassGradient);
            g2.fillRoundRect(0, 0, w - 1, h - 1, 22, 22);

            // Subtle cyan tint
            RadialGradientPaint tint = new RadialGradientPaint(
                new Point2D.Float(w * 0.1f, h * 0.1f),
                Math.max(w, h) * 0.8f,
                new float[]{0.0f, 1.0f},
                new Color[]{ new Color(56, 189, 248, 30), new Color(56, 189, 248, 0) }
            );
            g2.setPaint(tint);
            g2.fillRoundRect(0, 0, w - 1, h - 1, 22, 22);

            // Glass rim border
            GradientPaint borderGradient = new GradientPaint(
                0, 0, new Color(255, 255, 255, 95),
                w, h, new Color(255, 255, 255, 25)
            );
            g2.setPaint(borderGradient);
            g2.setStroke(new BasicStroke(1.2f));
            g2.drawRoundRect(0, 0, w - 1, h - 1, 22, 22);

            // 2. Chart Titles
            g2.setFont(new Font("Segoe UI", Font.BOLD, 15));
            g2.setColor(Color.WHITE);
            g2.drawString("📊 Course Performance & Average Scores", 20, 28);

            g2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            g2.setColor(new Color(148, 163, 184));
            g2.drawString("Mean score distribution across enrolled courses", 20, 44);

            if (courseAverages == null || courseAverages.isEmpty()) {
                drawNoDataMessage(g2);
                g2.dispose();
                return;
            }

            drawBarChart(g2);
            g2.dispose();
        }

        private void drawNoDataMessage(Graphics2D g2) {
            g2.setColor(new Color(148, 163, 184));
            g2.setFont(new Font("Segoe UI", Font.ITALIC, 13));
            String msg = "No grade data available for chart";
            FontMetrics fm = g2.getFontMetrics();
            int x = (getWidth() - fm.stringWidth(msg)) / 2;
            int y = getHeight() / 2 + 10;
            g2.drawString(msg, x, y);
        }

        private void drawBarChart(Graphics2D g2) {
            int width = getWidth();
            int height = getHeight();

            int marginLeft = 55;
            int marginRight = 30;
            int marginTop = 65;
            int marginBottom = 45;

            int chartWidth = width - marginLeft - marginRight;
            int chartHeight = height - marginTop - marginBottom;
            int chartBottom = height - marginBottom;

            if (chartWidth <= 0 || chartHeight <= 0) return;

            // Draw translucent horizontal gridlines
            g2.setFont(new Font("Segoe UI", Font.PLAIN, 10));
            FontMetrics fm = g2.getFontMetrics();

            for (int i = 0; i <= 4; i++) {
                int y = chartBottom - (i * chartHeight / 4);
                g2.setColor(new Color(255, 255, 255, 25));
                g2.drawLine(marginLeft, y, width - marginRight, y);

                String label = String.valueOf(i * 25);
                g2.setColor(new Color(148, 163, 184));
                int labelW = fm.stringWidth(label);
                g2.drawString(label, marginLeft - labelW - 8, y + 4);
            }

            // Course items
            List<String> courseCodes = new java.util.ArrayList<>(courseAverages.keySet());
            int barCount = courseCodes.size();
            if (barCount == 0) return;

            int barWidth = Math.max(16, Math.min(50, (chartWidth / barCount) - 14));
            int totalBarsWidth = barCount * barWidth;
            int barSpacing = Math.max(8, (chartWidth - totalBarsWidth) / (barCount + 1));

            int x = marginLeft + barSpacing;

            for (int i = 0; i < barCount; i++) {
                String courseCode = courseCodes.get(i);
                double score = courseAverages.get(courseCode);
                double cappedScore = Math.min(100.0, Math.max(0.0, score));
                int barHeight = Math.max(4, (int) ((cappedScore / 100.0) * chartHeight));

                Color[] palette = BAR_PALETTES[i % BAR_PALETTES.length];

                // Vibrant gradient bar
                GradientPaint barGrad = new GradientPaint(
                    x, chartBottom - barHeight, palette[0],
                    x, chartBottom, palette[1]
                );
                g2.setPaint(barGrad);
                g2.fillRoundRect(x, chartBottom - barHeight, barWidth, barHeight, 8, 8);

                // Top highlight cap (specular light)
                g2.setColor(new Color(255, 255, 255, 180));
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawLine(x + 2, chartBottom - barHeight, x + barWidth - 2, chartBottom - barHeight);

                // Score pill / text above bar
                g2.setFont(new Font("Segoe UI", Font.BOLD, 10));
                String scoreStr = String.format("%.1f", score);
                int scoreW = g2.getFontMetrics().stringWidth(scoreStr);
                g2.setColor(Color.WHITE);
                g2.drawString(scoreStr, x + (barWidth - scoreW) / 2, chartBottom - barHeight - 6);

                // Course code label below bar
                g2.setFont(new Font("Segoe UI", Font.BOLD, 11));
                String displayCode = courseCode.length() > 7 ? courseCode.substring(0, 6) + "…" : courseCode;
                int codeW = g2.getFontMetrics().stringWidth(displayCode);
                g2.setColor(new Color(226, 232, 240));
                g2.drawString(displayCode, x + (barWidth - codeW) / 2, chartBottom + 16);

                x += barWidth + barSpacing;
            }

            // Baseline axis line
            g2.setColor(new Color(255, 255, 255, 60));
            g2.setStroke(new BasicStroke(1.5f));
            g2.drawLine(marginLeft, chartBottom, width - marginRight, chartBottom);
        }
    }
}
