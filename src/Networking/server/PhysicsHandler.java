/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Networking.server;

import Game.GameMain;
import Game.Map;
import Game.PhysPlayer;
import Game.blocks.Block;
import Game.blocks.BlocksType;
import Networking.communication.KeyMessage;
import Networking.communication.Message;
import java.util.HashSet;
import java.util.Set;
import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.callbacks.ContactListener;
import org.jbox2d.collision.Manifold;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.*;
import org.jbox2d.dynamics.contacts.Contact;

/**
 *
 * @author Aidan A class that handles all of the physics of the game. This is
 * the game's physics engine.
 */
public class PhysicsHandler {

    /**
     * A blank constructor for the PhysicsHandler Class. This is the standard
     * constructor
     */
    public PhysicsHandler() {
        init();
    }
    //-------------------------------------------------------
    //---------------------PHYSICS OBJECTS-------------------
    //-------------------------------------------------------
    //  Note: 1 meter = 60 pixels
    private final World world = new World(new Vec2(0, -9.8f), false);
    /**
     *
     */
    public final static float PHYSICS_SCALE = 1 / 60f;
    /**
     * The conversion factor from pixels to meters
     */
    public Set<Block> blocks = new HashSet<Block>();
    CentralStorage gameSync;
    int bNum;

    /**
     * Steps the physics simulation by the specified timestep
     *
     * @param delta the timestep
     */
    public void update(int delta) {
        world.step(delta / 1000f, 16, 3);
    }

    /**
     * Initializes the basic objects needed for the functioning of the
     * PhysicsHandler object
     */
    public void init() {
        gameSync = new CentralStorage();

        FootListener l = new FootListener();
        world.setContactListener(l);
    }

    private FixtureDef makeFixture(float rx, float ry, float w, float h, float d, float b) {
        PolygonShape boxShape = new PolygonShape();
        boxShape.setAsBox(w, h, new Vec2(rx, ry), 0);
        FixtureDef fixture = new FixtureDef();
        fixture.density = d;
        fixture.friction = 0.2f;
        fixture.restitution = b;
        fixture.shape = boxShape;
        return fixture;
    }

    /**
     * Creates a player body with the specified properties, adds in to the
     * physics engine, and returns it to the calling statement.
     *
     * @param n The ID of the player being created, used to identify the player
     * object
     * @param p The position at which the player is being created
     * @param user The player's username, used to identify the player object
     * @return Returns the PhysPlayer object created to the calling statement
     */
    public PhysPlayer makePlayer(int n, float[] p, String user) {
        System.out.println("Created player " + n);
        Body playerb = makeEmptyBody(p[0], p[1], BodyType.DYNAMIC);
        PhysPlayer op = new PhysPlayer(playerb, n);
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(op.size.x / 2 * PHYSICS_SCALE, op.size.y / 2 * PHYSICS_SCALE);
        FixtureDef fixture = new FixtureDef();
        fixture.shape = shape;
        playerb.setFixedRotation(true);
        playerb.createFixture(fixture);
        gameSync.Players.add(op);

        FixtureDef foot = new FixtureDef();
        PolygonShape footShape = new PolygonShape();
        footShape.setAsBox(op.size.x / 4 * PHYSICS_SCALE, op.size.y / 4 * PHYSICS_SCALE, new Vec2(0, -op.size.y / 2 * PHYSICS_SCALE), 0);
        foot.shape = footShape;
        foot.isSensor = true;
        Fixture f = playerb.createFixture(foot);
        f.setUserData(user);
        op.footCount = 0;

        op.username = user;

        return op;
    }

