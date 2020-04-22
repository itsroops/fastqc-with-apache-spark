package de.uni_bonn.bit.lsi.groupfive.fastqc_spark;

import java.util.ArrayList;
import java.util.List;

import org.apache.spark.api.java.JavaRDD;

public class FileLoaderSam {
	
	public List<JavaRDD<String>> read(String filename){
		
			List<JavaRDD<String>> listFullRdd = new ArrayList<>();
		
			JavaRDD<String> fullRdd = App.sc.textFile(filename);
		
				  
		    // Sequence Rdd
				
			JavaRDD<String> seqRdd = fullRdd.map(line -> line.split("\t")[9]);
				
			listFullRdd.add(seqRdd);
		        
				   
			// Quality Rdd 
			   
			 JavaRDD<String> qualityRdd = fullRdd.map(line -> line.split("\t")[10]);
				
						
			listFullRdd.add(qualityRdd);
			
			return listFullRdd;
			
			
			}	
			
		
		
}
