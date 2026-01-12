import javax.swing.*;
import java.awt.*;

public class HeaderPanel extends JPanel {

    public HeaderPanel(String title) {
        setLayout(new BorderLayout());
        setBackground(new Color(45, 118, 232)); // blue header
        setPreferredSize(new Dimension(0, 50));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 10));

        add(titleLabel, BorderLayout.WEST);
    }
}
