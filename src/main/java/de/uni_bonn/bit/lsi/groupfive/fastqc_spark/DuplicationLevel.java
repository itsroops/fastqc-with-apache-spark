package de.uni_bonn.bit.lsi.groupfive.fastqc_spark;

import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;

import scala.Tuple2;

public class DuplicationLevel {
	
	// takes sequences Rdd as input and returns a pair Rdd (duplication number, its percent
        // within the whole file
	public static JavaPairRDD<Integer,Double> dup(JavaRDD<String> seqRdd) {
	
	//pair rdd to get each sequence count (map -> reduce by key)
	JavaPairRDD<String,Integer> dupRdd = seqRdd.mapToPair(seq -> 
	   new Tuple2<String,Integer>(seq,1)).reduceByKey((va1,va2) -> va1+va2);
	 
	//pair rdd maps each sequence count to its count (map -> reduce by key)
	 JavaPairRDD<Integer, Integer> dupCountRdd = dupRdd.mapToPair(tup ->
	   new Tuple2<Integer,Integer>(tup._2,1)).reduceByKey((va1,va2) -> va1+va2);
	 int temp_count=(int) dupRdd.count();
	 
	// map original sequence counts to their percentsges and sorting
	 JavaPairRDD<Integer,Double> dupPercentRdd = dupCountRdd.mapToPair(t ->
	  new Tuple2<Integer,Double>(t._1,(t._2*100.0/temp_count))).sortByKey();
	 
	 
	 return dupPercentRdd;

}
}