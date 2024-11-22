import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.ArrayList;

public class PostDetailScreen extends JFrame {
    private JButton profileButton, deletePostButton, homeButton, addCommentButton;
    private User currentUser;
    private String postAuthorId;
    private String postContent;
    private int articleId;
    private JTextArea commentArea;
    private JTextField commentField;
    private DatabaseManager dbManager;

    public PostDetailScreen(User user, String authorId, String content, int articleId) {
        this.currentUser = user;
        this.postAuthorId = authorId;
        this.postContent = content;
        this.articleId = articleId;

        try {
            dbManager = new DatabaseManager();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Database Connection Failed", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Frame settings
        setTitle("Post Detail");
        setSize(600, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(null);
        add(panel);

        // Post author ID label
        JLabel authorIdLabel = new JLabel("Author: " + postAuthorId);
        authorIdLabel.setBounds(20, 20, 200, 25);
        panel.add(authorIdLabel);

        // Profile button
        profileButton = new JButton("View Profile");
        profileButton.setBounds(400, 20, 120, 25);
        panel.add(profileButton);

        // Post content area
        JTextArea postContentArea = new JTextArea(postContent);
        postContentArea.setBounds(20, 60, 540, 100);
        postContentArea.setEditable(false);
        panel.add(postContentArea);

        // Delete post button (only visible to the author)
        if (currentUser.getId().equals(postAuthorId)) {
            deletePostButton = new JButton("Delete Post");
            deletePostButton.setBounds(20, 180, 120, 25);
            panel.add(deletePostButton);

            deletePostButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        if (dbManager.deleteArticle(articleId)) {
                            JOptionPane.showMessageDialog(null, "Post deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                            dispose();
                            TimelineScreen timelineScreen = new TimelineScreen(currentUser);
                            timelineScreen.setVisible(true);
                        } else {
                            JOptionPane.showMessageDialog(null, "Failed to delete post", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(null, "Database Error", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            });
        }

        // Comment area
        commentArea = new JTextArea();
        commentArea.setBounds(20, 220, 540, 200);
        commentArea.setEditable(false);
        panel.add(commentArea);

        loadComments();

        // Comment field
        commentField = new JTextField();
        commentField.setBounds(20, 440, 400, 25);
        panel.add(commentField);

        // Add comment button
        addCommentButton = new JButton("Add Comment");
        addCommentButton.setBounds(440, 440, 120, 25);
        panel.add(addCommentButton);

        addCommentButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String commentContent = commentField.getText().trim();
                if (!commentContent.isEmpty()) {
                    try {
                        if (dbManager.insertComment(articleId, currentUser.getId(), commentContent)) {
                            JOptionPane.showMessageDialog(null, "Comment added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                            loadComments();
                            commentField.setText("");
                        } else {
                            JOptionPane.showMessageDialog(null, "Failed to add comment", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(null, "Database Error", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Comment cannot be empty!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Home button
        homeButton = new JButton("Home");
        homeButton.setBounds(250, 500, 100, 25);
        panel.add(homeButton);

        // Profile button action listener
        profileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                ProfileScreen profileScreen = new ProfileScreen(currentUser, postAuthorId);
                profileScreen.setVisible(true);
            }
        });

        // Home button action listener
        homeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                TimelineScreen timelineScreen = new TimelineScreen(currentUser);
                timelineScreen.setVisible(true);
            }
        });

        // Comment area mouse listener for deleting comments
        commentArea.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int offset = commentArea.viewToModel(e.getPoint());
                try {
                    int line = commentArea.getLineOfOffset(offset);
                    int start = commentArea.getLineStartOffset(line);
                    int end = commentArea.getLineEndOffset(line);
                    String selectedComment = commentArea.getText().substring(start, end).trim();
                    if (selectedComment.startsWith("Comment ID: ")) {
                        String commentIdStr = selectedComment.split(" ")[2];
                        int commentId = Integer.parseInt(commentIdStr);
                        int confirmation = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete this comment?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);
                        if (confirmation == JOptionPane.YES_OPTION) {
                            if (dbManager.deleteComment(commentId)) {
                                JOptionPane.showMessageDialog(null, "Comment deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                                loadComments();
                            } else {
                                JOptionPane.showMessageDialog(null, "Failed to delete comment", "Error", JOptionPane.ERROR_MESSAGE);
                            }
                        }
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Error loading comment details", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    private void loadComments() {
        try {
            ArrayList<Comment> comments = dbManager.getCommentsWithDetailsByArticleId(articleId);
            StringBuilder commentText = new StringBuilder();
            for (Comment comment : comments) {
                commentText.append(comment.getAuthorName())
                           .append(": ")
                           .append(comment.getContent())
                           .append("\n\n");
            }
            commentArea.setText(commentText.toString());
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Failed to load comments", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

}
