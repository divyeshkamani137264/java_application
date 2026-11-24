import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.sql.*;
import java.text.SimpleDateFormat;

public class DiaryAttachmentsFrame extends JFrame {

    private JTextField noteIdField;
    private JTextField filePathField;

    private JTable attachmentTable;
    private DefaultTableModel tableModel;

    private int selectedAttachmentId = -1;

    private final Color backgroundColor = new Color(250, 240, 248);
    private final Color pastelPink = new Color(235, 180, 205);
    private final Color pastelPurple = new Color(190, 175, 220);
    private final Color darkPurple = new Color(100, 75, 120);

    private final SimpleDateFormat dateFormat =
            new SimpleDateFormat("yyyy-MM-dd");

    public DiaryAttachmentsFrame() {

        setTitle("Digital Diary - Diary Attachments");
        setSize(1000, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(backgroundColor);
        mainPanel.setBorder(
                BorderFactory.createEmptyBorder(15, 20, 15, 20)
        );

        // ================= HEADER =================

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(pastelPink);
        headerPanel.setBorder(
                BorderFactory.createEmptyBorder(15, 20, 15, 20)
        );

        JLabel titleLabel =
                new JLabel("DIARY ATTACHMENTS");

        titleLabel.setFont(
                new Font("Arial", Font.BOLD, 28)
        );

        titleLabel.setForeground(darkPurple);

        headerPanel.add(
                titleLabel,
                BorderLayout.WEST
        );

        // ================= FORM =================

        JPanel formPanel =
                new JPanel(new GridBagLayout());

        formPanel.setBackground(backgroundColor);

        formPanel.setBorder(
                BorderFactory.createTitledBorder(
                        "Attachment Details"
                )
        );

        GridBagConstraints gbc =
                new GridBagConstraints();

        gbc.insets =
                new Insets(8, 8, 8, 8);

        gbc.fill =
                GridBagConstraints.HORIZONTAL;

        // Note ID

        JLabel noteIdLabel =
                new JLabel("Note ID:");

        noteIdField =
                new JTextField(15);

        gbc.gridx = 0;
        gbc.gridy = 0;

        formPanel.add(
                noteIdLabel,
                gbc
        );

        gbc.gridx = 1;

        formPanel.add(
                noteIdField,
                gbc
        );

        // File Path

        JLabel filePathLabel =
                new JLabel("File:");

        filePathField =
                new JTextField(30);

        filePathField.setEditable(false);

        JButton browseButton =
                new JButton("Browse");

        styleButton(browseButton);

        JPanel filePanel =
                new JPanel(new BorderLayout(5, 0));

        filePanel.setBackground(backgroundColor);

        filePanel.add(
                filePathField,
                BorderLayout.CENTER
        );

        filePanel.add(
                browseButton,
                BorderLayout.EAST
        );

        gbc.gridx = 0;
        gbc.gridy = 1;

        formPanel.add(
                filePathLabel,
                gbc
        );

        gbc.gridx = 1;

        formPanel.add(
                filePanel,
                gbc
        );

        // ================= BUTTONS =================

        JButton addButton =
                new JButton("Add Attachment");

        JButton openButton =
                new JButton("Open File");

        JButton deleteButton =
                new JButton("Delete");

        JButton clearButton =
                new JButton("Clear");

        JButton mainMenuButton =
                new JButton("Main Menu");

        styleButton(addButton);
        styleButton(openButton);
        styleButton(deleteButton);
        styleButton(clearButton);
        styleButton(mainMenuButton);

        JPanel buttonPanel =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.CENTER,
                                10,
                                5
                        )
                );

        buttonPanel.setBackground(backgroundColor);

