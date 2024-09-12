/* EnemyEntity.java
 * November 24, 2022
 * Represents one of the enemies
 */
public class EnemyEntity extends Entity {

    private double horizontalMoveSpeed = 250; // horizontal speed enemies move at
    private double verticalMoveSpeed = 250; // vertical speed enemies move at
    private boolean isNormal = false; // is this a normal enemy
    private boolean isVertical = false; // is this a vertical enemy
    private boolean isStill = false; // is this a still enemy
    private boolean isBoss = false; // is this a boss enemy
    private int spriteHeight; // height of enemy sprite
    private int spriteWidth; // width of enemy sprite
    private int timer = 0; // time until next enemy action
    private int flashTime = 0; // ms until reset to regular sprite after damaged
    private int health; // number of shots to kill enemy
    private int shots; // number of shots to shoot (for boss)
    private static int speedUp = 0; // makes all enemies stronger in late stage boss fight
    private static String[] explosion = {"sprites/explosion0.png", "sprites/explosion1.png",
            "sprites/explosion2.png", "sprites/explosion3.png", "sprites/explosion4.png",
            "sprites/explosion5.png", "sprites/explosion6.png"}; // sprites for death explosion animation

    private Game game; // the game in which the enemy exists

    /*
     * construct a new alien input: game - the game in which the alien is being
     * created r - the image representing the alien x, y - initial location of alien
     */
    public EnemyEntity(Game g, String r, String type, int newX, int newY) {
        super(r, newX, newY); // calls the constructor in Entity
        game = g;
        if (type.equals("Normal")) {
            dx = -horizontalMoveSpeed - (speedUp / 20); // start off moving left
            isNormal = true;
            health = 10;
        } // if

        else if (type.equals("Vertical")) {
            dy = -verticalMoveSpeed;
            isVertical = true;
            timer = 1200 - (speedUp / 10);
            health = 5;
        } // else if

        else if (type.equals("Still")) {
            dx = -horizontalMoveSpeed;
            isStill = true;
            timer = 2500;
            health = 12;
        } // else if
        
        else if (type.equals("Boss")) {
            dx = -100;
            isBoss = true;
            timer = 9999;
            health = 500;
        } // else if
        
        spriteWidth = sprite.getWidth();
        spriteHeight = sprite.getHeight();
        
    } // constructor

    /*
     * move input: delta - time elapsed since last move (ms) purpose: move alien
     */
    public void move(long delta) {

        if (isVertical) {
            
            // turn around at top of screen
            if ((dy < 0) && (y < 0)) {
                dy *= -1;
            } // if

            // turn around at bottom of screen
            else if ((dy > 0) && (y + spriteHeight > 1030)) {
                dy *= -1;
            } // if
        } // if

        else if (isStill) {
            
            // stop at a certain point on the screen
            if ((dx < 0) && (x < 1120)) {
                dx = 0;
            } // if
        } // else if
        
        else if (isBoss) {
            
            // stop at certain points on the screen
            if ((dx < 0) && (x < 1020)) {
                dx = 0;
            } // if
            else if ((dx > 0) && (x > 1420)) {
                dx = 0;
            } // if
        } // else if

        // proceed with normal move
        super.move(delta);
        
        // if enemy moves off left side of screen, kill it
        if (x + spriteWidth < 0) {
            game.notifyEnemyKilled();
            game.removeEntity(this);
        } // if
        
    } // move
    
