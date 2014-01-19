/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Networking.communication;

import java.io.Serializable;

/**
 * The PlayerInfo class, which is used as a means of communication in the team
 * select process, which is currently in implementation
 *
 * @author Aidan
 */
public class PlayerInfo implements Serializable {

    /**
     * The player's username
     */
    public String username;
    /**
     * The team number the player is on
     */
    public int team;
    /**
     * The player number. Can either be 1 or 0, corresponding to whether the
     * player is player 1 or 2.
     */
    public int player;

    /**
     * The default constructor for the PlayerInfo class, which specifies all
     * it's data.
     *
     * @param u The player's username
     * @param t The team number
     * @param p the player number
     */
    public PlayerInfo(String u, int t, int p) {
        username = u;
        team = t;
        player = p;
    }

    @Override
    public String toString() {
        return (team + " " + player + " " + username);
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof PlayerInfo) {
            PlayerInfo p = (PlayerInfo) o;
            if (username.equals(p.username)) {
                return true;
            }
        }
        return false;
    }
}
