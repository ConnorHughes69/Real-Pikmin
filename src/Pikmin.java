//What to add
/*
make it so that when you walk to the top of the screen it brings you to a knew area
in the new area there should be 2 objectives, collecting ship parts
one ship part should be protected by an enemy you have to use a pikmin to kill
and other there should be a ball the Picmin have to break down
the pikmin should carry the ship parts back to the ship and then you leave and live happy ever after
also need a title screen 

 */

//imports
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import javax.swing.*;
import java.util.Iterator;

public class Pikmin extends JPanel implements ActionListener, KeyListener {
    //board width and hieght used for colision detection
    int boardWidth = 1280;
    int boardHeight = 720;
    //creating image variables
    Image backgroundImg, whistleImg, pikminImg, idleImage;
    Image flowerImg, pelitFarm, pikminHouse, OmarsShip, unbornPikmin;
    //image list to store images for animations
    Image[] backWalkFrames = new Image[7];
    Image[] forwardWalkFrames = new Image[7];
    Image[] leftWalkFrames = new Image[7];
    Image[] rightWalkFrames = new Image[7];

    int animationIndex = 0;//used to keep track of where we are in the animation
    int animationCounter = 0; //how fast the frames update
    int animationSpeed = 4;//how many frames must pass before the frame changes

    class Character {
        //width and hieght used when drawing character controls size
        int width = 85;
        int height = 85;
        // x and y used to determine where the character spawns and for colision detertion
        int x = boardWidth / 2 - width / 2;
        int y = boardHeight - height - 10;
        Image img;
        Character(Image img) {
            //used to change image for animation
            this.img = img;
        }
        Rectangle getBounds() {
            //used for earier colision detection, function we can call
            return new Rectangle(x + 20, y + 20, width - 40, height - 40);
        }
    }

    class Flower {
        int x;
        int y;
        int hitCount = 0;//used to count how many times the pikmin has hit the flower
        int requiredHits = 3;//how many times the flower must be hit to break
        boolean harvested;
        Flower(int x, int y) {
            //flower object has x and y position used for when I draw will be used in colision detection
            this.x = x; this.y = y;
        }
        Rectangle getBounds() {
            //used for colision dection
            return new Rectangle(x + 50, y + 50, 100, 100);
        }
    }

    //class for pelit
    class Pelit {
        int x;
        int y;
        Image img;
        Pelit(int x, int y, Image img) {
            //pelit has x and y positon and image varaible because image can be different based on flower color
            this.x = x;
            this.y = y;
            this.img = img;
        }
    }

