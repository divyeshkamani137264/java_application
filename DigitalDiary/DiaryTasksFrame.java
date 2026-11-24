import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DiaryTasksFrame extends JFrame {

    private JTextField titleField;
    private JTextArea descriptionArea;
    private JTextField taskDateField;
    private JTextField dueDateField;
    private JComboBox<String> statusCombo;

    private JTable taskTable;
    private DefaultTableModel tableModel;

    private int selectedTaskId = -1;

    private final Color backgroundColor = new Color(250, 240, 248);
    private final Color pastelPink = new Color(235, 180, 205);
    private final Color pastelPurple = new Color(190, 175, 220);
    private final Color darkPurple = new Color(100, 75, 120);
    private final Color textColor = new Color(70, 55, 75);

    private final SimpleDateFormat dateFormat =
            new SimpleDateFormat("yyyy-MM-dd");

    public DiaryTasksFrame() {

        setTitle("Digital Diary - Diary Tasks");
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(backgroundColor);
        mainPanel.setBorder(
                BorderFactory.createEmptyBorder(15, 20, 15, 20)
        );

        // ================= HEADER =================

        JLabel titleLabel = new JLabel("DIARY TASKS");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(darkPurple);

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(pastelPink);
        headerPanel.setBorder(
                BorderFactory.createEmptyBorder(15, 20, 15, 20)
        );

        headerPanel.add(titleLabel, BorderLayout.WEST);

        // ================= FORM =================

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(backgroundColor);
        formPanel.setBorder(
                BorderFactory.createTitledBorder("Task Details")
        );

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel taskTitleLabel = new JLabel("Task Title:");
        JLabel descriptionLabel = new JLabel("Description:");
        JLabel taskDateLabel = new JLabel("Task Date:");
        JLabel dueDateLabel = new JLabel("Due Date:");
        JLabel statusLabel = new JLabel("Status:");

        titleField = new JTextField(20);

        descriptionArea = new JTextArea(3, 20);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);

        taskDateField = new JTextField(10);
        dueDateField = new JTextField(10);

        taskDateField.setToolTipText("Format: yyyy-MM-dd");
        dueDateField.setToolTipText("Format: yyyy-MM-dd");

        statusCombo = new JComboBox<>(
                new String[]{"PENDING", "COMPLETED"}
        );

        // Row 0
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(taskTitleLabel, gbc);

        gbc.gridx = 1;
        formPanel.add(titleField, gbc);

        // Row 1
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(descriptionLabel, gbc);

        gbc.gridx = 1;
        formPanel.add(new JScrollPane(descriptionArea), gbc);

        // Row 2
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(taskDateLabel, gbc);

        gbc.gridx = 1;
        formPanel.add(taskDateField, gbc);

        // Row 3
        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(dueDateLabel, gbc);

        gbc.gridx = 1;
        formPanel.add(dueDateField, gbc);

        // Row 4
        gbc.gridx = 0;
        gbc.gridy = 4;
        formPanel.add(statusLabel, gbc);

        gbc.gridx = 1;
        formPanel.add(statusCombo, gbc);

        // ================= BUTTONS =================

        JButton addButton = new JButton("Add Task");
        JButton updateButton = new JButton("Update");
        JButton deleteButton = new JButton("Delete");
        JButton clearButton = new JButton("Clear");
        JButton mainMenuButton = new JButton("Main Menu");

        styleButton(addButton);
        styleButton(updateButton);
        styleButton(deleteButton);
        styleButton(clearButton);
        styleButton(mainMenuButton);

        JPanel buttonPanel = new JPanel(
                new FlowLayout(FlowLayout.CENTER, 10, 5)
        );
        buttonPanel.setBackground(backgroundColor);

        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(clearButton);
        buttonPanel.add(mainMenuButton);

        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        topPanel.setBackground(backgroundColor);
        topPanel.add(formPanel, BorderLayout.CENTER);
        topPanel.add(buttonPanel, BorderLayout.SOUTH);

        // ================= TABLE =================

        String[] columns = {
                "Task ID",
                "Title",
                "Description",
                "Task Date",
                "Due Date",
                "Status"
        };

        tableModel = new DefaultTableModel(columns, 0) {

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        taskTable = new JTable(tableModel);
        taskTable.setRowHeight(28);
        taskTable.setFont(new Font("Arial", Font.PLAIN, 13));
        taskTable.getTableHeader().setFont(
                new Font("Arial", Font.BOLD, 13)
        );
        taskTable.getTableHeader().setBackground(pastelPurple);
        taskTable.getTableHeader().setForeground(Color.WHITE);

        JScrollPane tableScrollPane = new JScrollPane(taskTable);

        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(backgroundColor);
        tablePanel.setBorder(
                BorderFactory.createTitledBorder("Task List")
        );
        tablePanel.add(tableScrollPane, BorderLayout.CENTER);

        // ================= ADD TO FRAME =================

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(topPanel, BorderLayout.CENTER);
        mainPanel.add(tablePanel, BorderLayout.SOUTH);

        tablePanel.setPreferredSize(new Dimension(950, 300));

        add(mainPanel);

        // ================= EVENTS =================

        addButton.addActionListener(e -> addTask());

        updateButton.addActionListener(e -> updateTask());

        deleteButton.addActionListener(e -> deleteTask());

        clearButton.addActionListener(e -> clearFields());

        mainMenuButton.addActionListener(e -> dispose());

        taskTable.getSelectionModel().addListSelectionListener(e -> {

            if (!e.getValueIsAdjusting()) {
                loadSelectedTask();
            }
        });

        loadTasks();
    }

    // =========================================================
    // STYLE BUTTON
    // =========================================================

    private void styleButton(JButton button) {

        button.setBackground(pastelPurple);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                darkPurple, 1
                        ),
                        BorderFactory.createEmptyBorder(
                                8, 15, 8, 15
                        )
                )
        );
    }

    // =========================================================
    // LOAD TASKS
    // =========================================================

    private void loadTasks() {

        tableModel.setRowCount(0);

        String sql =
                "SELECT TASK_ID, TASK_TITLE, TASK_DESCRIPTION, " +
                "TASK_DATE, DUE_DATE, STATUS " +
                "FROM DIARY_TASKS " +
                "ORDER BY TASK_DATE DESC NULLS LAST, TASK_ID DESC";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                Date taskDate = rs.getDate("TASK_DATE");
                Date dueDate = rs.getDate("DUE_DATE");

                tableModel.addRow(new Object[]{
                        rs.getInt("TASK_ID"),
                        rs.getString("TASK_TITLE"),
                        rs.getString("TASK_DESCRIPTION"),
                        taskDate != null
                                ? dateFormat.format(taskDate)
                                : "",
                        dueDate != null
                                ? dateFormat.format(dueDate)
                                : "",
                        rs.getString("STATUS")
                });
            }

        } catch (SQLException ex) {

            JOptionPane.showMessageDialog(
                    this,
                    "Error loading tasks:\n" + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // =========================================================
    // ADD TASK
    // =========================================================

    private void addTask() {

        if (titleField.getText().trim().isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please enter Task Title."
            );

            return;
        }

        Date taskDate = parseDate(taskDateField.getText().trim());
        Date dueDate = parseDate(dueDateField.getText().trim());

        if (!taskDateField.getText().trim().isEmpty()
                && taskDate == null) {

            JOptionPane.showMessageDialog(
                    this,
                    "Task Date must be yyyy-MM-dd."
            );

            return;
        }

        if (!dueDateField.getText().trim().isEmpty()
                && dueDate == null) {

            JOptionPane.showMessageDialog(
                    this,
                    "Due Date must be yyyy-MM-dd."
            );

            return;
        }

        String sql =
                "INSERT INTO DIARY_TASKS " +
                "(TASK_ID, USER_ID, TASK_TITLE, TASK_DESCRIPTION, " +
                "TASK_DATE, DUE_DATE, STATUS) " +
                "VALUES " +
                "((SELECT NVL(MAX(TASK_ID),0)+1 FROM DIARY_TASKS), " +
                "1, ?, ?, ?, ?, ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, titleField.getText().trim());
            ps.setString(2, descriptionArea.getText().trim());

            if (taskDate != null) {
                ps.setDate(
                        3,
                        new java.sql.Date(taskDate.getTime())
                );
            } else {
                ps.setNull(3, Types.DATE);
            }

            if (dueDate != null) {
                ps.setDate(
                        4,
                        new java.sql.Date(dueDate.getTime())
                );
            } else {
                ps.setNull(4, Types.DATE);
            }

            ps.setString(
                    5,
                    statusCombo.getSelectedItem().toString()
            );

            ps.executeUpdate();

            JOptionPane.showMessageDialog(
                    this,
                    "Task added successfully!"
            );

            clearFields();
            loadTasks();

        } catch (SQLException ex) {

            JOptionPane.showMessageDialog(
                    this,
                    "Error adding task:\n" + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // =========================================================
    // UPDATE TASK
    // =========================================================

    private void updateTask() {

        if (selectedTaskId == -1) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please select a task from the table."
            );

            return;
        }

        if (titleField.getText().trim().isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please enter Task Title."
            );

            return;
        }

        Date taskDate = parseDate(taskDateField.getText().trim());
        Date dueDate = parseDate(dueDateField.getText().trim());

        if (!taskDateField.getText().trim().isEmpty()
                && taskDate == null) {

            JOptionPane.showMessageDialog(
                    this,
                    "Task Date must be yyyy-MM-dd."
            );

            return;
        }

        if (!dueDateField.getText().trim().isEmpty()
                && dueDate == null) {

            JOptionPane.showMessageDialog(
                    this,
                    "Due Date must be yyyy-MM-dd."
            );

            return;
        }

        String sql =
                "UPDATE DIARY_TASKS SET " +
                "TASK_TITLE = ?, " +
                "TASK_DESCRIPTION = ?, " +
                "TASK_DATE = ?, " +
                "DUE_DATE = ?, " +
                "STATUS = ? " +
                "WHERE TASK_ID = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, titleField.getText().trim());
            ps.setString(2, descriptionArea.getText().trim());

            if (taskDate != null) {
                ps.setDate(
                        3,
                        new java.sql.Date(taskDate.getTime())
                );
            } else {
                ps.setNull(3, Types.DATE);
            }

            if (dueDate != null) {
                ps.setDate(
                        4,
                        new java.sql.Date(dueDate.getTime())
                );
            } else {
                ps.setNull(4, Types.DATE);
            }

            ps.setString(
                    5,
                    statusCombo.getSelectedItem().toString()
            );

            ps.setInt(6, selectedTaskId);

            ps.executeUpdate();

            JOptionPane.showMessageDialog(
                    this,
                    "Task updated successfully!"
            );

            clearFields();
            loadTasks();

        } catch (SQLException ex) {

            JOptionPane.showMessageDialog(
                    this,
                    "Error updating task:\n" + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // =========================================================
    // DELETE TASK
    // =========================================================

    private void deleteTask() {

        if (selectedTaskId == -1) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please select a task from the table."
            );

            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete this task?",
                "Delete Task",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        String sql =
                "DELETE FROM DIARY_TASKS WHERE TASK_ID = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, selectedTaskId);

            ps.executeUpdate();

            JOptionPane.showMessageDialog(
                    this,
                    "Task deleted successfully!"
            );

            clearFields();
            loadTasks();

        } catch (SQLException ex) {

            JOptionPane.showMessageDialog(
                    this,
                    "Error deleting task:\n" + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // =========================================================
    // LOAD SELECTED TASK
    // =========================================================

    private void loadSelectedTask() {

        int row = taskTable.getSelectedRow();

        if (row == -1) {
            return;
        }

        selectedTaskId =
                Integer.parseInt(
                        tableModel.getValueAt(row, 0).toString()
                );

        titleField.setText(
                tableModel.getValueAt(row, 1).toString()
        );

        descriptionArea.setText(
                tableModel.getValueAt(row, 2) == null
                        ? ""
                        : tableModel.getValueAt(row, 2).toString()
        );

        taskDateField.setText(
                tableModel.getValueAt(row, 3).toString()
        );

        dueDateField.setText(
                tableModel.getValueAt(row, 4).toString()
        );

        statusCombo.setSelectedItem(
                tableModel.getValueAt(row, 5).toString()
        );
    }

    // =========================================================
    // CLEAR
    // =========================================================

    private void clearFields() {

        titleField.setText("");
        descriptionArea.setText("");
        taskDateField.setText("");
        dueDateField.setText("");
        statusCombo.setSelectedIndex(0);

        selectedTaskId = -1;

        taskTable.clearSelection();
    }

    // =========================================================
    // DATE PARSER
    // =========================================================

    private Date parseDate(String text) {

        if (text == null || text.trim().isEmpty()) {
            return null;
        }

        try {

            dateFormat.setLenient(false);

            return dateFormat.parse(text);

        } catch (Exception e) {

            return null;
        }
    }

    // =========================================================
    // MAIN - TEST
    // =========================================================

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() ->
                new DiaryTasksFrame().setVisible(true)
        );
    }
}