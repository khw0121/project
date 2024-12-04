import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.sql.SQLException;
import java.util.ArrayList;

public class ProfileScreen extends JFrame {
    private User currentUser;
    private String otherUserId;
    private JButton followButton, backButton, homeButton;
    private JLabel followingLabel, followerLabel;
    private JTextArea postArea;
    private DatabaseManager dbManager;
    private boolean isFollowing;

    public ProfileScreen(User user, String otherUserId) {
        this.currentUser = user;
        this.otherUserId = otherUserId;

        try {
            dbManager = new DatabaseManager();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Database Connection Failed", "Error", JOptionPane.ERROR_MESSAGE);
            dispose();
            return;
        }

        // 프레임 기본 설정
        setTitle("User Profile");
        setSize(600, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(null);
        add(panel);

        // 상단 중앙: 사용자 ID 표시
        JLabel userIdLabel = new JLabel("User ID: " + otherUserId);
        userIdLabel.setBounds(200, 20, 200, 25);
        panel.add(userIdLabel);

        // 팔로우/언팔로우 버튼 (다른 사용자일 경우에만 표시)
        if (!currentUser.getId().equals(otherUserId)) {
            try {
                isFollowing = dbManager.getFolloweeById(currentUser.getId()).contains(otherUserId);
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, "Failed to fetch following info", "Error", JOptionPane.ERROR_MESSAGE);
                isFollowing = false; // 기본값 설정
            }

            followButton = new JButton(isFollowing ? "Unfollow" : "Follow");
            followButton.setBounds(20, 20, 100, 25);
            panel.add(followButton);

            followButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        if (isFollowing) {
                            if (dbManager.unfollowUser(currentUser.getId(), otherUserId)) {
                                followButton.setText("Follow");
                                isFollowing = false;
                            } else {
                                JOptionPane.showMessageDialog(null, "Failed to unfollow user", "Error", JOptionPane.ERROR_MESSAGE);
                            }
                        } else {
                            if (dbManager.insertFollow(currentUser.getId(), otherUserId)) {
                                followButton.setText("Unfollow");
                                isFollowing = true;
                            } else {
                                JOptionPane.showMessageDialog(null, "Failed to follow user", "Error", JOptionPane.ERROR_MESSAGE);
                            }
                        }
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(null, "Database Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            });
        }

        // 팔로잉 수 및 팔로워 수 표시
        try {
            ArrayList<String> followees = dbManager.getFolloweeById(otherUserId);
            ArrayList<String> followers = dbManager.getFollowerIdsByUserId(otherUserId);

            followingLabel = new JLabel("Following: " + followees.size());
            followingLabel.setBounds(150, 70, 100, 25);
            panel.add(followingLabel);

            followerLabel = new JLabel("Followers: " + followers.size());
            followerLabel.setBounds(270, 70, 100, 25);
            panel.add(followerLabel);

            followingLabel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent evt) {
                    dispose();
                    FollowingScreen followingScreen = new FollowingScreen(currentUser, otherUserId);
                    followingScreen.setVisible(true);
                }
            });

            followerLabel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent evt) {
                    dispose();
                    FollowerScreen followerScreen = new FollowerScreen(currentUser, otherUserId);
                    followerScreen.setVisible(true);
                }
            });
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Failed to load following/follower info", "Error", JOptionPane.ERROR_MESSAGE);
        }

        // 게시글 목록 표시
        postArea = new JTextArea();
        postArea.setEditable(false);

        JScrollPane scrollPane = new JScrollPane(postArea);
        scrollPane.setBounds(50, 100, 500, 300);
        panel.add(scrollPane);

        loadUserPosts();

        // 하단 중앙: 홈 버튼
        homeButton = new JButton("Home");
        homeButton.setBounds(250, 500, 100, 25);
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

    private void loadUserPosts() {
        try {
            ArrayList<Article> articles = dbManager.getUserArticlesWithDetails(otherUserId);
            StringBuilder posts = new StringBuilder();
            for (Article article : articles) {
                posts.append(article.getAuthorName()).append(" : ").append(article.getContent()).append("\n\n");
            }
            postArea.setText(posts.toString());
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Failed to load posts: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void dispose() {
        if (followingLabel != null) {
            for (MouseListener listener : followingLabel.getMouseListeners()) {
                followingLabel.removeMouseListener(listener);
            }
        }
        if (followerLabel != null) {
            for (MouseListener listener : followerLabel.getMouseListeners()) {
                followerLabel.removeMouseListener(listener);
            }
        }
        super.dispose();
    }
}
