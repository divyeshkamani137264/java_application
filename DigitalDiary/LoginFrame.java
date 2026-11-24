

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class LoginFrame extends JFrame {

    private JTextField txtUsername;
    private JPasswordField txtPassword;

    private final Color backgroundColor = new Color(250, 240, 248);

    private final Color pastelPurple = new Color(190, 175, 220);

    private final Color darkPurple = new Color(100, 75, 120);

    private final Color textColor = new Color(70, 55, 75);

    public LoginFrame() {

        setTitle("Digital Diary - Login");
        setSize(500, 400);
        setLocationRelativeTo(null);

        setDefaultCloseOperation(
                JFrame.EXIT_ON_CLOSE);

        setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));

        mainPanel.setBackground(
                backgroundColor);

        mainPanel.setBorder(
                BorderFactory.createEmptyBorder(
                        30, 40, 30, 40));

        JLabel title = new JLabel(
                "DIGITAL DIARY",
                SwingConstants.CENTER);

        title.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        28));

        title.setForeground(darkPurple);

        mainPanel.add(
                title,
                BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridBagLayout());

        formPanel.setBackground(
                backgroundColor);

        GridBagConstraints gbc = new GridBagConstraints();

        gbc.insets = new Insets(12, 10, 12, 10);

        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel usernameLabel = createLabel("Username:");

        txtUsername = new JTextField();

        JLabel passwordLabel = createLabel("Password:");

        txtPassword = new JPasswordField();

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.3;

        formPanel.add(
                usernameLabel,
                gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;

        formPanel.add(
                txtUsername,
                gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.3;

        formPanel.add(
                passwordLabel,
                gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;

        formPanel.add(
                txtPassword,
                gbc);

        mainPanel.add(
                formPanel,
                BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();

        buttonPanel.setBackground(
                backgroundColor);

        JButton loginButton = new JButton("LOGIN");

        JButton forgotButton = new JButton("Forgot Password?");

        styleButton(loginButton);

        buttonPanel.add(loginButton);
        buttonPanel.add(forgotButton);

        mainPanel.add(
                buttonPanel,
                BorderLayout.SOUTH);

        add(mainPanel);

        loginButton.addActionListener(
                e -> login());

        txtPassword.addActionListener(
                e -> login());

        forgotButton.addActionListener(
                e -> forgotPassword());
    }

    private JLabel createLabel(String text) {

        JLabel label = new JLabel(text);

        label.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        14));

        label.setForeground(textColor);

        return label;
    }

    private void styleButton(
            JButton button) {

        button.setBackground(
                pastelPurple);

        button.setForeground(
                Color.WHITE);

        button.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        14));

        button.setFocusPainted(false);

        button.setBorder(
                BorderFactory.createEmptyBorder(
                        10, 20, 10, 20));
    }

    private void login() {

        String username = txtUsername.getText().trim();

        String password = new String(
                txtPassword.getPassword());

        if (username.isEmpty()
                || password.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please enter username and password.");

            return;
        }

        String sql = "SELECT USER_ID, FULL_NAME " +
                "FROM USERS " +
                "WHERE USERNAME = ? " +
                "AND PASSWORD = ?";

        try (
                Connection con = DBConnection.getConnection();

                PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setString(1, username);
            pst.setString(2, password);

            ResultSet rs = pst.executeQuery();

            if (rs.next()) {

                String fullName = rs.getString("FULL_NAME");

                JOptionPane.showMessageDialog(
                        this,
                        "Welcome " + fullName + "!");

                dispose();

                // Username MainFrame ને આપીએ છીએ
                MainFrame frame = new MainFrame(username);

                frame.setVisible(true);

            } else {

                JOptionPane.showMessageDialog(
                        this,
                        "Invalid username or password.",
                        "Login Failed",
                        JOptionPane.ERROR_MESSAGE);
            }

        } catch (SQLException ex) {

            JOptionPane.showMessageDialog(
                    this,
                    "Database connection error:\n"
                            + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void forgotPassword() {

        String username = JOptionPane.showInputDialog(
                this,
                "Enter your username:",
                "Forgot Password",
                JOptionPane.QUESTION_MESSAGE);

        if (username == null ||
                username.trim().isEmpty()) {

            return;
        }

        String sql = "SELECT FULL_NAME " +
                "FROM USERS " +
                "WHERE USERNAME = ?";

        try (
                Connection con = DBConnection.getConnection();

                PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setString(
                    1,
                    username.trim());

            ResultSet rs = pst.executeQuery();

            if (rs.next()) {

                JOptionPane.showMessageDialog(
                        this,
                        "Username found.\n" +
                                "Please use the password recovery " +
                                "option or contact administrator.",
                        "Forgot Password",
                        JOptionPane.INFORMATION_MESSAGE);

            } else {

                JOptionPane.showMessageDialog(
                        this,
                        "Username not found.",
                        "Forgot Password",
                        JOptionPane.ERROR_MESSAGE);
            }

        } catch (SQLException ex) {

            JOptionPane.showMessageDialog(
                    this,
                    "Database Error:\n"
                            + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            new LoginFrame()
                    .setVisible(true);
        });
    }
}
