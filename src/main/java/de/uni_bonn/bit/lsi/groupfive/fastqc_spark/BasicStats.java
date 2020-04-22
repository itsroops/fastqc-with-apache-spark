package de.uni_bonn.bit.lsi.groupfive.fastqc_spark;

import java.util.ArrayList;
import java.util.List;

import org.apache.spark.api.java.JavaRDD;

// Basic statistics of the input file
public class BasicStats {
	
	 static List<Integer> basicCount(List<JavaRDD<String>> array) {
			
			List<Integer> count = new ArrayList<>();
			
			long no_of_objects = array.get(0).count();    // count no. of reads
			
			count.add((int) no_of_objects);
		 
			 int seq_len = array.get(0).first().length(); // count length of sequence
			count.add(seq_len);    
			
			return count;         // list of count & length
		
		}

	

}