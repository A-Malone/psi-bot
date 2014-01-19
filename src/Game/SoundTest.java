/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Game;

/**
 *
 * @author student
 */
/**
 * ***********************************************************************
 * Compilation: javac -classpath .:jl1.0.jar MP3.java (OS X) javac -classpath
 * .;jl1.0.jar MP3.java (Windows) Execution: java -classpath .:jl1.0.jar MP3
 * filename.mp3 (OS X / Linux) java -classpath .;jl1.0.jar MP3 filename.mp3
 * (Windows)
 *
 * Plays an MP3 file using the JLayer MP3 library.
 *
 * Reference: http://www.javazoom.net/javalayer/sources.html
 *
 *
 * To execute, get the file jl1.0.jar from the website above or from
 *
 * http://www.cs.princeton.edu/introcs/24inout/jl1.0.jar
 *
 * and put it in your working directory with this file MP3.java.
 *
 ************************************************************************
 */
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.util.Random;
import javazoom.jl.player.Player;

/**
 * Class responsible for all sound events.
 * @author Iefan
 */
public class SoundTest {

    private String filename;
    private Player player;
    static Random r;

    // constructor that takes the name of an MP3 file
    /**
     * Constructor that takes the name of an MP3 file.
     * @param filename the name of the MP3 file. 
     */
    
    public SoundTest(String filename) {
        this.filename = filename;
    }
/**
 * If player object exists then close it.  
 */

    public void close() {
        if (player != null) {
            player.close();
        }
    }

    // play the MP3 file to the sound card
    /**
     * Play the MP3 file to the sound card.
     */
   
    public void play() {
        try {
            FileInputStream fis = new FileInputStream(filename);
            BufferedInputStream bis = new BufferedInputStream(fis);
            player = new Player(bis);
        } catch (Exception e) {
            System.out.println("Problem playing file " + filename);
            System.out.println(e);
        }

        // run in new thread to play in background
        new Thread() {
            public void run() {
                try {
                    player.play();
                } catch (Exception e) {
                    System.out.println(e);
                }
            }
        }.start();
    }
/**
 * Randomly selects a sound file and plays it. The thread then sleeps for 
 * the length of the song. Once the song is done the precess repeats.  
 */
    
    public static void getSong() {
        r = new Random();
        new Thread() {
            public void run() {
                String[] filenames = new String[3];
                filenames[0] = "res/sounds/Robot Rock.mp3";
                filenames[1] = "res/sounds/6-Sultans Of Swing.mp3";
                filenames[2] = "res/sounds/She Left Me.mp3";
                int[] songtimes = new int[3];
                songtimes[0] = 287000;
                songtimes[1] = 348000;
                songtimes[2] = 171000;
                String filename;
                //int i = 0;
                //i = r.nextInt(3);
                filename = filenames[0];
                try {
                    // Delay is legth of song in millis
                    SoundTest mp3 = new SoundTest(filename);
                    mp3.play();
                    Thread.sleep(songtimes[0]);
                    mp3.close();
                } catch (InterruptedException ex) {
                    System.out.println(ex);
                }
            }
        }.start();

        
    }

    
}