import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class SearchNotesFrame extends JFrame {

    Color creamColor = new Color(248, 244, 230);
    Color oliveColor = new Color(128, 128, 70);
    Color darkOlive = new Color(85, 90, 45);
    Color textColor = new Color(65, 65, 45);

    JTextField txtSearch;
    JComboBox<String> searchType;

    JTable table;
    DefaultTableModel model;

    JTextArea txtPreview;

    public SearchNotesFrame() {

        setTitle("Digital Diary - Search Notes");
        setSize(1100, 700);
        setLocationRelativeTo(null);

        setDefaultCloseOperation(
                JFrame.DISPOSE_ON_CLOSE);

        JPanel mainPanel =
                new JPanel(new BorderLayout(15, 15));

        mainPanel.setBackground(creamColor);

        mainPanel.setBorder(
                BorderFactory.createEmptyBorder(
                        20, 20, 20, 20));

        // =========================
        // TITLE
        // =========================

        JLabel title =
                new JLabel(
                        "SEARCH DIARY NOTES",
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
        // SEARCH PANEL
        // =========================

        JPanel searchPanel =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.CENTER,
                                10,
                                10));

        searchPanel.setBackground(
                creamColor);

        JLabel searchLabel =
                new JLabel("Search:");

        searchLabel.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        14));

        searchLabel.setForeground(
                textColor);

        searchType =
                new JComboBox<>(
                        new String[] {
                                "Title",
                                "Content",
                                "Category",
                                "Mood",
                                "All"
                        });

        txtSearch =
                new JTextField(30);

        JButton searchButton =
                new JButton("Search");

        JButton clearButton =
                new JButton("Clear");

        styleButton(searchButton);
        styleButton(clearButton);

        searchPanel.add(searchLabel);
        searchPanel.add(searchType);
        searchPanel.add(txtSearch);
        searchPanel.add(searchButton);
        searchPanel.add(clearButton);

        mainPanel.add(
                searchPanel,
                BorderLayout.PAGE_START);

        // =========================
        // TABLE
        // =========================

        model =
                new DefaultTableModel(
                        new Object[] {
                                "Note ID",
                                "Title",
                                "Category",
                                "Mood",
                                "Content",
                                "Date"
                        },
                        0) {

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

        JScrollPane tableScroll =
                new JScrollPane(table);

        // =========================
        // PREVIEW
        // =========================

        JPanel previewPanel =
                new JPanel(
                        new BorderLayout(5, 5));

        previewPanel.setBackground(
                creamColor);

        JLabel previewLabel =
                new JLabel(
                        "Selected Note Content:");

        previewLabel.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        14));

        previewLabel.setForeground(
                darkOlive);

        txtPreview =
                new JTextArea();

        txtPreview.setEditable(false);

        txtPreview.setLineWrap(true);
        txtPreview.setWrapStyleWord(true);

        txtPreview.setFont(
                new Font(
                        "Arial",
                        Font.PLAIN,
                        14));

        JScrollPane previewScroll =
                new JScrollPane(txtPreview);

        previewPanel.add(
                previewLabel,
                BorderLayout.NORTH);

        previewPanel.add(
                previewScroll,
                BorderLayout.CENTER);

        previewPanel.setPreferredSize(
                new Dimension(
                        1000,
                        130));

        // =========================
        // CENTER
        // =========================

        JPanel centerPanel =
                new JPanel(
                        new BorderLayout(10, 10));

        centerPanel.setBackground(
                creamColor);

        centerPanel.add(
                tableScroll,
                BorderLayout.CENTER);

        centerPanel.add(
                previewPanel,
                BorderLayout.SOUTH);

        mainPanel.add(
                centerPanel,
                BorderLayout.CENTER);

        // =========================
        // BOTTOM BUTTON
        // =========================

        JPanel bottomPanel =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.CENTER));

        bottomPanel.setBackground(
                creamColor);

        JButton mainMenuButton =
                new JButton("← Main Menu");

        styleButton(mainMenuButton);

        bottomPanel.add(
                mainMenuButton);

        mainPanel.add(
                bottomPanel,
                BorderLayout.SOUTH);

        add(mainPanel);

        // =========================
        // LOAD ALL NOTES
        // =========================

        loadAllNotes();

        // =========================
        // SEARCH BUTTON
        // =========================

        searchButton.addActionListener(
                e -> searchNotes());

        // =========================
        // CLEAR BUTTON
        // =========================

        clearButton.addActionListener(
                e -> {

                    txtSearch.setText("");

                    loadAllNotes();
                });

        // =========================
        // MAIN MENU
        // =========================

        mainMenuButton.addActionListener(
                e -> {

                    dispose();

                    new MainFrame()
                            .setVisible(true);
                });

        // =========================
        // TABLE SELECTION
        // =========================

        table.getSelectionModel()
                .addListSelectionListener(
                        e -> {

                            if (!e.getValueIsAdjusting()) {

                                showContent();
                            }
                        });
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
    // LOAD ALL NOTES
    // =====================================================

    private void loadAllNotes() {

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

                                rs.getInt("NOTE_ID"),

                                rs.getString("TITLE"),

                                rs.getString(
                                        "CATEGORY_NAME"),

                                rs.getString("MOOD"),

                                rs.getString("CONTENT"),

                                rs.getDate("NOTE_DATE")
                        });
            }

        } catch (SQLException ex) {

            JOptionPane.showMessageDialog(
                    this,
                    "Error loading notes:\n"
                            + ex.getMessage());
        }
    }

    // =====================================================
    // SEARCH NOTES
    // =====================================================

    private void searchNotes() {

        String searchText =
                txtSearch.getText().trim();

        if (searchText.isEmpty()) {

            loadAllNotes();

            return;
        }

        String type =
                searchType
                        .getSelectedItem()
                        .toString();

        String condition;

        switch (type) {

            case "Title":

                condition =
                        "UPPER(d.TITLE) LIKE UPPER(?)";

                break;

            case "Content":

                condition =
                        "UPPER(d.CONTENT) LIKE UPPER(?)";

                break;

            case "Category":

                condition =
                        "UPPER(c.CATEGORY_NAME) LIKE UPPER(?)";

                break;

            case "Mood":

                condition =
                        "UPPER(d.MOOD) LIKE UPPER(?)";

                break;

            default:

                condition =
                        "(UPPER(d.TITLE) LIKE UPPER(?) " +
                        "OR UPPER(d.CONTENT) LIKE UPPER(?) " +
                        "OR UPPER(c.CATEGORY_NAME) LIKE UPPER(?) " +
                        "OR UPPER(d.MOOD) LIKE UPPER(?))";
        }

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
                "WHERE " + condition +
                " ORDER BY d.NOTE_DATE DESC";

        model.setRowCount(0);

        txtPreview.setText("");

        try (
                Connection con =
                        DBConnection.getConnection();

                PreparedStatement pst =
                        con.prepareStatement(sql)) {

            String value =
                    "%" + searchText + "%";

            if (type.equals("All")) {

                pst.setString(1, value);
                pst.setString(2, value);
                pst.setString(3, value);
                pst.setString(4, value);

            } else {

                pst.setString(1, value);
            }

            ResultSet rs =
                    pst.executeQuery();

            while (rs.next()) {

                model.addRow(
                        new Object[] {

                                rs.getInt("NOTE_ID"),

                                rs.getString("TITLE"),

                                rs.getString(
                                        "CATEGORY_NAME"),

                                rs.getString("MOOD"),

                                rs.getString("CONTENT"),

                                rs.getDate("NOTE_DATE")
                        });
            }

            if (model.getRowCount() == 0) {

                JOptionPane.showMessageDialog(
                        this,
                        "No matching diary notes found.");
            }

        } catch (SQLException ex) {

            JOptionPane.showMessageDialog(
                    this,
                    "Search error:\n"
                            + ex.getMessage());
        }
    }

    // =====================================================
    // SHOW CONTENT
    // =====================================================

    private void showContent() {

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
    // MAIN
    // =====================================================

    public static void main(
            String[] args) {

        SwingUtilities.invokeLater(
                () -> {

                    new SearchNotesFrame()
                            .setVisible(true);
                });
    }
}