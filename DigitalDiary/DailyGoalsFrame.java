import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class DailyGoalsFrame extends JFrame {

    private final Color backgroundColor = new Color(250, 240, 248);
    private final Color pastelPurple = new Color(190, 175, 220);
    private final Color pastelPink = new Color(235, 180, 205);
    private final Color darkPurple = new Color(100, 75, 120);
    private final Color textColor = new Color(70, 55, 75);

    private JTextField txtGoal;
    private JTextField txtDate;
    private JComboBox<String> statusCombo;

    private JTable table;
    private DefaultTableModel model;

    public DailyGoalsFrame() {

        setTitle("Digital Diary - Daily Goals");
        setSize(950, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBackground(backgroundColor);
        mainPanel.setBorder(
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        );

        // ================= TITLE =================

        JLabel title = new JLabel(
                "DAILY GOALS",
                SwingConstants.CENTER
        );

        title.setFont(new Font("Arial", Font.BOLD, 28));
        title.setForeground(darkPurple);

        mainPanel.add(title, BorderLayout.NORTH);

        // ================= INPUT PANEL =================

        JPanel inputPanel = new JPanel(
                new GridBagLayout()
        );

        inputPanel.setBackground(pastelPink);
        inputPanel.setBorder(
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        );

        GridBagConstraints gbc = new GridBagConstraints();

        gbc.insets = new Insets(5, 8, 5, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Goal
        JLabel goalLabel = new JLabel("Goal:");
        goalLabel.setFont(new Font("Arial", Font.BOLD, 14));
        goalLabel.setForeground(textColor);

        txtGoal = new JTextField(25);

        // Date
        JLabel dateLabel = new JLabel("Date:");
        dateLabel.setFont(new Font("Arial", Font.BOLD, 14));
        dateLabel.setForeground(textColor);

        txtDate = new JTextField(10);
        txtDate.setText(
                new java.text.SimpleDateFormat("yyyy-MM-dd")
                        .format(new java.util.Date())
        );

        // Status
        JLabel statusLabel = new JLabel("Status:");
        statusLabel.setFont(new Font("Arial", Font.BOLD, 14));
        statusLabel.setForeground(textColor);

        statusCombo = new JComboBox<>(
                new String[]{
                        "PENDING",
                        "COMPLETED"
                }
        );

        // Row 1
        gbc.gridx = 0;
        gbc.gridy = 0;
        inputPanel.add(goalLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        inputPanel.add(txtGoal, gbc);

        gbc.gridx = 2;
        gbc.gridy = 0;
        inputPanel.add(dateLabel, gbc);

        gbc.gridx = 3;
        gbc.gridy = 0;
        inputPanel.add(txtDate, gbc);

        // Row 2
        gbc.gridx = 0;
        gbc.gridy = 1;
        inputPanel.add(statusLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        inputPanel.add(statusCombo, gbc);

        // Buttons
        JButton addButton = new JButton("Add Goal");
        JButton updateButton = new JButton("Update");
        JButton deleteButton = new JButton("Delete");
        JButton clearButton = new JButton("Clear");

        styleButton(addButton);
        styleButton(updateButton);
        styleButton(deleteButton);
        styleButton(clearButton);

        gbc.gridx = 2;
        gbc.gridy = 1;
        inputPanel.add(addButton, gbc);

        gbc.gridx = 3;
        gbc.gridy = 1;
        inputPanel.add(updateButton, gbc);

        gbc.gridx = 4;
        gbc.gridy = 1;
        inputPanel.add(deleteButton, gbc);

        gbc.gridx = 5;
        gbc.gridy = 1;
        inputPanel.add(clearButton, gbc);

        mainPanel.add(inputPanel, BorderLayout.PAGE_START);

        // ================= TABLE =================

        model = new DefaultTableModel(
                new Object[]{
                        "Goal ID",
                        "Goal",
                        "Date",
                        "Status"
                },
                0
        ) {

            @Override
            public boolean isCellEditable(
                    int row,
                    int column
            ) {
                return false;
            }
        };

        table = new JTable(model);

        table.setRowHeight(35);

        table.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );

        table.getTableHeader().setBackground(
                pastelPurple
        );

        table.getTableHeader().setForeground(
                Color.WHITE
        );

        table.getTableHeader().setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        13
                )
        );

        table.getColumnModel()
                .getColumn(0)
                .setPreferredWidth(80);

        table.getColumnModel()
                .getColumn(1)
                .setPreferredWidth(400);

        table.getColumnModel()
                .getColumn(2)
                .setPreferredWidth(150);

        table.getColumnModel()
                .getColumn(3)
                .setPreferredWidth(150);

        JScrollPane scrollPane =
                new JScrollPane(table);

        mainPanel.add(
                scrollPane,
                BorderLayout.CENTER
        );

        // ================= BOTTOM =================

        JPanel bottomPanel = new JPanel(
                new FlowLayout(
                        FlowLayout.CENTER
                )
        );

        bottomPanel.setBackground(
                backgroundColor
        );

        JButton mainMenuButton =
                new JButton("← Main Menu");

        styleButton(mainMenuButton);

        bottomPanel.add(mainMenuButton);

        mainPanel.add(
                bottomPanel,
                BorderLayout.SOUTH
        );

        add(mainPanel);

        // ================= ACTIONS =================

        addButton.addActionListener(
                e -> addGoal()
        );

        updateButton.addActionListener(
                e -> updateGoal()
        );

        deleteButton.addActionListener(
                e -> deleteGoal()
        );

        clearButton.addActionListener(
                e -> clearFields()
        );

        mainMenuButton.addActionListener(e -> {
            dispose();
            new MainFrame().setVisible(true);
        });

        table.getSelectionModel()
                .addListSelectionListener(e -> {

                    if (!e.getValueIsAdjusting()) {
                        loadSelectedGoal();
                    }

                });

        loadGoals();
    }

    // ================= LOAD GOALS =================

    private void loadGoals() {

        model.setRowCount(0);

        String sql =
                "SELECT GOAL_ID, GOAL_TITLE, GOAL_DATE, STATUS " +
                "FROM DAILY_GOALS " +
                "ORDER BY GOAL_DATE DESC, GOAL_ID DESC";

        try (
                Connection con =
                        DBConnection.getConnection();

                Statement st =
                        con.createStatement();

                ResultSet rs =
                        st.executeQuery(sql)
        ) {

            while (rs.next()) {

                model.addRow(
                        new Object[]{

                                rs.getInt("GOAL_ID"),

                                rs.getString("GOAL_TITLE"),

                                rs.getDate("GOAL_DATE"),

                                rs.getString("STATUS")
                        }
                );
            }

        } catch (SQLException ex) {

            JOptionPane.showMessageDialog(
                    this,
                    "Error loading goals:\n"
                            + ex.getMessage()
            );
        }
    }

    // ================= ADD GOAL =================

    private void addGoal() {

        String goal =
                txtGoal.getText().trim();

        String date =
                txtDate.getText().trim();

        String status =
                statusCombo.getSelectedItem()
                        .toString();

        if (goal.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please enter goal."
            );

            return;
        }

        String sql =
                "INSERT INTO DAILY_GOALS " +
                "(GOAL_ID, USER_ID, GOAL_TITLE, GOAL_DATE, STATUS) " +
                "VALUES (DAILY_GOAL_SEQ.NEXTVAL, 1, ?, TO_DATE(?, 'YYYY-MM-DD'), ?)";

        try (
                Connection con =
                        DBConnection.getConnection();

                PreparedStatement pst =
                        con.prepareStatement(sql)
        ) {

            pst.setString(1, goal);
            pst.setString(2, date);
            pst.setString(3, status);

            pst.executeUpdate();

            JOptionPane.showMessageDialog(
                    this,
                    "Goal added successfully!"
            );

            clearFields();
            loadGoals();

        } catch (SQLException ex) {

            JOptionPane.showMessageDialog(
                    this,
                    "Error adding goal:\n"
                            + ex.getMessage()
            );
        }
    }

    // ================= UPDATE GOAL =================

    private void updateGoal() {

        int row =
                table.getSelectedRow();

        if (row == -1) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please select a goal first."
            );

            return;
        }

        int goalId =
                Integer.parseInt(
                        table.getValueAt(row, 0)
                                .toString()
                );

        String goal =
                txtGoal.getText().trim();

        String date =
                txtDate.getText().trim();

        String status =
                statusCombo.getSelectedItem()
                        .toString();

        if (goal.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please enter goal."
            );

            return;
        }

        String sql =
                "UPDATE DAILY_GOALS " +
                "SET GOAL_TITLE = ?, " +
                "GOAL_DATE = TO_DATE(?, 'YYYY-MM-DD'), " +
                "STATUS = ? " +
                "WHERE GOAL_ID = ?";

        try (
                Connection con =
                        DBConnection.getConnection();

                PreparedStatement pst =
                        con.prepareStatement(sql)
        ) {

            pst.setString(1, goal);
            pst.setString(2, date);
            pst.setString(3, status);
            pst.setInt(4, goalId);

            pst.executeUpdate();

            JOptionPane.showMessageDialog(
                    this,
                    "Goal updated successfully!"
            );

            clearFields();
            loadGoals();

        } catch (SQLException ex) {

            JOptionPane.showMessageDialog(
                    this,
                    "Error updating goal:\n"
                            + ex.getMessage()
            );
        }
    }

    // ================= DELETE GOAL =================

    private void deleteGoal() {

        int row =
                table.getSelectedRow();

        if (row == -1) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please select a goal first."
            );

            return;
        }

        int goalId =
                Integer.parseInt(
                        table.getValueAt(row, 0)
                                .toString()
                );

        int result =
                JOptionPane.showConfirmDialog(
                        this,
                        "Are you sure you want to delete this goal?",
                        "Delete Goal",
                        JOptionPane.YES_NO_OPTION
                );

        if (result != JOptionPane.YES_OPTION) {
            return;
        }

        String sql =
                "DELETE FROM DAILY_GOALS " +
                "WHERE GOAL_ID = ?";

        try (
                Connection con =
                        DBConnection.getConnection();

                PreparedStatement pst =
                        con.prepareStatement(sql)
        ) {

            pst.setInt(1, goalId);

            pst.executeUpdate();

            JOptionPane.showMessageDialog(
                    this,
                    "Goal deleted successfully!"
            );

            clearFields();
            loadGoals();

        } catch (SQLException ex) {

            JOptionPane.showMessageDialog(
                    this,
                    "Error deleting goal:\n"
                            + ex.getMessage()
            );
        }
    }

    // ================= SELECTED GOAL =================

    private void loadSelectedGoal() {

        int row =
                table.getSelectedRow();

        if (row == -1) {
            return;
        }

        txtGoal.setText(
                table.getValueAt(row, 1)
                        .toString()
        );

        txtDate.setText(
                table.getValueAt(row, 2)
                        .toString()
        );

        statusCombo.setSelectedItem(
                table.getValueAt(row, 3)
                        .toString()
        );
    }

    // ================= CLEAR =================

    private void clearFields() {

        txtGoal.setText("");

        txtDate.setText(
                new java.text.SimpleDateFormat(
                        "yyyy-MM-dd"
                ).format(new java.util.Date())
        );

        statusCombo.setSelectedIndex(0);

        table.clearSelection();
    }

    // ================= BUTTON STYLE =================

    private void styleButton(JButton button) {

        button.setBackground(
                pastelPurple
        );

        button.setForeground(
                Color.WHITE
        );

        button.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        13
                )
        );

        button.setFocusPainted(false);

        button.setCursor(
                new Cursor(
                        Cursor.HAND_CURSOR
                )
        );

        button.setBorder(
                BorderFactory.createCompoundBorder(

                        BorderFactory.createLineBorder(
                                darkPurple,
                                1
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
                new DailyGoalsFrame().setVisible(true)
        );
    }
}