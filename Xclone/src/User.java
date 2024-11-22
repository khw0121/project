public class User {
    private String id;      // 사용자 ID
    private String name;    // 사용자 이름
    private String password; // 사용자 비밀번호
    private String email;    // 사용자 이메일
    private int age;        // 사용자 나이

    // 생성자
    public User(String id, String name, String password, String email, int age) {
        this.id = id;
        this.name = name;
        this.password = password;
        this.email = email;
        this.age = age;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    // 사용자 정보 출력
    @Override
    public String toString() {
        return "User [id=" + id + ", name=" + name + ", email=" + email + ", age=" + age + "]";
    }
}