    class UnspawnedPikmin {
        int x, y;
        Rectangle getBounds() {
            //used to keep track of pelit when it is traveling to homebase
            return new Rectangle(x, y, 50, 50);
        }
        //create an object to keep track of its position
        UnspawnedPikmin(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    //class for Pikmin
    class PikminUnit {
        // Position and size
        int x;
        int y;
        int width = 90;
        int height = 90;

        // image
        Image img;

        // flags
        boolean following = false;       // True if Pikmin is following the player
        boolean thrown = false;          // True if Pikmin has been thrown

        // Velocity for throwing pikmin
        double vx = 0;
        double vy = 0;

        // Timer for how long the Pikmin is in air
        int throwTimer = 0;
        int throwDuration = 30;          // Frames it stays in thrown motion

        // Flower interaction
        Flower assignedFlower = null;    // Flower currently being harvested
        int farmingCooldown = 0;         // Delay between harvest hits

        // Pelit carrying logic
        boolean carryingPelit = false;   // If the Pikmin is carrying a Pelit
        Pelit carriedPelit = null;       // The Pelit being carried

        // Constructor
        PikminUnit(int x, int y, Image img) {
            this.x = x;
            this.y = y;
            this.img = img;
        }

        // Makes the Pikmin follow a target point
        void follow(int targetX, int targetY) {
            int dx = targetX - x, dy = targetY - y;
            double dist = Math.sqrt(dx * dx + dy * dy);

            // Move only if distance is large enough
            if (dist > 30) {
                x += (int) (dx / dist * 5);  // Normalize and move
                y += (int) (dy / dist * 5);
            }
        }

        // Throws the Pikmin toward a target
        void throwing(int mouseX, int mouseY) {
            //changing the flags
            following = false;
            thrown = true;
            throwTimer = throwDuration;
            assignedFlower = null;
            carryingPelit = false;
            carriedPelit = null;

            // Calculate throw velocity vector
            int dx = mouseX - (x + width / 2);
            int dy = mouseY - (y + height / 2);
            double dist = Math.sqrt(dx * dx + dy * dy);
            double speed = 10;

            vx = (dx / dist) * speed;
            vy = (dy / dist) * speed;
        }

        // Called every frame to update a Pikmin's behavior if it was thrown
        void updateThrow() {
            // Movement during throw
            if (thrown && throwTimer-- > 0) {
                x += vx;
                y += vy;
            } else {
                thrown = false;
                vx = 0;
                vy = 0;
            }

            // logic for farming flower
            if (assignedFlower != null && !assignedFlower.harvested) {
                farmingCooldown--;

                // Hit the flower when cooldown is over
                if (farmingCooldown <= 0) {
                    assignedFlower.hitCount++;
                    farmingCooldown = 30;
                }

                // If flower is harvested, spawn a Pelit
                if (assignedFlower.hitCount >= assignedFlower.requiredHits) {
                    assignedFlower.harvested = true;
                    Pelit pelit = new Pelit(assignedFlower.x + 75, assignedFlower.y + 75, pelitFarm);
                    pelitList.add(pelit);
                    carriedPelit = pelit;
                    carryingPelit = true;
                    assignedFlower = null;
                }
            }

            // Return the Pelit to base if carrying it
            if (carryingPelit && carriedPelit != null) {
                int dx = 355 - x;
                int dy = 295 - y;
                double dist = Math.sqrt(dx * dx + dy * dy);

                if (dist > 5) {
                    // Move toward drop-off point
                    x += (int) (dx / dist * 2);
                    y += (int) (dy / dist * 2);
                    carriedPelit.x = x + 20;
                    carriedPelit.y = y;
                } else {
                    // Drop off Pelit at base
                    carryingPelit = false;
                    if (carriedPelit != null) {
                        pelitsToRemove.add(carriedPelit);
                    }
                    carriedPelit = null;

                    // Spawn a new UnspawnedPikmin randomly but away from home base and ship
                    Rectangle homeBaseBounds = new Rectangle(245, 185, 220, 220);
                    Rectangle shipBounds = new Rectangle(545, 205, 200, 200);
                    int spawnX, spawnY;
                    Rectangle newBounds;

                    // Make sure it doesn't spawn inside base or ship
                    do {
                        spawnX = 100 + (int)(Math.random() * (boardWidth - 200));
                        spawnY = 100 + (int)(Math.random() * (boardHeight - 200));
                        newBounds = new Rectangle(spawnX, spawnY, 60, 60);
                    } while (newBounds.intersects(homeBaseBounds) || newBounds.intersects(shipBounds));

                    // Add it to the unspawned Pikmin list
                    unspawnedPikminList.add(new UnspawnedPikmin(spawnX, spawnY));
                }
            }
        }

        // Gets the bounding box used for collision detection (smaller than actual image for fairness)
        Rectangle getBounds() {
            return new Rectangle(x + 20, y + 20, width - 40, height - 40);
        }
    }


    Character character;
    ArrayList<PikminUnit> pikminList = new ArrayList<>();
    ArrayList<UnspawnedPikmin> unspawnedPikminList = new ArrayList<>();
    ArrayList<Flower> flowerList = new ArrayList<>();
    ArrayList<Pelit> pelitList = new ArrayList<>();
    ArrayList<Pelit> pelitsToRemove = new ArrayList<>();

    boolean upPressed, downPressed, leftPressed, rightPressed, drawWhistle = false;
    int mousex, mousey, moveSpeed = 5;
    Timer gameLoop;

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
        unbornPikmin = new ImageIcon(getClass().getResource("unborn_pikmin.png")).getImage();

        for (int i = 0; i < 7; i++) {
            leftWalkFrames[i] = new ImageIcon(getClass().getResource("/OmarAnimations/OmarAnimationleft" + (i + 1) + ".png")).getImage();
            backWalkFrames[i] = new ImageIcon(getClass().getResource("/OmarAnimations/OmarAnimationback" + (i + 1) + ".png")).getImage();
            forwardWalkFrames[i] = new ImageIcon(getClass().getResource("/OmarAnimations/OmarAnimationup" + (i + 1) + ".png")).getImage();
            rightWalkFrames[i] = flipImageHorizontally(leftWalkFrames[i]);
        }

        character = new Character(idleImage);
        for (int i = 0; i < 5; i++) pikminList.add(new PikminUnit(100 + i * 60, 600, pikminImg));
        flowerList.add(new Flower(65, 110));
        flowerList.add(new Flower(900, 50));
        flowerList.add(new Flower(800, 500));

        gameLoop = new Timer(1000 / 60, this);
        gameLoop.start();
    }

