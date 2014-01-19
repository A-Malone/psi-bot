/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package glmodel;

import java.io.*;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class Test {
    public static ArrayList vertices = new ArrayList();  // Contains float[3] for each Vertex (XYZ)

    public static void main(String[] args) {

        // The name of the file to open.
        String fileName = "res/models/run/run1.obj";
        String fileWriteName = "C:/Users/Victor/Desktop/run1copy.obj";

        // This will reference one line at a time
        String line = null;

        try {
            FileWriter fileWriter =
                    new FileWriter(fileWriteName);

            // Always wrap FileWriter in BufferedWriter.
            BufferedWriter bufferedWriter =
                    new BufferedWriter(fileWriter);

            // Note that write() does not automatically
            // append a newline character.


            // FileReader reads text files in the default encoding.
            FileReader fileReader =
                    new FileReader(fileName);

            // Always wrap FileReader in BufferedReader.
            BufferedReader bufferedReader =
                    new BufferedReader(fileReader);

            while ((line = bufferedReader.readLine()) != null) {
                line = line.trim();
				line = line.replaceAll("  ", " ");
				if (line.length() > 0) {
                //System.out.println(line);
                if (line.startsWith("v ")) {
                    // vertex coord line looks like: v 2.628657 -5.257312 8.090169 [optional W value]
                    vertices.add(read3Floats(line));
                } else if (line.startsWith("vt")) {
                    // texture coord line looks like: vt 0.187254 0.276553 0.000000
                   // textureCoords.add(read3Floats(line));
                } else if (line.startsWith("vn")) {
                    // normal line looks like: vn 0.083837 0.962494 -0.258024
                  //  normals.add(read3Floats(line));
                }
                                }
                //  bufferedWriter.write(line);
                // bufferedWriter.newLine();

            }

            // Always close files.
            bufferedReader.close();
            // Always close files.
            bufferedWriter.close();
            
            for(int i=0;i<vertices.size();i++){
             //  String s = (String)vertices.get(i);
                System.out.println(vertices.get(i).toString());
                
            }
        } catch (FileNotFoundException ex) {
            System.out.println(
                    "Unable to open file '"
                    + fileName + "'");
        } catch (IOException ex) {
            System.out.println(
                    "Error reading file '"
                    + fileName + "'");
            // Or we could just do this: 
            // ex.printStackTrace();
        }
    }

   private static float[] read3Floats(String line)
	{
		try
		{
			StringTokenizer st = new StringTokenizer(line, " ");
			st.nextToken();   // throw out line marker (vn, vt, etc.)
			if (st.countTokens() == 2) { // texture uv may have only 2 values
				return new float[] {Float.parseFloat(st.nextToken()),
									Float.parseFloat(st.nextToken()),
									0};
			}
			else {
				return new float[] {Float.parseFloat(st.nextToken()),
									Float.parseFloat(st.nextToken()),
									Float.parseFloat(st.nextToken())};
			}
		}
		catch (Exception e)
		{
			System.out.println("GL_OBJ_Reader.read3Floats(): error on line '" + line + "', " + e);
			return null;
		}
	}

}