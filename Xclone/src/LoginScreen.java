import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

public class LoginScreen extends JFrame {
    private JTextField idField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton signUpButton;
    private DatabaseManager dbManager;

    public LoginScreen() {
        try {
            dbManager = new DatabaseManager();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Database Connection Failed", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        setTitle("Login Screen");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(null);
        add(panel);

        JLabel idLabel = new JLabel("ID:");
        idLabel.setBounds(50, 50, 80, 25);
        panel.add(idLabel);

        idField = new JTextField(20);
        idField.setBounds(150, 50, 165, 25);
        panel.add(idField);

        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setBounds(50, 100, 80, 25);
        panel.add(passwordLabel);

        passwordField = new JPasswordField(20);
        passwordField.setBounds(150, 100, 165, 25);
        panel.add(passwordField);

        loginButton = new JButton("Login");
        loginButton.setBounds(50, 150, 100, 25);
        panel.add(loginButton);

        signUpButton = new JButton("Sign Up");
        signUpButton.setBounds(200, 150, 100, 25);
        panel.add(signUpButton);

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String userId = idField.getText();
                String password = new String(passwordField.getPassword());

                try {
                    User user = dbManager.getUserById(userId);
                    if (user != null && user.getPassword().equals(password)) {
                        dispose();
                        TimelineScreen timelineScreen = new TimelineScreen(user);
                        timelineScreen.setVisible(true);
                    } else {
                        JOptionPane.showMessageDialog(null, "Invalid ID or Password", "Login Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null, "Database Error", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        signUpButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                SignUpScreen signUpScreen = new SignUpScreen();
                signUpScreen.setVisible(true);
            }
        });
    }
}