        buttonPanel.add(addButton);
        buttonPanel.add(openButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(clearButton);
        buttonPanel.add(mainMenuButton);

        JPanel topPanel =
                new JPanel(new BorderLayout(10, 10));

        topPanel.setBackground(backgroundColor);

        topPanel.add(
                formPanel,
                BorderLayout.CENTER
        );

        topPanel.add(
                buttonPanel,
                BorderLayout.SOUTH
        );

        // ================= TABLE =================

        String[] columns = {
                "Attachment ID",
                "Note ID",
                "File Name",
                "File Path",
                "Date"
        };

        tableModel =
                new DefaultTableModel(
                        columns,
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

        attachmentTable =
                new JTable(tableModel);

        attachmentTable.setRowHeight(28);

        attachmentTable.setFont(
                new Font(
                        "Arial",
                        Font.PLAIN,
                        13
                )
        );

        attachmentTable
                .getTableHeader()
                .setFont(
                        new Font(
                                "Arial",
                                Font.BOLD,
                                13
                        )
                );

        attachmentTable
                .getTableHeader()
                .setBackground(
                        pastelPurple
                );

        attachmentTable
                .getTableHeader()
                .setForeground(
                        Color.WHITE
                );

        JScrollPane tableScrollPane =
                new JScrollPane(
                        attachmentTable
                );

        JPanel tablePanel =
                new JPanel(
                        new BorderLayout()
                );

        tablePanel.setBackground(
                backgroundColor
        );

        tablePanel.setBorder(
                BorderFactory.createTitledBorder(
                        "Attachment List"
                )
        );

        tablePanel.add(
                tableScrollPane,
                BorderLayout.CENTER
        );

        // ================= FRAME =================

        mainPanel.add(
                headerPanel,
                BorderLayout.NORTH
        );

        mainPanel.add(
                topPanel,
                BorderLayout.CENTER
        );

        mainPanel.add(
                tablePanel,
                BorderLayout.SOUTH
        );

        tablePanel.setPreferredSize(
                new Dimension(950, 320)
        );

        add(mainPanel);

        // ================= EVENTS =================

        browseButton.addActionListener(
                e -> browseFile()
        );

        addButton.addActionListener(
                e -> addAttachment()
        );

        openButton.addActionListener(
                e -> openSelectedFile()
        );

        deleteButton.addActionListener(
                e -> deleteAttachment()
        );

        clearButton.addActionListener(
                e -> clearFields()
        );

        mainMenuButton.addActionListener(
                e -> dispose()
        );

        attachmentTable
                .getSelectionModel()
                .addListSelectionListener(e -> {

                    if (!e.getValueIsAdjusting()) {
                        loadSelectedAttachment();
                    }
                });

        loadAttachments();
    }

    // =========================================================
    // STYLE
    // =========================================================

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
                        14
                )
        );

        button.setFocusPainted(false);

        button.setCursor(
                new Cursor(
                        Cursor.HAND_CURSOR
                )
        );
    }

    // =========================================================
    // BROWSE FILE
    // =========================================================

    private void browseFile() {

        JFileChooser chooser =
                new JFileChooser();

        int result =
                chooser.showOpenDialog(this);

        if (result ==
                JFileChooser.APPROVE_OPTION) {

            File file =
                    chooser.getSelectedFile();

            filePathField.setText(
                    file.getAbsolutePath()
            );
        }
    }

    // =========================================================
    // LOAD ATTACHMENTS
    // =========================================================

    private void loadAttachments() {

        tableModel.setRowCount(0);

        String sql =
                "SELECT ATTACHMENT_ID, NOTE_ID, " +
                "FILE_NAME, FILE_PATH, ATTACHMENT_DATE " +
                "FROM DIARY_ATTACHMENTS " +
                "ORDER BY ATTACHMENT_ID DESC";

        try (
                Connection con =
                        DBConnection.getConnection();

                PreparedStatement ps =
                        con.prepareStatement(sql);

                ResultSet rs =
                        ps.executeQuery()
        ) {

            while (rs.next()) {

                Date date =
                        rs.getDate(
                                "ATTACHMENT_DATE"
                        );

                tableModel.addRow(
                        new Object[]{
                                rs.getInt(
                                        "ATTACHMENT_ID"
                                ),

                                rs.getInt(
                                        "NOTE_ID"
                                ),

                                rs.getString(
                                        "FILE_NAME"
                                ),

                                rs.getString(
                                        "FILE_PATH"
                                ),

                                date != null
                                        ? dateFormat.format(date)
                                        : ""
                        }
                );
            }

        } catch (SQLException ex) {

            JOptionPane.showMessageDialog(
                    this,
                    "Error loading attachments:\n"
                            + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // =========================================================
    // ADD ATTACHMENT
    // =========================================================

    private void addAttachment() {

        if (noteIdField.getText()
                .trim().isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please enter Note ID."
            );

            return;
        }

        if (filePathField.getText()
                .trim().isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please select a file."
            );

            return;
        }

        int noteId;

        try {

            noteId =
                    Integer.parseInt(
                            noteIdField
                                    .getText()
                                    .trim()
                    );

        } catch (NumberFormatException ex) {

            JOptionPane.showMessageDialog(
                    this,
                    "Note ID must be a number."
            );

            return;
        }

        File file =
                new File(
                        filePathField
                                .getText()
                                .trim()
                );

        String sql =
                "INSERT INTO DIARY_ATTACHMENTS " +
                "(ATTACHMENT_ID, NOTE_ID, FILE_NAME, " +
                "FILE_PATH, ATTACHMENT_DATE) " +
                "VALUES " +
                "(ATTACHMENT_SEQ.NEXTVAL, ?, ?, ?, SYSDATE)";

        try (
                Connection con =
                        DBConnection.getConnection();

                PreparedStatement ps =
                        con.prepareStatement(sql)
        ) {

            ps.setInt(
                    1,
                    noteId
            );

            ps.setString(
                    2,
                    file.getName()
            );

            ps.setString(
                    3,
                    file.getAbsolutePath()
            );

            ps.executeUpdate();

            JOptionPane.showMessageDialog(
                    this,
                    "Attachment added successfully!"
            );

            clearFields();

            loadAttachments();

        } catch (SQLException ex) {

            JOptionPane.showMessageDialog(
                    this,
                    "Error adding attachment:\n"
                            + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // =========================================================
    // OPEN SELECTED FILE
    // =========================================================

    private void openSelectedFile() {

        int row =
                attachmentTable
                        .getSelectedRow();

        if (row == -1) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please select an attachment."
            );

            return;
        }

        String path =
                tableModel
                        .getValueAt(row, 3)
                        .toString();

        File file =
                new File(path);

        if (!file.exists()) {

            JOptionPane.showMessageDialog(
                    this,
                    "File not found:\n" + path,
                    "File Error",
                    JOptionPane.ERROR_MESSAGE
            );

            return;
        }

        try {

            Desktop.getDesktop()
                    .open(file);

        } catch (Exception ex) {

            JOptionPane.showMessageDialog(
                    this,
                    "Cannot open file:\n"
                            + ex.getMessage(),
                    "File Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // =========================================================
    // LOAD SELECTED
    // =========================================================

    private void loadSelectedAttachment() {

        int row =
                attachmentTable
                        .getSelectedRow();

        if (row == -1) {
            return;
        }

        selectedAttachmentId =
                Integer.parseInt(
                        tableModel
                                .getValueAt(
                                        row,
                                        0
                                )
                                .toString()
                );

        noteIdField.setText(
                tableModel
                        .getValueAt(row, 1)
                        .toString()
        );

        filePathField.setText(
                tableModel
                        .getValueAt(row, 3)
                        .toString()
        );
    }

    // =========================================================
    // DELETE
    // =========================================================

    private void deleteAttachment() {

        if (selectedAttachmentId == -1) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please select an attachment."
            );

            return;
        }

        int confirm =
                JOptionPane.showConfirmDialog(
                        this,
                        "Delete this attachment record?",
                        "Delete Attachment",
                        JOptionPane.YES_NO_OPTION
                );

        if (confirm !=
                JOptionPane.YES_OPTION) {

            return;
        }

        String sql =
                "DELETE FROM DIARY_ATTACHMENTS " +
                "WHERE ATTACHMENT_ID = ?";

        try (
                Connection con =
                        DBConnection.getConnection();

                PreparedStatement ps =
                        con.prepareStatement(sql)
        ) {

            ps.setInt(
                    1,
                    selectedAttachmentId
            );

            ps.executeUpdate();

            JOptionPane.showMessageDialog(
                    this,
                    "Attachment deleted successfully!"
            );

            clearFields();

            loadAttachments();

        } catch (SQLException ex) {

            JOptionPane.showMessageDialog(
                    this,
                    "Error deleting attachment:\n"
                            + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // =========================================================
    // CLEAR
    // =========================================================

    private void clearFields() {

        noteIdField.setText("");

        filePathField.setText("");

        selectedAttachmentId = -1;

        attachmentTable.clearSelection();
    }

    // =========================================================
    // MAIN
    // =========================================================

    public static void main(String[] args) {

        SwingUtilities.invokeLater(
                () -> new DiaryAttachmentsFrame()
                        .setVisible(true)
        );
    }
}