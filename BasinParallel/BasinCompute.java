package BasinParallel;
import java.util.concurrent.ForkJoinPool; 
import java.util.concurrent.RecursiveTask;
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

public class BasinCompute{
    static long startTime = 0;
    static final ForkJoinPool pool = new ForkJoinPool();
    
    private static void tick(){
        startTime = System.currentTimeMillis();
    }

    private static float tock(){
        return (System.currentTimeMillis() - startTime) / 1000.0f;
    }

    private static void printUsage(){
      
        System.out.println("Usage: BasinCompute [file:in] [file:out]");
    }

    public static void main(String[] args){
        FileReader reader = null;
        FileWriter writer = null;
        BufferedReader br = null;
 
        List<String> basin = new ArrayList<String>();
        List<Float> compare = new ArrayList<Float>();
        List<Float> average = new ArrayList<Float>();

        float[][] matrix = null;
        int rows, columns = 0;

        if(args.length < 2){
            printUsage();
            return;
        }
        try{
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
           

            rows = Integer.parseInt(token[0]);
            columns = Integer.parseInt(token[1]);

            matrix = new float[rows][columns];
            String data = br.readLine();
            String[] array = data.split(" ");

            for(int i = 0; i < rows; i++){
                for(int j = 0; j < columns; j++){  
                    String value = array[(i*rows) + j];            
                    matrix[i][j] = Float.parseFloat(value);
                }
            }

            System.gc();

            for(int i = 0; i < 50; i++){
            tick();
            basin =  pool.invoke(new BasinRecursive(matrix, 0, rows));
            float deltaTime = tock();
            if(i > 10){
            average.add(deltaTime);
            }
           
            }

            size = basin.size();
            float avg = 0;
            for(int p = 0; p < average.size(); p++){
                avg += average.get(p);
            }

            avg /= average.size();
            System.out.print(avg + " ms\n");

            String sizeString = Integer.toString(size);
            writer.write(sizeString + '\n');
            
            for(int i = 0; i < size; i++){
                writer.write( basin.get(i) + '\n' );
            }

            writer.close();

    }catch(Exception ex){

        System.out.print(ex);

    }
}

}