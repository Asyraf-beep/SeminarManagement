import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class CardLayoutExample {
    private final JFrame frame = new JFrame("Switching Panels Demo");
    private final JPanel cards = new JPanel(new CardLayout()); // The panel that uses CardLayout

    public CardLayoutExample() {
        // Create the individual panels (screens)
        JPanel mainMenuPanel = new JPanel();
        mainMenuPanel.add(new JLabel("Welcome to the Main Menu!"));
        JButton goToDetailButton = new JButton("Go to Detail");
        mainMenuPanel.add(goToDetailButton);

        JPanel detailPanel = new JPanel();
        detailPanel.add(new JLabel("This is the Detail Screen."));
        JButton goToMainButton = new JButton("Go to Main Menu");
        detailPanel.add(goToMainButton);

        // Add the panels to the 'cards' panel with unique names
        cards.add(mainMenuPanel, "MAIN");
        cards.add(detailPanel, "DETAIL");

        // Add Action Listeners to buttons to trigger the switch
        ActionListener navigationListener = e -> {
            CardLayout cl = (CardLayout) (cards.getLayout());
            // Use the button's action command to specify which card to show
            cl.show(cards, e.getActionCommand()); 
        };
        
        goToDetailButton.setActionCommand("DETAIL");
        goToDetailButton.addActionListener(navigationListener);
        
        goToMainButton.setActionCommand("MAIN");
        goToMainButton.addActionListener(navigationListener);

        // Configure the main frame
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setContentPane(cards); // Set the cards panel as the frame's content pane
        frame.pack(); // Resize the frame to fit components
        frame.setLocationRelativeTo(null); // Center the window
         
    }

    public static void main(String[] args) {
        // Ensure GUI creation is done on the Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(CardLayoutExample::new);
    }
}