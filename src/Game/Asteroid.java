
package Game;

import glmodel.GLModel;
import org.lwjgl.opengl.GL11;

/**
 *
 * @author Victor
 */
public class Asteroid{
     GLModel model;
     float x_pos,y_pos,z_pos,x_rot_speed,y_rot_speed,z_rot_speed,x_speed,y_speed,size;
     float x_rot = 0;
     float y_rot = 0;
     float z_rot = 0;

     
     /**
     * Constructor holding all the information required to create an 
     * asteroid.
     * @param x x position of the asteroid.
     * @param y y position of the asteroid.
     * @param z z position of the asteroid.
     * @param x_r x rotation speed of the asteroid.
     * @param y_r y rotation speed of the asteroid.
     * @param z_r z rotation speed of the asteroid.
     * @param x_s x speed of the asteroid.
     * @param y_s y speed of the asteroid.
     * @param siz Size of the asteroid.
     */
    public Asteroid(float x, float y,float z,float x_r,float y_r, float z_r, float x_s, float y_s, float siz){
         x_pos = x;
         y_pos = y;
         z_pos = z;
         x_rot_speed = x_r;
         y_rot_speed = y_r;
         z_rot_speed = z_r;
         x_speed = x_s;
         y_speed = y_s;
         size = siz;
     }
     
     /**
     * Updates the speeds of the asteroid.
     */
    public void update(){

         x_pos -= x_speed;
         y_pos -= y_speed;
         x_rot += x_rot_speed;
         y_rot += y_rot_speed;
         z_rot += z_rot_speed;

         
          GL11.glPushMatrix();
            {
                //Updates the other players' animations
                GL11.glTranslated(x_pos,y_pos,z_pos);
                GL11.glRotated(x_rot, 1, 0, 0);  // x axis rotation
                GL11.glRotated(y_rot, 0, 1, 0);  // x axis rotation
                GL11.glRotated(z_rot, 0, 0, 1);  // x axis rotation
                GL11.glScaled(size,size,size);
                model.render();
            }
            GL11.glPopMatrix();

     }
     
     /**
     * Sets the model to display.
     * @param x
     */
    public void setModel(GLModel x){
             model = x;

     }
   
}
