/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Networking.communication;

import java.io.Serializable;

/**
 * A class used for the communication of key events such as button presses.
 *
 * @author Aidan
 */
public class KeyMessage implements Serializable {

    /**
     * The ID of the player who created the KeyMessage object
     */
    public short ID;
    /**
     * The username of the player who created the KeyMessage object
     */
    public String user;
    /**
     * A code representing which key was pressed.
     */
    public short key;
    /**
     * The amount of time the key was down for.
     */
    public short delta;

    /**
     * The standard constructor for the KeyMessage class.
     *
     * @param n The ID of the player
     * @param k The key code
     * @param d The delta value
     * @param u The username
     */
    public KeyMessage(short n, short k, short d, String u) {
        user = u;
        ID = n;
        key = k;
        delta = d;
    }
    //Key variable values
    //  0: left
    //  1: right
    //  2: jump
}
