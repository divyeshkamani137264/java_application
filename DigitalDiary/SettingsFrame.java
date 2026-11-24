

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class SettingsFrame extends JFrame {

    private String username;

   
    public SettingsFrame() {
        this("");
    }

    // Constructor with username
    public SettingsFrame(String username) {
        this.username = username;

        setTitle("Digital Diary - Settings");
        setSize(500, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);

        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.setBackground(new Color(245, 240, 255));

        JLabel title = new JLabel("⚙ Settings");
        title.setFont(new Font("Arial", Font.BOLD, 28));
        title.setBounds(160, 25, 250, 40);
        panel.add(title);

        JLabel userLabel = new JLabel("Username:");
        userLabel.setFont(new Font("Arial", Font.BOLD, 16));
        userLabel.setBounds(70, 90, 120, 30);
        panel.add(userLabel);

        JLabel usernameLabel = new JLabel(
                username.isEmpty() ? "Guest" : username);
        usernameLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        usernameLabel.setBounds(190, 90, 200, 30);
        panel.add(usernameLabel);

        JButton changePasswordBtn = new JButton("Change Password");
        changePasswordBtn.setBounds(120, 140, 250, 45);
        panel.add(changePasswordBtn);

        JButton themeBtn = new JButton("Change Theme");
        themeBtn.setBounds(120, 200, 250, 45);
        panel.add(themeBtn);

        JButton logoutBtn = new JButton("Logout");
        logoutBtn.setBounds(120, 260, 250, 45);
        panel.add(logoutBtn);

        JButton backBtn = new JButton("Back");
        backBtn.setBounds(120, 320, 250, 45);
        panel.add(backBtn);

        // Change Password
        changePasswordBtn.addActionListener(e -> changePassword());

        // Change Theme
        themeBtn.addActionListener(e -> {

            String[] themes = {
                    "Light",
                    "Dark"
            };

            String selected = (String) JOptionPane.showInputDialog(
                    this,
                    "Select Theme:",
                    "Theme",
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    themes,
                    themes[0]);

            if ("Dark".equals(selected)) {

                panel.setBackground(new Color(45, 45, 45));

                title.setForeground(Color.WHITE);
                userLabel.setForeground(Color.WHITE);
                usernameLabel.setForeground(Color.WHITE);

            } else if ("Light".equals(selected)) {

                panel.setBackground(
                        new Color(245, 240, 255));

                title.setForeground(Color.BLACK);
                userLabel.setForeground(Color.BLACK);
                usernameLabel.setForeground(Color.BLACK);
            }
        });

        // Logout
        logoutBtn.addActionListener(e -> {

            int result = JOptionPane.showConfirmDialog(
                    this,
                    "Are you sure you want to logout?",
                    "Logout",
                    JOptionPane.YES_NO_OPTION);

            if (result == JOptionPane.YES_OPTION) {

                dispose();

                new LoginFrame().setVisible(true);
            }
        });

        // Back
        backBtn.addActionListener(e -> dispose());

        add(panel);
    }

    // Change Password Method
    private void changePassword() {

        if (username == null || username.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Username not available!");

            return;
        }

        JPasswordField oldPassword = new JPasswordField();

        JPasswordField newPassword = new JPasswordField();

        JPasswordField confirmPassword = new JPasswordField();

        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));

        panel.add(new JLabel("Old Password:"));
        panel.add(oldPassword);

        panel.add(new JLabel("New Password:"));
        panel.add(newPassword);

        panel.add(new JLabel("Confirm Password:"));
        panel.add(confirmPassword);

        int result = JOptionPane.showConfirmDialog(
                this,
                panel,
                "Change Password",
                JOptionPane.OK_CANCEL_OPTION);

        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        String oldPass = new String(oldPassword.getPassword());

        String newPass = new String(newPassword.getPassword());

        String confirmPass = new String(confirmPassword.getPassword());

        // Empty check
        if (oldPass.isEmpty()
                || newPass.isEmpty()
                || confirmPass.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please fill all fields!");

            return;
        }

        // Password match check
        if (!newPass.equals(confirmPass)) {

            JOptionPane.showMessageDialog(
                    this,
                    "New password and confirm password do not match!");

            return;
        }

        // Old and new password same
        if (oldPass.equals(newPass)) {

            JOptionPane.showMessageDialog(
                    this,
                    "New password must be different from old password!");

            return;
        }

        Connection con = null;
        PreparedStatement checkPs = null;
        PreparedStatement updatePs = null;
        ResultSet rs = null;

        try {

            con = DBConnection.getConnection();

            // Check old password
            String checkSql = "SELECT * FROM users " +
                    "WHERE username = ? AND password = ?";

            checkPs = con.prepareStatement(checkSql);

            checkPs.setString(1, username);
            checkPs.setString(2, oldPass);

            rs = checkPs.executeQuery();

            if (rs.next()) {

                // Update password
                String updateSql = "UPDATE users SET password = ? " +
                        "WHERE username = ?";

                updatePs = con.prepareStatement(updateSql);

                updatePs.setString(1, newPass);
                updatePs.setString(2, username);

                int rows = updatePs.executeUpdate();

                if (rows > 0) {

                    JOptionPane.showMessageDialog(
                            this,
                            "Password changed successfully!");

                } else {

                    JOptionPane.showMessageDialog(
                            this,
                            "Password could not be changed!");
                }

            } else {

                JOptionPane.showMessageDialog(
                        this,
                        "Old password is incorrect!");
            }

        } catch (SQLException ex) {

            JOptionPane.showMessageDialog(
                    this,
                    "Database Error: " +
                            ex.getMessage());

        } finally {

            try {
                if (rs != null)
                    rs.close();
            } catch (SQLException ignored) {
            }

            try {
                if (checkPs != null)
                    checkPs.close();
            } catch (SQLException ignored) {
            }

            try {
                if (updatePs != null)
                    updatePs.close();
            } catch (SQLException ignored) {
            }

            try {
                if (con != null)
                    con.close();
            } catch (SQLException ignored) {
            }
        }
    }

    // Test Main
    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            new SettingsFrame("divyesh")
                    .setVisible(true);
        });
    }
}
