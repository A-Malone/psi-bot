/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Game;

import Game.blocks.Block;
import java.io.Serializable;

/**
 *
 * @author Aidan
 */
public class Map implements Serializable {
    //This is a new class to represent one map, so that it can be loaded parsed 
    //  by the client that ecounters it, and then forwarded to the server
    
    //The number of the map
    /**
     * Integer representing the number of the map the player. 
     */
    public int mapNum;
    
    //The walls contained in the map
    /**
     * Block array containing all the walls within the current map.
     */
    public Block[] walls;
    
    //The width of the map
    /**
     * Integer representing the width of the current map.
     */
    public int width;
    
    /**
     * Object containing all the information about one map file.
     * @param ID the number of the map
     * @param w the width of the map
     * @param b an array containing all the walls in the map  
     */
    public Map(int ID, int w, Block[] b){
        walls = b;
        mapNum = ID;
        width = w;
    }
}