    private Image flipImageHorizontally(Image img) {
        int w = img.getWidth(null), h = img.getHeight(null);
        BufferedImage flipped = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = flipped.createGraphics();
        g2d.drawImage(img, 0, 0, w, h, w, 0, 0, h, null);
        g2d.dispose();
        return flipped;
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(backgroundImg, 0, 0, boardWidth, boardHeight, null);
        for (Flower f : flowerList) if (!f.harvested) g.drawImage(flowerImg, f.x, f.y, 200, 200, null);
        g.drawImage(character.img, character.x, character.y, character.width, character.height, null);
        g.drawImage(pikminHouse, 245, 185, 220, 220, null);
        g.drawImage(OmarsShip, 545, 205, 200, 200, null);
        for (PikminUnit p : pikminList) g.drawImage(p.img, p.x, p.y, p.width, p.height, null);
        for (Pelit pelit : pelitList) g.drawImage(pelit.img, pelit.x, pelit.y, 50, 50, null);
        for (UnspawnedPikmin up : unspawnedPikminList) g.drawImage(unbornPikmin, up.x, up.y, 60, 60, null);
        if (drawWhistle) g.drawImage(whistleImg, mousex - 100, mousey - 100, 200, 200, null);
    }

    public void move() {
        Rectangle homeBaseBounds = new Rectangle(245, 185, 220, 220);
        Rectangle shipBounds = new Rectangle(545, 205, 200, 200);

        int nextX = character.x, nextY = character.y;
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
            p.updateThrow();
            if (!p.thrown && p.assignedFlower == null && !p.carryingPelit && p.following) {
                p.follow(targetX, targetY);
                targetX = p.x;
                targetY = p.y;
            }
        }

        farm();
        pelitList.removeAll(pelitsToRemove);
        pelitsToRemove.clear();
        updateCharacterAnimation();
    }

    public void farm() {
        for (PikminUnit p : pikminList) {
            if (p.thrown) {
                for (Flower f : flowerList) {
                    if (!f.harvested && f.getBounds().intersects(p.getBounds())) {
                        p.thrown = false;
                        p.vx = 0;
                        p.vy = 0;
                        p.assignedFlower = f;
                        p.farmingCooldown = 30;
                        break;
                    }
                }
            }
        }
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
        if (++animationCounter >= animationSpeed) {
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
            for (PikminUnit p : pikminList)
                if (whistleZone.intersects(p.getBounds())) p.following = true;
            new Timer(2000, evt -> { drawWhistle = false; ((Timer) evt.getSource()).stop(); }).start();
        }

        if (key == KeyEvent.VK_Q) {
            for (PikminUnit p : pikminList) {
                if (p.following && !p.thrown) {
                    p.throwing(mousex, mousey);
                    break;
                }
            }
        }

        if (key == KeyEvent.VK_F) {
            Iterator<UnspawnedPikmin> iterator = unspawnedPikminList.iterator();
            while (iterator.hasNext()) {
                UnspawnedPikmin up = iterator.next();
                if (character.getBounds().intersects(up.getBounds())) {
                    pikminList.add(new PikminUnit(up.x, up.y, pikminImg));
                    iterator.remove();
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
