import javax.swing.*;
import java.awt.*;
import java.io.*;

public class LoginSignupUI {

    static final String FILE_NAME = "users.txt";

    public static void main(String[] args) {
        
        showLogin();
    }

    // LOGIN WINDOW
    static void showLogin() {
        
        MyFrame frame = new MyFrame();
        frame.setTitle("Login");
        frame.setLayout(new BorderLayout());

        // HEADER
        frame.add(new HeaderPanel("Login"), BorderLayout.NORTH);

        // FORM PANEL
        JPanel formPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));

        JLabel userLabel = new JLabel("Username:");
        JTextField userField = new JTextField();
        JLabel passLabel = new JLabel("Password:");
        JPasswordField passField = new JPasswordField();

        formPanel.add(userLabel);
        formPanel.add(userField);
        formPanel.add(passLabel);
        formPanel.add(passField);
        frame.add(formPanel, BorderLayout.CENTER);

        // BUTTON PANEL
        JPanel buttonPanel = new JPanel(new FlowLayout());

        JButton loginBtn = new JButton("Login");
        JButton signupBtn = new JButton("Sign Up");

        buttonPanel.add(loginBtn);
        buttonPanel.add(signupBtn);

        frame.add(buttonPanel, BorderLayout.SOUTH);

        // ACTIONS
        loginBtn.addActionListener(e -> {
            String user = userField.getText();
            String pass = new String(passField.getPassword());

            if (checkLogin(user, pass)) {
                JOptionPane.showMessageDialog(frame, "Login Successful!");
            } else {
                JOptionPane.showMessageDialog(frame, "Invalid username or password");
            }
        });

        signupBtn.addActionListener(e -> {
            frame.dispose();
            showSignup();
        });

        frame.setVisible(true);
    }

    // SIGN UP WINDOW
    static void showSignup() {
        MyFrame frame = new MyFrame();
        frame.setTitle("Sign Up");

        JLabel userLabel = new JLabel("Username:");
        userLabel.setBounds(30, 30, 80, 25);
        frame.add(userLabel);

        JTextField userField = new JTextField();
        userField.setBounds(120, 30, 120, 25);
        frame.add(userField);

        JLabel passLabel = new JLabel("Password:");
        passLabel.setBounds(30, 70, 80, 25);
        frame.add(passLabel);

        JPasswordField passField = new JPasswordField();
        passField.setBounds(120, 70, 120, 25);
        frame.add(passField);

        JButton signupBtn = new JButton("Create Account");
        signupBtn.setBounds(80, 120, 140, 30);
        frame.add(signupBtn);

        signupBtn.addActionListener(e -> {
            String user = userField.getText();
            String pass = new String(passField.getPassword());

            saveUser(user, pass);
            JOptionPane.showMessageDialog(frame, "Account Created!");
            frame.dispose();
            showLogin();
        });

        frame.setVisible(true);
    }

    // SAVE USER TO FILE
    static void saveUser(String username, String password) {
        try (FileWriter fw = new FileWriter(FILE_NAME, true)) {
            fw.write(username + "," + password + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // CHECK LOGIN DETAILS
    static boolean checkLogin(String username, String password) {
        try (BufferedReader br = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                if (data[0].equals(username) && data[1].equals(password)) {
                    return true;
                }
            }
        } catch (IOException e) {
            return false;
        }
        return false;
    }
}
