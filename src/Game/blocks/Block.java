/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Game.blocks;

import java.io.Serializable;
import org.jbox2d.dynamics.Body;

/**
 * @author Aidan
 */
public class Block implements Serializable {

    /**
     * Integer representing block ID.
     */
    public int bID;
    /**
     * Integer representing block block type.
     */
    public int BT;
    /**
     * Body object that is used in the physics engine. It is transient and is
     * therefore not serialized and sent over the socket.
     */
    public transient Body body;
    /**
     * Integer representing number of consecutive blocks.
     */
    public int n;
    /**
     * Float representing the current x velocity of the block.
     */
    public float vx,
            /**
             * Float representing the current y velocity of the block.
             */
            vy;
    /**
     * Float representing the width of the block.
     */
    public float w,
            /**
             * Float representing the height of the block.
             */
            h,
            /**
             * Float representing the x position of the block.
             */
            x,
            /**
             * Float representing the y position of the block.
             */
            y;
    /**
     * Float representing the angle of rotation of the block.
     */
    public float theta;

    //Empty constructor used for boxes dropped by the player
    /**
     * Empty constructor used for boxes dropped by the player.
     */
    public Block() {
    }

    //This constructor is used because multiple fixtures may be put inside the same bod
    /**
     * This constructor is used because multiple fixtures may be put inside the
     * same body.
     *
     * @param x x position of the block.
     * @param y y position of the block.
     * @param w Width of the block.
     * @param h Height of the block.
     * @param num Number of consecutive blocks
     * @param blocktype Integer determining the type of the block
     */
    public Block(float x, float y, float w, float h, int num, int blocktype) {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        n = num;
        BT = blocktype;
    }
}