import javax.swing.*;
import java.awt.event.*;

public class LoginUI {

    public static void main(String[] args) {
        JFrame frame = new JFrame("Login");
        frame.setSize(300, 200);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(null);

        // Username label
        JLabel userLabel = new JLabel("Username:");
        userLabel.setBounds(30, 30, 80, 25);
        frame.add(userLabel);

        // Username field
        JTextField userField = new JTextField();
        userField.setBounds(120, 30, 120, 25);
        frame.add(userField);

        // Password label
        JLabel passLabel = new JLabel("Password:");
        passLabel.setBounds(30, 70, 80, 25);
        frame.add(passLabel);

        // Password field
        JPasswordField passField = new JPasswordField();
        passField.setBounds(120, 70, 120, 25);
        frame.add(passField);

        // Login button
        JButton loginButton = new JButton("Login");
        loginButton.setBounds(90, 110, 100, 30);
        frame.add(loginButton);

        // Button action
        loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String username = userField.getText();
                String password = new String(passField.getPassword());

                if (username.equals("admin") && password.equals("1234")) {
                    JOptionPane.showMessageDialog(frame, "Login Successful!");
                } else {
                    JOptionPane.showMessageDialog(frame, "Invalid username or password");
                }
            }
        });

        frame.setVisible(true);
    }
}
