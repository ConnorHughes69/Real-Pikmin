import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import javax.swing.*;

public class Pikmin extends JPanel implements ActionListener, KeyListener {
    int boardWidth = 1280;
    int boardHeight = 720;

    // Images
    Image backgroundImg;
    Image whistleImg;
    Image pikminImg;
    Image idleImage;

    Image[] backWalkFrames = new Image[7];
    Image[] forwardWalkFrames = new Image[7];
    Image[] leftWalkFrames = new Image[7];
    Image[] rightWalkFrames = new Image[7];

    int animationIndex = 0;
    int animationCounter = 0;
    int animationSpeed = 4;

    class Character {
        int x = boardWidth / 2;
        int y = boardHeight / 2;
        int width = 100, height = 100;
        Image img;

        Character(Image img) {
            this.img = img;
        }
    }

    class PikminUnit {
        int x, y;
        int width = 90, height = 90;
        Image img;
        boolean following = false;

        PikminUnit(int x, int y, Image img) {
            this.x = x;
            this.y = y;
            this.img = img;
        }

        void follow(int targetX, int targetY) {
            int dx = targetX - x;
            int dy = targetY - y;
            double dist = Math.sqrt(dx * dx + dy * dy);
            if (dist > 30) {
                x += (int) (dx / dist * 5);
                y += (int) (dy / dist * 5);
            }
        }

        Rectangle getBounds() {
            return new Rectangle(x, y, width, height);
        }
    }

    Character character;
    ArrayList<PikminUnit> pikminList = new ArrayList<>();

    boolean upPressed, downPressed, leftPressed, rightPressed;
    boolean drawWhistle = false;

    int mousex, mousey;
    Timer gameLoop;
    int moveSpeed = 5;

    public Pikmin() {
        setPreferredSize(new Dimension(boardWidth, boardHeight));
        setFocusable(true);
        addKeyListener(this);

        backgroundImg = new ImageIcon(getClass().getResource("grass.png")).getImage();
        whistleImg = new ImageIcon(getClass().getResource("whistle.png")).getImage();
        pikminImg = new ImageIcon(getClass().getResource("Pikmin1.png")).getImage();
        idleImage = new ImageIcon(getClass().getResource("/OmarAnimations/OmarAnimation1idle.png")).getImage();

        for (int i = 0; i < 7; i++) {
            backWalkFrames[i] = new ImageIcon(getClass().getResource("/OmarAnimations/OmarAnimationback" + (i + 1) + ".png")).getImage();
            forwardWalkFrames[i] = new ImageIcon(getClass().getResource("/OmarAnimations/OmarAnimationup" + (i + 1) + ".png")).getImage();
            leftWalkFrames[i] = new ImageIcon(getClass().getResource("/OmarAnimations/OmarAnimationleft" + (i + 1) + ".png")).getImage();
        }

        // Generate rightWalkFrames by flipping leftWalkFrames
        for (int i = 0; i < 7; i++) {
            rightWalkFrames[i] = flipImageHorizontally(leftWalkFrames[i]);
        }

        character = new Character(idleImage);

        for (int i = 0; i < 5; i++) {
            pikminList.add(new PikminUnit(100 + i * 60, 600, pikminImg));
        }

        gameLoop = new Timer(1000 / 60, this);
        gameLoop.start();
    }

    private Image flipImageHorizontally(Image img) {
        int w = img.getWidth(null);
        int h = img.getHeight(null);
        BufferedImage flipped = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = flipped.createGraphics();
        g2d.drawImage(img, 0, 0, w, h, w, 0, 0, h, null);
        g2d.dispose();
        return flipped;
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        g.drawImage(backgroundImg, 0, 0, boardWidth, boardHeight, null);
        g.drawImage(character.img, character.x, character.y, character.width, character.height, null);

        for (PikminUnit p : pikminList) {
            g.drawImage(p.img, p.x, p.y, p.width, p.height, null);
        }

        if (drawWhistle) {
            Graphics2D g2d = (Graphics2D) g.create();
            int centerX = character.x + character.width / 2;
            int centerY = character.y + character.height / 2;
            double angle = Math.atan2(mousey - centerY, mousex - centerX);
            g2d.rotate(angle, centerX, centerY);
            g2d.drawImage(whistleImg, centerX + 60, centerY - 50, 200, 200, null);
            g2d.dispose();
        }
    }

    public void move() {
        if (upPressed) character.y -= moveSpeed;
        if (downPressed) character.y += moveSpeed;
        if (leftPressed) character.x -= moveSpeed;
        if (rightPressed) character.x += moveSpeed;

        character.x = Math.max(0, Math.min(character.x, boardWidth - character.width));
        character.y = Math.max(0, Math.min(character.y, boardHeight - character.height));

        Point mousePosition = MouseInfo.getPointerInfo().getLocation();
        mousex = (int) mousePosition.getX();
        mousey = (int) mousePosition.getY();

        int targetX = character.x;
        int targetY = character.y;
        for (PikminUnit p : pikminList) {
            if (p.following) {
                p.follow(targetX, targetY);
                targetX = p.x;
                targetY = p.y;
            }
        }

        updateCharacterAnimation();
    }

    private void updateCharacterAnimation() {
        if (upPressed) {
            animate(forwardWalkFrames);
        } else if (downPressed) {
            animate(backWalkFrames);
        } else if (leftPressed) {
            animate(leftWalkFrames);
        } else if (rightPressed) {
            animate(rightWalkFrames);
        } else {
            character.img = idleImage;
            animationIndex = 0;
            animationCounter = 0;
        }
    }

    private void animate(Image[] frames) {
        animationCounter++;
        if (animationCounter >= animationSpeed) {
            animationCounter = 0;
            animationIndex = (animationIndex + 1) % frames.length;
            character.img = frames[animationIndex];
        }
    }

    public void actionPerformed(ActionEvent e) {
        move();
        repaint();
    }

    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_W) upPressed = true;
        if (key == KeyEvent.VK_S) downPressed = true;
        if (key == KeyEvent.VK_A) leftPressed = true;
        if (key == KeyEvent.VK_D) rightPressed = true;

        if (key == KeyEvent.VK_E) {
            drawWhistle = true;
            Rectangle whistleZone = new Rectangle(character.x - 200, character.y - 200, 500, 500);
            for (PikminUnit p : pikminList) {
                if (whistleZone.intersects(p.getBounds())) {
                    p.following = true;
                }
            }
            new Timer(2000, evt -> {
                drawWhistle = false;
                ((Timer) evt.getSource()).stop();
            }).start();
        }
    }

    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_W) upPressed = false;
        if (key == KeyEvent.VK_S) downPressed = false;
        if (key == KeyEvent.VK_A) leftPressed = false;
        if (key == KeyEvent.VK_D) rightPressed = false;
    }

    public void keyTyped(KeyEvent e) {}
}
