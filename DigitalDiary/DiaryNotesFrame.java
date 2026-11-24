import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.sql.*;

public class DiaryNotesFrame extends JFrame {

    Color creamColor = new Color(248, 244, 230);
    Color oliveColor = new Color(128, 128, 70);
    Color darkOlive = new Color(85, 90, 45);
    Color textColor = new Color(65, 65, 45);

    JTable table;
    DefaultTableModel model;

    JTextField txtTitle;
    JTextArea txtContent;
    JTextArea txtPreview;

    JComboBox<String> categoryCombo;
    JComboBox<String> moodCombo;

    public DiaryNotesFrame() {

        setTitle("Digital Diary - Diary Notes");
        setSize(1100, 750);
        setLocationRelativeTo(null);

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));

        mainPanel.setBackground(creamColor);

        mainPanel.setBorder(
                BorderFactory.createEmptyBorder(
                        20, 20, 20, 20));

        // =========================
        // TITLE
        // =========================

        JLabel title = new JLabel(
                "DIARY NOTES",
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

        // =========================
        // FORM
        // =========================

        JPanel formPanel = new JPanel(
                new GridLayout(
                        4, 2, 10, 10));

        formPanel.setBackground(creamColor);

        formPanel.add(
                createLabel("Title:"));

        txtTitle = new JTextField();

        formPanel.add(txtTitle);

        // CATEGORY

        formPanel.add(
                createLabel("Category:"));

        categoryCombo = new JComboBox<>();

        formPanel.add(categoryCombo);

        // MOOD

        formPanel.add(
                createLabel("Mood:"));

        moodCombo = new JComboBox<>();

        formPanel.add(moodCombo);

        // CONTENT

        formPanel.add(
                createLabel("Content:"));

        txtContent = new JTextArea(4, 20);

        txtContent.setLineWrap(true);
        txtContent.setWrapStyleWord(true);

        txtContent.setFont(
                new Font(
                        "Arial",
                        Font.PLAIN,
                        14));

        formPanel.add(
                new JScrollPane(txtContent));

        mainPanel.add(
                formPanel,
                BorderLayout.NORTH);

        // =========================
        // TABLE
        // =========================

        model = new DefaultTableModel(
                new Object[] {
                        "Note ID",
                        "Title",
                        "Category",
                        "Mood",
                        "Content",
                        "Date"
                }, 0) {

            @Override
            public boolean isCellEditable(
                    int row,
                    int column) {

                return false;
            }
        };

        table = new JTable(model);

        table.setRowHeight(35);

        table.setAutoResizeMode(
                JTable.AUTO_RESIZE_OFF);

        table.getTableHeader()
                .setBackground(oliveColor);

        table.getTableHeader()
                .setForeground(Color.WHITE);

        table.getTableHeader()
                .setFont(
                        new Font(
                                "Arial",
                                Font.BOLD,
                                13));

        table.setFont(
                new Font(
                        "Arial",
                        Font.PLAIN,
                        13));

        // Column widths

        table.getColumnModel()
                .getColumn(0)
                .setPreferredWidth(70);

        table.getColumnModel()
                .getColumn(1)
                .setPreferredWidth(180);

        table.getColumnModel()
                .getColumn(2)
                .setPreferredWidth(140);

        table.getColumnModel()
                .getColumn(3)
                .setPreferredWidth(120);

        table.getColumnModel()
                .getColumn(4)
                .setPreferredWidth(400);

        table.getColumnModel()
                .getColumn(5)
                .setPreferredWidth(120);

        JScrollPane tableScrollPane =
                new JScrollPane(table);

        tableScrollPane.setPreferredSize(
                new Dimension(
                        1000,
                        300));

        // =========================
        // CONTENT PREVIEW
        // =========================

        JPanel previewPanel = new JPanel(
                new BorderLayout(5, 5));

        previewPanel.setBackground(
                creamColor);

        JLabel previewLabel = new JLabel(
                "Selected Note Content:");

        previewLabel.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        14));

        previewLabel.setForeground(
                darkOlive);

        previewPanel.add(
                previewLabel,
                BorderLayout.NORTH);

        txtPreview = new JTextArea();

        txtPreview.setEditable(false);

        txtPreview.setLineWrap(true);
        txtPreview.setWrapStyleWord(true);

        txtPreview.setFont(
                new Font(
                        "Arial",
                        Font.PLAIN,
                        14));

        txtPreview.setBackground(
                Color.WHITE);

        txtPreview.setForeground(
                textColor);

        txtPreview.setBorder(
                BorderFactory.createLineBorder(
                        oliveColor));

        JScrollPane previewScroll =
                new JScrollPane(txtPreview);

        previewPanel.add(
                previewScroll,
                BorderLayout.CENTER);

        JPanel centerPanel = new JPanel(
                new BorderLayout(10, 10));

        centerPanel.setBackground(
                creamColor);

        centerPanel.add(
                tableScrollPane,
                BorderLayout.CENTER);

        centerPanel.add(
                previewPanel,
                BorderLayout.SOUTH);

        mainPanel.add(
                centerPanel,
                BorderLayout.CENTER);

        // =========================
        // BUTTONS
        // =========================

        JPanel buttonPanel = new JPanel(
                new FlowLayout(
                        FlowLayout.CENTER,
                        10,
                        10));

        buttonPanel.setBackground(
                creamColor);

        JButton addButton =
                new JButton("Add Note");

        JButton deleteButton =
                new JButton("Delete Note");

        JButton favoriteButton =
                new JButton("Add to Favorites");

        JButton refreshButton =
                new JButton("Refresh");

        JButton mainMenuButton =
                new JButton("← Main Menu");

        styleButton(addButton);
        styleButton(deleteButton);
        styleButton(favoriteButton);
        styleButton(refreshButton);
        styleButton(mainMenuButton);

        buttonPanel.add(addButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(favoriteButton);
        buttonPanel.add(refreshButton);
        buttonPanel.add(mainMenuButton);

        mainPanel.add(
                buttonPanel,
                BorderLayout.SOUTH);

        add(mainPanel);

        // =========================
        // LOAD DATA
        // =========================

        loadCategories();
        loadMoods();
        loadNotes();

        // =========================
        // BUTTON EVENTS
        // =========================

        addButton.addActionListener(
                e -> addNote());

        deleteButton.addActionListener(
                e -> deleteNote());

        favoriteButton.addActionListener(
                e -> addToFavorites());

        refreshButton.addActionListener(
                e -> loadNotes());

        mainMenuButton.addActionListener(
                e -> {

                    dispose();

                    new MainFrame()
                            .setVisible(true);
                });

        // =========================
        // TABLE CLICK
        // =========================

        table.getSelectionModel()
                .addListSelectionListener(e -> {

                    if (!e.getValueIsAdjusting()) {

                        showSelectedContent();
                    }
                });
    }

    // =====================================================
    // LABEL
    // =====================================================

    private JLabel createLabel(
            String text) {

        JLabel label =
                new JLabel(text);

        label.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        13));

        label.setForeground(
                textColor);

        return label;
    }

    // =====================================================
    // BUTTON STYLE
    // =====================================================

    private void styleButton(
            JButton button) {

        button.setBackground(
                oliveColor);

        button.setForeground(
                Color.WHITE);

        button.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        13));

        button.setFocusPainted(false);
    }

    // =====================================================
    // LOAD CATEGORIES
    // =====================================================

    private void loadCategories() {

        categoryCombo.removeAllItems();

        String sql =
                "SELECT CATEGORY_ID, CATEGORY_NAME " +
                "FROM CATEGORIES " +
                "ORDER BY CATEGORY_NAME";

        try (
                Connection con =
                        DBConnection.getConnection();

                Statement st =
                        con.createStatement();

                ResultSet rs =
                        st.executeQuery(sql)) {

            while (rs.next()) {

                categoryCombo.addItem(
                        rs.getInt(
                                "CATEGORY_ID")
                                + " - "
                                + rs.getString(
                                        "CATEGORY_NAME"));
            }

        } catch (SQLException ex) {

            JOptionPane.showMessageDialog(
                    this,
                    "Category loading error:\n"
                            + ex.getMessage());
        }
    }

    // =====================================================
    // LOAD MOODS
    // =====================================================

    private void loadMoods() {

        moodCombo.removeAllItems();

        String sql =
                "SELECT MOOD_ID, MOOD_NAME " +
                "FROM MOODS " +
                "ORDER BY MOOD_NAME";

        try (
                Connection con =
                        DBConnection.getConnection();

                Statement st =
                        con.createStatement();

                ResultSet rs =
                        st.executeQuery(sql)) {

            while (rs.next()) {

                moodCombo.addItem(
                        rs.getInt(
                                "MOOD_ID")
                                + " - "
                                + rs.getString(
                                        "MOOD_NAME"));
            }

        } catch (SQLException ex) {

            JOptionPane.showMessageDialog(
                    this,
                    "Mood loading error:\n"
                            + ex.getMessage());
        }
    }

    // =====================================================
    // LOAD NOTES
    // =====================================================

    private void loadNotes() {

        model.setRowCount(0);

        txtPreview.setText("");

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
                "ORDER BY d.NOTE_DATE DESC";

        try (
                Connection con =
                        DBConnection.getConnection();

                Statement st =
                        con.createStatement();

                ResultSet rs =
                        st.executeQuery(sql)) {

            while (rs.next()) {

                model.addRow(
                        new Object[] {

                                rs.getInt(
                                        "NOTE_ID"),

                                rs.getString(
                                        "TITLE"),

                                rs.getString(
                                        "CATEGORY_NAME"),

                                rs.getString(
                                        "MOOD"),

                                rs.getString(
                                        "CONTENT"),

                                rs.getDate(
                                        "NOTE_DATE")
                        });
            }

        } catch (SQLException ex) {

            JOptionPane.showMessageDialog(
                    this,
                    "Notes loading error:\n"
                            + ex.getMessage());
        }
    }

    // =====================================================
    // ADD NOTE
    // =====================================================

    private void addNote() {

        String title =
                txtTitle.getText().trim();

        String content =
                txtContent.getText().trim();

        if (title.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please enter note title.");

            return;
        }

        Integer categoryId = null;

        if (categoryCombo
                .getSelectedIndex() >= 0) {

            String value =
                    categoryCombo
                            .getSelectedItem()
                            .toString();

            categoryId =
                    Integer.parseInt(
                            value.substring(
                                    0,
                                    value.indexOf("-"))
                                    .trim());
        }

        String mood = null;

        if (moodCombo
                .getSelectedIndex() >= 0) {

            String value =
                    moodCombo
                            .getSelectedItem()
                            .toString();

            mood =
                    value.substring(
                            value.indexOf("-") + 1)
                            .trim();
        }

        String sql =
                "INSERT INTO DIARY_NOTES " +
                "(NOTE_ID, USER_ID, CATEGORY_ID, " +
                "TITLE, CONTENT, NOTE_DATE, MOOD) " +
                "VALUES " +
                "(DIARY_NOTE_SEQ.NEXTVAL, ?, ?, ?, ?, " +
                "SYSDATE, ?)";

        try (
                Connection con =
                        DBConnection.getConnection();

                PreparedStatement pst =
                        con.prepareStatement(sql)) {

            // USER ID = 1
            pst.setInt(1, 1);

            if (categoryId == null) {

                pst.setNull(
                        2,
                        Types.NUMERIC);

            } else {

                pst.setInt(
                        2,
                        categoryId);
            }

            pst.setString(
                    3,
                    title);

            pst.setString(
                    4,
                    content);

            if (mood == null ||
                    mood.isEmpty()) {

                pst.setNull(
                        5,
                        Types.VARCHAR);

            } else {

                pst.setString(
                        5,
                        mood);
            }

            pst.executeUpdate();

            JOptionPane.showMessageDialog(
                    this,
                    "Diary note added successfully!");

            txtTitle.setText("");
            txtContent.setText("");

            loadNotes();

        } catch (SQLException ex) {

            JOptionPane.showMessageDialog(
                    this,
                    "Error adding note:\n"
                            + ex.getMessage());
        }
    }

    // =====================================================
    // ADD SELECTED NOTE TO FAVORITES
    // =====================================================

    private void addToFavorites() {

        int row = table.getSelectedRow();

        if (row == -1) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please select a note first.",
                    "Select Note",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int noteId = Integer.parseInt(
                table.getValueAt(row, 0).toString());

        // This project currently uses USER_ID = 1.
        int userId = 1;

        String checkSql =
                "SELECT COUNT(*) FROM FAVORITE_NOTES " +
                "WHERE NOTE_ID = ? AND USER_ID = ?";

        String nextIdSql =
                "SELECT NVL(MAX(FAVORITE_ID), 0) + 1 " +
                "FROM FAVORITE_NOTES";

        String insertSql =
                "INSERT INTO FAVORITE_NOTES " +
                "(FAVORITE_ID, NOTE_ID, USER_ID, FAVORITE_DATE) " +
                "VALUES (?, ?, ?, SYSDATE)";

        try (Connection con = DBConnection.getConnection()) {

            // Avoid adding the same note to favorites twice.
            try (PreparedStatement check = con.prepareStatement(checkSql)) {
                check.setInt(1, noteId);
                check.setInt(2, userId);

                try (ResultSet rs = check.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0) {
                        JOptionPane.showMessageDialog(
                                this,
                                "This note is already in Favorites.");
                        return;
                    }
                }
            }

            // Generate the next ID without requiring FAVORITE_SEQ.
            int favoriteId;
            try (Statement st = con.createStatement();
                 ResultSet rs = st.executeQuery(nextIdSql)) {
                rs.next();
                favoriteId = rs.getInt(1);
            }

            try (PreparedStatement pst = con.prepareStatement(insertSql)) {
                pst.setInt(1, favoriteId);
                pst.setInt(2, noteId);
                pst.setInt(3, userId);

                int result = pst.executeUpdate();

                if (result > 0) {
                    JOptionPane.showMessageDialog(
                            this,
                            "Note added to Favorites successfully!");
                }
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Could not add note to Favorites:\\n" + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // =====================================================
    // SHOW SELECTED CONTENT
    // =====================================================

    private void showSelectedContent() {

        int row =
                table.getSelectedRow();

        if (row == -1) {

            txtPreview.setText("");

            return;
        }

        Object content =
                table.getValueAt(
                        row,
                        4);

        if (content == null) {

            txtPreview.setText(
                    "No content available.");

        } else {

            txtPreview.setText(
                    content.toString());
        }

        txtPreview.setCaretPosition(0);
    }

    // =====================================================
    // DELETE NOTE
    // =====================================================

    private void deleteNote() {

        int row =
                table.getSelectedRow();

        if (row == -1) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please select a note.");

            return;
        }

        int noteId =
                Integer.parseInt(
                        table.getValueAt(
                                row,
                                0)
                                .toString());

        int confirm =
                JOptionPane.showConfirmDialog(
                        this,
                        "Delete selected note?",
                        "Confirm Delete",
                        JOptionPane.YES_NO_OPTION);

        if (confirm !=
                JOptionPane.YES_OPTION) {

            return;
        }

        String sql =
                "DELETE FROM DIARY_NOTES " +
                "WHERE NOTE_ID = ?";

        try (
                Connection con =
                        DBConnection.getConnection();

                PreparedStatement pst =
                        con.prepareStatement(sql)) {

            pst.setInt(
                    1,
                    noteId);

            pst.executeUpdate();

            JOptionPane.showMessageDialog(
                    this,
                    "Note deleted successfully.");

            loadNotes();

        } catch (SQLException ex) {

            JOptionPane.showMessageDialog(
                    this,
                    "Delete error:\n"
                            + ex.getMessage());
        }
    }

    // =====================================================
    // MAIN
    // =====================================================

    public static void main(
            String[] args) {

        SwingUtilities.invokeLater(
                () -> {

                    new DiaryNotesFrame()
                            .setVisible(true);
                });
    }
}