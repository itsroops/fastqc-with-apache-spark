package de.uni_bonn.bit.lsi.groupfive.fastqc_spark;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.spark.api.java.JavaRDD;



public class perBaseSequenceQuality {
	
	static JavaRDD<Double> meanRdd;

	// A function to get an rdd of quality score lists of each base position from an
		// array of read objects

		 static JavaRDD<List<Double>> perBaseQuality(JavaRDD<String> qualityRdd, int rddCount) {
		 // get the qualities for each base and sum up (map -> reduce)
		  JavaRDD<List<Integer>> qualityScoreRdd = qualityRdd.map(line -> qualityScore(line));
	
		  List<Integer> sumPerBase = qualityScoreRdd.reduce((value1,value2) -> 
		    sum_per_base(value1,value2));
		    meanRdd = App.sc.parallelize(sumPerBase)  //parallelize and divide by reads counts to get means
		    		.map(sum ->(double) sum/rddCount);
		  
		    // sort the quality score rdd to get percentiles	   
		    List<List<Integer>> listList = new ArrayList<>();
		    List<Integer> vList = new ArrayList<>();
		   for(int i=0;i<150; i++) {
			   int x = i;
			 listList.add(qualityScoreRdd.map(value -> value.get(x)).collect().stream().sorted(Comparator.naturalOrder()).collect(Collectors.toList()));
			 
		   }
			//parallelize to calculate percentiles
		   JavaRDD<List<Double>> rddRdd = App.sc.parallelize(listList).map(val -> calculatePercentiles(val));
		return rddRdd;
		   
		     
		  
	
}
		 //A function to return to a list of score for each position
		 public static List<Integer> qualityScore(String line) {
				
				List<Integer> qualityList = new ArrayList<>();
				
				for(int i=0;i<line.length();i++)
					qualityList.add(line.charAt(i) - 33);
				return qualityList;
			}
		 // a function to get sum of scores of each position (reduce) (given 2 lists and add value by value)
		 private static List<Integer> sum_per_base(List<Integer>  value1, List<Integer>  value2) {
				
				List<Integer> temp = new ArrayList<>();
				for(int i=0;i<value1.size();i++)
					temp.add(value1.get(i) + value2.get(i));
				return temp;
				
			}
		 // The main calculations
		 private static List<Double> calculatePercentiles(List<Integer> list) {
				double sum = 0.0;
				int count = list.size();
				List<Double> PercentilesList = new ArrayList<>();
				for (int i = 0; i <count;i++) {
					sum = sum + list.get(i);  // getting the sum of scores for calculating means
				}
				PercentilesList.add(sum/count);
				double[] percentiles = {0.1,0.25,0.5, 0.75, 0.9}; //percentiles list to calculate
				for (double j: percentiles){
					double value;
					if((((count+1)*j)%1)==0) { //if percentile is integer 
						
						value = list.get((int) ((count+1)*j)-1);  // add the direct index value
					}
				
					else if ((((count+1)*j)%1)==0.5) { // if percentile = .5
					    value = (list.get((int) ((count+1)*j)-1) + list.get((int) ((count+1)*j)))/2.0;
					}   // add the average value of the floor and the ceiling indices values
					else { // if double
						value = list.get((int) Math.round(((count+1)*j)-1)); //add nearest index value
					}
					PercentilesList.add(value);   //list of all percentiles
				}
					
				return PercentilesList;	
			}
		 
		 
}