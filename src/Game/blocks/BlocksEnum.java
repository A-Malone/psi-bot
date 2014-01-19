/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Game.blocks;

import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.Body;
import static org.jbox2d.dynamics.BodyType.*;

/**
 * The BlocksEnum class contains all properties of each block.
 * All parameters are in put in the following order:
 * Density, friction, bouciness
 * @author zeev
 */
public enum BlocksEnum {
    //blockType = 0, an immovable, standard wall block.
    /**
     * Standard wall block.
     */
    WALL(STATIC, 1.0f, 0.5f, 0f),
    
    //blockType = 1, a movable standard crate.
    /**
     * A movable standard crate.
     */
    STANDARD(DYNAMIC, 1.0f, 0.5f, 0f),
    
    //blockType = 2 an Immovable piece of ice. Slippery and low density.
    /**
     * An Immovable piece of ice. Slippery and low density.
     */
    ICE(STATIC, 0.5f, 0f, 0f),
    
    //blockType = 3. An immovable bouncy tile. 
    /**
     * An immovable bouncy tile. 
     */
    TRAMPOLINE(STATIC, 1.0f, 0.5f, 1f),
    
    //blockType = 4. A mobile ice block
    /**
     * A mobile ice block.
     */
    MOVEICE(DYNAMIC, 0.5f, 0f, 0f),
    
    //blockType = 5. A mobile bouncy block
    /**
     * A mobile bouncy block. 
     */
    MOVETRAMP(DYNAMIC, 3f, 0.8f, 0.8f),
    
    //blockType = 6. A very dense block
    /**
     * A very dense block.
     */
    HEAVY(DYNAMIC, 10.0f, 0.8f, 0f),
    
    //blockType = 7. Bouncy, slidey and dense. 
    /**
     * Bouncy, slidey and dense. 
     */
    BLUESHELL (DYNAMIC, 10.0f, 0.1f, 0.8f);
    
    
    private Body body;
    //A normal wooden block has a density of 1f.
    private float density;
    //This should be between 0 and 1, with 0 being no friction.
    private float friction;
    //A bouncyness of 0 means no bouncing, a bouncyness of 1 means things bounce back up to the height they fell from. 
    private float bouncyness;
    //This should be either static or dynamic. 
    private BodyType t;
    private int blockType;

    BlocksEnum(BodyType type, float d, float f, float b) {
        t = type;
        density = d;
        friction = f;
        bouncyness = b;
    }
/**
 * Returns the type of block selected. 
 * @return Integer blockType
 */
   
    public int getBlockType() {
        return blockType;
    }

    /**
     * Returns the type of the selected body.
     * @return Body body
     */
    public Body getBody() {
        return body;
    }

    /**
     * Returns the bouncyness of the selected body.
     * @return float bouncyness
     */
    public float getBouncyness() {
        return bouncyness;
    }

    /**
     * Returns the density of the selected body.
     * @return float density 
     */
    public float getDensity() {
        return density;
    }

    /**
     * Returns the friction of the selected body.
     * @return float friction
     */
    public float getFriction() {
        return friction;
    }
/**
 * Returns the type of the selected body.
 * @return 
 */
    public BodyType getT() {
        return t;
    }
}
