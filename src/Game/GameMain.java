/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Game;

import Game.blocks.Block;
import Networking.communication.KeyMessage;
import Networking.communication.Message;
import Networking.server.NetObject;
import glapp.GLApp;
import glapp.GLImage;
import glmodel.GLModel;
import java.io.*;
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.callbacks.ContactListener;
import org.jbox2d.collision.Manifold;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.*;
import org.jbox2d.dynamics.contacts.Contact;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.tiled.TiledMap;

/**
 *
 * @author Aidan
 *
 */
public class GameMain extends GLApp {

    /**
     * Float representing the width of the window.
     */
    public final float WIDTH = 800,
            /**
             * Float representing the height of the window.
             */
            HEIGHT = 600;

    //Default empty constructor
    /**
     * Default empty constructor.
     */
    public GameMain() {
    }

    //Networked constructor
    /**
     * Networked constructor
     *
     * @param Sinput input stream
     * @param Soutput output stream
     * @param p2 boolean representing the presence of the second player.
     */
    public GameMain(ObjectInputStream Sinput, ObjectOutputStream Soutput, String user) {
        IO = new ClientHandler(Sinput, Soutput);
        username = user;
    }

    //Pseudo main method called by the Client
    /**
     * Creates the game window.
     *
     * @param Sinput input stream
     * @param Soutput output stream
     * @param p2 boolean representing the presence of the second player.
     */
    public static void createGame(ObjectInputStream Sinput, ObjectOutputStream Soutput, String user) {
        GameMain demo = new GameMain(Sinput, Soutput, user);
        demo.window_title = "PsiBot";
        demo.displayWidth = 800;
        demo.displayHeight = 600;
        //demo.fullScreen = true;
        demo.run();
    }
    float lightDirection[] = {10f, 10f, 30f, 0f};
    float rotation = 0;
    //The models used by the game    
    GLModel[] crates;
    //GLModel blockModel;
    GLImage sky;
    GLModel asteroid_model;
    //background image variables
    //background consists of 2 seperate images "leapfrogging" one after another to create an infinite loop of backgrounds
    public static Texture bg, bg2;//textures for both backgrounds
    public static Texture[] bg_img = new Texture[8];//stores all the background images
    public static int bg_x = -200;//first background x coordinate
    public static int bg2_x = bg_x + 4096;//second background x coordinate
    public static int bg_y = 20;//background y coordinate(top vertices)
    public boolean bg_check = false;
    //timer for asteroid spawning
    int current_time = 0;
    int asteroid_timer = 0;
    //current player position
    float player_pos;
    //random number generator
    Random r = new Random();
    //The TiledMap used for the stage
    public TiledMap stage;
    //The time of the last update, used for the getDelta() method
    long lastUpdate;
    ArrayList<Asteroid> asteroids = new ArrayList<Asteroid>();//arraylist of all the asteroids
    //-------------------------------------------------------
    //---------------------CAMERA VARIABLES------------------
    //-------------------------------------------------------
    Camera cam;
    /**
     * Scales all drawn objects to the desired size (in this case 1/4f).
     */
    public final static float DRAWSCALE = 1 / 4f;
    //-------------------------------------------------------
    //---------------------PHYSICS OBJECTS-------------------
    //-------------------------------------------------------
    //  Note: 1 meter = 60 pixels
    private final World world = new World(new Vec2(0, -9.8f), false);
    /**
     * Conversion factor of meters per pixel (in this case 1 meter = 60 pixels).
     */
    public final static float PHYSICS_SCALE = 1 / 60f;
    private Block[] bodies = new Block[0];
    //-------------------------------------------------------
    //---------------------PLAYER OBJECTS-------------------
    //-------------------------------------------------------
    final int PLAYER = 1;
    PhysPlayer pp;
    int footCount;
    int mapNum;
    //-------------------------------------------------------
    //-------------------PLAYER 2 OBJECTS--------------------
    //-------------------------------------------------------    
    final int[] BOXINTERVAL = {500, 500, 1000, 5000, 6000, 20000};
    long[] lastBox = new long[6];
    //The new map object
    GameMap map;
    //-------------------------------------------------------
    //---------------NETWORKING-RELATED OBJECTS--------------
    //-------------------------------------------------------
    ClientHandler IO;
    ArrayList<PhysPlayer> oPlayers = new ArrayList<PhysPlayer>();
    //int PNum = 0;   
    ArrayList netQueue = new ArrayList();
    float TimeSinceBlock = 0;
    String username;

