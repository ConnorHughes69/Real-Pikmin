import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import javax.swing.*;

public class Pikmin extends JPanel implements ActionListener, KeyListener {
    int boardWidth = 1280;
    int boardHeight = 720;

    // Images
    Image backgroundImg, whistleImg, pikminImg, idleImage;
    Image flowerImg, pelitFarm, pikminHouse, OmarsShip;
    Image[] backWalkFrames = new Image[7];
    Image[] forwardWalkFrames = new Image[7];
    Image[] leftWalkFrames = new Image[7];
    Image[] rightWalkFrames = new Image[7];

    int animationIndex = 0, animationCounter = 0, animationSpeed = 4;

    class Character {
        int width = 85, height = 85;
        int x = boardWidth / 2 - width / 2;
        int y = boardHeight - height - 10; // Spawn at bottom
        Image img;

        Character(Image img) {
            this.img = img;
        }

        Rectangle getBounds() {
            return new Rectangle(x + 20, y + 20, width - 40, height - 40);
        }
    }

    class PikminUnit {
        int x, y;
        int width = 90, height = 90;
        Image img;
        boolean following = false, thrown = false;
        double vx = 0, vy = 0;
        int throwTimer = 0, throwDuration = 30;

        PikminUnit(int x, int y, Image img) {
            this.x = x;
            this.y = y;
            this.img = img;
        }

        void follow(int targetX, int targetY) {
            int dx = targetX - x, dy = targetY - y;
            double dist = Math.sqrt(dx * dx + dy * dy);
            if (dist > 30) {
                x += (int) (dx / dist * 5);
                y += (int) (dy / dist * 5);
            }
        }

        void throwing(int mouseX, int mouseY) {
            following = false;
            thrown = true;
            throwTimer = throwDuration;
            int centerX = x + width / 2;
            int centerY = y + height / 2;
            int dx = mouseX - centerX;
            int dy = mouseY - centerY;
            double dist = Math.sqrt(dx * dx + dy * dy);
            double speed = 10;
            vx = (dx / dist) * speed;
            vy = (dy / dist) * speed;
        }

        void updateThrow() {
            if (thrown && throwTimer > 0) {
                x += vx;
                y += vy;
                throwTimer--;
            } else {
                thrown = false;
                vx = 0;
                vy = 0;
            }
        }

        Rectangle getBounds() {
            return new Rectangle(x + 20, y + 20, width - 40, height - 40);
        }
    }

    class Flower {
        int x, y;
        boolean harvested;

        Flower(int x, int y) {
            this.x = x;
            this.y = y;
            this.harvested = false;
        }

        Rectangle getBounds() {
            return new Rectangle(x + 50, y + 50, 100, 100); // Much smaller hitbox
        }
    }

    Character character;
    ArrayList<PikminUnit> pikminList = new ArrayList<>();
    ArrayList<Flower> flowerList = new ArrayList<>();
    boolean upPressed, downPressed, leftPressed, rightPressed;
    boolean drawWhistle = false;
    int mousex, mousey;
    Timer gameLoop;
    int moveSpeed = 5;

    public Pikmin() {
        setPreferredSize(new Dimension(boardWidth, boardHeight));
        setFocusable(true);
        addKeyListener(this);
        requestFocusInWindow();

        backgroundImg = new ImageIcon(getClass().getResource("map.png")).getImage();
        whistleImg = new ImageIcon(getClass().getResource("whistle.png")).getImage();
        pikminImg = new ImageIcon(getClass().getResource("Pikmin1.png")).getImage();
        idleImage = new ImageIcon(getClass().getResource("/OmarAnimations/OmarAnimation1idle.png")).getImage();
        flowerImg = new ImageIcon(getClass().getResource("flowerRed1.png")).getImage();
        pelitFarm = new ImageIcon(getClass().getResource("pelit.png")).getImage();
        pikminHouse = new ImageIcon(getClass().getResource("homebase.png")).getImage();
        OmarsShip = new ImageIcon(getClass().getResource("spaceShip.png")).getImage();

        for (int i = 0; i < 7; i++) {
            backWalkFrames[i] = new ImageIcon(getClass().getResource("/OmarAnimations/OmarAnimationback" + (i + 1) + ".png")).getImage();
            forwardWalkFrames[i] = new ImageIcon(getClass().getResource("/OmarAnimations/OmarAnimationup" + (i + 1) + ".png")).getImage();
            leftWalkFrames[i] = new ImageIcon(getClass().getResource("/OmarAnimations/OmarAnimationleft" + (i + 1) + ".png")).getImage();
            rightWalkFrames[i] = flipImageHorizontally(leftWalkFrames[i]);
        }

        character = new Character(idleImage);

        for (int i = 0; i < 5; i++) {
            pikminList.add(new PikminUnit(100 + i * 60, 600, pikminImg));
        }

        flowerList.add(new Flower(65, 110));
        flowerList.add(new Flower(900, 50));
        flowerList.add(new Flower(800, 500));

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

        for (Flower f : flowerList) {
            if (!f.harvested) {
                g.drawImage(flowerImg, f.x, f.y, 200, 200, null);
            }
        }

        g.drawImage(character.img, character.x, character.y, character.width, character.height, null);
        g.drawImage(pikminHouse, 245, 185, 220, 220, null);
        g.drawImage(OmarsShip, 545, 205, 200, 200, null);

        for (PikminUnit p : pikminList) {
            g.drawImage(p.img, p.x, p.y, p.width, p.height, null);
        }

        if (drawWhistle) {
            g.drawImage(whistleImg, mousex - 100, mousey - 100, 200, 200, null);
        }
    }

    public void move() {
        Rectangle homeBaseBounds = new Rectangle(245, 185, 220, 220);
        Rectangle shipBounds = new Rectangle(545, 205, 200, 200);

        int nextX = character.x;
        int nextY = character.y;

        if (upPressed) nextY -= moveSpeed;
        if (downPressed) nextY += moveSpeed;
        if (leftPressed) nextX -= moveSpeed;
        if (rightPressed) nextX += moveSpeed;

        Rectangle nextBounds = new Rectangle(nextX + 20, nextY + 20, character.width - 40, character.height - 40);
        if (!nextBounds.intersects(homeBaseBounds) && !nextBounds.intersects(shipBounds)) {
            character.x = nextX;
            character.y = nextY;
        }

        character.x = Math.max(0, Math.min(character.x, boardWidth - character.width));
        character.y = Math.max(0, Math.min(character.y, boardHeight - character.height));

        Point mousePosition = getMousePosition();
        if (mousePosition != null) {
            mousex = mousePosition.x;
            mousey = mousePosition.y;
        }

        int targetX = character.x, targetY = character.y;
        for (PikminUnit p : pikminList) {
            if (p.thrown) {
                p.updateThrow();
            } else if (p.following) {
                p.follow(targetX, targetY);
                targetX = p.x;
                targetY = p.y;
            }
        }

        updateCharacterAnimation();
    }

    private void updateCharacterAnimation() {
        if (upPressed) animate(forwardWalkFrames);
        else if (downPressed) animate(backWalkFrames);
        else if (leftPressed) animate(leftWalkFrames);
        else if (rightPressed) animate(rightWalkFrames);
        else {
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
            Rectangle whistleZone = new Rectangle(mousex - 150, mousey - 150, 300, 300);
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

        if (key == KeyEvent.VK_Q) {
            for (PikminUnit p : pikminList) {
                if (p.following && !p.thrown) {
                    p.throwing(mousex, mousey);
                    break;
                }
            }
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
