/* PowerUpEntity.java
 * November 24, 2022
 * Represents collectible powerups that help the player
 */

public class PowerUpEntity extends Entity {

    private String type; // type of power up
    private int spriteHeight; // height of enemy sprite
    private int spriteWidth; // width of enemy sprite

    private Game game;

    public PowerUpEntity(Game g, String r, int newX, int newY, String type) {
        super(r, newX, newY); // calls the constructor in Entity     
        game = g;
        spriteWidth = sprite.getWidth();
        spriteHeight = sprite.getHeight();
        dx = -200;
        this.type = type;
    } // PowerupEntity Constructor
    
    /*
     * move input: delta - time elapsed since last move (ms) purpose: move shot
     */
    public void move(long delta) {
        super.move(delta); // calls the move method in Entity

        // if powerup moves off left side of screen, remove it from entity list
        if (x < -60) {
            game.removeEntity(this);
        } // if

    } // move
    
    public void collidedWith(Entity other) {
        
        // Activate boost and dissapear if collected by ship
        if (other instanceof ShipEntity) {
            if (type.equals("Attack")) {
                game.setFiringInterval(67);
            } // if

            else if (type.equals("Heal")) {
                game.addHealth(1);
            } // else if

            else if (type.equals("Speed")) {
                game.setShipSpeed(1000);
            } // else if

            game.removeEntity(this);
        } // if

    } // collidedWith

} // PowerUpEntity