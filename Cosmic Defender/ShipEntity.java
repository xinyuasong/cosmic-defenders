/* ShipEntity.java
 * November 24 2022
 * Represents player's ship
 */
public class ShipEntity extends Entity {

    private Game game; // the game in which the ship exists
    private int lives = 3; // how many hits the ship can take
    private int invincibilityTime = 0; // how much longer to remain invincible
    private int flashTime = 0; // how long each flash during invincibility frames is
    private boolean isFlash = false; // if ship sprite is in hurt version

    /*
     * construct the player's ship input: game - the game in which the ship is being
     * created ref - a string with the name of the image associated to the sprite
     * for the ship x, y - initial location of ship
     */
    public ShipEntity(Game g, String r, int newX, int newY) {
        super(r, newX, newY); // calls the constructor in Entity
        game = g;
    } // constructor

    /*
     * move input: delta - time elapsed since last move (ms) purpose: move ship
     */
    public void move(long delta) {

        // stop moving at left side of screen
        if ((dx < 0) && (x < 30)) {
            dx = 0;
            x = 30;
        } // if

        // stop moving at right side of screen
        if ((dx > 0) && (x > 1790)) {
            dx = 0;
            x = 1790;
        } // if

        // stop at top of screen
        if ((dy < 0) && (y < 30)) {
            dy = 0;
            y = 30;
        } // if

        // stop at bottom of screen
        if ((dy > 0) && (y > 924)) {
            dy = 0;
            y = 924;
        } // if

        super.move(delta); // calls the move method in Entity
    } // move
    
    // Handle player invincibility after taking damage
    public void update(long d) {
        if (invincibilityTime > 0) {
            invincibilityTime -= d;
            flashTime -= d;
            if (flashTime <= 0) {
                if (isFlash) {
                    sprite = (SpriteStore.get()).getSprite("sprites/ship.png");
                } // if
                else {
                    sprite = (SpriteStore.get()).getSprite("sprites/shipHurt.png");
                } // else
                isFlash = !isFlash; // swap flash state
                flashTime = 100;
            } // if
        } // if
        else if (isFlash) {
            sprite = (SpriteStore.get()).getSprite("sprites/ship.png");
            isFlash = false;
        } // else if
    } // update

    /*
     * collidedWith input: other - the entity with which the ship has collided
     * purpose: notification that the player's ship has collided with something
     */
    public void collidedWith(Entity other) {
        if (lives <= 0) {
            return;
        } // if
        if (invincibilityTime <= 0 && (other instanceof EnemyEntity || other instanceof EnemyShotEntity)) {
            lives--;
            if (lives <= 0) {
                game.notifyDeath();
                return;
            } // if
            invincibilityTime = 2000;
            flashTime = 100;
            sprite = (SpriteStore.get()).getSprite("sprites/shipHurt.png");
            isFlash = true;
        } // if
    } // collidedWith
    
    public int getLives() {
        return lives;
    } // getLives
    
    public void addLives(int heal) {
        lives += heal;
    } // addLives
    
} // ShipEntity class