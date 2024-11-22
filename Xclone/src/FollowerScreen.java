import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.ArrayList;

public class FollowerScreen extends JFrame {
    private User currentUser;
    private String otherUserId;
    private JButton homeButton;
    private DatabaseManager dbManager;

    public FollowerScreen(User user, String otherUserId) {
        this.currentUser = user;
        this.otherUserId = otherUserId;

        try {
            dbManager = new DatabaseManager();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Database connection failed", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 프레임 기본 설정
        setTitle("Follower List");
        setSize(400, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(null);
        add(panel);

        // 팔로워 사용자 목록 표시
        JTextArea followerArea = new JTextArea();
        followerArea.setBounds(20, 20, 340, 400);
        followerArea.setEditable(false);
        panel.add(followerArea);

        try {
            ArrayList<String> followers = dbManager.getFollowerIdsByUserId(otherUserId);
            StringBuilder followerList = new StringBuilder();
            for (String follower : followers) {
                followerList.append(follower).append("\n");
            }
            followerArea.setText(followerList.toString());
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Failed to load follower list", "Error", JOptionPane.ERROR_MESSAGE);
        }

        // 팔로워 사용자 클릭 시 프로필 화면으로 이동
        followerArea.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int offset = followerArea.viewToModel(e.getPoint());
                try {
                    int line = followerArea.getLineOfOffset(offset);
                    int start = followerArea.getLineStartOffset(line);
                    int end = followerArea.getLineEndOffset(line);
                    String selectedUserId = followerArea.getText().substring(start, end).trim();
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

        // 홈 버튼 추가
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
