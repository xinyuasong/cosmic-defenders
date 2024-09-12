/* ShotEntity.java
 * November 24 2022
 * Represents player's shots
 */
public class ShotEntity extends Entity {

    private double moveSpeed = 1200; // speed shot moves
    private boolean used = false; // true if shot hits something
    private Game game; // the game in which the shot exists

    /*
     * construct the shot input: game - the game in which the shot is being created
     * ref - a string with the name of the image associated to the sprite for the
     * shot x, y - initial location of shot
     */
    public ShotEntity(Game g, String r, int newX, int newY) {
        super(r, newX, newY); // calls the constructor in Entity
        game = g;
        dx = moveSpeed;
    } // constructor

    /*
     * move input: delta - time elapsed since last move (ms) purpose: move shot
     */
    public void move(long delta) {
        super.move(delta); // calls the move method in Entity

        // if shot moves off right side of screen, remove it from entity list
        if (x > 1920) {
            game.removeEntity(this);
        } // if

    } // move

    /*
     * collidedWith input: other - the entity with which the shot has collided
     * purpose: notification that the shot has collided with something
     */
    public void collidedWith(Entity other) {

        // prevents double hits
        if (used) {
            return;
        } // if

        // if shot has hit an enemy, remove shot
        if (other instanceof EnemyEntity) {
            // remove shot from the Entity list
            game.removeEntity(this);
            used = true;
        } // if

    } // collidedWith

} // ShipEntity class