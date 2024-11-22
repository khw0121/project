import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.ArrayList;

public class TimelineScreen extends JFrame {
    private JButton profileButton, settingsButton, createPostButton, homeButton;
    private User currentUser;
    private DatabaseManager dbManager;

    public TimelineScreen(User user) {
        this.currentUser = user;
        try {
            dbManager = new DatabaseManager();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Database Connection Failed", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 프레임 기본 설정
        setTitle("Timeline");
        setSize(600, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(null);
        add(panel);

        // 상단 왼쪽: 프로필 버튼
        profileButton = new JButton("Profile");
        profileButton.setBounds(10, 10, 100, 25);
        panel.add(profileButton);

        // 상단 오른쪽: 설정 버튼
        settingsButton = new JButton("Settings");
        settingsButton.setBounds(480, 10, 100, 25);
        panel.add(settingsButton);

        // 모든 사용자의 게시글 목록 표시
        JTextArea postArea = new JTextArea();
        postArea.setBounds(50, 50, 500, 400);
        postArea.setEditable(false);
        panel.add(postArea);

        try {
            ArrayList<Article> articles = dbManager.getAllArticlesWithDetails();
            StringBuilder posts = new StringBuilder();
            for (Article article : articles) {
                posts.append(article.getAuthorName()).append(" : ").append(article.getContent()).append("\n\n");
            }
            postArea.setText(posts.toString());
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Failed to load posts", "Error", JOptionPane.ERROR_MESSAGE);
        }

        // 게시글 클릭 시 게시글 상세 보기 화면으로 이동
        postArea.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int line = postArea.viewToModel(e.getPoint());
                try {
                    int start = postArea.getLineStartOffset(postArea.getLineOfOffset(line));
                    int end = postArea.getLineEndOffset(postArea.getLineOfOffset(line));
                    String selectedText = postArea.getText().substring(start, end).trim();
                    if (selectedText.contains(" : ")) {
                        String authorName = selectedText.split(" : ")[0];
                        String content = selectedText.split(" : ")[1];
                        int articleId = dbManager.getArticleIdByContent(content);
                        dispose();
                        PostDetailScreen postDetailScreen = new PostDetailScreen(currentUser, authorName, content, articleId);
                        postDetailScreen.setVisible(true);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Error loading post details", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // 하단 중앙: 홈 버튼
        homeButton = new JButton("Home");
        homeButton.setBounds(250, 500, 100, 25);
        panel.add(homeButton);

        // 하단 우측: 게시글 작성 버튼
        createPostButton = new JButton("Create Post");
        createPostButton.setBounds(450, 500, 120, 25);
        panel.add(createPostButton);

        // 프로필 버튼 액션 리스너 추가
        profileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                ProfileScreen profileScreen = new ProfileScreen(currentUser, currentUser.getId());
                profileScreen.setVisible(true);
            }
        });

        // 설정 버튼 액션 리스너 추가
        settingsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                SettingsScreen settingsScreen = new SettingsScreen(currentUser);
                settingsScreen.setVisible(true);
            }
        });

        // 홈 버튼 액션 리스너 추가
        homeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 홈 버튼 클릭 시 타임라인 화면 새로고침
                dispose();
                TimelineScreen timelineScreen = new TimelineScreen(currentUser);
                timelineScreen.setVisible(true);
            }
        });

        // 게시글 작성 버튼 액션 리스너 추가
        createPostButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                CreatePostScreen createPostScreen = new CreatePostScreen(currentUser);
                createPostScreen.setVisible(true);
            }
        });
    }
}