    /**
     * Moves the player specified by the KeyMessage in the manner specified in
     * the KeyMessage.
     *
     * @param msg The KeyMessage object containing the data specifying how the
     * player should be moved
     */
    public void movePlayer(KeyMessage msg) {
        PhysPlayer p = getPlayerWithName(msg.user);
        if (p != null) {
            //System.out.println("Server: Moving player " + msg.ID + " " + msg.delta);
            switch (msg.key) {
                case 0:
                    p.keyLeft(PHYSICS_SCALE, msg.delta);
                    //System.out.println("Left" + p.body.getPosition());
                    break;
                case 1:
                    p.keyRight(PHYSICS_SCALE, msg.delta);
                    //System.out.println("Right" + p.body.getPosition());
                    break;
                case 2:
                    System.out.println(p.footCount + " " + p.jumping);
                    if (p.footCount != 0 && !p.jumping) {
                        p.keyJump();
                    } else if (System.currentTimeMillis() - p.lastJump > 250 && p.footCount != 0 && p.jumping) {
                        p.jumping = false;
                    }

                    //System.out.println("Jump" + p.body.getPosition());
                    break;
                default:
                //System.out.println("No direction");
            }
        } else {
            //System.out.println("Server: No player found with that ID");
        }
    }

    /**
     * Retrieves the player with the specified name from the server's storage.
     *
     * @param user The username of the player that is being fetched.
     * @return Returns the player found, or null if no match is found
     */
    public PhysPlayer getPlayerWithName(String user) {
        for (int i = 0; i < gameSync.Players.size(); i++) {
            if (gameSync.Players.get(i).username.equals(user)) {
                return gameSync.Players.get(i);
            }
        }
        return null;
    }

    /**
     * Creates and returns an array of blocks corresponding to the position of
     * blocks on the server. This method is called with the purpose of sending
     * the blocks to the client as an update.
     *
     * @return Returns the Block[] containing the block updates.
     */
    public Block[] getBlockUpdates() {
        Block[] up = new Block[blocks.size()];
        int i = 0;
        for (Block a : blocks) {
            Block z = new Block();
            Vec2 pos = a.body.getPosition();
            z.BT = a.BT;
            z.w = a.w;
            z.x = pos.x;
            z.y = pos.y;
            z.theta = a.body.getAngle();
            z.vx = a.body.getLinearVelocity().x;
            z.vy = a.body.getLinearVelocity().y;
            up[i] = z;
            i++;
        }
        return up;
    }

    /**
     * Adds the block received to the game's physics engine.
     *
     * @param block The block to add.
     */
    public void makeBlock(Block block) {
        System.out.print("Block made, #");
        BlocksType b1 = new BlocksType(block.BT, block.x, block.y, (int) block.w, 0f);
        FixtureDef blockDef = makeFixture(0, 0, 16 * PHYSICS_SCALE, 16 * PHYSICS_SCALE, b1.density, b1.bouncyness);
        Body x = makeEmptyBody(b1.xPosition, b1.yPosition, b1.t);
        x.createFixture(blockDef);
        block.body = x;
        block.bID = bNum;
        blocks.add(block);
        System.out.println(bNum);
        bNum++;
    }

    /**
     * Creates and returns an array of Message objects corresponding to the
     * position of players on the server. This method is called with the purpose
     * of sending the Messages to the client as an update.
     *
     * @return Returns the array of Message updates
     */
    public Message[] getPlayerUpdates() {
        Message[] updates = new Message[gameSync.Players.size()];
        for (int i = 0; i < gameSync.Players.size(); i++) {
            updates[i] = gameSync.Players.get(i).getMessageUpdate();
        }
        return updates;
    }

    private Body makeEmptyBody(float x, float y, BodyType bodytype) {
        BodyDef bodydef = new BodyDef();
        bodydef.position.set(x, y);
        bodydef.type = bodytype;
        Body body = world.createBody(bodydef);
        if (body == null) {
            System.out.println("ERROR: " + x + "," + y + "," + bodytype);
        }
        return body;
    }

