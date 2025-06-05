import java.awt.*;
import java.awt.event.*;
import javax.swing.*;


public class Picmin extends JPanel implements ActionListener, KeyListener {
    int boardWidth = 360;
    int boardHeight = 640;


    // Images
    Image backgroundImg;
    Image characterImg;


    // Character properties
    int characterX = boardWidth / 8;
    int characterY = boardHeight / 2;
    int characterWidth = 34;
    int characterHeight = 24;


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
    }


    public void move() {
        if (upPressed) character.y -= moveSpeed;
        if (downPressed) character.y += moveSpeed;
        if (leftPressed) character.x -= moveSpeed;
        if (rightPressed) character.x += moveSpeed;


        // Clamp within bounds
        character.x = Math.max(0, Math.min(character.x, boardWidth - character.width));
        character.y = Math.max(0, Math.min(character.y, boardHeight - character.height));
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
