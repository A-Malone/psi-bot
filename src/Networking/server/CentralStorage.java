/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Networking.server;

import Game.Map;
import Game.PhysPlayer;
import java.util.ArrayList;

/**
 *
 * @author ubuntu
 * A class that is used to store all the objects used by the server in one place
 */
public class CentralStorage {

    /**
     * An ArrayList that stores the player objects on the server end
     */
    public ArrayList<PhysPlayer> Players = new ArrayList<PhysPlayer>(2);    
    /**
     * An ArrayList that stores all the maps on the server end
     */
    public ArrayList<Map> maps = new ArrayList<Map>();    
    
    /**
     * A ArrayList which acts as a queue of objects that have been received by the Input thread, and have to be processed by the server updater code
     */
    public ArrayList queue = new ArrayList();

    /**
     * A blank constructor for the CentralStorage class
     */
    public CentralStorage() {
    }
}
