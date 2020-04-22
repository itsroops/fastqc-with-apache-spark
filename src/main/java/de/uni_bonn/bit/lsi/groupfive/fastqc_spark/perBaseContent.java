package de.uni_bonn.bit.lsi.groupfive.fastqc_spark;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.spark.api.java.JavaRDD;

public class perBaseContent {
	// a class method to return an rdd of the percentages of A,G,T,C,N within each base position of the given sequence rdd 
	public static JavaRDD<List<Double>> atgcnContent(JavaRDD<String> seqRdd, int rddCount) {
		// map sequences to per base counts of each base 
		JavaRDD<List<List<Integer>>> perBaseATGCRdd = seqRdd.map(line -> ATGC_per_base(line));
		// reduce and sum all lists of each position to get counts
	    List<List<Integer>> sumPerBaseATGC = perBaseATGCRdd.reduce((value1,value2) -> sum_ATGC_per_base(value1,value2));
	    List<List<Double>> perBasePercent = new ArrayList<>();
	    for(List<Integer> l : sumPerBaseATGC) {
	        List<Double> percentList = new ArrayList<>();
	    	for(int i=0;i<l.size();i++) {
	    		percentList.add((double) (((int)l.get(i)*100)/rddCount));  //a list of percentages for each base position
	    	}
	    	perBasePercent.add(percentList); //total list of lists
	    }
	    // parallelize into an rdd and return
	    JavaRDD<List<Double>> perBasePercentRdd = App.sc.parallelize(perBasePercent);
	    
		return perBasePercentRdd;
	
		
	}
	
	// a fuction to return the bases counts for each position of the given sequence
	private static List<List<Integer>> ATGC_per_base(String line) {
		 List<List<Integer>> base_count = new ArrayList<>();
		 
			
			for(int i=0;i<line.length();i++) {
				if(line.charAt(i) == 'A')
					base_count.add(Arrays.asList(1,0,0,0,0)); // each position in the list refers to a certain base
				else if(line.charAt(i) == 'T')
					base_count.add(Arrays.asList(0,1,0,0,0));
				else if(line.charAt(i) == 'G')
					base_count.add(Arrays.asList(0,0,1,0,0));
				else if(line.charAt(i) == 'C')
					base_count.add(Arrays.asList(0,0,0,1,0));
				else
					base_count.add(Arrays.asList(0,0,0,0,1));
			}

			return base_count;
			
			}

		// a function to sum each base count at each position 
		private static List<List<Integer>> sum_ATGC_per_base(List<List<Integer>> value1, List<List<Integer>> value2) {
			List<List<Integer>> atgcn_count = new ArrayList<>();
			for(int i=0;i<value1.size();i++) {
				atgcn_count.add(Arrays.asList(value1.get(i).get(0) + value2.get(i).get(0), 
						                     value1.get(i).get(1) + value2.get(i).get(1),
											 value1.get(i).get(2) + value2.get(i).get(2),
											 value1.get(i).get(3) + value2.get(i).get(3),
											 value1.get(i).get(4) + value2.get(i).get(4)));
			}
			
			return atgcn_count;
		}
	

}