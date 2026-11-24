import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class CalendarFrame extends JFrame {

    private final Color backgroundColor = new Color(250, 240, 248);
    private final Color pastelPurple = new Color(190, 175, 220);
    private final Color darkPurple = new Color(100, 75, 120);
    private final Color textColor = new Color(70, 55, 75);

    private JTable table;
    private DefaultTableModel model;

    private JComboBox<String> monthCombo;
    private JComboBox<Integer> yearCombo;

    public CalendarFrame() {

        setTitle("Digital Diary - Calendar View");
        setSize(1000, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBackground(backgroundColor);
        mainPanel.setBorder(
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        );

        // ================= TITLE =================

        JLabel title = new JLabel(
                "CALENDAR VIEW",
                SwingConstants.CENTER
        );

        title.setFont(new Font("Arial", Font.BOLD, 28));
        title.setForeground(darkPurple);

        mainPanel.add(title, BorderLayout.NORTH);

        // ================= FILTER PANEL =================

        JPanel filterPanel = new JPanel(
                new FlowLayout(FlowLayout.CENTER, 10, 10)
        );

        filterPanel.setBackground(backgroundColor);

        JLabel monthLabel = new JLabel("Month:");
        monthLabel.setFont(new Font("Arial", Font.BOLD, 14));
        monthLabel.setForeground(textColor);

        String[] months = {
                "January", "February", "March",
                "April", "May", "June",
                "July", "August", "September",
                "October", "November", "December"
        };

        monthCombo = new JComboBox<>(months);

        JLabel yearLabel = new JLabel("Year:");
        yearLabel.setFont(new Font("Arial", Font.BOLD, 14));
        yearLabel.setForeground(textColor);

        yearCombo = new JComboBox<>();

        for (int year = 2020; year <= 2035; year++) {
            yearCombo.addItem(year);
        }

        JButton viewButton = new JButton("View Notes");
        styleButton(viewButton);

        filterPanel.add(monthLabel);
        filterPanel.add(monthCombo);
        filterPanel.add(yearLabel);
        filterPanel.add(yearCombo);
        filterPanel.add(viewButton);

        mainPanel.add(filterPanel, BorderLayout.PAGE_START);

        // ================= TABLE =================

        model = new DefaultTableModel(
                new Object[]{
                        "Note ID",
                        "Title",
                        "Category",
                        "Mood",
                        "Content",
                        "Date"
                }, 0) {

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(model);

        table.setRowHeight(35);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        table.getTableHeader().setBackground(pastelPurple);
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setFont(
                new Font("Arial", Font.BOLD, 13)
        );

        table.getColumnModel().getColumn(0).setPreferredWidth(70);
        table.getColumnModel().getColumn(1).setPreferredWidth(180);
        table.getColumnModel().getColumn(2).setPreferredWidth(140);
        table.getColumnModel().getColumn(3).setPreferredWidth(120);
        table.getColumnModel().getColumn(4).setPreferredWidth(400);
        table.getColumnModel().getColumn(5).setPreferredWidth(120);

        JScrollPane scrollPane = new JScrollPane(table);

        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // ================= BOTTOM =================

        JPanel bottomPanel = new JPanel(
                new FlowLayout(FlowLayout.CENTER)
        );

        bottomPanel.setBackground(backgroundColor);

        JButton mainMenuButton = new JButton("← Main Menu");
        styleButton(mainMenuButton);

        bottomPanel.add(mainMenuButton);

        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel);

        // ================= DEFAULT DATE =================

        java.util.Calendar calendar =
                java.util.Calendar.getInstance();

        monthCombo.setSelectedIndex(
                calendar.get(java.util.Calendar.MONTH)
        );

        yearCombo.setSelectedItem(
                calendar.get(java.util.Calendar.YEAR)
        );

        // ================= ACTIONS =================

        viewButton.addActionListener(e -> loadNotes());

        mainMenuButton.addActionListener(e -> {
            dispose();
            new MainFrame().setVisible(true);
        });

        loadNotes();
    }

    // ================= LOAD NOTES =================

    private void loadNotes() {

        model.setRowCount(0);

        int month = monthCombo.getSelectedIndex() + 1;
        int year = (Integer) yearCombo.getSelectedItem();

        String sql =
                "SELECT d.NOTE_ID, " +
                "d.TITLE, " +
                "c.CATEGORY_NAME, " +
                "d.MOOD, " +
                "d.CONTENT, " +
                "d.NOTE_DATE " +
                "FROM DIARY_NOTES d " +
                "LEFT JOIN CATEGORIES c " +
                "ON d.CATEGORY_ID = c.CATEGORY_ID " +
                "WHERE EXTRACT(MONTH FROM d.NOTE_DATE) = ? " +
                "AND EXTRACT(YEAR FROM d.NOTE_DATE) = ? " +
                "ORDER BY d.NOTE_DATE DESC";

        try (
                Connection con = DBConnection.getConnection();
                PreparedStatement pst = con.prepareStatement(sql)
        ) {

            pst.setInt(1, month);
            pst.setInt(2, year);

            ResultSet rs = pst.executeQuery();

            while (rs.next()) {

                model.addRow(new Object[]{
                        rs.getInt("NOTE_ID"),
                        rs.getString("TITLE"),
                        rs.getString("CATEGORY_NAME"),
                        rs.getString("MOOD"),
                        rs.getString("CONTENT"),
                        rs.getDate("NOTE_DATE")
                });
            }

            if (model.getRowCount() == 0) {

                JOptionPane.showMessageDialog(
                        this,
                        "No diary notes found for "
                                + monthCombo.getSelectedItem()
                                + " " + year
                );
            }

        } catch (SQLException ex) {

            JOptionPane.showMessageDialog(
                    this,
                    "Error loading calendar notes:\n"
                            + ex.getMessage()
            );
        }
    }

    // ================= BUTTON STYLE =================

    private void styleButton(JButton button) {

        button.setBackground(pastelPurple);
        button.setForeground(Color.WHITE);

        button.setFont(
                new Font("Arial", Font.BOLD, 14)
        );

        button.setFocusPainted(false);

        button.setCursor(
                new Cursor(Cursor.HAND_CURSOR)
        );

        button.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                darkPurple, 1
                        ),
                        BorderFactory.createEmptyBorder(
                                10, 15, 10, 15
                        )
                )
        );
    }

    // ================= MAIN =================

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() ->
                new CalendarFrame().setVisible(true)
        );
    }
}