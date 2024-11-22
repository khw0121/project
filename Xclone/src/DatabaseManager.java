import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {
    private static final String URL = "jdbc:mysql://localhost:3306/TwitterClone"; // DB URL
    private static final String USER = "root"; // 사용자 이름
    private static final String PASSWORD = "1234"; // 비밀번호

    // 데이터베이스 연결 객체
    private Connection connection;

    // 생성자에서 데이터베이스 연결 설정	
    public DatabaseManager() throws SQLException {
        try {
            // JDBC 드라이버 로드
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Database connected successfully.");
        } catch (ClassNotFoundException e) {
            System.err.println("JDBC Driver not found.");
            throw new SQLException(e);
        }
    }
    
    // userID가 있는지 없는지 확인하는 함수
    public boolean isUserExists(String userId) throws SQLException {
        String query = "SELECT COUNT(*) FROM Users WHERE ID = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, userId); // 사용자 ID 설정
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0; // 사용자가 존재하면 true, 존재하지 않으면 false
                }
            }
        }
        return false; // 예기치 않은 경우에는 false 반환
    }

    // 특정 ID의 사용자 조회
    public User getUserById(String userId) throws SQLException {
        String query = "SELECT ID, name, password, email, age FROM Users WHERE ID = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, userId); // 사용자 ID 설정
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    // User 객체 생성 및 반환 (비밀번호 포함)
                    return new User(
                        rs.getString("ID"),
                        rs.getString("name"),
                        rs.getString("password"), // 비밀번호 포함
                        rs.getString("email"),
                        rs.getInt("age")
                    );
                }
            }
        }
        return null; // 사용자 정보가 없을 경우 null 반환
    }
    
    // 특정 사용자가 팔로우하는 유저들의 게시글 ID 가져오기
    public ArrayList<Integer> getFolloweeArticlesByUserId(String userId) throws SQLException {
        ArrayList<Integer> articleIds = new ArrayList<>();
        String query = "SELECT a.article_ID FROM Follow f JOIN Article a ON f.followee_id = a.ID WHERE f.follower_id = ? ORDER BY a.created_at DESC ";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, userId); // 팔로워 ID 설정
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    int articleId = rs.getInt("article_ID");
                    articleIds.add(articleId);
                }
            }
        }
        return articleIds;
    }
    
    
    
    //팔로워 게시글의 상세 정보_추가
    public ArrayList<Article> getFolloweeArticlesWithDetails(String userId) throws SQLException {
        ArrayList<Article> articles = new ArrayList<>();
        String query = "SELECT a.article_ID, a.content, u.name FROM Follow f JOIN Article a ON f.followee_id = a.ID JOIN Users u ON u.ID = a.ID WHERE f.follower_id = ? ORDER BY a.created_at DESC";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, userId); // 팔로워 ID 설정
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    int articleId = rs.getInt("article_ID");
                    String content = rs.getString("content");
                    String authorName = rs.getString("name");

                    articles.add(new Article(articleId, authorName, content));
                }
            }
        }
        return articles;
    }

    //팔로워 게시글의 내용_추가
    public int getArticleIdByContent(String content) throws SQLException {
        String query = "SELECT article_ID FROM Article WHERE content = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, content);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("article_ID");
                }
            }
        }
        throw new SQLException("Article not found with the provided content.");
    }

    //프로필 작성자 게시글_추가
    public ArrayList<Article> getUserArticlesWithDetails(String userId) throws SQLException {
        ArrayList<Article> articles = new ArrayList<>();
        String query = "SELECT a.article_ID, u.name AS author_name, a.content FROM Article a JOIN Users u ON a.ID = u.ID WHERE a.ID = ? ORDER BY a.created_at DESC";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    int articleId = rs.getInt("article_ID");
                    String authorName = rs.getString("author_name");
                    String content = rs.getString("content");

                    articles.add(new Article(articleId, authorName, content));
                }
            }
        } catch (SQLException e) {
            System.err.println("Failed to load articles for user ID: " + userId + " - Error: " + e.getMessage());
            throw e;
        }
        return articles;
    }

    //모든 작성자의 게시글_추가
    public ArrayList<Article> getAllArticlesWithDetails() throws SQLException {
        ArrayList<Article> articles = new ArrayList<>();
        String query = "SELECT a.article_ID, a.content, u.name FROM Article a JOIN Users u ON u.ID = a.ID ORDER BY a.created_at DESC";

        try (PreparedStatement pstmt = connection.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                int articleId = rs.getInt("article_ID");
                String content = rs.getString("content");
                String authorName = rs.getString("name");

                articles.add(new Article(articleId, authorName, content));
            }
        }
        return articles;
    }
    
    //특정 게시글에 대한 댓글
    public ArrayList<Comment> getCommentsWithDetailsByArticleId(int articleId) throws SQLException {
        ArrayList<Comment> comments = new ArrayList<>();
        String query = "SELECT c.comment_ID, u.name AS author_name, c.content FROM Comments c JOIN Users u ON c.user_id = u.ID WHERE c.tweet_id = ? ORDER BY c.created_at ASC";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, articleId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    int commentId = rs.getInt("comment_ID");
                    String authorName = rs.getString("author_name");
                    String content = rs.getString("content");
                    comments.add(new Comment(commentId, authorName, content));
                }
            }
        }
        return comments;
    }

    
    
 // 특정 트윗 ID에 대한 모든 댓글 ID 가져오기
    public ArrayList<Integer> getAllCommentIdsByTweetId(int tweetId) throws SQLException {
        ArrayList<Integer> commentIds = new ArrayList<>();
        String query = "SELECT comment_ID FROM Comments WHERE tweet_id = ? ORDER BY created_at DESC";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, tweetId); // 트윗 ID 설정
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    int commentId = rs.getInt("comment_ID");
                    commentIds.add(commentId);
                }
            }
        }
        return commentIds;
    }
    
    // 특정 사용자 ID가 팔로우하는 사용자 목록 가져오기
    public ArrayList<String> getFolloweeById(String userId) throws SQLException {
        ArrayList<String> followees = new ArrayList<>();
        String query = "SELECT followee_id FROM Follow WHERE follower_id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, userId); // 팔로워 ID 설정
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String followee = rs.getString("followee_id");
                    followees.add(followee);
                }
            }
        }
        return followees;
    }
    
    

    
 // 특정 사용자 ID로 follower ID 가져오기
    public ArrayList<String> getFollowerIdsByUserId(String userId) throws SQLException {
        ArrayList<String> followerIds = new ArrayList<>();
        String query = "SELECT follower_id FROM Follow WHERE followee_id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, userId); // 팔로우 대상 ID 설정
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String followerId = rs.getString("follower_id");
                    followerIds.add(followerId);
                }
            }
        }
        return followerIds;
    }


    // ------------------------------------------------------------------------------
    // 데이터베이스에 정보 집어넣는 함수들

    // user 정보 넣기
    public boolean insertUser(User user) throws SQLException {
        String query = "INSERT INTO Users (ID, name, password, email, age) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, user.getId());
            pstmt.setString(2, user.getName());
            pstmt.setString(3, user.getPassword());
            pstmt.setString(4, user.getEmail());
            pstmt.setInt(5, user.getAge());

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0; // 성공적으로 삽입된 경우 true 반환
        }
    }
    // 유저 탈퇴 기능
    public boolean deleteUser(String userId) throws SQLException {
        // 트랜잭션을 사용하여 모든 삭제를 원자적으로 처리
        String deleteCommentsQuery = "DELETE FROM Comments WHERE user_id = ?";
        String deleteArticlesQuery = "DELETE FROM Article WHERE ID = ?";
        String deleteFollowersQuery = "DELETE FROM Follow WHERE followee_id = ?";
        String deleteFollowingQuery = "DELETE FROM Follow WHERE follower_id = ?";
        String deleteUserQuery = "DELETE FROM Users WHERE ID = ?";

        try {
            // 트랜잭션 시작
            connection.setAutoCommit(false); // 수동 커밋 모드로 설정

            // 유저가 작성한 댓글 삭제
            try (PreparedStatement pstmt = connection.prepareStatement(deleteCommentsQuery)) {
                pstmt.setString(1, userId);
                pstmt.executeUpdate();
            }

            // 유저가 작성한 게시글 삭제
            try (PreparedStatement pstmt = connection.prepareStatement(deleteArticlesQuery)) {
                pstmt.setString(1, userId);
                pstmt.executeUpdate();
            }

            // 유저가 팔로우한 관계 삭제
            try (PreparedStatement pstmt = connection.prepareStatement(deleteFollowersQuery)) {
                pstmt.setString(1, userId);
                pstmt.executeUpdate();
            }

            // 유저를 팔로우한 관계 삭제
            try (PreparedStatement pstmt = connection.prepareStatement(deleteFollowingQuery)) {
                pstmt.setString(1, userId);
                pstmt.executeUpdate();
            }

            // 유저 삭제
            try (PreparedStatement pstmt = connection.prepareStatement(deleteUserQuery)) {
                pstmt.setString(1, userId);
                int rowsAffected = pstmt.executeUpdate();
                if (rowsAffected == 0) {
                    // 유저 삭제 실패 시 트랜잭션 롤백
                    connection.rollback();
                    return false;
                }
            }

            // 트랜잭션 커밋
            connection.commit();
            return true;

        } catch (SQLException e) {
            // 예외가 발생하면 롤백
            connection.rollback();
            throw new SQLException("Failed to delete user and related data.", e);
        } finally {
            // 트랜잭션 모드 복원
            connection.setAutoCommit(true);
        }
    }

    //article 정보 추가
    public boolean insertArticle(String userId, String content) throws SQLException {
        String query = "INSERT INTO Article (ID, content) VALUES (?, ?)";
        
        if (!isUserExists(userId)) {
            System.out.println("Follower user does not exist.");
            return false;
        }
        
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, userId); // 작성자 ID 설정
            pstmt.setString(2, content); // 게시글 내용 설정

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0; // 성공적으로 삽입된 경우 true 반환
        }
    }
    
    // article 정보 삭제
    public boolean deleteArticle(int articleId) throws SQLException {
        // 게시글 삭제 쿼리
        String query = "DELETE FROM Article WHERE article_ID = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, articleId);  // 삭제할 게시글의 ID 설정

            int rowsAffected = pstmt.executeUpdate();
            
            // 성공적으로 삭제된 경우 true 반환
            return rowsAffected > 0;
        }
    }

    // 팔로우 추가
    public boolean insertFollow(String followerId, String followeeId) throws SQLException {
        // follower_id와 followee_id가 Users 테이블에 존재하는지 확인
        if (!isUserExists(followerId)) {
            System.out.println("Follower user does not exist.");
            return false;
        }
        
        if (!isUserExists(followeeId)) {
            System.out.println("Followee user does not exist.");
            return false;
        }

        // 두 사용자 모두 존재하면 Follow 테이블에 데이터 삽입
        String query = "INSERT INTO Follow (follower_id, followee_id) VALUES (?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, followerId); // follower_id 설정
            pstmt.setString(2, followeeId); // followee_id 설정

            int rowsAffected = pstmt.executeUpdate();
            
            // 성공적으로 삽입된 경우 true 반환
            return rowsAffected > 0;
        }
    }
    
    //언팔로우 기능
    public boolean unfollowUser(String followerId, String followeeId) throws SQLException {
        // 팔로우 관계 삭제 쿼리
        String query = "DELETE FROM Follow WHERE follower_id = ? AND followee_id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, followerId);  // 팔로워 ID 설정
            pstmt.setString(2, followeeId);  // 팔로우 대상 ID 설정

            int rowsAffected = pstmt.executeUpdate();
            
            // 성공적으로 삭제된 경우 true 반환
            return rowsAffected > 0;
        }
    }

    // 댓글 추가 기능
    public boolean insertComment(int tweetId, String userId, String content) throws SQLException {
        String query = "INSERT INTO Comments (tweet_id, user_id, content) VALUES (?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, tweetId); // tweet_id 설정
            pstmt.setString(2, userId); // user_id 설정
            pstmt.setString(3, content); // content 설정

            int rowsAffected = pstmt.executeUpdate();
            
            // 성공적으로 삽입된 경우 true 반환
            return rowsAffected > 0;
        }
    }
    // 댓글 삭제 기능	
    public boolean deleteComment(int commentId) throws SQLException {
        String query = "DELETE FROM Comments WHERE comment_ID = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, commentId); // comment_ID 설정
            int rowsAffected = pstmt.executeUpdate();
            
            // 성공적으로 삭제된 경우 true 반환
            return rowsAffected > 0;
        }
    }

    
    // 데이터베이스 연결 닫기
    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Database connection closed.");
            }
        } catch (SQLException e) {
            System.err.println("Failed to close the connection.");
        }
    }

    
}
