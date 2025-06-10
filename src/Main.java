import javax.swing.*;
import java.awt.*;

public class Main {
    public static void main(String[] args) {
        int boardWidth = 1920;
        int boardHeight = 1040;

        JFrame frame = new JFrame("Pikmin");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(true); // Or false if you prefer a fixed size

        // 1. Create a main panel with CardLayout
        JPanel mainPanel = new JPanel(new CardLayout());

        // 2. Create instances of all your screens (panels)
        TitleScreen titleScreen = new TitleScreen(mainPanel, frame);
        Pikmin gameScreen = new Pikmin(); // Your original game panel
        OptionsScreen optionsScreen = new OptionsScreen(mainPanel);

        // 3. Add each screen to the main panel with a unique name
        mainPanel.add(titleScreen, "TITLE");
        mainPanel.add(gameScreen, "GAME");
        mainPanel.add(optionsScreen, "OPTIONS");

        // 4. Add the main panel to the frame
        frame.add(mainPanel);

        // 5. Finalize frame setup
        frame.pack(); // pack() is better than setSize() when using layout managers
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}