    // Updates the timers for non normal type enemies and does
    // time dependent functionality ex: shooting back and damaged flashes
    public void update(long d) {
        
        // Flash when damaged
        if (flashTime > 0) {
            flashTime -= d;
            if (flashTime <= 0) {
                if (isNormal) {
                    sprite = (SpriteStore.get()).getSprite("sprites/normalEnemy.png");
                } // if
                else if (isVertical) {
                    sprite = (SpriteStore.get()).getSprite("sprites/verticalEnemy.png");
                } // else if
                else if (isStill) {
                    sprite = (SpriteStore.get()).getSprite("sprites/stillEnemy.png");
                } // else if
                else if (isBoss) {
                    sprite = (SpriteStore.get()).getSprite("sprites/bossEnemy.png");
                } // else if
            } // if
        } // if
        
        // Stop if has no timed action
        if (isNormal) {
            return;
        } // if
        
        timer -= d;
        
        // Do timed action
        if (timer <= 0) {
            if (isVertical) {
                shoot(false, (int)(x + 52), (int)(y + (spriteHeight / 2) + (dy / 1000)));
                timer = 1200 - (speedUp / 10);
            } // if
            else if (isStill) {
                shoot(true, (int)x, (int)(y + (spriteHeight / 2) - 20));
                timer = 2500 - (speedUp / 20);
            } // else if
            else if (isBoss) {
                
                // Death animation
                if (health <= 0) {
                    createExplosion((int)(x + (Math.random() * spriteWidth)) - 105,
                            (int)(y + (Math.random() * spriteHeight)) - 105);
                    timer = 10;
                    health--;
                    if (flashTime <= 0) {
                        sprite = (SpriteStore.get()).getSprite("sprites/bossEnemyHurt.png");
                        flashTime = 99999;
                    } // if
                    if (health < -100) {
                        game.setWave(10);
                        die(); // game actually goes to wave 11 after boss death because of this
                    } // if
                    return;
                } // if
                
                speedUp = Math.abs(health - 500) * 10; // time between moves reduced by 10 ms per hp lost
                if (shots <= 0) {
                    if (x > 1420) {
                        dx = -100;
                        timer = 6000;
                        return;
                    } // if
                    
                    if ((int)(Math.random() * 3) < 2) {
                        
                        // Use a shooting attack
                        if ((int)(Math.random() * 2) == 0) {
                            
                            // Shoot 4 homing projectiles backwards
                            shoot(true, (int)(x + (spriteWidth / 2) + 50), (int)(y + 150), 300, -450);
                            shoot(true, (int)(x + (spriteWidth / 2) + 100), (int)(y + 200), 200, -350);
                            shoot(true, (int)(x + (spriteWidth / 2) + 50), (int)(y + spriteHeight - 150),
                                    300, 450);
                            shoot(true, (int)(x + (spriteWidth / 2) + 100), (int)(y + spriteHeight - 100),
                                    200, 350);
                            timer = 5000 - speedUp;
                            
                        } // if
                        else {
                            
                            // Start shooting 8 shots forwards in quick succession
                            shoot(false, (int)(x + 195), (int)(y + 245));
                            shoot(false, (int)(x + 195), (int)(y + 310));
                            shots = 3;
                            timer = 500;
                            
                        } // else
                        
                    } // if
                    else {
                        
                        // Spawn enemies and move backwards
                        int randWave = (int)(Math.random() * 3); // random wave to summon
                        game.setWave(randWave + 6);
                        dx = 150;
                        timer = (Math.abs(randWave - 3) * 1000) + 12000 - speedUp; // timer is longer if 
                                                                                   // randwave is lower
                        
                    } // else
                } // if
                else {
                    int offset = Math.abs(shots - 4);
                    shoot(false, (int)(x + 195 + (offset * 20)), (int)(y + 245 - (offset * 30)));
                    shoot(false, (int)(x + 195 + (offset * 20)), (int)(y + 310 + (offset * 30)));
                    shots--;
                    timer = (shots <= 0) ? (3000 - speedUp) : 500;
                } // else
                
            } // else if
        } // if
        
    } // update

    /*
     * collidedWith input: other - the entity with which the alien has collided
     * purpose: notification that the alien has collided with something
     */
    public void collidedWith(Entity other) {
        if (other instanceof ShotEntity) {
            health--;
            
            flashTime = 40;
            if (isNormal) {
                sprite = (SpriteStore.get()).getSprite("sprites/normalEnemyHurt.png");
            } // if
            else if (isVertical) {
                sprite = (SpriteStore.get()).getSprite("sprites/verticalEnemyHurt.png");
            } // else if
            else if (isStill) {
                sprite = (SpriteStore.get()).getSprite("sprites/stillEnemyHurt.png");
            } // else if
            else if (isBoss) {
                sprite = (SpriteStore.get()).getSprite("sprites/bossEnemyHurt.png");
            } // else if
            
            if (health == 0 && !isBoss) {
                die();
            } // if
        } // if
    } // collidedWith
    
    // Performs on-death tasks of enemies
    private void die() {
        
        // Animate death explosion
        createExplosion((int)(x + spriteWidth / 2 - 52), (int)(y + spriteHeight / 2 - 52));
        
        // Chance to create powerup on death
        int random = (int)(Math.random() * 10); // random number from 0 to 9
        if (random < 3) {
            PowerUpEntity p = null;
            switch(random) {
                case 0:
                    p = new PowerUpEntity(game, "sprites/atkPowerUp.png",
                            (int)(x + (spriteWidth / 2) - 30),
                            (int)(y + (spriteHeight / 2) - 30), "Attack");
                    break;
                case 1:
                    p = new PowerUpEntity(game, "sprites/hpPowerUp.png",
                            (int)(x + (spriteWidth / 2) - 30),
                            (int)(y + (spriteHeight / 2) - 30), "Heal");
                    break;
                case 2:
                    p = new PowerUpEntity(game, "sprites/speedPowerUp.png",
                            (int)(x + (spriteWidth / 2) - 30),
                            (int)(y + (spriteHeight / 2) - 30), "Speed");
            } // switch
            game.addEntity(p);
        } // if
        
        // Kill enemy
        game.removeEntity(this);
        game.notifyEnemyKilled();
    } // die
    
    // Animates a death explosion at the given location
    private void createExplosion(int x, int y) {
        EffectsEntity e = new EffectsEntity(game, explosion, x, y, true);
        game.addEntity(e);
    } // createExplosion
    
    // Fires a shot at the given position
    private void shoot(boolean isHoming, int x, int y) {
        Entity e = null;
        if (isHoming) {
            e = new HomingEnemyShot(game, "sprites/homingShot.png", x, y);
        } // if
        else {
            e = new EnemyShotEntity(game, "sprites/enemyShot.png", x, y);
        } // else
        game.addEntity(e);
    } // shoot
    
    // Fires a shot with custom speed
    private void shoot(boolean isHoming, int x, int y, int dx, int dy) {
        Entity e = null;
        if (isHoming) {
            e = new HomingEnemyShot(game, "sprites/homingShot.png", x, y, dx, dy);
        } // if
        else {
            e = new EnemyShotEntity(game, "sprites/enemyShot.png", x, y, dx, dy);
        } // else
        game.addEntity(e);
    } // shoot

} // EnemyEntity class