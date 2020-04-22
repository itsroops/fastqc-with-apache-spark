package de.uni_bonn.bit.lsi.groupfive.fastqc_spark;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.spark.api.java.JavaRDD;

public class FileLoaderSparkv3 {

	 
public static JavaRDD<SeqChunk> read(String filename) throws IOException { 
	ArrayList<SeqChunk> Reads = new ArrayList<SeqChunk>();
	
	 
     BufferedReader br = new BufferedReader(new FileReader(filename)); 
         int ln=0;
         String line;
         String[] chunk = new String[4]; 
         while ((line = br.readLine()) != null) {
             chunk[ln]=line;
             ln++;
             if(ln==4) {
            	     SeqChunk obj = new SeqChunk(chunk[0],chunk[1],chunk[3]);
            	     Reads.add(obj);
                     ln=0;
                     }
             }
         br.close();
         
        JavaRDD<SeqChunk> myRDD = App.sc.parallelize(Reads);
        
        
return myRDD;

}

}