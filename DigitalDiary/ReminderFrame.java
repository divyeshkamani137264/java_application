
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class ReminderFrame extends JFrame {

    private final String username;

    private final Color background = new Color(250, 240, 248);
    private final Color pastelPurple = new Color(190, 175, 220);
    private final Color darkPurple = new Color(100, 75, 120);

    private JTable table;
    private DefaultTableModel model;
    private JTextField titleField;
    private JTextField dateField;
    private JTextField descriptionField;
    private JComboBox<String> statusCombo;

    public ReminderFrame(String username) {
        this.username = username;

        setTitle("Digital Diary - Reminders");
        setSize(950, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);

        createUI();
        loadReminders();
    }

    private void createUI() {

        JPanel mainPanel = new JPanel(new BorderLayout(12, 12));
        mainPanel.setBackground(background);
        mainPanel.setBorder(
                BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel heading = new JLabel("REMINDERS", SwingConstants.CENTER);
        heading.setFont(new Font("Arial", Font.BOLD, 28));
        heading.setForeground(darkPurple);
        mainPanel.add(heading, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(background);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        titleField = new JTextField(18);
        dateField = new JTextField(18);
        descriptionField = new JTextField(18);

        statusCombo = new JComboBox<>(
                new String[]{"PENDING", "COMPLETED"});

        addFormRow(formPanel, gbc, 0, "Title:", titleField);
        addFormRow(formPanel, gbc, 1,
                "Date (YYYY-MM-DD):", dateField);
        addFormRow(formPanel, gbc, 2,
                "Description:", descriptionField);
        addFormRow(formPanel, gbc, 3, "Status:", statusCombo);

        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBackground(background);
        leftPanel.add(formPanel, BorderLayout.NORTH);

        mainPanel.add(leftPanel, BorderLayout.WEST);

        model = new DefaultTableModel(
                new Object[]{
                    "Reminder ID", "Title", "Date",
                    "Description", "Status"
                }, 0) {

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(model);
        table.setRowHeight(25);
        table.getTableHeader().setFont(
                new Font("Arial", Font.BOLD, 13));
        table.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION);

        mainPanel.add(
                new JScrollPane(table), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(
                new FlowLayout(FlowLayout.CENTER, 10, 5));
        buttonPanel.setBackground(background);

        JButton addButton = new JButton("Add Reminder");
        JButton updateButton = new JButton("Update");
        JButton deleteButton = new JButton("Delete");
        JButton refreshButton = new JButton("Refresh");
        JButton clearButton = new JButton("Clear");
        JButton closeButton = new JButton("Close");

        JButton[] buttons = {
            addButton, updateButton, deleteButton,
            refreshButton, clearButton, closeButton
        };

        for (JButton button : buttons) {
            button.setBackground(pastelPurple);
            button.setForeground(Color.WHITE);
            button.setFont(new Font("Arial", Font.BOLD, 13));
            button.setFocusPainted(false);
            button.setCursor(
                    new Cursor(Cursor.HAND_CURSOR));
        }

        for (JButton button : buttons) {
            buttonPanel.add(button);
        }

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        add(mainPanel);

        addButton.addActionListener(e -> addReminder());
        updateButton.addActionListener(e -> updateReminder());
        deleteButton.addActionListener(e -> deleteReminder());
        refreshButton.addActionListener(e -> loadReminders());
        clearButton.addActionListener(e -> clearFields());
        closeButton.addActionListener(e -> dispose());

        table.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) {
                return;
            }

            int row = table.getSelectedRow();

            if (row >= 0) {
                titleField.setText(
                        String.valueOf(model.getValueAt(row, 1)));

                dateField.setText(
                        String.valueOf(model.getValueAt(row, 2)));

                descriptionField.setText(
                        String.valueOf(model.getValueAt(row, 3)));

                statusCombo.setSelectedItem(
                        String.valueOf(model.getValueAt(row, 4)));
            }
        });
    }

    private void addFormRow(
            JPanel panel,
            GridBagConstraints gbc,
            int row,
            String labelText,
            JComponent component) {

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        panel.add(new JLabel(labelText), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        panel.add(component, gbc);
    }

    // LOAD ALL REMINDERS
    private void loadReminders() {

        model.setRowCount(0);

        String sql =
                "SELECT REMINDER_ID, TITLE, " +
                "TO_CHAR(REMINDER_DATE, 'YYYY-MM-DD') AS RDATE, " +
                "DESCRIPTION, STATUS " +
                "FROM REMINDERS ORDER BY REMINDER_DATE";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("REMINDER_ID"),
                    rs.getString("TITLE"),
                    rs.getString("RDATE"),
                    rs.getString("DESCRIPTION"),
                    rs.getString("STATUS")
                });
            }

        } catch (SQLException ex) {
            showError("Unable to load reminders", ex);
        }
    }

    // VALIDATE INPUT
    private boolean validateFields() {

        String title = titleField.getText().trim();
        String date = dateField.getText().trim();

        if (title.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this, "Please enter reminder title.");
            titleField.requestFocus();
            return false;
        }

        if (!date.matches("\\d{4}-\\d{2}-\\d{2}")) {
            JOptionPane.showMessageDialog(
                    this,
                    "Date must be in YYYY-MM-DD format.\n" +
                    "Example: 2026-10-26");
            dateField.requestFocus();
            return false;
        }

        try {
            java.time.LocalDate.parse(date);
        } catch (java.time.format.DateTimeParseException ex) {
            JOptionPane.showMessageDialog(
                    this, "Please enter a valid date.");
            dateField.requestFocus();
            return false;
        }

        return true;
    }

    // ADD REMINDER
    private void addReminder() {

        if (!validateFields()) {
            return;
        }

        String sql =
                "INSERT INTO REMINDERS " +
                "(REMINDER_ID, TITLE, REMINDER_DATE, " +
                "DESCRIPTION, STATUS) " +
                "VALUES (REMINDER_SEQ.NEXTVAL, ?, " +
                "TO_DATE(?, 'YYYY-MM-DD'), ?, ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, titleField.getText().trim());
            ps.setString(2, dateField.getText().trim());
            ps.setString(3, descriptionField.getText().trim());
            ps.setString(4, statusCombo.getSelectedItem().toString());

            ps.executeUpdate();

            JOptionPane.showMessageDialog(
                    this, "Reminder added successfully!");

            clearFields();
            loadReminders();

        } catch (SQLException ex) {
            showError("Unable to add reminder", ex);
        }
    }

    // UPDATE REMINDER
    private void updateReminder() {

        int row = table.getSelectedRow();

        if (row < 0) {
            JOptionPane.showMessageDialog(
                    this, "Please select a reminder first.");
            return;
        }

        if (!validateFields()) {
            return;
        }

        int reminderId =
                Integer.parseInt(model.getValueAt(row, 0).toString());

        String sql =
                "UPDATE REMINDERS SET TITLE = ?, " +
                "REMINDER_DATE = TO_DATE(?, 'YYYY-MM-DD'), " +
                "DESCRIPTION = ?, STATUS = ? " +
                "WHERE REMINDER_ID = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, titleField.getText().trim());
            ps.setString(2, dateField.getText().trim());
            ps.setString(3, descriptionField.getText().trim());
            ps.setString(4, statusCombo.getSelectedItem().toString());
            ps.setInt(5, reminderId);

            int rows = ps.executeUpdate();

            if (rows > 0) {
                JOptionPane.showMessageDialog(
                        this, "Reminder updated successfully!");
                clearFields();
                loadReminders();
            } else {
                JOptionPane.showMessageDialog(
                        this, "Reminder not found.");
            }

        } catch (SQLException ex) {
            showError("Unable to update reminder", ex);
        }
    }

    // DELETE REMINDER
    private void deleteReminder() {

        int row = table.getSelectedRow();

        if (row < 0) {
            JOptionPane.showMessageDialog(
                    this, "Please select a reminder first.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete this reminder?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        int reminderId =
                Integer.parseInt(model.getValueAt(row, 0).toString());

        String sql =
                "DELETE FROM REMINDERS WHERE REMINDER_ID = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, reminderId);

            int rows = ps.executeUpdate();

            if (rows > 0) {
                JOptionPane.showMessageDialog(
                        this, "Reminder deleted successfully!");
                clearFields();
                loadReminders();
            } else {
                JOptionPane.showMessageDialog(
                        this, "Reminder not found.");
            }

        } catch (SQLException ex) {
            showError("Unable to delete reminder", ex);
        }
    }

    // CLEAR FORM
    private void clearFields() {
        titleField.setText("");
        dateField.setText("");
        descriptionField.setText("");
        statusCombo.setSelectedIndex(0);
        table.clearSelection();
    }

    private void showError(String message, SQLException ex) {
        JOptionPane.showMessageDialog(
                this,
                message + ":\n" + ex.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(
                () -> new ReminderFrame("divyesh").setVisible(true));
    }
}
