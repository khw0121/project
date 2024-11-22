public class Comment {
    private int commentId;
    private String authorName;
    private String content;

    public Comment(int commentId, String authorName, String content) {
        this.commentId = commentId;
        this.authorName = authorName;
        this.content = content;
    }

    public int getCommentId() {
        return commentId;
    }

    public String getAuthorName() {
        return authorName;
    }

    public String getContent() {
        return content;
    }
}
