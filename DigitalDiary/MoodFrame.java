import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class MoodFrame extends JFrame {

    Color creamColor = new Color(248, 244, 230);
    Color oliveColor = new Color(128, 128, 70);
    Color darkOlive = new Color(85, 90, 45);
    Color textColor = new Color(65, 65, 45);

    JTable moodTable;
    DefaultTableModel model;

    JTextField txtMoodName;

    public MoodFrame() {

        setTitle("Digital Diary - Mood Management");
        setSize(700, 550);
        setLocationRelativeTo(null);

        setDefaultCloseOperation(
                JFrame.DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));

        mainPanel.setBackground(creamColor);

        mainPanel.setBorder(
                BorderFactory.createEmptyBorder(
                        20, 20, 20, 20));

        // TITLE

        JLabel title = new JLabel(
                "MOOD MANAGEMENT",
                SwingConstants.CENTER);

        title.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        26));

        title.setForeground(darkOlive);

        mainPanel.add(
                title,
                BorderLayout.NORTH);

        // FORM

        JPanel formPanel = new JPanel(
                new GridBagLayout());

        formPanel.setBackground(creamColor);

        GridBagConstraints gbc = new GridBagConstraints();

        gbc.insets = new Insets(10, 10, 10, 10);

        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel moodLabel = new JLabel("Mood Name:");

        moodLabel.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        14));

        moodLabel.setForeground(textColor);

        txtMoodName = new JTextField(25);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.3;

        formPanel.add(
                moodLabel,
                gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;

        formPanel.add(
                txtMoodName,
                gbc);

        mainPanel.add(
                formPanel,
                BorderLayout.CENTER);

        // TABLE

        model = new DefaultTableModel(
                new Object[] {
                        "Mood ID",
                        "Mood Name"
                }, 0);

        moodTable = new JTable(model);

        moodTable.setRowHeight(25);

        moodTable.getTableHeader()
                .setBackground(oliveColor);

        moodTable.getTableHeader()
                .setForeground(Color.WHITE);

        moodTable.getTableHeader()
                .setFont(
                        new Font(
                                "Arial",
                                Font.BOLD,
                                13));

        JScrollPane scrollPane = new JScrollPane(moodTable);

        JPanel tablePanel = new JPanel(
                new BorderLayout());

        tablePanel.setBackground(creamColor);

        tablePanel.add(
                scrollPane,
                BorderLayout.CENTER);

        // Put form + table together

        JPanel centerPanel = new JPanel(
                new BorderLayout(10, 10));

        centerPanel.setBackground(creamColor);

        centerPanel.add(
                formPanel,
                BorderLayout.NORTH);

        centerPanel.add(
                tablePanel,
                BorderLayout.CENTER);

        mainPanel.add(
                centerPanel,
                BorderLayout.CENTER);

        // BUTTONS

        JPanel buttonPanel = new JPanel(
                new FlowLayout(
                        FlowLayout.CENTER,
                        10,
                        10));

        buttonPanel.setBackground(creamColor);

        JButton addButton = new JButton("Add Mood");

        JButton deleteButton = new JButton("Delete Mood");

        JButton refreshButton = new JButton("Refresh");

        JButton mainMenuButton = new JButton("← Main Menu");

        styleButton(addButton);
        styleButton(deleteButton);
        styleButton(refreshButton);
        styleButton(mainMenuButton);

        buttonPanel.add(addButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);
        buttonPanel.add(mainMenuButton);

        mainPanel.add(
                buttonPanel,
                BorderLayout.SOUTH);

        add(mainPanel);

        // LOAD DATA

        loadMoods();

        // ACTIONS

        addButton.addActionListener(
                e -> addMood());

        deleteButton.addActionListener(
                e -> deleteMood());

        refreshButton.addActionListener(
                e -> loadMoods());

        mainMenuButton.addActionListener(e -> {

            dispose();

            new MainFrame().setVisible(true);
        });
    }

    private void styleButton(
            JButton button) {

        button.setBackground(oliveColor);

        button.setForeground(Color.WHITE);

        button.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        13));

        button.setFocusPainted(false);
    }

    // LOAD MOODS

    private void loadMoods() {

        model.setRowCount(0);

        String sql = "SELECT MOOD_ID, MOOD_NAME " +
                "FROM MOODS " +
                "ORDER BY MOOD_ID";

        try (
                Connection con = DBConnection.getConnection();

                Statement st = con.createStatement();

                ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {

                model.addRow(
                        new Object[] {
                                rs.getInt("MOOD_ID"),
                                rs.getString("MOOD_NAME")
                        });
            }

        } catch (SQLException ex) {

            JOptionPane.showMessageDialog(
                    this,
                    "Error loading moods:\n"
                            + ex.getMessage());
        }
    }

    // ADD MOOD

    private void addMood() {

        String moodName = txtMoodName
                .getText()
                .trim();

        if (moodName.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please enter mood name.");

            return;
        }

        String sql = "INSERT INTO MOODS " +
                "(MOOD_ID, MOOD_NAME) " +
                "VALUES (MOOD_SEQ.NEXTVAL, ?)";

        try (
                Connection con = DBConnection.getConnection();

                PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setString(
                    1,
                    moodName);

            pst.executeUpdate();

            JOptionPane.showMessageDialog(
                    this,
                    "Mood added successfully!");

            txtMoodName.setText("");

            loadMoods();

        } catch (SQLException ex) {

            JOptionPane.showMessageDialog(
                    this,
                    "Error adding mood:\n"
                            + ex.getMessage());
        }
    }

    // DELETE MOOD

    private void deleteMood() {

        int row = moodTable.getSelectedRow();

        if (row == -1) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please select a mood.");

            return;
        }

        int moodId = Integer.parseInt(
                moodTable
                        .getValueAt(
                                row,
                                0)
                        .toString());

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Delete selected mood?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        String sql = "DELETE FROM MOODS " +
                "WHERE MOOD_ID = ?";

        try (
                Connection con = DBConnection.getConnection();

                PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setInt(
                    1,
                    moodId);

            pst.executeUpdate();

            JOptionPane.showMessageDialog(
                    this,
                    "Mood deleted successfully.");

            loadMoods();

        } catch (SQLException ex) {

            JOptionPane.showMessageDialog(
                    this,
                    "Delete error:\n"
                            + ex.getMessage());
        }
    }

    public static void main(
            String[] args) {

        SwingUtilities.invokeLater(() -> {

            new MoodFrame()
                    .setVisible(true);
        });
    }
}