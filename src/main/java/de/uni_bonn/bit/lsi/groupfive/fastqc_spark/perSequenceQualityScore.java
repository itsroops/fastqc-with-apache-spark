package de.uni_bonn.bit.lsi.groupfive.fastqc_spark;

import java.util.List;

import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;

import scala.Tuple2;

public class perSequenceQualityScore{
	// generate an rdd of mean qualities per sequence with their counts
	public static JavaPairRDD<Integer, Long> perSeqQuality (JavaRDD<String> qualityRdd) {
	// calculate scores of each sequence and their means 
	JavaRDD<List<Integer>> qualityScoreRdd = qualityRdd.map(line -> perBaseSequenceQuality.qualityScore(line));
	JavaRDD<Integer> perSeqQualityScoreRdd = qualityScoreRdd.map(value -> mean_QS_perseq(value));
	// map and reduce by key to get counts
	JavaPairRDD<Integer, Long> qualityCountRdd = perSeqQualityScoreRdd
    		.mapToPair(val -> new Tuple2<Integer,Long>(val , 1L))
    		.reduceByKey((val1,val2) -> val1+val2).sortByKey();
	return qualityCountRdd;
		

}
//add and get the mean of the scores list per sequence
private static int mean_QS_perseq(List<Integer> value) {
	double count = 0.0; 
	for(int i = 0; i < value.size(); i++) { 
		count = count + value.get(i);
	}
	
		return ((int) Math.round(count/value.size()));
	
}
}