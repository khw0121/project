import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.ArrayList;

public class FollowingScreen extends JFrame {
    private User currentUser;
    private String otherUserId;
    private JButton homeButton;
    private DatabaseManager dbManager;

    public FollowingScreen(User user, String otherUserId) {
        this.currentUser = user;
        this.otherUserId = otherUserId;

        try {
            dbManager = new DatabaseManager();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Database Connection Failed", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 프레임 기본 설정
        setTitle("Following List");
        setSize(400, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(null);
        add(panel);

        // 팔로잉 사용자 목록 표시
        JTextArea followingArea = new JTextArea();
        followingArea.setBounds(20, 20, 340, 400);
        followingArea.setEditable(false);
        panel.add(followingArea);

        try {
            ArrayList<String> followees = dbManager.getFolloweeById(otherUserId);
            StringBuilder followingList = new StringBuilder();
            for (String followee : followees) {
                followingList.append(followee).append("\n");
            }
            followingArea.setText(followingList.toString());
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Failed to load following list", "Error", JOptionPane.ERROR_MESSAGE);
        }

        followingArea.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int offset = followingArea.viewToModel(e.getPoint());
                try {
                    int line = followingArea.getLineOfOffset(offset);
                    int start = followingArea.getLineStartOffset(line);
                    int end = followingArea.getLineEndOffset(line);
                    String selectedUserId = followingArea.getText().substring(start, end).trim();
                    if (!selectedUserId.isEmpty()) {
                        dispose();
                        ProfileScreen otherProfileScreen = new ProfileScreen(currentUser, selectedUserId);
                        otherProfileScreen.setVisible(true);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Error loading user profile", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // 하단 중앙: 홈 버튼
        homeButton = new JButton("Home");
        homeButton.setBounds(150, 500, 100, 25);
        panel.add(homeButton);

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
