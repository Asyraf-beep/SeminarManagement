import javax.swing.*;
import java.awt.*;

public class MyFrame extends JFrame{

    //constructor
    MyFrame(int width, int height){
        // int height = 500;
        // int width = 620;
        setSize(width, height);
        //this.setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setBackground(new Color(146, 209, 216));
        setLocationRelativeTo(null);
    }
    
}
