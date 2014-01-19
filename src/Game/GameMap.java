/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Game;

import Game.blocks.*;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.*;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.tiled.TiledMap;

/**
 *
 * @author Aidan
 */
public class GameMap {

    public Map[] MapList;
    public int[] mapCodes = {0, 1, 2, 0, 0};
    public TiledMap[] allMaps = new TiledMap[4];

    public GameMap(World w) {
        init(w);
    }

    public void init(World world) {
        try {
            allMaps[0] = new TiledMap("res/maps/Map1.tmx");
            allMaps[1] = new TiledMap("res/maps/Map3.tmx");
            allMaps[2] = new TiledMap("res/maps/Map4.tmx");
            //allMaps[3] = new TiledMap("/res/maps/StartMap.tmx");
        } catch (SlickException ex) {
            Logger.getLogger(GameMain.class.getName()).log(Level.SEVERE, null, ex);
        }

        MapList = new Map[3];

        //Adds a spacer in at the beggining
        MapList[0] = null;

        Block[] w = loadMap(allMaps[mapCodes[0]], world, 0);
        MapList[1] = new Map(0, allMaps[mapCodes[0]].getWidth() * allMaps[mapCodes[0]].getTileWidth(), w);

        w = loadMap(allMaps[mapCodes[1]], world, allMaps[mapCodes[0]].getHeight() * allMaps[mapCodes[0]].getTileHeight());
        MapList[2] = new Map(1, allMaps[mapCodes[1]].getWidth() * allMaps[mapCodes[1]].getTileWidth(), w);
    }

    public Block[] loadMap(TiledMap map, World world, int offset) {
        System.out.println("loading map");

        //Debugging variables
        int c = 0, g = 0;

        int SIZE = map.getTileHeight();
        ArrayList<Block> blocks = new ArrayList<Block>();
        for (int yAxis = 0; yAxis < map.getHeight(); yAxis++) {
            for (int xAxis = 0; xAxis < map.getWidth(); xAxis++) {
                int tileID = map.getTileId(xAxis, yAxis, 0);
                if (map.getTileProperty(tileID, "blocked", "false").equals("true")) {

                    //Create the blank body, does not contain any fixtures yet, because there is a block there
                    Body template = makeEmptyBody(xAxis * SIZE * GameMain.PHYSICS_SCALE, (map.getHeight() - yAxis * SIZE) * GameMain.PHYSICS_SCALE, world, BodyType.STATIC);
                    int bType = Integer.parseInt(map.getTileProperty(tileID, "blockType", "-1"));
                    //FLOOR SMOOTHING ALGORITHM, Still in progress
                    int dx = 0;
                    for (; dx < map.getWidth() - xAxis; dx++) {
                        int tileID2 = map.getTileId((dx + xAxis), yAxis, 0);
                        //Detect if the next square is also solid, if it is, make it solid
                        if (map.getTileProperty(tileID2, "blocked", "false").equals("true") && Integer.parseInt(map.getTileProperty(tileID2, "blockType", "-1")) == bType) {
                            c++;
                        } else {
                            break;
                        }
                    }

                    //FixtureDef blockProps = makeFixture(offset * GameMain.PHYSICS_SCALE, 0, SIZE / 2 * GameMain.PHYSICS_SCALE, SIZE / 2 * GameMain.PHYSICS_SCALE, 1.0f, 0.1f);
                    BlocksType bT = new BlocksType(bType, (xAxis * SIZE + offset) * GameMain.PHYSICS_SCALE, (map.getHeight() - yAxis * SIZE) * GameMain.PHYSICS_SCALE, SIZE, offset * GameMain.PHYSICS_SCALE);
                    //Block block = new Block((xAxis * SIZE + offset) * GameMain.PHYSICS_SCALE, (map.getHeight() - yAxis * SIZE) * GameMain.PHYSICS_SCALE, SIZE * GameMain.PHYSICS_SCALE, SIZE * GameMain.PHYSICS_SCALE, dx, blockProps);
                    Block block = new Block(bT.xPosition, bT.yPosition, bT.w, bT.h, dx, bType);
                    blocks.add(block);

                    block.body = template;

                    //Create some basic objects
                    PolygonShape edgeShape = new PolygonShape();
                    FixtureDef fixture = new FixtureDef();
                    //Set the properties of the block
                    fixture.friction = bT.friction;
                    fixture.restitution = bT.bouncyness;
                    fixture.density = bT.density;

                    //Add top edge                    
                    edgeShape.setAsEdge(new Vec2((-SIZE / 2 + offset) * GameMain.PHYSICS_SCALE, SIZE / 2 * GameMain.PHYSICS_SCALE + 0.01f), new Vec2(((dx - 1 / 2f) * SIZE + offset) * GameMain.PHYSICS_SCALE, SIZE / 2 * GameMain.PHYSICS_SCALE + 0.01f));
                    fixture.shape = edgeShape;
                    template.createFixture(fixture);

                    //Add bottom edge
                    edgeShape.setAsEdge(new Vec2((-SIZE / 2 + offset) * GameMain.PHYSICS_SCALE, -SIZE / 2 * GameMain.PHYSICS_SCALE), new Vec2(((dx - 1 / 2f) * SIZE + offset) * GameMain.PHYSICS_SCALE, -SIZE / 2 * GameMain.PHYSICS_SCALE));
                    fixture.shape = edgeShape;
                    template.createFixture(fixture);

                    //Add left edge
                    edgeShape.setAsEdge(new Vec2((-SIZE / 2 + offset) * GameMain.PHYSICS_SCALE, SIZE / 2 * GameMain.PHYSICS_SCALE), new Vec2((-SIZE / 2 + offset) * GameMain.PHYSICS_SCALE, -SIZE / 2 * GameMain.PHYSICS_SCALE));
                    fixture.shape = edgeShape;
                    template.createFixture(fixture);

                    //Add right edge
                    edgeShape.setAsEdge(new Vec2(((dx - 1 / 2f) * SIZE + offset) * GameMain.PHYSICS_SCALE, SIZE / 2 * GameMain.PHYSICS_SCALE), (new Vec2(((dx - 1 / 2f) * SIZE + offset) * GameMain.PHYSICS_SCALE, -SIZE / 2 * GameMain.PHYSICS_SCALE)));
                    fixture.shape = edgeShape;
                    template.createFixture(fixture);

                    xAxis += dx;
                    g++;
                }
            }
        }
        blocks.trimToSize();
        Block[] walls = new Block[blocks.size()];
        for (int i = 0; i < walls.length; i++) {
            walls[i] = (Block) blocks.get(i);
        }
        System.out.println("Loaded " + c + " blocks in " + g + " groups");
        return walls;
    }

