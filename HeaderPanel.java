import javax.swing.*;
import java.awt.*;

public class HeaderPanel extends JPanel{

    public HeaderPanel() {
        setLayout(new FlowLayout(FlowLayout.CENTER,0,30));
        setBackground(new Color(241, 173, 44)); // blue header
        setPreferredSize(new Dimension(0, 100));

        JLabel titleLabel = new JLabel("SEMINAR MANAGEMENT SYSTEM");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 10));

        add(titleLabel, BorderLayout.CENTER);
    }
}
