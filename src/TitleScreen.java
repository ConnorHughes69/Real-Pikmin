import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TitleScreen extends JPanel {

    private Image backgroundImg;
    private JButton playButton, optionsButton, quitButton;

    // Constructor accepts the main panel with CardLayout and the JFrame
    public TitleScreen(JPanel mainPanel, JFrame frame) {
        setLayout(null); // Use null layout for absolute positioning of buttons
        setPreferredSize(new Dimension(1920, 1040)); // Match your frame size

        // Load the background image
        try {
            backgroundImg = new ImageIcon(getClass().getResource("/grass.png")).getImage();
        } catch (Exception e) {
            System.err.println("Error loading grass.png. Make sure it's in your resources folder.");
            // Set a fallback color if the image fails to load
            setBackground(Color.GREEN);
        }

        // --- Create Buttons ---
        playButton = new JButton("Play");
        optionsButton = new JButton("Options");
        quitButton = new JButton("Quit");

        // --- Position and Size Buttons ---
        // Center the buttons on the screen
        int buttonWidth = 200;
        int buttonHeight = 50;
        int centerX = (1920 / 2) - (buttonWidth / 2);

        playButton.setBounds(centerX, 400, buttonWidth, buttonHeight);
        optionsButton.setBounds(centerX, 470, buttonWidth, buttonHeight);
        quitButton.setBounds(centerX, 540, buttonWidth, buttonHeight);

        // --- Add Buttons to the Panel ---
        add(playButton);
        add(optionsButton);
        add(quitButton);

        // --- Add Action Listeners ---
        CardLayout cardLayout = (CardLayout) mainPanel.getLayout();

        // Play Button: Switch to the "GAME" panel
        playButton.addActionListener(e -> {
            cardLayout.show(mainPanel, "GAME");
            // Find the Pikmin panel and request focus so key listeners work
            for (Component comp : mainPanel.getComponents()) {
                if (comp.isVisible() && comp instanceof Pikmin) {
                    comp.requestFocusInWindow();
                }
            }
        });

        // Options Button: Switch to the "OPTIONS" panel
        optionsButton.addActionListener(e -> cardLayout.show(mainPanel, "OPTIONS"));

        // Quit Button: Exit the application
        quitButton.addActionListener(e -> System.exit(0));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Draw the background image, covering the entire panel
        if (backgroundImg != null) {
            g.drawImage(backgroundImg, 0, 0, this.getWidth(), this.getHeight(), this);
        }
    }
}