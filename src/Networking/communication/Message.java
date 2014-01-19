/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Networking.communication;

import java.io.Serializable;

/**
 * This class is a communication class which contains several fields which can
 * contain various types of data with the end goal of keeping the clients in
 * sync with each other and the server
 *
 * @author Aidan
 */
//-------------------------------------------------------
//-------------------MESSAGE STRUCTURE-------------------
//-------------------------------------------------------
public class Message implements Serializable {

    /**
     * The type of message.
     */
    public short type;
    /**
     * The ID of the sending object.
     */
    public short ID;
    /**
     * The username of the player that sent the Message
     */
    public String user;
    /**
     * A set of coordinates used to specify a location
     */
    public float[] p;
    /**
     * A linear velocity
     */
    public float[] v;
    /**
     * An angle of rotation
     */
    public float rotation;
}
