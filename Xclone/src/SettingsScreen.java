import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

public class SettingsScreen extends JFrame {
    private User currentUser;
    private JButton logoutButton, deleteAccountButton, homeButton;
    private DatabaseManager dbManager;

    public SettingsScreen(User user) {
        this.currentUser = user;

        try {
            dbManager = new DatabaseManager();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Database connection failed", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 프레임 기본 설정
        setTitle("Settings");
        setSize(400, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(null);
        add(panel);

        // 로그아웃 버튼
        logoutButton = new JButton("Logout");
        logoutButton.setBounds(150, 60, 100, 25);
        panel.add(logoutButton);

        // 계정 삭제 버튼
        deleteAccountButton = new JButton("Delete Account");
        deleteAccountButton.setBounds(150, 100, 150, 25);
        panel.add(deleteAccountButton);

        // 홈 버튼
        homeButton = new JButton("Home");
        homeButton.setBounds(150, 300, 100, 25);
        panel.add(homeButton);

        // 계정 삭제 버튼 액션 리스너 추가
        deleteAccountButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int confirmation = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete your account?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);
                if (confirmation == JOptionPane.YES_OPTION) {
                    try {
                        if (dbManager.deleteUser(currentUser.getId())) {
                            JOptionPane.showMessageDialog(null, "Account deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                            dispose();
                            LoginScreen loginScreen = new LoginScreen();
                            loginScreen.setVisible(true);
                        } else {
                            JOptionPane.showMessageDialog(null, "Failed to delete account", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(null, "Database Error", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        // 로그아웃 버튼 액션 리스너 추가
        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                LoginScreen loginScreen = new LoginScreen();
                loginScreen.setVisible(true);
            }
        });

        // 홈 버튼 액션 리스너 추가
        homeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                TimelineScreen timelineScreen = new TimelineScreen(currentUser);
                timelineScreen.setVisible(true);
            }
        });
    }
}