    public Map updateMaps(int oldMap, int newMap, World world) {
        System.out.println("entered new map #:" + newMap);
        if (newMap > oldMap) {

            if (MapList[0] != null) {
                System.out.println("Unloading walls");
                disposeBlocks(MapList[0].walls, world);
            }
            MapList[0] = MapList[1];
            MapList[1] = MapList[2];

            //Check to see if we should be loading another map
            //If yes, then load the next map in the array
            if (newMap + 1 < mapCodes.length) {
                int offset = 0;
                for (int i = 0; i < newMap + 1; i++) {
                    offset += allMaps[mapCodes[i]].getHeight() * allMaps[mapCodes[i]].getTileHeight();
                }
                Block[] w = loadMap(allMaps[mapCodes[newMap + 1]], world, offset);
                System.out.println("loaded map #:" + (newMap + 1));
                MapList[2] = new Map(newMap + 1, allMaps[mapCodes[newMap + 1]].getWidth() * allMaps[mapCodes[newMap + 1]].getTileWidth(), w);
            } //If no, put in a null placeholder
            else {
                MapList[2] = null;
            }
            return MapList[2];
        } else if (newMap < oldMap) {

            if (MapList[2] != null) {
                System.out.println("Unloading walls");
                disposeBlocks(MapList[2].walls, world);
            }
            MapList[2] = MapList[1];
            MapList[1] = MapList[0];

            //Check to see if we should be loading another map
            //If yes, then load the next map in the array
            if (newMap - 1 >= 0) {
                int offset = 0;
                for (int i = 0; i < newMap - 1; i++) {
                    offset += allMaps[mapCodes[i]].getHeight() * allMaps[mapCodes[i]].getTileHeight();
                }
                Block[] w = loadMap(allMaps[mapCodes[newMap - 1]], world, offset);
                System.out.println("loaded map #:" + (newMap - 1));
                MapList[0] = new Map(newMap - 1, allMaps[mapCodes[newMap - 1]].getWidth() * allMaps[mapCodes[newMap - 1]].getTileWidth(), w);
            } //If no, put in a null placeholder
            else {
                MapList[0] = null;
            }
            return MapList[0];
        }
        return null;
    }

    public int getOffset(int ind) {
        int offset = 0;
        for (int i = 0; i < ind; i++) {
            offset += allMaps[mapCodes[i]].getHeight() * allMaps[mapCodes[i]].getTileHeight();
        }
        return offset;
    }

    private Body makeEmptyBody(float x, float y, World world, BodyType bodytype) {
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
    //Given an array of maps and a player's X coordinate, this method finds the
    //map that the player is in.

    public int mapNumber(float position, int[] codes) {

        int[] mapEndX = new int[codes.length];
        int accumulator = 0;
        //For every map, the end position of the map is added on to the
        //end position of all previous maps to get the actual end position.
        //The mapEndX array holds the end position of every map.
        for (int i = 0; i < codes.length; i++) {
            accumulator += allMaps[codes[i]].getTileWidth() * allMaps[codes[i]].getWidth();
            mapEndX[i] = accumulator;
            if (position < mapEndX[i]) {
                return i;
            }
        }
        //This should never happen.
        return -1;
    }

    public void disposeBlocks(Block[] toDispose, World world) {
        int c = 0;
        for (Block b : toDispose) {
            if (b.body != null) {
                c++;
                world.destroyBody(b.body);
            }
        }
        System.out.println("Destroyed " + c + " bodies");
    }

    public int renderMap(glmodel.GLModel m, Vec2 p, Camera cam) {
        int c = 0;
        for (int i = 0; i < MapList.length; i++) {
            if (MapList[i] != null) {
                for (Block b : MapList[i].walls) {
                    //-------------------------------------------------------
                    //---------------------PROXIMITY TESTING-----------------
                    //-------------------------------------------------------                    
                    if (cam.isInView(p, b.x, b.y, b.w * b.n)) {
                        c++;
                        //Render the block
                        for (int z = 0; z < 2; z++) {
                            for (int n = 0; n < b.n; n++) {
                                GL11.glPushMatrix();
                                {
                                    GL11.glTranslatef((b.x + b.w * n) * GameMain.DRAWSCALE / GameMain.PHYSICS_SCALE, b.y * GameMain.DRAWSCALE / GameMain.PHYSICS_SCALE, (z + 1) * -10f);
                                    GL11.glScalef(b.w / GameMain.PHYSICS_SCALE * GameMain.DRAWSCALE, b.h / GameMain.PHYSICS_SCALE * GameMain.DRAWSCALE, 10f);
                                    m.render();
                                }
                                GL11.glPopMatrix();
                            }
                        }
                    }
                }
            }
        }
        return c;
    }
}
