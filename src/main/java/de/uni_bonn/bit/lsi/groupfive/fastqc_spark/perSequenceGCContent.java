package de.uni_bonn.bit.lsi.groupfive.fastqc_spark;

import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;

import scala.Tuple2;

public class perSequenceGCContent {
	// given the sequence rdd and the sequence length and return a pair rdd with GC% to their frequencies
	public static  JavaPairRDD<Integer,Integer>  GC_content(JavaRDD<String> seqRdd, int length) {
		// get the counts and percents of GC for each sequence
    JavaRDD<Integer> GCRdd = seqRdd.map(value -> baseCount(value,'G') 
    		+ baseCount(value,'C'));
    double division = 100.0/length ;
    JavaRDD<Integer> GCPercentRdd = GCRdd.map(value -> (int) Math.round(value*division)); 
   //map each percent to its count
    JavaPairRDD<Integer,Integer> perSeqGCRdd =  GCPercentRdd.mapToPair
    		(value -> new Tuple2<Integer,Integer>(value,1))
    		.reduceByKey((value1,value2) -> value1 + value2).sortByKey();
	
    
    return perSeqGCRdd;  // an rdd of (gc%,count) for each sequence
	


}
	
	// count the given base within the given sequence
	private static Integer baseCount(String seq, char c ) {
		  int count = 0;    
	    
	    for(int i = 0; i < seq.length(); i++) {    
	        if(seq.charAt(i) == c)    
	            count++;    
		
	}
		return count;


	}	
}
