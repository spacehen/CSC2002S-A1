import java.io.BufferedReader;
import java.io.Console;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;



public class BasinSequential {
    static long startTime = 0;

    // Timer Functions
    private static void tick(){
        startTime = System.currentTimeMillis();
    }

    private static float tock(){
        return (System.currentTimeMillis() - startTime) / 1000.0f;
    }

    // Display App Usage
    private static void printUsage(){
      
        System.out.println("Usage: BasinSequential [file:in] [file:out]");
    }

    
    public static void main(String[] args){
        // Local definitions
        FileReader reader = null;
        FileWriter writer = null;
        BufferedReader br = null;
 
        // ArrayLists to hold basins and compare values
        List<String> basin = new ArrayList<String>();
        List<Float> compare = new ArrayList<Float>();

        // Master matrix index (to be processed by classifier)
        float[][] matrix = null;
        int rows, columns = 0;

        if(args.length < 2){
            printUsage();
            return;
        }
        try{
            // Open reader and writer 
            reader = new FileReader(new File(args[0]));
            writer = new FileWriter(args[1]);
        } 
        catch(Exception ex){
            System.out.println(ex.toString());
            return;
        }

        br = new BufferedReader(reader);

        try{

            int z = 0;
            int size = 0;
            String init = br.readLine();
            String[] token = init.split(" ");

            // Get rows and columns from input file
            rows = Integer.parseInt(token[0]);
            columns = Integer.parseInt(token[1]);

            // store file contents into matrix
            matrix = new float[rows][columns];
            String data = br.readLine();
            String[] array = data.split(" ");

            for(int i = 0; i < rows; i++){
                for(int j = 0; j < columns; j++){  
                    String value = array[(i*rows) + j];            
                    matrix[i][j] = Float.parseFloat(value);
                }
            }

            System.out.println("Running 50 times....");
            System.out.println("Results saved on last pass...");

            // run classifier
            for(int l = 0; l < 50; l++){

            tick();
            basin.clear();
            // iterate over rows/columns
            for(int i = 1; i < rows-1; i++){
                for(int j = 1; j < columns-1 ; j++){
                    float center = matrix[i][j];
                    float delta = 0.01f;
                    
                    compare.clear();

                    // add comparison values
                    compare.add( matrix[i-1][j] - center );
                    compare.add( matrix[i+1][j] - center );

                    compare.add( matrix[i][j-1] - center );
                    compare.add( matrix[i][j+1] - center );

                    compare.add( matrix[i-1][j-1] - center );
                    compare.add( matrix[i-1][j+1] - center );
                    compare.add( matrix[i+1][j-1] - center );
                    compare.add( matrix[i+1][j+1] - center );

                    size = compare.size();
                    // if a comparisons fails do not add basin
                    for(z = 0; z < size; z++){
                        if(compare.get(z) <= delta){
                            break;
                        }
                    }
                    // add basin if compare successful (z== size)
                    if( z == size){
                         basin.add(i + " " + j);
                     }
                }
            }
            float deltaTime = tock();

            System.out.print(deltaTime + " ms\n");
        }

            // write basins
            size = basin.size();
            String sizeString = Integer.toString(size);
            writer.write(sizeString + '\n');
            
            for(int i = 0; i < size; i++){
                writer.write( basin.get(i) + '\n' );
            }

            writer.close();

        }catch(IOException ex){
            return;
        }
    }
}