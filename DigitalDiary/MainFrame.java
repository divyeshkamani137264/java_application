import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    private String username;

    private final Color backgroundColor = new Color(250, 240, 248);
    private final Color pastelPink = new Color(235, 180, 205);
    private final Color pastelPurple = new Color(190, 175, 220);
    private final Color darkPurple = new Color(100, 75, 120);
    private final Color textColor = new Color(70, 55, 75);

    public MainFrame() {
        this("divyesh");
    }

    public MainFrame(String username) {

        this.username = username;

       setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
setResizable(true);
setMinimumSize(new Dimension(900, 650));
setSize(1100, 800);
setLocationRelativeTo(null);
setExtendedState(JFrame.MAXIMIZED_BOTH);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(backgroundColor);

        // ================= HEADER =================

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(pastelPink);
        headerPanel.setBorder(
                BorderFactory.createEmptyBorder(20, 30, 20, 30)
        );

        JLabel title = new JLabel("DIGITAL DIARY");
        title.setFont(new Font("Arial", Font.BOLD, 30));
        title.setForeground(darkPurple);

        headerPanel.add(title, BorderLayout.WEST);

        JLabel userLabel = new JLabel("Welcome, " + username);
        userLabel.setFont(new Font("Arial", Font.BOLD, 16));
        userLabel.setForeground(textColor);

        headerPanel.add(userLabel, BorderLayout.EAST);

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // ================= BUTTON PANEL =================

        JPanel buttonPanel = new JPanel(
                new GridLayout(4, 3, 15, 15)
        );

        buttonPanel.setBackground(backgroundColor);

        buttonPanel.setBorder(
                BorderFactory.createEmptyBorder(
                        25, 50, 25, 50
                )
        );

        // ================= BUTTONS =================

        JButton diaryButton =
                new JButton("My Diary");

        JButton categoryButton =
                new JButton("Categories");

        JButton reminderButton =
                new JButton("Reminders");

        JButton favoriteButton =
                new JButton("Favorite Notes");

        JButton moodButton =
                new JButton("Mood");

        JButton settingsButton =
                new JButton("Settings");

        JButton searchButton =
                new JButton("Search Notes");

        JButton calendarButton =
                new JButton("Calendar View");

        JButton dailyGoalsButton =
                new JButton("Daily Goals");

        JButton tasksButton =
                new JButton("Diary Tasks");

        JButton attachmentsButton =
                new JButton("Diary Attachments");

        JButton logoutButton =
                new JButton("Logout");

        // ================= STYLE =================

        JButton[] buttons = {
                diaryButton,
                categoryButton,
                reminderButton,
                favoriteButton,
                moodButton,
                settingsButton,
                searchButton,
                calendarButton,
                dailyGoalsButton,
                tasksButton,
                attachmentsButton,
                logoutButton
        };

        for (JButton button : buttons) {

            styleButton(button);

            buttonPanel.add(button);
        }

        mainPanel.add(
                buttonPanel,
                BorderLayout.CENTER
        );

        // ================= FOOTER =================

        JLabel footer =
                new JLabel(
                        "Your memories, your diary",
                        SwingConstants.CENTER
                );

        footer.setFont(
                new Font(
                        "Arial",
                        Font.ITALIC,
                        14
                )
        );

        footer.setForeground(darkPurple);

        mainPanel.add(
                footer,
                BorderLayout.SOUTH
        );

        add(mainPanel);

        // ================= ACTIONS =================

        diaryButton.addActionListener(e ->
                new DiaryNotesFrame().setVisible(true)
        );

        categoryButton.addActionListener(e ->
                new CategoryFrame().setVisible(true)
        );

        reminderButton.addActionListener(e ->
                new ReminderFrame(username).setVisible(true)
        );

        favoriteButton.addActionListener(e ->
                new FavoriteNotesFrame().setVisible(true)
        );

        moodButton.addActionListener(e ->
                new MoodFrame().setVisible(true)
        );

        settingsButton.addActionListener(e ->
                new SettingsFrame(username).setVisible(true)
        );

        searchButton.addActionListener(e ->
                new SearchNotesFrame().setVisible(true)
        );

        calendarButton.addActionListener(e ->
                new CalendarFrame().setVisible(true)
        );

        dailyGoalsButton.addActionListener(e ->
                new DailyGoalsFrame().setVisible(true)
        );

        tasksButton.addActionListener(e ->
                new DiaryTasksFrame().setVisible(true)
        );

        // NEW: DIARY ATTACHMENTS

        attachmentsButton.addActionListener(e ->
                new DiaryAttachmentsFrame().setVisible(true)
        );

        // ================= LOGOUT =================

        logoutButton.addActionListener(e -> {

            int result =
                    JOptionPane.showConfirmDialog(
                            this,
                            "Are you sure you want to logout?",
                            "Logout",
                            JOptionPane.YES_NO_OPTION
                    );

            if (result ==
                    JOptionPane.YES_OPTION) {

                dispose();

                new LoginFrame().setVisible(true);
            }
        });
    }

    // =========================================================
    // BUTTON STYLE
    // =========================================================

    private void styleButton(JButton button) {

        button.setBackground(pastelPurple);

        button.setForeground(Color.WHITE);

        button.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        16
                )
        );

        button.setFocusPainted(false);

        button.setCursor(
                new Cursor(
                        Cursor.HAND_CURSOR
                )
        );

        button.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                darkPurple,
                                1
                        ),
                        BorderFactory.createEmptyBorder(
                                15,
                                10,
                                15,
                                10
                        )
                )
        );
    }

    // =========================================================
    // MAIN
    // =========================================================

    public static void main(String[] args) {

        SwingUtilities.invokeLater(
                () -> new LoginFrame().setVisible(true)
        );
    }
}