/* EnemyShotEntity.java
 * November 24, 2022
 * Represents enemy's shots
 */
public class EnemyShotEntity extends Entity {

    private double moveSpeed = -450; // speed shot moves
    private boolean used = false; // true if shot hits something
    protected Game game; // the game in which the ship exists

    /*
     * construct the shot input: game - the game in which the shot is being created
     * ref - a string with the name of the image associated to the sprite for the
     * shot x, y - initial location of shot
     */
    public EnemyShotEntity(Game g, String r, int newX, int newY) {
        super(r, newX, newY); // calls the constructor in Entity
        game = g;
        dx = moveSpeed;
    } // constructor
    
    // Constructs a shot with unique speed
    public EnemyShotEntity(Game g, String r, int newX, int newY, int newDX, int newDY) {
        super(r, newX, newY); // calls the constructor in Entity
        game = g;
        dx = newDX;
        dy = newDY;
    } // constructor

    /*
     * move input: delta - time elapsed since last move (ms) purpose: move shot
     */
    public void move(long delta) {
        super.move(delta); // calls the move method in Entity

        // if shot moves off left side of screen, remove it from entity list
        if (x < -40) {
            game.removeEntity(this);
        } // if

    } // move

    /*
     * collidedWith input: other - the entity with which the shot has collided
     * purpose: notification that the shot has collided with something
     */
    public void collidedWith(Entity other) {

        // prevents double kills
        if (used) {
            return;
        } // if

        // if shot has hit the ship, remove shot
        if (other instanceof ShipEntity) {
            // remove affect entities from the Entity list
            game.removeEntity(this);
            used = true;
        } // if

    } // collidedWith

} // EnemyShotEntity class