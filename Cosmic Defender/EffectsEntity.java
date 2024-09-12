/* EffectsEntity.java
 * November 24, 2022
 * Represents purely visual entities and animations
 */

public class EffectsEntity extends Entity {
    
    private Sprite[] frames; // all sprites of entity
    private int currentFrame; // current frame being drawn
    private boolean isAnimated; // if the sprite should be animated
    private Game game; // game the effect is in
    
    public EffectsEntity(Game g, String[] r, int newX, int newY, boolean animated) {
        super(r[0], newX, newY);
        game = g;
        currentFrame = 0;
        
        // Add sprites to array
        frames = new Sprite[r.length];
        for (int i = 0; i < r.length; i++) {
            frames[i] = (SpriteStore.get()).getSprite(r[i]);
        } // for
        
        isAnimated = animated;
        
    } // EffectsEntity constructor
    
    // Sets entity sprite to a frame
    public void setFrame(int f) {
        sprite = frames[f];
    } // setFrame
    
    public int getFrameNum() {
        return currentFrame;
    } // getFrameNum
    
    // Animate effects that request it
    public void update(long d) {
        if (isAnimated) {
            currentFrame++;
            if (currentFrame >= frames.length) {
                game.removeEntity(this);
                return;
            } // if
            setFrame(currentFrame);
        } // if
    } // update
    
    public void collidedWith(Entity other) { 
    } // collidedWith
    
    // Effects never collide with other entities
    public boolean collidesWith(Entity other) {
        return false;
    } // collidesWith
    
} // EffectsEntity class