    @Override
    public void setup() {
        //Creates a new map object
        map = new GameMap(world);
        mapNum = 0;

        // enable lighting and texture rendering
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_TEXTURE_2D);

        GL11.glClearColor(.5f, .6f, .9f, 1f);

        // setup and enable the camera and perspective        
        cam = new Camera(aspectRatio);

        // Create a light
        setLight(GL11.GL_LIGHT1,
                new float[]{1f, 1f, 1f, 1f}, // diffuse color
                new float[]{.6f, .6f, .9f, 1f}, // ambient
                new float[]{1f, 1f, 1f, 1f}, // specular
                lightDirection);                     // direction/position

        setAmbientLight(new float[]{.6f, .6f, .9f, 1f});

        //Background image
        sky = loadImage("res/images/bg/space1.jpg");

        // Load the model
        crates = new GLModel[12];
        for (int i = 0; i < crates.length; i++) {
            crates[i] = new GLModel("res/models/Crate/Future/Block" + (i + 1) + ".obj");
        }
        //blockModel = new GLModel("res/models/Crate/Future/blo
        asteroid_model = new GLModel("res/models/asteroid/asteroid_model.obj");

        //loads background images into an array of Textures
        for (int i = 0; i < bg_img.length; i++) {
            try {
                bg_img[i] = TextureLoader.getTexture("JPG", new FileInputStream(new File("res/images/bg/space" + (i + 1) + ".jpg")));

            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Display.destroy();
                System.exit(1);
            } catch (IOException e) {
                e.printStackTrace();
                Display.destroy();
                System.exit(1);
            }
        }
        //sets the default beginning background images
        bg = bg_img[0];
        bg2 = bg_img[1];


        //Creates the physics player object, using a rectangle as the hitbox
        Body playerb = makeEmptyBody(200f * PHYSICS_SCALE, -100f * PHYSICS_SCALE, BodyType.DYNAMIC);
        pp = new PhysPlayer(playerb, 0);
        pp.loadAllModels();
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(pp.size.x / 2 * PHYSICS_SCALE, pp.size.y / 2 * PHYSICS_SCALE);
        FixtureDef fixture = new FixtureDef();
        fixture.shape = shape;
        playerb.setFixedRotation(true);
        playerb.createFixture(fixture);

        //Creates the foot listener
        FixtureDef foot = new FixtureDef();
        PolygonShape footShape = new PolygonShape();
        footShape.setAsBox(pp.size.x / 4 * PHYSICS_SCALE, pp.size.y / 4 * PHYSICS_SCALE, new Vec2(0, -pp.size.y / 2 * PHYSICS_SCALE), 0);
        foot.shape = footShape;
        foot.isSensor = true;
        Fixture f = playerb.createFixture(foot);
        f.setUserData((int) 3);
        footCount = 0;

        pp.username = username;

        IO.start();

        //Send over the starting maps
        IO.send(map.MapList[1]);
        IO.send(map.MapList[2]);

        //Send the player object to all the connected players
        Message pmsg = pp.getMessageUpdate();
        IO.send(pmsg);


        lastUpdate = System.nanoTime();
        for (int i = 0; i < lastBox.length; i++) {
            lastBox[i] = System.currentTimeMillis();
        }

        //Asteroid A = new Asteroid(-100, 200, -10, 1, 1, 1, 0, 0, 3);
        //A.setModel(asteroid_model);
        //asteroids.add(A);

