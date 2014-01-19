/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Game.blocks;

import Game.GameMain;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.*;

/**
 * The purpose of this class is to be used as a superclass that stores templates,
 * similar to WeaponsEnum in netspace.
 *
 * @author Zeev
 */
public class BlocksType {

    /**
     * This variable will store the x coordinate of the block to be made.
     */
    public float xPosition;
    /**
     * This variable will store the y cooridnate of the block to be made.
     */
    public float yPosition;
    /**
     * This variable will store the size (height and width) of a tile.
     */
    public int size;
    /**
     * This variable will store the width of the block to be made.
     */
    public float w;
    /**
     * This variable will store the height of the block to be made.
     */
    public float h;
    /**
     * This variable will store the block's density.
     *A normal wooden block has a density of 1f.
     */
    public float density;
    /**
     * This variable will store the block's friction.
     * This value should be between 0 and 1. 
     */
    public float friction;
    /**
     * This variable will store the block's bouncyness/restitution.
     * A value of 0 means that things do not bounce.
     * A value of 1 means that things bounce back to the height they fell from.
     */
    public float bouncyness;
    /**
     * This variable will store a fixture's x rotation.
     */
    public float rx;
    /**
     * This variable will store the block's BodyType.
     * It will be either Static or Dynamic.
     * Static blocks are unaffected by gravity.
     */
    public BodyType t;

    /**
     * The integer e determines the type of block to be made.
     * If e is zero, a static wall block will be made.
     *  If e is 1, a dynamic wall block will be made.
     * If e is 2, a static ice block will be made.
     * If e is 3, a static trampoline block will be made.
     * If e is 4, a dynamic ice block will be made.
     * If e is 5, a dynamic trampoline block will be made.
     * If e is 6, a dynamic heavy block will be made.
     */
    public BlocksType(int e, float xPos, float yPos, int s, float rx) {
        xPosition = xPos;
        yPosition = yPos;
        size = s;
        w = size * GameMain.PHYSICS_SCALE;
        h = size * GameMain.PHYSICS_SCALE;
        this.rx = rx;
        BlocksEnum data;

        //This switch statement determines which enum is used based on the integer e, which will be passed with the method. 
        switch (e) {
            case 0:
                data = BlocksEnum.WALL;
                break;
            case 1:
                data = BlocksEnum.STANDARD;
                break;
            case 2:
                data = BlocksEnum.ICE;
                break;
            case 3:
                data = BlocksEnum.TRAMPOLINE;
                break;
            case 4:
                data = BlocksEnum.MOVEICE;
                break;
            case 5:
                data = BlocksEnum.MOVETRAMP;
                break;
            case 6:
                data = BlocksEnum.HEAVY;
                break;
            case 7:
                data = BlocksEnum.BLUESHELL;
                break;

            default:
                data = BlocksEnum.STANDARD;
                break;
        }

        density = data.getDensity();
        friction = data.getFriction();
        bouncyness = data.getBouncyness();
        t = data.getT();

        // body = makeBody(xPosition, yPosition, w, h, density, friction, bouncyness, t, gameWorld);

        //fix = makeFixture(rx, 0, w/2, h/2, density, friction, bouncyness);
        //body = makeBody (xPosition, yPosition, w/2, h/2, density, friction, bouncyness, t, gameWorld);
    }

//These variables would make a standard block that's part of the wall 
    //x = xAxis * SIZE * PHYSICS_SCALE
    //y =  (map.getHeight() - yAxis * SIZE) * PHYSICS_SCALE
    //w = SIZE / 2 * PHYSICS_SCALE
    //h =  SIZE / 2 * PHYSICS_SCALE
    //d = 1.0f
    //f = 0.5f
    //b = 0f    
    //bodytype = BodyType.STATIC
    //This method makes and returns the body with the given parameters. 
    private Body makeBody(float x, float y, float w, float h, float d, float f, float b, BodyType bodytype, World world) {
        BodyDef bodydef = new BodyDef();
        bodydef.position.set(x, y);
        bodydef.type = bodytype;
        PolygonShape boxShape = new PolygonShape();
        boxShape.setAsBox(w, h);
        Body body = world.createBody(bodydef);
        FixtureDef fixture = new FixtureDef();
        fixture.density = d;
        fixture.friction = f;
        //fixture.restitution is the bounciness. 
        fixture.restitution = b;
        fixture.shape = boxShape;
        body.createFixture(fixture);
        return body;
    }

    private FixtureDef makeFixture(float rx, float ry, float w, float h, float d, float f, float b) {
        PolygonShape boxShape = new PolygonShape();
        boxShape.setAsBox(w, h, new Vec2(rx, ry), 0);
        FixtureDef fixture = new FixtureDef();
        fixture.density = d;
        fixture.friction = f;
        fixture.restitution = b;
        fixture.shape = boxShape;
        return fixture;
    }
}
