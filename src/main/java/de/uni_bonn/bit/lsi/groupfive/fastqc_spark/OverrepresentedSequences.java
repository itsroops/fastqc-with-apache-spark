package de.uni_bonn.bit.lsi.groupfive.fastqc_spark;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFlatMapFunction;
import org.apache.spark.broadcast.Broadcast;

import scala.Tuple2;

public class OverrepresentedSequences {
	
	public static  JavaPairRDD<String,LinkedHashSet<Tuple2<Integer,Integer>>> getKmers(JavaRDD<String> seqRdd) {
	
	
	   int K = 5;    // k-mer length
	   int N = 6;    // top k-mers count

		// broadcast K and N as global shared objects,
		// which can be accessed from all cluster nodes
		final Broadcast<Integer> broadcastK = App.sc.broadcast(K);
		final Broadcast<Integer> broadcastN = App.sc.broadcast(N);

		//pair rdd for kmer and count
		JavaPairRDD<String, Integer> kmers = seqRdd.flatMapToPair
				(new PairFlatMapFunction<String,String,Integer>() {
			@Override
			public Iterator<Tuple2<String, Integer>> call(String sequence) {
				int K = broadcastK.value();
				List<Tuple2<String, Integer>> list = new ArrayList<Tuple2<String, Integer>>();                      // generate all kmers of length k
				for (int i = 0; i < sequence.length() - K + 1; i++) {
					String kmer = sequence.substring(i, K + i);
					list.add(new Tuple2<String, Integer>(kmer, 1));
				}   //map each kmer to 1 in a list of tuples
				return list.iterator();
			}
		});
		
		//reduce by key to get counts
		JavaPairRDD<String, Integer> kmersGrouped = kmers.reduceByKey(new Function2<Integer, Integer, Integer>() {
			@Override
			public Integer call(Integer i1, Integer i2) {
				return i1 + i2;
			}
		});
		
		// sorting kmer according to count
		JavaRDD<SortedMap<Integer, String>> partitions = kmersGrouped
				.mapPartitions(new FlatMapFunction<Iterator<Tuple2<String, Integer>>, SortedMap<Integer, String>>() {
					@Override
					public Iterator<SortedMap<Integer, String>> call(Iterator<Tuple2<String, Integer>> iter) {
						int N = broadcastN.value();  //get the top N kmers
						SortedMap<Integer, String> topN = new TreeMap<Integer, String>();
						while (iter.hasNext()) {
							Tuple2<String, Integer> tuple = iter.next();
							String kmer = tuple._1;
							int frequency = tuple._2;
							topN.put(frequency, kmer); // k , v
							if (topN.size() > N) {
						// order by removing the least key(frequency) 
								topN.remove(topN.firstKey());
							}
						}
						// print the top N list
						//System.out.println("topN=" + topN);
						return Collections.singletonList(topN).iterator();
					}
				});

		
		// collect and find topN kmers from all partitions
		SortedMap<Integer, String> finaltopN = new TreeMap<Integer, String>();
		List<SortedMap<Integer, String>> alltopN = partitions.collect();
		for (SortedMap<Integer, String> localtopN : alltopN) {
			//(frequency,kmer)
			for (Map.Entry<Integer, String> entry : localtopN.entrySet()) {
				finaltopN.put(entry.getKey(), entry.getValue());
				// get max count
				if (finaltopN.size() > N) {
					finaltopN.remove(finaltopN.firstKey());
				}
			}
		}


		List<Integer> frequencies = new ArrayList<Integer>(finaltopN.keySet());
		for (int i = frequencies.size() - 1; i >= 0; i--) {
			// System.out.println(frequencies.get(i) + "\t" +
			// finaltopN.get(frequencies.get(i)));
		}
		
		List<String> topKmersList = new ArrayList<>();
		for (Map.Entry<Integer, String> entry : finaltopN.entrySet()) {
			//print the kmers and their counts
			// System.out.println(entry.getValue() + ":" + entry.getKey());
			topKmersList.add(entry.getValue());  // add top kmers to list
		}
		// parallelize the list in an rdd to get positions
		JavaRDD<String> topKmersRdd = App.sc.parallelize(topKmersList);
		// extract positions of each kmer from the sequence rdd
		JavaRDD<List<Tuple2<String,List<Integer>>>> topKmerPosRdd = seqRdd.map(seq ->
		 extractKmerPos(seq,topKmersList)); List<List<Tuple2<String, List<Integer>>>>
		 collect = topKmerPosRdd.collect(); //collect kmers, positions in a list of lists
		 // adding all tuples in one list
		 for (int i=1; i< collect.size(); i++) {
			 collect.get(0).addAll(collect.get(i)); } 
//parallelizing to pair rdd to get frequency of each position using map
JavaPairRDD<String,
			 Iterable<List<Integer>>> PosRdd = App.sc.parallelizePairs(collect.get(0))
			 .groupByKey(); JavaPairRDD<String, List<Integer>> kmerPosRdd =
			 PosRdd.mapToPair(t -> new
			 Tuple2<String,List<Integer>>(t._1,converToList(t._2))); // convert iterable to a list to get frequncy
 JavaPairRDD<String,
			 LinkedHashSet<Tuple2<Integer,Integer>>> finalKmersRdd = kmerPosRdd.mapToPair(t
			 -> new   
			 Tuple2<String,LinkedHashSet<Tuple2<Integer,Integer>>>(t._1,getFreq(t._2)));
			 // Linked Hash set disallow duplication
			//final pair rdd for each kmer and a set of its positions with their frequencies of repetition
			 
			return finalKmersRdd;
 

	
	}
	
	// a function converting an iterable of lists to one list
	private static List<Integer> converToList(Iterable<List<Integer>> l) {
		
		List<List<Integer>> list = new ArrayList<>(); 
		l.forEach(list::add);
		for (int i=1; i< list.size(); i++) {
			  list.get(0).addAll(list.get(i)); 
			  }
			return list.get(0);
		}
	
	// a function to get the frequency of each position inside the pair rdd value
	private static LinkedHashSet<Tuple2<Integer, Integer>> getFreq(List<Integer> l) {
		LinkedHashSet<Tuple2<Integer,Integer>> hashSet = new LinkedHashSet<>();
		for(int i:l) {
			int freq = Collections.frequency(l,i);
			Tuple2<Integer,Integer> freqTup= new Tuple2<Integer, Integer>(i,freq);
			hashSet.add(freqTup); //(position,its frequency)
		}
		return hashSet; // final set of tuples for each kmer
	}
	
	// a function extracts all positions of a given kmer within each sequence (given the sequence
	// and the kmer as parameters) and returns a list of tuples (kmers,positions) for each sequence 
	private static List<Tuple2<String, List<Integer>>> extractKmerPos(String seq,List<String> list) {
		List<Tuple2<String,List<Integer>>> kmersPos = new ArrayList<>();
		for (String str: list) {
			List<Integer> posList = new ArrayList<>();
			for (int j = 0; j + str.length() <= seq.length(); j++) {
			if (str.equals(seq.substring(j, j + str.length()))) {
				posList.add(j);
				}
			}
			Tuple2<String,List<Integer>> tup = new Tuple2<String,List<Integer>>(str,posList);
			if (tup._2.size()==0) 
				continue;
			else
				kmersPos.add(tup);
		  }
		return 	kmersPos;
	}

}


