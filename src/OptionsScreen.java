import javax.swing.*;
import java.awt.*;

public class OptionsScreen extends JPanel {

    public OptionsScreen(JPanel mainPanel) {
        setLayout(new BorderLayout()); // Use a layout manager
        setBackground(Color.BLACK);

        // "Silly goose" text
        JLabel messageLabel = new JLabel("silly goose", SwingConstants.CENTER);
        messageLabel.setFont(new Font("Serif", Font.BOLD, 100));
        messageLabel.setForeground(Color.WHITE);
        add(messageLabel, BorderLayout.CENTER);

        // Back button
        JButton backButton = new JButton("Back");

        // Panel to hold the back button at the bottom
        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false); // Make it transparent
        buttonPanel.add(backButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // Action listener to go back to the title screen
        backButton.addActionListener(e -> {
            CardLayout cardLayout = (CardLayout) mainPanel.getLayout();
            cardLayout.show(mainPanel, "TITLE");
        });
    }
}