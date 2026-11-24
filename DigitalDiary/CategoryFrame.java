
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class CategoryFrame extends JFrame {

    private final Color creamColor = new Color(248, 244, 230);
    private final Color oliveColor = new Color(128, 128, 70);
    private final Color darkOlive = new Color(85, 90, 45);
    private final Color textColor = new Color(65, 65, 45);

    private JTable categoryTable;
    private DefaultTableModel model;
    private JTextField txtCategoryName;
    private JTextField txtDescription;

    public CategoryFrame() {
        setTitle("Digital Diary - Categories");
        setSize(1000, 680);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBackground(creamColor);
        mainPanel.setBorder(
                BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel(
                "CATEGORY MANAGEMENT", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 25));
        title.setForeground(darkOlive);
        mainPanel.add(title, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        formPanel.setBackground(creamColor);

        JLabel nameLabel = new JLabel("Category Name:");
        nameLabel.setForeground(textColor);
        nameLabel.setFont(new Font("Arial", Font.BOLD, 13));

        txtCategoryName = new JTextField();

        JLabel descriptionLabel = new JLabel("Description:");
        descriptionLabel.setForeground(textColor);
        descriptionLabel.setFont(new Font("Arial", Font.BOLD, 13));

        txtDescription = new JTextField();

        formPanel.add(nameLabel);
        formPanel.add(txtCategoryName);
        formPanel.add(descriptionLabel);
        formPanel.add(txtDescription);

        model = new DefaultTableModel(
                new Object[]{"Category ID", "Category Name", "Description"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        categoryTable = new JTable(model);
        categoryTable.setRowHeight(30);
        categoryTable.getTableHeader().setBackground(oliveColor);
        categoryTable.getTableHeader().setForeground(Color.WHITE);
        categoryTable.getTableHeader().setFont(
                new Font("Arial", Font.BOLD, 13));

        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.setBackground(creamColor);
        centerPanel.add(formPanel, BorderLayout.NORTH);
        centerPanel.add(new JScrollPane(categoryTable), BorderLayout.CENTER);

        mainPanel.add(centerPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(
                new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBackground(creamColor);

        JButton addButton = new JButton("Add Category");
        JButton deleteButton = new JButton("Delete Category");
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

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        add(mainPanel);

        addButton.addActionListener(e -> addCategory());
        deleteButton.addActionListener(e -> deleteCategory());
        refreshButton.addActionListener(e -> loadCategories());

        mainMenuButton.addActionListener(e -> {
            dispose();
            new MainFrame().setVisible(true);
        });

        loadCategories();
    }

    private void styleButton(JButton button) {
        button.setBackground(oliveColor);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 13));
        button.setFocusPainted(false);
    }

    private void loadCategories() {
        model.setRowCount(0);

        String sql = "SELECT CATEGORY_ID, CATEGORY_NAME, DESCRIPTION " +
                     "FROM CATEGORIES ORDER BY CATEGORY_ID";

        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("CATEGORY_ID"),
                        rs.getString("CATEGORY_NAME"),
                        rs.getString("DESCRIPTION")
                });
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Error loading categories:\n" + ex.getMessage());
        }
    }

    private void addCategory() {
        String name = txtCategoryName.getText().trim();
        String description = txtDescription.getText().trim();

        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please enter category name.");
            txtCategoryName.requestFocus();
            return;
        }

        String sql = "INSERT INTO CATEGORIES " +
                "(CATEGORY_ID, CATEGORY_NAME, DESCRIPTION) " +
                "VALUES (CATEGORY_SEQ.NEXTVAL, ?, ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setString(1, name);
            pst.setString(2, description);

            int rows = pst.executeUpdate();

            if (rows > 0) {
                JOptionPane.showMessageDialog(this,
                        "Category added successfully!\nCategory ID is generated automatically.");

                txtCategoryName.setText("");
                txtDescription.setText("");
                loadCategories();
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Error adding category:\n" + ex.getMessage());
        }
    }

    private void deleteCategory() {
        int row = categoryTable.getSelectedRow();

        if (row == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a category.");
            return;
        }

        int categoryId = (int) model.getValueAt(row, 0);

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Delete category ID " + categoryId + "?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        String sql = "DELETE FROM CATEGORIES WHERE CATEGORY_ID = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setInt(1, categoryId);

            int rows = pst.executeUpdate();

            if (rows > 0) {
                JOptionPane.showMessageDialog(this,
                        "Category deleted successfully.");
                loadCategories();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Category was not found.");
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Delete error:\n" + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() ->
                new CategoryFrame().setVisible(true));
    }
}
