/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Networking.server;

/**
 *
 * @author Aidan
 */
public class Team {

    int TeamID;
    boolean[] players = new boolean[2];

    public Team(int id) {
        TeamID = id;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Team) {
            if (((Team) o).TeamID == TeamID) {
                return true;
            }
        }
        return false;
    }
}
