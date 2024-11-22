public class Article {
    private int articleId;
    private String authorName;
    private String content;

    // 생성자
    public Article(int articleId, String authorName, String content) {
        this.articleId = articleId;
        this.authorName = authorName;
        this.content = content;
    }

    // Getter 메서드들
    public int getArticleId() {
        return articleId;
    }

    public String getAuthorName() {
        return authorName;
    }

    public String getContent() {
        return content;
    }
}
