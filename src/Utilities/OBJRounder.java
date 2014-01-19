//this program is used to read in .obj files, round the vertices to a smaller amount of decimals, and then rewrite the new file
//this decreases the amount of memory required to store and read .objs files
//in .obj files, vertices usually have 6 decimal places. I wrote this program to decrease that number to 2 decimal places, decreasing file size
package Utilities;

import java.io.*;
import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 *
 * @author Victor
 */
public class OBJRounder {

    public static ArrayList vertices = new ArrayList();  // stores the vertice coordinates that will be rounded

    public static void main(String[] args) {

        //manually change the amount of .obj files located in the folder for each animation
        for (int i = 1; i <= 12; i++) {

            // The name of the file to open.
            String fileName = "res/models/run_jump/run_jump" + i + ".obj";
            String fileWriteName = "C:/Users/Victor/Desktop/run_jump/run_jump" + i + ".obj";//the name of the file to write to

            // This will reference one line at a time
            String line = null;

            try {
                FileWriter fileWriter = new FileWriter(fileWriteName);//set up the filewriter

                BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);//set up the bufferedwriter



                // FileReader reads text files .
                FileReader fileReader = new FileReader(fileName);

                BufferedReader bufferedReader = new BufferedReader(fileReader);//set up the bufferedreader

                //while there are lines in the file, go through each, trim it, and round the vertices
                while ((line = bufferedReader.readLine()) != null) {
                    line = line.trim();
                    line = line.replaceAll("  ", " ");//replaces all double spaces with single spaces
                    if (line.length() > 0) {
                        //if the line describes a vertice or textured vertice
                        if (line.startsWith("v ") || line.startsWith("vt")) {
                            bufferedWriter.write(readLine(line));
                            bufferedWriter.newLine();
                        } //if not, it is a line that describes a face, which can not be rounded
                        else {
                            bufferedWriter.write(line);
                            bufferedWriter.newLine();
                        }

                    }
                }

                // close files.
                bufferedReader.close();
                bufferedWriter.close();

            } catch (FileNotFoundException ex) {
                System.out.println(
                        "Unable to open file '"
                        + fileName + "'");
            } catch (IOException ex) {
                System.out.println(
                        "Error reading file '"
                        + fileName + "'");
            }
        }
    }

    //the readLine method which reads in a string, splits it up into the x,y,z coordinates of a vertice, rounds them, and returns the rounded vertice
    private static String readLine(String line) {
        String[] s = line.split(" ");
        Double[] nums = new Double[3];
        String str;
        //if the line is a vertice, not a textured vertice
        if (line.length() > 25) {
            try {
                //reads vertices coordinates, converts it to a float and rounds it to 2 decimal places
                for (int i = 1; i <= 3; i++) {
                    nums[i - 1] = Double.parseDouble(s[i]);
                    nums[i - 1] = Double.parseDouble(new DecimalFormat("#.##").format(nums[i - 1]));
                }

                str = s[0] + " " + Double.toString(nums[0]) + " " + Double.toString(nums[1]) + " " + Double.toString(nums[2]);//the rounded vertice line
                return str;
            } catch (Exception e) {
                System.out.println("GL_OBJ_Reader.read3Floats(): error on line '" + line + "', " + e);
                return null;
            }
        } //if the line is a textured vertice, not a normal vertice
        else {
            try {
                //reads vertices coordinates, converts it to a float and rounds it to 2 decimal places
                for (int i = 1; i <= 2; i++) {
                    nums[i - 1] = Double.parseDouble(s[i]);
                    nums[i - 1] = Double.parseDouble(new DecimalFormat("#.##").format(nums[i - 1]));
                }

                str = s[0] + " " + Double.toString(nums[0]) + " " + Double.toString(nums[1]);//the rounded vertice line
                return str;
            } catch (Exception e) {
                System.out.println("GL_OBJ_Reader.read3Floats(): error on line '" + line + "', " + e);
                return null;
            }
        }


    }
}