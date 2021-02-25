package BasinParallel;
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

public class BasinRecursive extends RecursiveTask<List<String>>{
    // define row threshold
    static final int ROW_THRESHOLD = 224;
    float[][] matrix;
    int start;
    int end;

    BasinRecursive(float[][] matrix, int start, int end){
        this.matrix = matrix;
        this.start = start;
        this.end = end;
    }

    // add compute to RecursiveTask derived class
    protected List<String> compute(){
        List<String> basin = new ArrayList<String>();
        List<Float> compare = new ArrayList<Float>();
        int rows = this.matrix.length;
        int columns = this.matrix[0].length;
        int z = 0;
        int size = 0;

        // iterate over end-start if less than row threshold
        if( (end-start) < ROW_THRESHOLD ){

            // wrap start and end
            if(start == 0){start = 1;}
            if(end == rows){end -=1;}

            for(int i = start; i < end; i++){
                for(int j = 1; j < columns-1 ; j++){
                    float center = matrix[i][j];
                    float delta = 0.01f;
                    
                    compare.clear();

                    compare.add( matrix[i-1][j] - center );
                    compare.add( matrix[i+1][j] - center );

                    compare.add( matrix[i][j-1] - center );
                    compare.add( matrix[i][j+1] - center );

                    compare.add( matrix[i-1][j-1] - center );
                    compare.add( matrix[i-1][j+1] - center );
                    compare.add( matrix[i+1][j-1] - center );
                    compare.add( matrix[i+1][j+1] - center );

                    size = compare.size();

                    for(z = 0; z < size; z++){
                        if(compare.get(z) <= delta){
                            break;
                        }
                    }

                    if( z == size){
                        basin.add((i + " " + j));
                    }
                }
            }

        } else{
            // fork and join if not less than row threshold
            BasinRecursive top = new BasinRecursive(matrix, start, (start+end)/2);
            BasinRecursive bottom = new BasinRecursive(matrix, (start+end)/2, end);

            top.fork();
            bottom.fork();
            
            basin.addAll( top.join() );
            basin.addAll( bottom.join() );
        }

        return basin;

    }
}