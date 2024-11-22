import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

public class CreatePostScreen extends JFrame {
    private JTextArea postContentArea;
    private JButton postButton, homeButton;
    private User currentUser;
    private DatabaseManager dbManager;

    public CreatePostScreen(User user) {
        this.currentUser = user;
        try {
            dbManager = new DatabaseManager();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Database Connection Failed", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 프레임 기본 설정
        setTitle("Create Post");
        setSize(400, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(null);
        add(panel);

        // 게시글 내용 입력 필드
        JLabel postLabel = new JLabel("Content:");
        postLabel.setBounds(20, 20, 100, 25);
        panel.add(postLabel);

        postContentArea = new JTextArea();
        postContentArea.setBounds(20, 50, 340, 150);
        panel.add(postContentArea);

        // 작성 완료 버튼
        postButton = new JButton("Post");
        postButton.setBounds(150, 220, 100, 25);
        panel.add(postButton);

        // 하단 중앙: 홈 버튼
        homeButton = new JButton("Home");
        homeButton.setBounds(150, 300, 100, 25);
        panel.add(homeButton);

        // 게시글 작성 버튼 액션 리스너 추가
        postButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String content = postContentArea.getText();
                if (!content.isEmpty()) {
                    try {
                        if (dbManager.insertArticle(currentUser.getId(), content)) {
                            JOptionPane.showMessageDialog(null, "Post created successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                            dispose();
                            TimelineScreen timelineScreen = new TimelineScreen(currentUser);
                            timelineScreen.setVisible(true);
                        } else {
                            JOptionPane.showMessageDialog(null, "Failed to create post!", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(null, "Database Error", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Content cannot be empty!", "Error", JOptionPane.ERROR_MESSAGE);
                }
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