        //Plays background music
        SoundTest.getSong();
    }

    //----------------------------------------------------------
    //----------------------RENDER METHOD-----------------------
    //----------------------------------------------------------
    @Override
    public void render() {
        // clear depth buffer and color
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

        drawImageFullScreen(sky);

//        //Draws the first background
//        GL11.glBindTexture(GL11.GL_TEXTURE_2D, bg.getTextureID());//binds current background texture
//
//        GL11.glPushMatrix();
//        {
//            GL11.glScalef(1, 1.5f, 1);
//            GL11.glBegin(GL11.GL_QUADS);//begins drawing
//            //bottom left coordinate
//            GL11.glTexCoord2f(0.f, 1.f);
//            GL11.glVertex3f(bg_x, bg_y - 520, -240);
//            //bottom right coordinate
//            GL11.glTexCoord2f(1.f, 1.f);
//            GL11.glVertex3f(bg_x + bg.getTextureWidth(), bg_y - 520, -240);
//            //top right coordinate
//            GL11.glTexCoord2f(1.f, 0.f);
//            GL11.glVertex3f(bg_x + bg.getTextureWidth(), bg_y, -240);
//            //top left coordinate
//            GL11.glTexCoord2f(0.f, 0.f);
//            GL11.glVertex3f(bg_x, bg_y, -240);
//            GL11.glEnd();//ends shape drawing
//        }
//
//        GL11.glPopMatrix();
//
//        //Draws the second background
//        GL11.glBindTexture(GL11.GL_TEXTURE_2D, bg2.getTextureID());//binds current background texture
//
//        GL11.glPushMatrix();
//        {
//            GL11.glScalef(1, 1.5f, 1);
//            GL11.glBegin(GL11.GL_QUADS);//begins drawing
//            //bottom left coordinate
//            GL11.glTexCoord2f(0.f, 1.f);
//            GL11.glVertex3f(bg2_x, bg_y - 520, -240);
//            //bottom right coordinate
//            GL11.glTexCoord2f(1.f, 1.f);
//            GL11.glVertex3f(bg2_x + bg.getTextureWidth(), bg_y - 520, -240);
//            //top right coordinate
//            GL11.glTexCoord2f(1.f, 0.f);
//            GL11.glVertex3f(bg2_x + bg.getTextureWidth(), bg_y, -240);
//            //top left coordinate
//            GL11.glTexCoord2f(0.f, 0.f);
//            GL11.glVertex3f(bg2_x, bg_y, -240);
//
//            GL11.glEnd();//ends shape drawing
//        }
//        GL11.glPopMatrix();


        // select model view and reset
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glLoadIdentity();

        cam.setViewpoint();

        int rendered = map.renderMap(crates[0], pp.body.getPosition(), cam);

        //Renders your Physics-based player
        GL11.glPushMatrix();
        {
            GL11.glTranslatef(pp.getX() / PHYSICS_SCALE * DRAWSCALE - pp.size.x / 8, pp.getY() / PHYSICS_SCALE * DRAWSCALE - pp.size.y / 16, -15f);
            GL11.glRotated(90, 0, 1, 0);  // turn it
            GL11.glRotated(-10, 0, 0, 1);  // turn it
            if (pp.face_left) {
                GL11.glRotated(180, 0, 1, 0);  // turn it
            }
            GL11.glScalef(pp.size.x * DRAWSCALE, pp.size.y * DRAWSCALE / 2, 10f);
            pp.render();
        }
        GL11.glPopMatrix();

        rendered++;
        //Renders all other Physics-based player
        for (int i = 0; i < oPlayers.size(); i++) {
            PhysPlayer player = oPlayers.get(i);
            //Proximity check
            //System.out.println("Player #" + player.ID + ": " + player.getX() / PHYSICS_SCALE * DRAWSCALE + "," + player.getY() / PHYSICS_SCALE * DRAWSCALE);
            if (cam.isInView(pp.body.getPosition(), player.body.getPosition().x, player.body.getPosition().y, player.size.x)) {
                GL11.glPushMatrix();
                {
                    //Renders the other players
                    //System.out.println("Player #" + player.ID + ": " + player.getX() / PHYSICS_SCALE * DRAWSCALE + "," + player.getY() / PHYSICS_SCALE * DRAWSCALE);
                    GL11.glTranslatef(player.getX() / PHYSICS_SCALE * DRAWSCALE - player.size.x / 8, player.getY() / PHYSICS_SCALE * DRAWSCALE - player.size.y / 16, -15f);
                    GL11.glRotated(90, 0, 1, 0);  // turn it
                    GL11.glRotated(-10, 0, 0, 1);  // turn it
                    if (player.face_left) {
                        GL11.glRotated(180, 0, 1, 0);  // turn it
                    }
                    GL11.glScalef(player.size.x * DRAWSCALE, player.size.y * DRAWSCALE / 2, 10f);
                    player.render();
                }
                GL11.glPopMatrix();
                rendered++;
            }
        }

        //renders asteroids in the background
        for (int i = 0; i < asteroids.size(); i++) {
            Asteroid a = (Asteroid) asteroids.get(i);
            //System.out.println(a.x_pos + "  " + a.y_pos);
            if (a.z_pos < 0) {
                a.update();
            }
        }

        //Renders all of the bodies in the list of bodies
        for (Block b : bodies) {
            //Proximity check
            if (cam.isInView(pp.body.getPosition(), b.x, b.y, 32)) {
                GL11.glPushMatrix();
                {
                    GL11.glTranslatef(((b.x + b.vx * TimeSinceBlock / 1000f) / PHYSICS_SCALE - pp.size.x / 2) * DRAWSCALE, ((b.y + b.vy * TimeSinceBlock / 1000f) / PHYSICS_SCALE + pp.size.x / 2) * DRAWSCALE, -15f);
                    GL11.glRotated(Math.toDegrees(b.theta), 0, 0, 1);  // turn it                
                    GL11.glScalef(pp.size.x * DRAWSCALE, pp.size.x * DRAWSCALE, 10f);
                    switch (b.BT) {
                        case 0:
                            crates[6].render();
                            break;
                        case 1:
                            crates[1].render();
                            break;
                        case 2:
                            crates[8].render();
                            break;
                        case 3:
                            crates[10].render();
                            break;
                        case 4:
                            crates[8].render();
                            break;
                        case 5:
                            crates[10].render();
                            break;
                        case 6:
                            crates[9].render();
                            break;
                        default:
                            crates[6].render();
                            break;
                    }
                    //crates[1].render();
                }
                GL11.glPopMatrix();
                rendered++;
            }
        }

        //renders asteroids in the foreground
        for (int i = 0; i < asteroids.size(); i++) {
            Asteroid a = (Asteroid) asteroids.get(i);

            //System.out.println(a.x_pos + "  " + a.y_pos);
            if (a.z_pos > 0) {
                a.update();
            }

        }

        //Debugging info
        print(0, displayHeight - 20, "FPS:" + Math.round((1 / getSecondsPerFrame())));
        print(0, displayHeight - 40, "NumBodies: " + world.getBodyCount());
        print(0, displayHeight - 60, "Username: " + username);
    }

    //----------------------------------------------------------
    //----------------------UPDATE METHOD-----------------------
    //----------------------------------------------------------
    @Override
    public void update() {
        //Gets the interval since the last update for physically accurate movement
        int delta = (int) Math.round((getDelta() / (float) Math.pow(10, 6)));

        //GODLINE - Handles movement, forces, collisions, gravity, all physics
        //world.step(delta / 1000f, 16, 6);

        //Updates timer
        TimeSinceBlock += delta;

        //Reads through all updates received since the last queue read
        if (!netQueue.isEmpty()) {
            IO.readNetQueue();
        }

        //Checks for player input
        checkInput(delta);

        //Updates the player's animations
        pp.update(delta);

        //Predictive motion        
        pp.body.getPosition().x += (pp.body.getLinearVelocity().x + pp.HiddenVelocity.x) * delta / 1000f;
        pp.body.getPosition().y += (pp.body.getLinearVelocity().y + pp.HiddenVelocity.y) * delta / 1000f;
        //System.out.println("playerpos " + pp.body.getPosition());

        for (int i = 0; i < oPlayers.size(); i++) {
            PhysPlayer player = oPlayers.get(i);
            //Updates the other players' animations
            player.update(delta);

            //Predicts the motion of the other players
            player.body.getPosition().x += (player.body.getLinearVelocity().x + player.HiddenVelocity.x) * delta / 1000f;
            player.body.getPosition().y += (player.body.getLinearVelocity().y + player.HiddenVelocity.y) * delta / 1000f;
        }

        //updates background, changing the position and image of the two background depending on player position to loop the background

        player_pos = pp.getX() / PHYSICS_SCALE * DRAWSCALE;//current player position
        //checks if the player's position is at the end of the current background
        //if it is, move the background in front of the second background to loop them forever
        if ((int) player_pos % bg.getTextureWidth() + 1000 == 0) {
            int image = ((int) player_pos / bg.getTextureWidth()) + 1;
            while (image > 8) {
                image = image - 8;
            }
            if (!bg_check) {
                bg_x += 2 * bg.getTextureWidth();
                bg = bg_img[image];
                bg_check = true;
            } else {
                bg2_x += 2 * bg.getTextureWidth();
                bg2 = bg_img[image];
                bg_check = false;
            }
        }

        //Updates the walls
        //System.out.println(mapNumber(pp.getX() / PHYSICS_SCALE, mapCodes));
        if (map.mapNumber(pp.getX() / PHYSICS_SCALE, map.mapCodes) != mapNum) {
            int newMap = map.mapNumber(pp.getX() / PHYSICS_SCALE, map.mapCodes);
            Map loaded = map.updateMaps(mapNum, newMap, world);
            mapNum = newMap;
            if (loaded != null) {
                IO.send(loaded);
            }
            System.out.println("Current Map: " + mapNum);
        }




        //updating and spawning asteroids
        current_time += delta;
        if (current_time >= asteroid_timer) {
            //System.out.println("created new asteroid");
            int temp_check = r.nextInt(2);
            int z_pos;
            if (temp_check == 0) {
                z_pos = r.nextInt(30) + 20;
            } else {
                z_pos = r.nextInt(15) - 70;
            }
            Asteroid newA = new Asteroid((float) (pp.getX() / PHYSICS_SCALE * DRAWSCALE) + 200 + r.nextInt(100), r.nextInt(50) + 20, z_pos, r.nextFloat(), r.nextFloat(), r.nextFloat(), r.nextFloat() + 0.1f, r.nextFloat() + 0.1f, r.nextInt(4) + 3);
            newA.setModel(asteroid_model);
            asteroids.add(newA);
            current_time = 0;
            asteroid_timer = r.nextInt(5000) + 3000;
            //asteroid_timer = 1000;

        }

        //deletes asteroids that are off screen
        for (int i = 0; i < asteroids.size(); i++) {
            Asteroid a = (Asteroid) asteroids.get(i);
            //System.out.println(pp.getX() / PHYSICS_SCALE * DRAWSCALE);
            if (a.y_pos < pp.getY() - 350 - a.size * 10) {
                asteroids.remove(i);
            }
        }

        //Adjusts the camera's position
        cam.setPosition(pp.getX() / PHYSICS_SCALE * DRAWSCALE, pp.getY() / PHYSICS_SCALE * DRAWSCALE + 50);
    }

    /**
     * Gets the time interval since the last update.
     *
     * @return Long a
     */
    public long getDelta() {
        long a = System.nanoTime() - lastUpdate;
        lastUpdate = System.nanoTime();
        return a;
    }

    /**
     * Set camera perspective.
     */
    public static void setPerspective() {
        // select projection matrix (controls perspective)
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glLoadIdentity();
        // fovy, aspect ratio, zNear, zFar
        GLU.gluPerspective(40f, // zoom in or out of view
                aspectRatio, // shape of viewport rectangle (width/height)
                .1f, // Min Z: how far from eye position does view start
                500f);       // max Z: how far from eye position does view extend
        // return to modelview matrix
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
    }

    /**
     * Checks what key has been pressed and executes the corresponding action.
     *
     * @param delta the time since the last update.
     */
    public void checkInput(int delta) {
        if (Keyboard.isKeyDown(Keyboard.KEY_A)) {
            IO.send(new KeyMessage(pp.ID, (short) 0, (short) delta, username));
        } else if (Keyboard.isKeyDown(Keyboard.KEY_D)) {
            IO.send(new KeyMessage(pp.ID, (short) 1, (short) delta, username));
        }

        if (Keyboard.isKeyDown(Keyboard.KEY_W)) {
            IO.send(new KeyMessage(pp.ID, (short) 2, (short) delta, username));
            if (Math.abs(pp.body.getLinearVelocity().x) > ((pp.MAXSPEED / 7) * 6)) {
                pp.runjump = true;
            } else {
                pp.runjump = false;
            }
            pp.jumping = true;
        }
        //If the 1 key is pressed and enough time has passed, then a dynamic  standard block is dropped.
        if (Keyboard.isKeyDown(Keyboard.KEY_1) && (System.currentTimeMillis() - lastBox[0]) > BOXINTERVAL[0]) {
            //Create a new block, and send it to the server
            Block b = new Block((cam.cameraPos[0] / DRAWSCALE + (Mouse.getX() - displayWidth / 2)) * PHYSICS_SCALE, (cam.cameraPos[1] / DRAWSCALE + (Mouse.getY() - 5 * displayHeight / 6)) * PHYSICS_SCALE, pp.size.x, pp.size.x, 0, 1);
            IO.send(b);
            lastBox[0] = System.currentTimeMillis();
        }
        //If the 2 key is pressed and enough time has passed, then a dynamic ice block is dropped.
        if (Keyboard.isKeyDown(Keyboard.KEY_2) && (System.currentTimeMillis() - lastBox[1]) > BOXINTERVAL[1]) {

            //Create a new block, and send it to the server
            Block b = new Block((cam.cameraPos[0] / DRAWSCALE + (Mouse.getX() - displayWidth / 2)) * PHYSICS_SCALE, (cam.cameraPos[1] / DRAWSCALE + (Mouse.getY() - 5 * displayHeight / 6)) * PHYSICS_SCALE, pp.size.x, pp.size.x, 0, 4);
            IO.send(b);
            lastBox[1] = System.currentTimeMillis();
        }
        //If the 3 key is pressed and enough time has passed, then a dynamic  heavy block is dropped.
        if (Keyboard.isKeyDown(Keyboard.KEY_3) && (System.currentTimeMillis() - lastBox[2]) > BOXINTERVAL[2]) {
            //Create a new block, and send it to the server
            Block b = new Block((cam.cameraPos[0] / DRAWSCALE + (Mouse.getX() - displayWidth / 2)) * PHYSICS_SCALE, (cam.cameraPos[1] / DRAWSCALE + (Mouse.getY() - 5 * displayHeight / 6)) * PHYSICS_SCALE, pp.size.x, pp.size.x, 0, 6);
            IO.send(b);
            lastBox[2] = System.currentTimeMillis();
        }
        //If the 4 key is pressed and enough time has passed, then a static standard block is dropped.
        if (Keyboard.isKeyDown(Keyboard.KEY_4) && (System.currentTimeMillis() - lastBox[3]) > BOXINTERVAL[3]) {
            //Create a new block, and send it to the server
            Block b = new Block((cam.cameraPos[0] / DRAWSCALE + (Mouse.getX() - displayWidth / 2)) * PHYSICS_SCALE, (cam.cameraPos[1] / DRAWSCALE + (Mouse.getY() - 5 * displayHeight / 6)) * PHYSICS_SCALE, pp.size.x, pp.size.x, 0, 0);
            IO.send(b);
            lastBox[3] = System.currentTimeMillis();
        }
        //If the 5 key is pressed and enough time has passed, then a static ice block is dropped.
        if (Keyboard.isKeyDown(Keyboard.KEY_5) && (System.currentTimeMillis() - lastBox[4]) > BOXINTERVAL[4]) {
            //Create a new block, and send it to the server
            Block b = new Block((cam.cameraPos[0] / DRAWSCALE + (Mouse.getX() - displayWidth / 2)) * PHYSICS_SCALE, (cam.cameraPos[1] / DRAWSCALE + (Mouse.getY() - 5 * displayHeight / 6)) * PHYSICS_SCALE, pp.size.x, pp.size.x, 0, 2);
            IO.send(b);
            lastBox[4] = System.currentTimeMillis();
        }
        //If the 6 key is pressed and enough time has passed, then a static trampoline block is dropped.
        if (Keyboard.isKeyDown(Keyboard.KEY_6) && (System.currentTimeMillis() - lastBox[5]) > BOXINTERVAL[5]) {


            //Create a new block, and send it to the server
            Block b = new Block((cam.cameraPos[0] / DRAWSCALE + (Mouse.getX() - displayWidth / 2)) * PHYSICS_SCALE, (cam.cameraPos[1] / DRAWSCALE + (Mouse.getY() - 5 * displayHeight / 6)) * PHYSICS_SCALE, pp.size.x, pp.size.x, 0, 3);
            IO.send(b);
            lastBox[5] = System.currentTimeMillis();
        }

    }

    private Body makeEmptyBody(float x, float y, BodyType bodytype) {
        BodyDef bodydef = new BodyDef();
        bodydef.position.set(x, y);
        bodydef.type = bodytype;
        Body body = world.createBody(bodydef);
        return body;
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

    //-------------------------------------------------------
    //----------------FOOT CONTACT LISTENER------------------
    //-------------------------------------------------------
    //Checks when the foot sensor on the bottom of the physics player intersects with another body
    class footListener implements ContactListener {

        @Override
        public void beginContact(Contact cntct) {

            //Tests to see if fixture A is the foot sensor
            Fixture test = cntct.m_fixtureA;
            if (test.getUserData() != null) {
                if (((Integer) test.getUserData()).intValue() == 3) {
                    footCount++;
                    if (pp.body.getLinearVelocity().y <= 0) {
                        pp.jumping = false;
                        pp.runningJump_count = 0;
                    }
                }
            }

            //Tests to see if fixture B is the foot sensor
            test = cntct.m_fixtureB;
            if (test.getUserData() != null) {
                if (((Integer) test.getUserData()).intValue() == 3) {
                    footCount++;
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
                if (((Integer) test.getUserData()).intValue() == 3) {
                    footCount--;
                }
            }

            test = cntct.m_fixtureB;
            if (test.getUserData() != null) {
                if (((Integer) test.getUserData()).intValue() == 3) {
                    footCount--;
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
    //-------------------------------------------------------
    //-------------------NETWORKING HANDLER------------------
    //-------------------------------------------------------
    //Handles the input and output of the client, and parses commands read in from the server

    class ClientHandler extends Thread {

        ClientHandler(ObjectInputStream Sinput, ObjectOutputStream Soutput) {
            IN = Sinput;
            OUT = Soutput;
        }
        final ObjectInputStream IN;
        final ObjectOutputStream OUT;

        @Override
        public void run() {

            //---------------PRE-LOOP ACTIONS---------------
            //Get the player number for this player    
            System.out.println("Started IO Thread");
            //send("serv.pnum");

//            System.out.println("Client: Waiting for client number");
//            //wait for the server to respond with the client number
//            while (true) {
//                try {
//                    Object reply = IN.readObject();
//                    //System.out.print("Client: Received reply - ");
//                    if (reply instanceof Integer) {
//                        PNum = ((Integer) reply).intValue();
//                        pp.ID = (short) PNum;
//                        System.out.println("Client Number : " + PNum);
//                        break;
//                    }
//                } catch (IOException e) {
//                } catch (ClassNotFoundException o) {
//                    o.printStackTrace();
//                }
//            }

            //----------------MAIN LOOP---------------------
            while (true) {
                try {
                    //Receives object input, and adds to the queue if it is recognized
                    Object objectIn = IN.readObject();
                    if (objectIn instanceof Message) {
                        enqueue(objectIn);
                    } else if (objectIn instanceof Message[]) {
                        enqueue(objectIn);
                    } else if (objectIn instanceof Block[]) {
                        enqueue(objectIn);
                    } else {
                        //System.out.println("Received unknown object of type: " + objectIn.getClass());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException o) {
                    o.printStackTrace();
                }
            }
        }

        public void parsePlayerMessage(Message msg) {
            //Check to see if we are dealing with a player           
            if (msg.type == 0) {
                //Check if we should be creating a new player
                if (!msg.user.equals(username)) {
                    PhysPlayer toUpdate = (PhysPlayer) (getObjectWithID(oPlayers, msg.ID));
                    if (toUpdate == null) {
                        System.out.println("creating new player, ID: " + msg.ID);
                        Body playerb = makeEmptyBody(msg.p[0], msg.p[1], BodyType.KINEMATIC);
                        PhysPlayer op = new PhysPlayer(playerb, msg.ID);
                        PolygonShape shape = new PolygonShape();
                        shape.setAsBox(op.size.x / 2 * PHYSICS_SCALE, op.size.y / 2 * PHYSICS_SCALE);
                        FixtureDef fixture = new FixtureDef();
                        fixture.shape = shape;
                        playerb.setFixedRotation(true);
                        playerb.createFixture(fixture);
                        op.idle = pp.idle;
                        op.jump = pp.jump;
                        op.run = pp.run;
                        op.running_jump = pp.running_jump;
                        oPlayers.add(op);
                    } else {
                        Vec2 pos = toUpdate.body.getPosition();
                        //System.out.println("Change In Position: " + (msg.p[0] - pos.x) + "," + (msg.p[1] - pos.y));
                        pos.x = msg.p[0];
                        pos.y = msg.p[1];
                        Vec2 vel = toUpdate.body.getLinearVelocity();
                        vel.x = msg.v[0];
                        vel.y = msg.v[1];
                        //smoothPositions(toUpdate, msg.p, msg.v);
                    }
                } else {
                    smoothPositions(pp, msg.p, msg.v);
                }
            }
        }

        public void readNetQueue() {
            for (int i = 0; i < netQueue.size(); i++) {
                parseObject(netQueue.get(i));
            }
            netQueue.clear();
        }

        public void parseObject(Object objectIn) {
            if (objectIn instanceof Message) {
                parsePlayerMessage((Message) objectIn);
            } else if (objectIn instanceof Message[]) {
                //parse all the messages
                for (Message x : (Message[]) objectIn) {
                    parsePlayerMessage(x);
                }
            } else if (objectIn instanceof Block[]) {
                bodies = (Block[]) objectIn;
                TimeSinceBlock = 0;
            }
        }

        public void smoothPositions(PhysPlayer p, float[] p2, float[] v2) {
            Vec2 pos1 = pp.body.getPosition();
            float dx = (p2[0] - pos1.x);
            float dy = (p2[1] - pos1.y);
            if (Math.abs(dx) < 0.5 && Math.abs(dy) < 0.5) {
                p.HiddenVelocity = new Vec2(dx * 8, dy * 8);
            } else {
                pp.body.getPosition().x = p2[0];
                pp.body.getPosition().y = p2[1];
            }

            p.body.setLinearVelocity(new Vec2(v2[0], v2[1]));
        }

        public Object getObjectWithID(ArrayList x, int ID) {
            for (Object a : x) {
                if (((NetObject) a).ID == ID) {
                    return a;
                }
            }
            return null;
        }

        public void enqueue(Object x) {
            netQueue.add(x);
        }

        public void send(Object x) {
            try {
                OUT.writeObject(x);
                OUT.flush();
            } catch (IOException ex) {
                Logger.getLogger(GameMain.class.getName()).log(Level.SEVERE, null, ex);
            }
            //System.out.println(x.getClass() + " Message sent");
        }
    }
}
