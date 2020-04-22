package de.uni_bonn.bit.lsi.groupfive.fastqc_spark;

import java.util.ArrayList;
import java.util.List;

import org.apache.spark.api.java.JavaRDD;

public class FileLoaderFastq {
	
		
	public List<JavaRDD<String>> read(String filename){
	
	List<JavaRDD<String>> listFullRdd = new ArrayList<>();
	
	JavaRDD<String> fullRdd = App.sc.textFile(filename);
	
   
		  
    // Sequence Rdd
		
		JavaRDD<String> seqRdd = fullRdd.filter(line -> 
		line.matches("[ATGC]*") && !(line.startsWith("@") || line.startsWith("+")));
		
		listFullRdd.add(seqRdd);
        
		//int length = seqRdd.first().length();
		   
	// Quality Rdd 
	   
		JavaRDD<String> qualityRdd = fullRdd.filter(line -> 
		!line.matches("[ATGC]*") && !(line.startsWith("@") || line.startsWith("+")));
		
		
		listFullRdd.add(qualityRdd);
		return listFullRdd;
	
	
	}	
	


	
}
