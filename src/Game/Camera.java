/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Game;

import org.jbox2d.common.Vec2;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

/**
 *
 * @author Aidan
 */
public class Camera {

    //-------------------------------------------------------
    //---------------------CAMERA VARIABLES------------------
    //-------------------------------------------------------
    //0 = basic lockon
    //1 = slightly ahead of character to show more when moving fast
    //2 = rotating cam (vomit cam)
    final int CAMTYPE = 0;
    //Variables used by the various types of cameras
    float camAdvance = 0;
    float camOffSet = 0;
    // Camera position array
    /**
     * Array containing the camera's position in 3 dimensions 
     */
    public float[] cameraPos = {0f, 50f, 200f};
    //The rotation of the camera
    float cameraRotation = 0f;
    // A constant used in navigation
    final float piover180 = 0.0174532925f;
    //Width and height of the proximity rendering window
    final float RWIDTH = 1200;
    final float RHEIGHT = 1000;

    /**
     * Constructor creating a camera object and setting its 
     * perspective.
     * @param ar the aspect ratio
     */
    public Camera(float ar) {
        setPerspective(ar);
    }

    /**
     * Sets the perspective of the camera based on the aspect ratio.
     * @param aspectRatio the aspect ratio.
     */
    public static void setPerspective(float aspectRatio) {
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
     * Sets the position of the camera.
     * @param x x position of the camera
     * @param y y position of the camera
     */
    public void setPosition(float x, float y) {
        cameraPos[0] = x;
        cameraPos[1] = y;
    }

    /**
     * Sets the view point of the camera.
     * In this case the camera is centered on the player.
     */
    public void setViewpoint() {
        // Set the viewpoint - PLAYER CENTERED
        GLU.gluLookAt(cameraPos[0] - (float) Math.sin(cameraRotation) + camAdvance * 20, cameraPos[1], cameraPos[2], // where is the eye
                // Where is the cam looking?
                cameraPos[0], cameraPos[1] - 0.25f, cameraPos[2] - 1,
                0f, 1f, 0f);   // which way is up        
    }

    /**
     * Checks to see if the player is within the view of the camera.
     * @param player the player that is being checked.
     * @param x x position of the player
     * @param y y position of the player
     * @param w Width of the player
     * @return Is the player in view or not.
     */
    public boolean isInView(Vec2 player, float x, float y, float w) {
        if (y < player.y + RHEIGHT / 2 * GameMain.PHYSICS_SCALE && y > player.y - RHEIGHT / 2 * GameMain.PHYSICS_SCALE) {
            //check x-co-ordinates
            if ((x < player.x + RWIDTH / 2 * GameMain.PHYSICS_SCALE) && (x > player.x - RWIDTH / 2 * GameMain.PHYSICS_SCALE || x + w > player.x - RWIDTH / 2 * GameMain.PHYSICS_SCALE)) {
                return true;
            }
        }
        return false;
    }
}
