/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Game;

import Networking.communication.Message;
import glmodel.GLModel;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;

/**
 *
 * @author ubuntu
 */
public class PhysPlayer extends Networking.server.NetObject {

    //The rectangular area that encompasses the player's sprite
    /**
     * A Rectangular body that surrounds the player sprite.
     */
    public Body body;
    
    /**
     * A String that contains the username used to identify the player
     */
    public String username;
    /**
     * A boolean value where true means that the player is jumping and 
     * where false means the player is not.
     */
    public boolean jumping = false;
    //player model
    GLModel model = new GLModel("res/models/idle/idle1.obj");
    //Player animations
    GLModel[] run = new GLModel[23];
    GLModel[] idle = new GLModel[26];
    GLModel[] backflip = new GLModel[21];
    GLModel[] jump = new GLModel[25];
    GLModel[] running_jump = new GLModel[12];
    //changes how fast animations are played
    float run_anim_speed = 0.2f;
    float idle_anim_speed = 0.008f;
    float jump_anim_speed = 1f;
    float runningJump_anim_speed = 0.4f;
    float base_jump_speed = .2f;
    //changes when animations are played
    int idleAnimBegin = 3000;
    //check if the player is doign a running jump or a normal jump
    boolean runjump = false;
    //boolean for if player is moving to the right
    boolean moveright = true;
    boolean face_left = false;
    //check for change in player orientation
    boolean player_orient = false;
    //The player's maximum X velocity
    static float MAXSPEED = 3.5f;
    //counters for animations
    double run_count = 0;
    double idle_count = 0;
    double jump_count = 6;
    /**
     * Represents the frames of the running jump animation. 
     */
    public double runningJump_count = 0;
    //timers for animations
    double idle_timer = 0;
    //Size of a player object
    /**
     * Vector representing the width and height of the player object. 
     */
    public final Vec2 size = new Vec2(32, 70);
    /**
     * Number of blocks touching the player's foot sensor. 
     */
    public int footCount;
    Vec2 HiddenVelocity;
    
    public long lastJump = 0;

    /**
     * Constructor that stores all information pertaining to the player 
     * object.
     * @param b hitbox around the player.
     * @param n player number.
     */
    public PhysPlayer(Body b, int n) {
        ID = (short) n;
        body = b;
        HiddenVelocity = new Vec2(0, 0);
    }

    /**
     * Loads all the models. 
     */
    public void loadAllModels() {
        loadAnims(run, "run");
        loadAnims(idle, "idle");
        loadAnims(jump, "jump");
        loadAnims(running_jump, "run_jump");
    }

    /**
     * Loads all animation files. 
     * @param m array that must be loaded.
     * @param path the path name of the required file.
     */
    public void loadAnims(GLModel[] m, String path) {
        for (int i = 0; i < m.length; i++) {
            m[i] = new GLModel("res/models/" + path + "/" + path + (i + 1) + ".obj");
        }
    }

    /**
     * If the left key is pressed apply a force based on the two 
     * parameters in that direction.
     * @param scale the physics scale.
     * @param delta time since last update.
     */
    public void keyLeft(float scale, int delta) {
        if (body.getLinearVelocity().x > -MAXSPEED) {
            body.applyForce(new Vec2(-60 * scale * delta, 0), body.getPosition());
        }
        idle_timer = 0;
    }
    
/**
     * If the right key is pressed apply a force based on the two 
     * parameters in that direction.
     * @param scale the physics scale.
     * @param delta time since last update.
     */
    public void keyRight(float scale, int delta) {
        if (body.getLinearVelocity().x < MAXSPEED) {
            body.applyForce(new Vec2(60 * scale * delta, 0), body.getPosition());
        }
        idle_timer = 0;
    }

    /**
     * If the jump key is pressed apply a force based on the two 
     * parameters upwards.
     */
    public void keyJump() {
        body.applyLinearImpulse(new Vec2(0, 7f), body.getPosition());
        jumping = true;
        lastJump = System.currentTimeMillis();
    }

    /**
     * Applies upwards force and advances the jumping animation.
     * @param delta time since last update.
     */
    public void jump(int delta) {
        if (runjump) {
            if (body.getLinearVelocity().x != 0) {
                runningJump_count += Math.abs((body.getLinearVelocity().x * delta / 1000f / runningJump_anim_speed));

                if (runningJump_count > (running_jump.length - 1)) {
                    runningJump_count = 0;
                    runjump = false;
                    jumping = false;
                }
                model = running_jump[(int) runningJump_count];
            } else {
                runjump = false;
                jumping = false;
                runningJump_count = 0;
            }
        } else {
            jump_count += base_jump_speed + Math.abs((body.getLinearVelocity().x * delta / 1000f / jump_anim_speed));
            if (jump_count > (jump.length - 1)) {
                jump_count = 6;
                jumping = false;
            }
            model = jump[(int) jump_count];
        }
    }

    /**
     * Advances the player's animation based on its velocity. 
     * @param delta time since last update.
     */
    public void update(int delta) {
        if (!jumping && Math.abs(body.getLinearVelocity().x) > 0.01) {
            //increases frame of animation based on velocity
            run_count += Math.abs(body.getLinearVelocity().x * delta / 1000f / run_anim_speed);
            //updates player model
            updateRunAnim();
        } else if (jumping) {
            jump(delta);
        } else {
            idle(delta);
        }
        checkDir();
    }

    /**
     * Checks which way the player should be facing.
     * If the player's velocity is less than 0 they will be facing left
     * If the player's velocity is greater than 0 they will be facing right
     */
    public void checkDir() {
        if ((body.getLinearVelocity().x < -0.01)) {
            face_left = true;
        } else if ((body.getLinearVelocity().x > 0.01)) {
            face_left = false;
        }
    }

