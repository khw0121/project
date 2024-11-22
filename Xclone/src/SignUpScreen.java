import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

public class SignUpScreen extends JFrame {
    private JTextField idField, emailField, ageField;
    private JPasswordField passwordField;
    private JButton registerButton, backButton;
    private DatabaseManager dbManager;

    public SignUpScreen() {
        try {
            dbManager = new DatabaseManager();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Database Connection Failed", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        setTitle("Sign Up Screen");
        setSize(400, 400);
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

        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setBounds(50, 150, 80, 25);
        panel.add(emailLabel);

        emailField = new JTextField(20);
        emailField.setBounds(150, 150, 165, 25);
        panel.add(emailField);

        JLabel ageLabel = new JLabel("Age:");
        ageLabel.setBounds(50, 200, 80, 25);
        panel.add(ageLabel);

        ageField = new JTextField(20);
        ageField.setBounds(150, 200, 165, 25);
        panel.add(ageField);

        registerButton = new JButton("Register");
        registerButton.setBounds(50, 250, 100, 25);
        panel.add(registerButton);

        backButton = new JButton("Back");
        backButton.setBounds(200, 250, 100, 25);
        panel.add(backButton);

        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String userId = idField.getText();
                String password = new String(passwordField.getPassword());
                String email = emailField.getText();
                int age;

                try {
                    age = Integer.parseInt(ageField.getText());
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Invalid age input", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                try {
                    User user = new User(userId, userId, password, email, age);
                    if (dbManager.insertUser(user)) {
                        JOptionPane.showMessageDialog(null, "Sign Up Successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
                        dispose();
                        LoginScreen loginScreen = new LoginScreen();
                        loginScreen.setVisible(true);
                    } else {
                        JOptionPane.showMessageDialog(null, "Sign Up Failed! User already exists.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null, "Database Error", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                LoginScreen loginScreen = new LoginScreen();
                loginScreen.setVisible(true);
            }
        });
    }
}