    /**
     * Adds a map to the server's ArrayList of loaded maps. If the server does
     * not already have this map loaded, this method will load all the blocks
     * contained by the map into the game's physics engine.
     *
     * @param map The map to be added.
     */
    public void addMap(Map map) {
        for (Map test : gameSync.maps) {
            if (test.mapNum == map.mapNum) {
                return;
            }
        }

        for (Block block : map.walls) {

            Body template = makeEmptyBody(block.x, block.y, BodyType.STATIC);

            PolygonShape edgeShape = new PolygonShape();
            FixtureDef fixture = new FixtureDef();

            BlocksType bT = new BlocksType(block.BT, 0, 0, 0, 0);
            //Set the properties of the block
            fixture.friction = bT.friction;
            fixture.restitution = bT.bouncyness;
            fixture.density = bT.density;

            //Add top edge
            edgeShape.setAsEdge(new Vec2(-block.w / 2 * GameMain.PHYSICS_SCALE, block.h / 2 * GameMain.PHYSICS_SCALE + 0.01f), new Vec2(block.n * block.w, block.h / 2 * GameMain.PHYSICS_SCALE + 0.01f));
            fixture.shape = edgeShape;
            template.createFixture(fixture);

            //Add bottom edge
            edgeShape.setAsEdge(new Vec2(-block.w / 2 * GameMain.PHYSICS_SCALE, -block.h / 2 * GameMain.PHYSICS_SCALE), new Vec2(block.n * block.w, -block.h / 2 * GameMain.PHYSICS_SCALE));
            fixture.shape = edgeShape;
            template.createFixture(fixture);

            //Add left edge
            edgeShape.setAsEdge(new Vec2(-block.w / 2 * GameMain.PHYSICS_SCALE, block.h / 2 * GameMain.PHYSICS_SCALE), new Vec2(-block.w / 2 * GameMain.PHYSICS_SCALE, -block.h / 2 * GameMain.PHYSICS_SCALE));
            fixture.shape = edgeShape;
            template.createFixture(fixture);

            //Add right edge
            edgeShape.setAsEdge(new Vec2(-block.w / 2 * GameMain.PHYSICS_SCALE + block.n * block.w, block.h / 2 * GameMain.PHYSICS_SCALE), new Vec2(-block.w / 2 * GameMain.PHYSICS_SCALE + block.n * block.w, -block.h / 2 * GameMain.PHYSICS_SCALE));
            fixture.shape = edgeShape;
            template.createFixture(fixture);
        }

        gameSync.maps.add(map);
    }

    /**
     * Queues an object received by the input stream to be processed.
     *
     * @param o The object to be added to the queue.
     */
    public void enQueue(Object o) {
        gameSync.queue.add(o);
    }

    //-------------------------------------------------------
    //----------------FOOT CONTACT LISTENER------------------
    //-------------------------------------------------------
    //Checks when the foot sensor on the bottom of the physics player intersects with another body
    class FootListener implements ContactListener {

        @Override
        public void beginContact(Contact cntct) {

            //Tests to see if fixture A is the foot sensor
            Fixture test = cntct.m_fixtureA;
            if (test.getUserData() != null) {
                PhysPlayer pp = getPlayerWithName((String) test.getUserData());
                if (pp != null) {
                    pp.footCount++;
                    if (pp.body.getLinearVelocity().y <= 0) {
                        pp.jumping = false;
                        pp.runningJump_count = 0;
                    }
                }
            }

            //Tests to see if fixture B is the foot sensor
            test = cntct.m_fixtureB;
            if (test.getUserData() != null) {
                PhysPlayer pp = getPlayerWithName((String) test.getUserData());
                if (pp != null) {
                    pp.footCount++;
                    if (pp.body.getLinearVelocity().y <= 0) {
                        pp.jumping = false;
                        pp.runningJump_count = 0;
                    }
                }
            }
        }

        @Override
        public void endContact(Contact cntct) {
            Fixture test = cntct.m_fixtureA;
            if (test.getUserData() != null) {
                PhysPlayer pp = getPlayerWithName((String) test.getUserData());
                if (pp != null) {
                    pp.footCount--;
                }
            }

            //Tests to see if fixture B is the foot sensor
            test = cntct.m_fixtureB;
            if (test.getUserData() != null) {
                PhysPlayer pp = getPlayerWithName((String) test.getUserData());
                if (pp != null) {
                    pp.footCount--;
                }
            }
        }

        @Override
        public void preSolve(Contact cntct, Manifold mnfld) {
            //throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void postSolve(Contact cntct, ContactImpulse ci) {
            //throw new UnsupportedOperationException("Not supported yet.");
        }
    }
}