    /**
     * Displays the next frame of the running animation.
     */
    public void updateRunAnim() {
        //if animation has reached its end, reset it
        if (run_count > (run.length - 1)) {
            run_count = 0;
        }
        if (!jumping) {
            model = run[(int) run_count];
        }

    }

    /**
     * Begins showing the idle animation if the player is stationary 
     * for a predetermined length of time.
     * @param delta time since last update.
     */
    public void idle(int delta) {
        idle_timer += delta;
        if (idle_timer > idleAnimBegin) {
            //increases frame of animation based on velocity
            idle_count += delta * idle_anim_speed;
            //if the animation is at an end, reset it to the beginning. Also reset idle timer
            if (idle_count > (idle.length - 1)) {
                idle_count = 0;
                idle_timer = 0;

            }
        }
        //sets the character model to current frame of animation
        model = idle[(int) idle_count];
    }

    /**
     * Creates an object that stores all the data required by the 
     * client to update the players position.
     * @return message pmsg
     */
    public Message getMessageUpdate() {
        Message pmsg = new Message();
        pmsg.user = username;
        pmsg.type = 0;
        pmsg.ID = ID;
        pmsg.p = new float[2];
        pmsg.p[0] = body.getPosition().x;
        pmsg.p[1] = body.getPosition().y;
        pmsg.v = new float[2];
        pmsg.v[0] = body.getLinearVelocity().x;
        pmsg.v[1] = body.getLinearVelocity().y;
        return pmsg;
    }

    /**
     * Get the player's x position.
     * @return x position of the body
     */
    public float getX() {
        return body.getPosition().x;
    }

     /**
     * Get the player's y position.
     * @return y position of the body
     */
    public float getY() {
        return body.getPosition().y;
    }

    /**
     * Draws the player model.
     */
    public void render() {
        model.render();
    }
}
/*
 * Here's the code for the second type of player, but it's all commented because
 * we don't really have a place for it yet.
 *         if (Keyboard.isKeyDown(Keyboard.KEY_SPACE) && (System.currentTimeMillis() - lastBox) > BOXINTERVAL) {
            if (bodies.size() < 100) {
                BlocksType b1 = new BlocksType(1, (cam.cameraPos[0] / DRAWSCALE + (Mouse.getX() - displayWidth / 2)) * PHYSICS_SCALE, (cam.cameraPos[1] / DRAWSCALE + (Mouse.getY() - 5 * displayHeight / 6)) * PHYSICS_SCALE, (int) pp.size.x, 0f);
                FixtureDef blockDef = makeFixture(0, 0, 16 * PHYSICS_SCALE, 16 * PHYSICS_SCALE, b1.density, b1.bouncyness);
                Body x = makeEmptyBody(b1.xPosition, b1.yPosition, b1.t);
                x.createFixture(blockDef);
                bodies.add(x);
                lastBox = System.currentTimeMillis();
            }
        }
 *  if (Keyboard.isKeyDown(Keyboard.KEY_A) && (System.currentTimeMillis() - lastBox) > BOXINTERVAL) {
            if (bodies.size() < 100) {
                BlocksType b1 = new BlocksType(1, (cam.cameraPos[0] / DRAWSCALE + (Mouse.getX() - displayWidth / 2)) * PHYSICS_SCALE, (cam.cameraPos[1] / DRAWSCALE + (Mouse.getY() - 5 * displayHeight / 6)) * PHYSICS_SCALE, (int) pp.size.x, 0f);
                FixtureDef blockDef = makeFixture(0, 0, 16 * PHYSICS_SCALE, 16 * PHYSICS_SCALE, b1.density, b1.bouncyness);
                Body x = makeEmptyBody(b1.xPosition, b1.yPosition, b1.t);
                x.createFixture(blockDef);
                bodies.add(x);
                lastBox = System.currentTimeMillis();
            }
        }
 *  if (Keyboard.isKeyDown(Keyboard.KEY_S) && (System.currentTimeMillis() - lastBox) > BOXINTERVAL) {
            if (bodies.size() < 100) {
                BlocksType b1 = new BlocksType(4, (cam.cameraPos[0] / DRAWSCALE + (Mouse.getX() - displayWidth / 2)) * PHYSICS_SCALE, (cam.cameraPos[1] / DRAWSCALE + (Mouse.getY() - 5 * displayHeight / 6)) * PHYSICS_SCALE, (int) pp.size.x, 0f);
                FixtureDef blockDef = makeFixture(0, 0, 16 * PHYSICS_SCALE, 16 * PHYSICS_SCALE, b1.density, b1.bouncyness);
                Body x = makeEmptyBody(b1.xPosition, b1.yPosition, b1.t);
                x.createFixture(blockDef);
                bodies.add(x);
                lastBox = System.currentTimeMillis();
            }
        }
 *  if (Keyboard.isKeyDown(Keyboard.KEY_D) && (System.currentTimeMillis() - lastBox) > BOXINTERVAL) {
            if (bodies.size() < 100) {
                BlocksType b1 = new BlocksType(6, (cam.cameraPos[0] / DRAWSCALE + (Mouse.getX() - displayWidth / 2)) * PHYSICS_SCALE, (cam.cameraPos[1] / DRAWSCALE + (Mouse.getY() - 5 * displayHeight / 6)) * PHYSICS_SCALE, (int) pp.size.x, 0f);
                FixtureDef blockDef = makeFixture(0, 0, 16 * PHYSICS_SCALE, 16 * PHYSICS_SCALE, b1.density, b1.bouncyness);
                Body x = makeEmptyBody(b1.xPosition, b1.yPosition, b1.t);
                x.createFixture(blockDef);
                bodies.add(x);
                lastBox = System.currentTimeMillis();
            }
        }
 */