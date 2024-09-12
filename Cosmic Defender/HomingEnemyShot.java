/* HomingEnemyShot.java
 * November 24, 2022
 * Represents homing enemy shots
 */

public class HomingEnemyShot extends EnemyShotEntity {
    
    private ShipEntity target; // target to home in on
    private int limit = 650; // max x and y speed of shot
    private int acceleration = 25; // how fast shot can change in speed
    private int timer = 2000; // how long to home for in milliseconds
    
    public HomingEnemyShot(Game g, String r, int newX, int newY) {
        super(g, r, newX, newY);
        target = game.getShip();
    } // HomingEnemyShot constructor
    
    // Constructs a shot that starts with momentum
    public HomingEnemyShot(Game g, String r, int newX, int newY, int newDX, int newDY) {
        super(g, r, newX, newY);
        target = game.getShip();
        dx = newDX;
        dy = newDY;
    } // HomingEnemyShot constructor

    public void move(long delta) {
        
        // Accelerate towards target
        if (timer > 0) {
            if (target.getX() < x) {
                dx -= acceleration;
                if (dx < -limit) {
                    dx = -limit;
                } // if
            } // if
            else if (target.getX() > x) {
                dx += acceleration;
                if (dx > limit) {
                    dx = limit;
                } // if
            } // else if
            if (target.getY() < y) {
                dy -= acceleration;
                if (dy < -limit) {
                    dy = -limit;
                } // if
            } // if
            else if (target.getY() > y) {
                dy += acceleration;
                if (dy > limit) {
                    dy = limit;
                } // if
            } // else if
        } // if
        
        super.move(delta); // calls the move method in EnemyShotEntity
        
        // if shot moves off screen, remove it from entity list (off left side is handled in super class)
        if (x > 1920 || y > 1080 || y < -40) {
            game.removeEntity(this);
        } // if

    } // move
    
    // decrease timer
    public void update(long d) {
        timer -= d;
    } // update
    
} // HomingEnemyShot Class
