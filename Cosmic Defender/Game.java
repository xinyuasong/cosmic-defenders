
/* Game.java
 * Cosmic Defender Main Program
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.util.ArrayList;

public class Game extends Canvas {

    private BufferStrategy strategy; // take advantage of accelerated graphics
    private boolean waitingForKeyPress = true; // true if game held up until

    private final int GAME_WIDTH = 1920; // width of game
    private final int GAME_HEIGHT = 1080; // height of game
                                          // a key is pressed
    private boolean leftPressed = false; // true if left arrow key currently pressed
    private boolean rightPressed = false; // true if right arrow key currently pressed
    private boolean upPressed = false; // true if up arrow key currently pressed
    private boolean downPressed = false; // true if down arrow key currently pressed
    private boolean firePressed = false; // true if firing

    private boolean gameRunning = true;

    private ArrayList entities = new ArrayList(); // list of entities
                                                  // in game
    private ArrayList removeEntities = new ArrayList(); // list of entities
                                                        // to remove this loop
    private Entity ship; // the ship
    private double moveSpeed = 600; // velocity of ship (px/s)
    private long lastFire = 0; // time last shot fired
    private long firingInterval = 100; // interval between shots (ms)

    private int timeToSpawn = 0; // time in ms till next enemy spawn
    private int enemiesSpawned = 0; // enemies already spawned in this wave
    private int nextWave = 1; // wave to spawn
    private int enemyCount = 0; // # of enemies alive

    private EffectsEntity lives; // player lives display
    private EffectsEntity screen; // start, tutorial, end, and win screens
    
    private int atkUpTimer = 0; // time until attack speed returns to normal
    private int speedUpTimer = 0; // time until move speed returns to normal

    /*
     * Construct our game and set it running.
     */
    public Game() {
        // create a frame to contain game
        JFrame container = new JFrame("Cosmic Defender");

        // get hold the content of the frame
        JPanel panel = (JPanel) container.getContentPane();

        // set up the resolution of the game
        panel.setPreferredSize(new Dimension(GAME_WIDTH, GAME_HEIGHT));
        panel.setLayout(null);

        // set up canvas size (this) and add to frame
        setBounds(0, 0, GAME_WIDTH, GAME_HEIGHT);
        panel.add(this);

        // Tell AWT not to bother repainting canvas since that will
        // be done using graphics acceleration
        setIgnoreRepaint(true);

        // make the window visible
        container.pack();
        container.setResizable(false);
        container.setVisible(true);

        // if user closes window, shutdown game and jre
        container.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            } // windowClosing
        });

        // add key listener to this canvas
        addKeyListener(new KeyInputHandler());

        // request focus so key events are handled by this canvas
        requestFocus();

        // create buffer strategy to take advantage of accelerated graphics
        createBufferStrategy(2);
        strategy = getBufferStrategy();
        
        // create start screen
        String[] screens = {"sprites/HowToPlay.jpg", "sprites/HowToPowerUps.jpg",
                "sprites/TryAgain.png", "sprites/EndingMessage.jpg"}; // full screen sprites 
        screen = new EffectsEntity(this, screens, 0, 0, false);
        
        // create health bar
        String[] health = new String[4]; // lives bar sprites
        for (int i = 0; i < health.length; i++) {
            health[i] = "sprites/lives" + i + ".png";
        } // for
        lives = new EffectsEntity(this, health, 20, 20, false);

        // initialize entities
        initEntities();

        // start the game
        gameLoop();
    } // constructor

    /*
     * initEntities input: none output: none purpose: Initialise the starting state
     * of the ship, enemy, and UI entities. Each entity will be added to the array of
     * entities in the game.
     */
    private void initEntities() {
        
        // create the ship and put on left side of screen
        ship = new ShipEntity(this, "sprites/ship.png", 30, 492);
        entities.add(ship);
        
        timeToSpawn = 3000;
    } // initEntities

    /*
     * Remove an entity from the game. It will no longer be moved or drawn.
     */
    public void removeEntity(Entity entity) {
        removeEntities.add(entity);
    } // removeEntity

    /*
     * Notification that the player has died.
     */
    public void notifyDeath() {
        screen.setFrame(2);
        waitingForKeyPress = true;
    } // notifyDeath

    /*
     * Notification than an enemy has been killed
     */
    public void notifyEnemyKilled() {
        enemyCount--;

        // Start next wave
        if (enemyCount == 0) {
            timeToSpawn = 1000;
            enemiesSpawned = 0;
            nextWave++;
        } // if

    } // notifyEnemyKilled

    /* Attempt to fire. */
    public void tryToFire() {

        // check that we've waited long enough to fire
        if ((System.currentTimeMillis() - lastFire) < firingInterval) {
            return;
        } // if

        // otherwise add a shot
        lastFire = System.currentTimeMillis();
        ShotEntity shot = new ShotEntity(this, "sprites/playerShot.png",
                ship.getX() + 96 + (int) (ship.getHorizontalMovement() / 1000), ship.getY() + 43);
        entities.add(shot);
    } // tryToFire

    // Creates an enemy at the given position
    private void spawn(String type, int x, int y) {
        String ref = ""; // enemy sprite
        ref = "sprites/" + type.toLowerCase() + "Enemy.png";
        Entity enemy = new EnemyEntity(this, ref, type, x, y);
        entities.add(enemy);
        enemyCount++;
        enemiesSpawned++;
    } // spawn

    // Spawn the next enemy in the current wave
    private void spawnWave(int num) {
        switch (num) {
            case 1:
                switch (enemiesSpawned) {
                    case 0:
                        spawn("Normal", GAME_WIDTH, 750);
                        timeToSpawn = 1000;
                        break;
                    case 1:
                        spawn("Normal", GAME_WIDTH, 200);
                        timeToSpawn = 1600;
                        break;
                    case 2:
                        spawn("Normal", GAME_WIDTH, 600);
                } // switch
                break;
    
            case 2:
                switch (enemiesSpawned) {
                    case 0:
                        spawn("Vertical", GAME_WIDTH - 200, -200);
                        timeToSpawn = 2000;
                        break;
                    case 1:
                        spawn("Normal", GAME_WIDTH, 400);
                        timeToSpawn = 500;
                        break;
                    case 2:
                        spawn("Vertical", GAME_WIDTH - 450, GAME_HEIGHT);
                } // switch
                break;
    
            case 3:
                switch (enemiesSpawned) {
                    case 0:
                        spawn("Still", GAME_WIDTH, 476);
                        timeToSpawn = 2000;
                        break;
                    case 1:
                        spawn("Normal", GAME_WIDTH, 100);
                        timeToSpawn = 750;
                        break;
                    case 2:
                        spawn("Normal", GAME_WIDTH, 650);
                } // switch
                break;
    
            case 4:
                switch (enemiesSpawned) {
                    case 0:
                        spawn("Vertical", GAME_WIDTH - 200, -200);
                        timeToSpawn = 0;
                        break;
                    case 1:
                        spawn("Vertical", GAME_WIDTH - 200, GAME_HEIGHT + 200);
                        timeToSpawn = 2000;
                        break;
                    case 2:
                        spawn("Normal", GAME_WIDTH, 200);
                        timeToSpawn = 2500;
                        break;
                    case 3:
                        spawn("Still", GAME_WIDTH, 350);
                } // switch
                break;
            
            case 5:
                switch (enemiesSpawned) {
                    case 0:
                        spawn("Normal", GAME_WIDTH, 750);
                        timeToSpawn = 1500;
                        break;
                    case 1:
                        spawn("Still", GAME_WIDTH, 600);
                        timeToSpawn = 0;
                        break;
                    case 2:
                        spawn("Still", GAME_WIDTH, 800);
                        timeToSpawn = 2500;
                        break;
                    case 3:
                        spawn("Normal", GAME_WIDTH, 300);
                        timeToSpawn = 500;
                        break;
                    case 4:
                        spawn("Normal", GAME_WIDTH, 450);
                } // switch
                break;
            
            case 6:
                switch (enemiesSpawned) {
                    case 0:
                        spawn("Vertical", GAME_WIDTH - 200, GAME_HEIGHT);
                        timeToSpawn = 1500;
                        break;
                    case 1:
                        spawn("Vertical", GAME_WIDTH - 400, -200);
                        timeToSpawn = 0;
                        break;
                    case 2:
                        spawn("Vertical", GAME_WIDTH - 600, GAME_HEIGHT);
                        timeToSpawn = 2000;
                        break;
                    case 3:
                        spawn("Normal", GAME_WIDTH, 500);
                        timeToSpawn = 1500;
                        break;
                    case 4:
                        spawn("Normal", GAME_WIDTH, 250);
                        break;
                } // switch
                break;
                
            case 7:
                switch (enemiesSpawned) {
                    case 0:
                        spawn("Still", GAME_WIDTH, GAME_HEIGHT - 400);
                        timeToSpawn = 0;
                        break;
                    case 1:
                        spawn("Still", GAME_WIDTH, 200);
                        timeToSpawn = 1500;
                        break;
                    case 2:
                        spawn("Still", GAME_WIDTH, 476);
                        timeToSpawn = 4500;
                        break;
                    case 3:
                        spawn("Normal", GAME_WIDTH, 250);
                        timeToSpawn = 0;
                        break;
                    case 4:
                        spawn("Normal", GAME_WIDTH, 530);
                        break;
                } // switch
                break;
                
            case 8:
                switch (enemiesSpawned) {
                    case 0:
                        spawn("Normal", GAME_WIDTH, 750);
                        timeToSpawn = 1500;
                        break;
                    case 1:
                        spawn("Normal", GAME_WIDTH, 300);
                        timeToSpawn = 1000;
                        break;
                    case 2:
                        spawn("Normal", GAME_WIDTH, 550);
                        timeToSpawn = 800;
                        break;
                    case 3:
                        spawn("Normal", GAME_WIDTH, 325);
                        timeToSpawn = 750;
                        break;
                    case 4:
                        spawn("Normal", GAME_WIDTH, 700);
                        timeToSpawn = 700;
                        break;
                    case 5:
                        spawn("Normal", GAME_WIDTH, 400);
                        timeToSpawn = 650;
                        break;
                    case 6:
                        spawn("Normal", GAME_WIDTH, 800);
                        timeToSpawn = 1000;
                        break;
                    case 7:
                        spawn("Normal", GAME_WIDTH, 200);
                        timeToSpawn = 500;
                        break;
                    case 8:
                        spawn("Normal", GAME_WIDTH, 600);
                        timeToSpawn = 600;
                        break;
                    case 9:
                        spawn("Normal", GAME_WIDTH, 350);
                        break;
                } // switch
                break;
            case 9:
                if (enemiesSpawned == 0) {
                    spawn("Boss", GAME_WIDTH, 240);
                } // if
                break;
                
            default:
                if (enemiesSpawned == 0) {
                    enemiesSpawned++;
                    timeToSpawn = 5000;
                } // if
                else {
                    screen.setFrame(3);
                    waitingForKeyPress = true;
                } // else
                
        } // switch
    } // startWave
    
    // Sets a wave to spawn
    public void setWave(int w) {
        enemiesSpawned = 0;
        nextWave = w;
    } // setWave

    // Allows other classes to add entities to the game
    public void addEntity(Entity e) {
        entities.add(e);
    } // addEntity

    // resets everything
    private void resetGame() {
        
        // close game if the boss has been beaten
        if (nextWave >= 10) {
            System.exit(0);
        } // if
        
        // clear out any existing entities and initalize a new set
        entities.clear();
        
        initEntities();
        
        // blank out any keyboard inputs that might exist
        leftPressed = false;
        rightPressed = false;
        firePressed = false;
        upPressed = false;
        
        // reset waves
        nextWave = 1;
        enemiesSpawned = 0;
        enemyCount = 0;
        
    } // resetGame

    public ShipEntity getShip() {
        return (ShipEntity) ship;
    } // getShip

    // adds health to player's ship from power up
    public void addHealth(int addedHealth) {
        ShipEntity player = (ShipEntity) ship;
        if (player.getLives() < 3) {
            player.addLives(addedHealth);
        } // if
    } // addHealth

    // Set the firing interval
    public void setFiringInterval(int firingSpeed) {
        firingInterval = firingSpeed;
        atkUpTimer = 15000; // 15 second timer
    } // setFiringInterval

    // Set the ship's movement speed
    public void setShipSpeed(int shipSpeed) {
        moveSpeed = shipSpeed;
        speedUpTimer = 20000; // 20 second timer
    } // setFiringInterval
    
    // Sets ship firing speed to default
    private void resetShipAttack() {
        firingInterval = 100;
    } // resetShipAttack
    
    // Sets ship movement speed to default
    private void resetShipSpeed() {
        moveSpeed = 600;
    } // resetShipSpeed

    /*
     * gameLoop input: none output: none purpose: Main game loop. Runs throughout
     * game play. Responsible for the following activities: - calculates speed of
     * the game loop to update moves - moves the game entities - draws the screen
     * contents (entities, text) - updates game events - checks input
     */
    public void gameLoop() {
        long lastLoopTime = System.currentTimeMillis();

        // Scrolling Background
        BufferedImage back = null; // background image
        Background backOne = new Background(); // first copy of background image (used for moving background)
        Background backTwo = new Background(backOne.getImageWidth(), 0); // second copy of background image (used for
                                                                         // moving background)

        // keep loop running until game ends
        while (gameRunning) {

            // calc. time since last update, will be used to calculate
            // entities movement and enemy spawns
            long delta = System.currentTimeMillis() - lastLoopTime;
            lastLoopTime = System.currentTimeMillis();
            if (!waitingForKeyPress) {
                timeToSpawn -= delta;
            } // if

            // get graphics context for the accelerated surface
            Graphics2D g = (Graphics2D) strategy.getDrawGraphics();

            // scrolling Background
            if (back == null)
                back = (BufferedImage) (createImage(getWidth(), getHeight()));

            // creates a buffer to draw to
            Graphics buffer = back.createGraphics();

            // puts the two copies of the background image onto the buffer
            backOne.draw(buffer);
            backTwo.draw(buffer);

            // draws the image onto the window
            g.drawImage(back, null, 0, 0);

            // move each entity
            if (!waitingForKeyPress) {
                for (int i = 0; i < entities.size(); i++) {
                    Entity entity = (Entity) entities.get(i);
                    entity.move(delta);
                } // for
            } // if

            // check lives
            lives.setFrame(((ShipEntity) ship).getLives());

            // draw all entities
            for (int i = 0; i < entities.size(); i++) {
                Entity entity = (Entity) entities.get(i);
                entity.draw(g);
            } // for

            // draw player lives bar
            lives.draw(g);
            
            // if waiting for "any key press", draw appropriate screen effects
            if (waitingForKeyPress) {
                screen.draw(g);
            } // if
            
            // brute force collisions, compare every entity
            // against every other entity. If any collisions
            // are detected notify both entities that it has
            // occurred
            for (int i = 0; i < entities.size(); i++) {
                for (int j = i + 1; j < entities.size(); j++) {
                    Entity me = (Entity) entities.get(i);
                    Entity him = (Entity) entities.get(j);

                    if (me.collidesWith(him)) {
                        me.collidedWith(him);
                        him.collidedWith(me);
                    } // if
                } // inner for
            } // outer for

            // remove dead entities
            entities.removeAll(removeEntities);
            removeEntities.clear();

            // clear graphics and flip buffer
            g.dispose();
            strategy.show();

            // ship should not move without user input
            ship.setHorizontalMovement(0);
            ship.setVerticalMovement(0);

            // respond to user moving ship
            // divide speed in each direction by square root of 2 if moving diagonally
            // to scale speed such that the ship doesn't speed up
            if ((leftPressed) && (!rightPressed) && (ship.getX() > 30)) {
                ship.setHorizontalMovement(upPressed || downPressed ? (-moveSpeed / 1.4142) : -moveSpeed);
            } // if
            else if ((rightPressed) && (!leftPressed) && (ship.getX() < 1790)) {
                ship.setHorizontalMovement(upPressed || downPressed ? (moveSpeed / 1.4142) : moveSpeed);
            } // else if
            if ((upPressed) && (!downPressed) && (ship.getY() > 30)) {
                ship.setVerticalMovement(leftPressed || downPressed ? (-moveSpeed / 1.4142) : -moveSpeed);
            } // if
            else if ((downPressed) && (!upPressed) && (ship.getY() < 924)) {
                ship.setVerticalMovement(leftPressed || downPressed ? (moveSpeed / 1.4142) : moveSpeed);
            } // else if

            // if spacebar pressed, try to fire
            if (firePressed) {
                tryToFire();
            } // if

            // spawn enemies
            if (timeToSpawn <= 0) {
                spawnWave(nextWave);
            } // if

            // update entity timers
            if (!waitingForKeyPress) {
                for (int i = 0; i < entities.size(); i++) {
                    Entity entity = (Entity) entities.get(i);
                    entity.update(delta);
                } // for
            } // if
            
            // update power up timers
            if (atkUpTimer > 0) {
                atkUpTimer -= delta;
            } // if
            else {
                resetShipAttack();
            } // else if
            if (speedUpTimer > 0) {
                speedUpTimer -= delta;
            } // else
            else {
                resetShipSpeed();
            } // else if
            
            // pause
            try {
                Thread.sleep(10);
            } catch (Exception e) {
            }

        } // while

    } // gameLoop

    /*
     * inner class KeyInputHandler handles keyboard input from the user
     */
    private class KeyInputHandler extends KeyAdapter {

        private int pressCount = 0; // the number of key presses since
                                    // waiting for 'any' key press

        /*
         * The following methods are required for any class that extends the abstract
         * class KeyAdapter. They handle keyPressed, keyReleased and keyTyped events.
         */
        public void keyPressed(KeyEvent e) {

            // if waiting for keypress to start game, do nothing
            if (waitingForKeyPress) {
                return;
            } // if

            // respond to movement or fire
            if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                leftPressed = true;
            } // if

            if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                rightPressed = true;
            } // if

            if (e.getKeyCode() == KeyEvent.VK_UP) {
                upPressed = true;
            } // if

            if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                downPressed = true;
            } // if

            if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                firePressed = true;
            } // if

        } // keyPressed

        public void keyReleased(KeyEvent e) {

            // if waiting for keypress to start game, do nothing
            if (waitingForKeyPress) {
                return;
            } // if

            // respond to movement or fire
            if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                leftPressed = false;
            } // if

            if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                rightPressed = false;
            } // if

            if (e.getKeyCode() == KeyEvent.VK_UP) {
                upPressed = false;
            } // if

            if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                downPressed = false;
            } // if

            if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                firePressed = false;
            } // if

        } // keyReleased

        public void keyTyped(KeyEvent e) {

            // if waiting for key press to start game
            if (waitingForKeyPress) {
                if (pressCount == 1) {
                    waitingForKeyPress = false;
                    resetGame();
                } else {
                    pressCount++;
                    screen.setFrame(1);
                } // else
            } // if waitingForKeyPress

            // if escape is pressed, end game
            if (e.getKeyChar() == 27) {
                System.exit(0);
            } // if escape pressed

        } // keyTyped

    } // class KeyInputHandler

    /**
     * Main Program
     */
    public static void main(String[] args) {
        // instantiate this object
        new Game();
    } // main
} // Game
