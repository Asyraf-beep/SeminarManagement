import javax.swing.*;
import java.awt.Color;

public class MyFrame extends JFrame{

    //constructor
    MyFrame(){
        int width = 400;
        int height = 420;
        this.setSize(width, height);
        //this.setResizable(false);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLayout(null);
        this.getContentPane().setBackground(new Color(146, 209, 216));
        this.setLocationRelativeTo(null);

    }
    
}
