import java.awt.*;
import java.awt.event.*;
import javax.swing.*;



public class Picmin extends JPanel implements ActionListener, KeyListener {
    int boardWidth = 1920;
    int boardHeight = 1080;


    // Images
    Image backgroundImg;
    Image characterImg;
    Image whistle;


    // Character properties
    int characterX = boardWidth / 8;
    int characterY = boardHeight / 2;
    int characterWidth = 64;
    int characterHeight = 48;

    boolean drawWhistle = false;

    int mousex;
    int mousey;


    class Character {
        int x = characterX;
        int y = characterY;
        int width = characterWidth;
        int height = characterHeight;
        Image img;


        Character(Image img) {
            this.img = img;
        }
    }


    Character character;


    // Movement flags
    boolean upPressed = false;
    boolean downPressed = false;
    boolean leftPressed = false;
    boolean rightPressed = false;


    Timer gameLoop;
    int moveSpeed = 5;


    Picmin() {
        setPreferredSize(new Dimension(boardWidth, boardHeight));
        setFocusable(true);
        addKeyListener(this);


        // Load images (grass is background, character1 is the player)
        backgroundImg = new ImageIcon(getClass().getResource("grass.png")).getImage();
        characterImg = new ImageIcon(getClass().getResource("Character1.png")).getImage();
        whistle = new ImageIcon(getClass().getResource("whistle.png")).getImage();

        character = new Character(characterImg);


        // Start game loop
        gameLoop = new Timer(1000 / 60, this);
        gameLoop.start();
    }


    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }


    public void draw(Graphics g) {
        g.drawImage(backgroundImg, 0, 0, boardWidth, boardHeight, null);
        g.drawImage(character.img, character.x, character.y, character.width, character.height, null);

        if (drawWhistle) {
            Graphics2D g2d = (Graphics2D) g.create();

            // Center of the character
            int centerX = character.x + character.width / 2;
            int centerY = character.y + character.height / 2;

            // Angle to face the mouse
            double angle = Math.atan2(mousey - centerY, mousex - centerX);

            // Set rotation around character's center
            g2d.rotate(angle, centerX, centerY);

            // Draw the whistle offset from character center
            g2d.drawImage(whistle, centerX + 60, centerY - 50, 200, 200, null);

            g2d.dispose();
        }
    }


    public void move() {
        if (upPressed) character.y -= moveSpeed;
        if (downPressed) character.y += moveSpeed;
        if (leftPressed) character.x -= moveSpeed;
        if (rightPressed) character.x += moveSpeed;


        // Clamp within bounds
        character.x = Math.max(0, Math.min(character.x, boardWidth - character.width));
        character.y = Math.max(0, Math.min(character.y, boardHeight - character.height));

        Point mousePosition = MouseInfo.getPointerInfo().getLocation();
        mousex = (int) mousePosition.getX();
        mousey = (int) mousePosition.getY();
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        repaint();
    }


    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();

        if (key == KeyEvent.VK_W) upPressed = true;
        if (key == KeyEvent.VK_S) downPressed = true;
        if (key == KeyEvent.VK_A) leftPressed = true;
        if (key == KeyEvent.VK_D) rightPressed = true;

        if (key == KeyEvent.VK_E) {
            drawWhistle = true;

            // Create a timer that turns off the whistle after 2 seconds (2000 ms)
            new Timer(2000, new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    drawWhistle = false;
                    ((Timer) e.getSource()).stop(); // stop the timer
                }
            }).start();
        }
    }



    @Override
    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();


        if (key == KeyEvent.VK_W) upPressed = false;
        if (key == KeyEvent.VK_S) downPressed = false;
        if (key == KeyEvent.VK_A) leftPressed = false;
        if (key == KeyEvent.VK_D) rightPressed = false;
    }


    @Override
    public void keyTyped(KeyEvent e) {}
}
