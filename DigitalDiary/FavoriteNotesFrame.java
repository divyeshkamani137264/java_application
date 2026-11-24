import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class FavoriteNotesFrame extends JFrame {

    Color creamColor = new Color(248, 244, 230);
    Color oliveColor = new Color(128, 128, 70);
    Color darkOlive = new Color(85, 90, 45);
    Color textColor = new Color(65, 65, 45);

    JTable favoriteTable;
    DefaultTableModel model;

    public FavoriteNotesFrame() {

        setTitle("Digital Diary - Favorite Notes");
        setSize(950, 600);
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
                "FAVORITE NOTES",
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

        // TABLE

        model = new DefaultTableModel(
                new Object[] {
                        "Favorite ID",
                        "Note ID",
                        "User ID",
                        "Title",
                        "Content",
                        "Note Date",
                        "Favorite Date"
                }, 0);

        favoriteTable = new JTable(model);

        favoriteTable.setRowHeight(25);

        favoriteTable.setAutoResizeMode(
                JTable.AUTO_RESIZE_LAST_COLUMN);

        favoriteTable.getTableHeader()
                .setBackground(oliveColor);

        favoriteTable.getTableHeader()
                .setForeground(Color.WHITE);

        favoriteTable.getTableHeader()
                .setFont(
                        new Font(
                                "Arial",
                                Font.BOLD,
                                13));

        JScrollPane scrollPane = new JScrollPane(
                favoriteTable);

        mainPanel.add(
                scrollPane,
                BorderLayout.CENTER);

        // BUTTON PANEL

        JPanel buttonPanel = new JPanel(
                new FlowLayout(
                        FlowLayout.CENTER,
                        12,
                        10));

        buttonPanel.setBackground(creamColor);

        JButton refreshButton = new JButton("Refresh");

        JButton deleteButton = new JButton("Remove Favorite");

        JButton viewButton = new JButton("View Note");

        JButton mainMenuButton = new JButton("← Main Menu");

        styleButton(refreshButton);
        styleButton(deleteButton);
        styleButton(viewButton);
        styleButton(mainMenuButton);

        buttonPanel.add(refreshButton);
        buttonPanel.add(viewButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(mainMenuButton);

        mainPanel.add(
                buttonPanel,
                BorderLayout.SOUTH);

        add(mainPanel);

        // ACTIONS

        refreshButton.addActionListener(
                e -> loadFavorites());

        deleteButton.addActionListener(
                e -> removeFavorite());

        viewButton.addActionListener(
                e -> viewNote());

        mainMenuButton.addActionListener(e -> {

            dispose();

            new MainFrame().setVisible(true);
        });

        loadFavorites();
    }

    // BUTTON STYLE

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

    // LOAD FAVORITE NOTES

    private void loadFavorites() {

        model.setRowCount(0);

        String sql = "SELECT f.FAVORITE_ID, " +
                "f.NOTE_ID, " +
                "f.USER_ID, " +
                "d.TITLE, " +
                "d.CONTENT, " +
                "d.NOTE_DATE, " +
                "f.FAVORITE_DATE " +
                "FROM FAVORITE_NOTES f " +
                "JOIN DIARY_NOTES d " +
                "ON f.NOTE_ID = d.NOTE_ID " +
                "ORDER BY f.FAVORITE_DATE DESC";

        try (
                Connection con = DBConnection.getConnection();

                Statement st = con.createStatement();

                ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {

                model.addRow(
                        new Object[] {
                                rs.getInt(
                                        "FAVORITE_ID"),
                                rs.getInt(
                                        "NOTE_ID"),
                                rs.getInt(
                                        "USER_ID"),
                                rs.getString(
                                        "TITLE"),
                                rs.getString(
                                        "CONTENT"),
                                rs.getDate(
                                        "NOTE_DATE"),
                                rs.getDate(
                                        "FAVORITE_DATE")
                        });
            }

        } catch (SQLException ex) {

            JOptionPane.showMessageDialog(
                    this,
                    "Error loading favorite notes:\n"
                            + ex.getMessage());
        }
    }

    // VIEW NOTE

    private void viewNote() {

        int row = favoriteTable.getSelectedRow();

        if (row == -1) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please select a favorite note.");

            return;
        }

        String title = String.valueOf(
                favoriteTable.getValueAt(
                        row,
                        3));

        String content = String.valueOf(
                favoriteTable.getValueAt(
                        row,
                        4));

        JTextArea textArea = new JTextArea(content);

        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setEditable(false);

        textArea.setFont(
                new Font(
                        "Arial",
                        Font.PLAIN,
                        14));

        JScrollPane scrollPane = new JScrollPane(textArea);

        scrollPane.setPreferredSize(
                new Dimension(500, 300));

        JOptionPane.showMessageDialog(
                this,
                scrollPane,
                title,
                JOptionPane.INFORMATION_MESSAGE);
    }

    // REMOVE FAVORITE

    private void removeFavorite() {

        int row = favoriteTable.getSelectedRow();

        if (row == -1) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please select a favorite note.");

            return;
        }

        int favoriteId = Integer.parseInt(
                favoriteTable
                        .getValueAt(
                                row,
                                0)
                        .toString());

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Remove this note from favorites?",
                "Confirm",
                JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        String sql = "DELETE FROM FAVORITE_NOTES " +
                "WHERE FAVORITE_ID = ?";

        try (
                Connection con = DBConnection.getConnection();

                PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setInt(
                    1,
                    favoriteId);

            pst.executeUpdate();

            JOptionPane.showMessageDialog(
                    this,
                    "Note removed from favorites.");

            loadFavorites();

        } catch (SQLException ex) {

            JOptionPane.showMessageDialog(
                    this,
                    "Error removing favorite:\n"
                            + ex.getMessage());
        }
    }

    public static void main(
            String[] args) {

        SwingUtilities.invokeLater(() -> {

            new FavoriteNotesFrame()
                    .setVisible(true);
        });
    }
}