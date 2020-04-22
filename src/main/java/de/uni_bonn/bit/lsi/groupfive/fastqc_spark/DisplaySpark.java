package de.uni_bonn.bit.lsi.groupfive.fastqc_spark;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;

import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;

import com.sun.tools.javac.util.Pair;

import scala.Tuple2;

import org.apache.commons.compress.compressors.CompressorException;

public class DisplaySpark {

	
public void display(String filename, String output) throws IOException, CompressorException {

	BufferedWriter writer = new BufferedWriter(new FileWriter(output));
	FileLoader fl = new FileLoader();

List<JavaRDD<String>> file = new ArrayList<>();

file = fl.read(filename);

//Basic Statistics
writer.write("Module : Basic Statistics:\n");
writer.write("Filename: "+ filename + "\n");
writer.write("Total Sequences: "+ BasicStats.basicCount(file).get(0) + "\n");
writer.write("Sequence Length: "+ BasicStats.basicCount(file).get(1) + "\n");



//per base sequence quality
writer.write("\n");
writer.write("Per Base Sequence Quality\n");
writer.write("Base  Mean    10%    LQ    Median   UQ   90%");
writer.write("\n");

JavaRDD<List<Double>> rddRdd = perBaseSequenceQuality.perBaseQuality(file.get(1), BasicStats.basicCount(file).get(0));

List<List<Double>> lrddRdd = rddRdd.collect();

for (int i=0;i<lrddRdd.size();i++)
	writer.write(i+1 + "   " + lrddRdd.get(i)+"\n");


//Per sequence quaity scores
writer.write("\n");
writer.write("Per Sequence Quality Score");
writer.write("\n");
writer.write("Mean Quality  Count");
writer.write("\n");

JavaPairRDD<Integer, Long> qualityCountRdd = perSequenceQualityScore.perSeqQuality(file.get(1));

List<Tuple2<Integer, Long>> lqualityCountRdd = qualityCountRdd.collect();


for(Tuple2<Integer, Long> t : lqualityCountRdd)
writer.write( t._1 + " \t" +"\t"+ t._2 +"\n");


//per base content
writer.write("\n");
writer.write("Per Base Sequence Content");
writer.write("\n");
writer.write("Base    A  " +"    T  " + "      G  " + "      C ");
writer.write("\n");

JavaRDD<List<Double>> ATGCN_CountRdd = perBaseContent.atgcnContent(file.get(0), BasicStats.basicCount(file).get(0));
List<List<Double>> lATGCN_CountRdd = ATGCN_CountRdd.collect();
int i1=1;
for(List<Double> l : lATGCN_CountRdd)
writer.write(i1++ +"   "+  l.subList(0,4) +"\n");


//per sequence GC content
writer.write("\n");
writer.write("Per Sequence GC Content");
writer.write("\n");
writer.write("GC%     count");
writer.write("\n");
JavaPairRDD<Integer,Integer> perSeqGCRdd  = 
perSequenceGCContent.GC_content(file.get(0), BasicStats.basicCount(file).get(1));

List<Tuple2<Integer, Integer>> lperSeqGCRdd = perSeqGCRdd.collect();
for(Tuple2<Integer, Integer> t : lperSeqGCRdd) 
writer.write( t._1 + " " +"\t" + t._2 +"\n");



//Per sequence N content
writer.write( "\n");
writer.write("Base   N content");
writer.write( "\n");
int i2=1;
for(List<Double> l : lATGCN_CountRdd)
writer.write(i2++ +"     " + l.get(4) +"\n");




//Duplication level
writer.write("\n");
writer.write("Duplication level");
writer.write("\n");
writer.write("duplication level   %");
writer.write("\n");

JavaPairRDD<Integer, Double> DuplicationRdd  = DuplicationLevel.dup(file.get(0));
List<Tuple2<Integer,Double>> lDuplicationRdd = DuplicationRdd.collect();
for(Tuple2<Integer, Double> t : lDuplicationRdd)
writer.write("  "+ t._1 + " \t" +"\t" + t._2 +"\n");


//Kmer analysis
writer.write("\n");
writer.write("Over represented sub-sequences");
writer.write("\n");

JavaPairRDD<String,LinkedHashSet<Tuple2<Integer,Integer>>> kmerAnalysisRDD = 
OverrepresentedSequences.getKmers(file.get(0));
List<Tuple2<String,LinkedHashSet<Tuple2<Integer,Integer>>>> lkmerAnalysisRDD= kmerAnalysisRDD.collect();

for(Tuple2<String, LinkedHashSet<Tuple2<Integer, Integer>>> t : lkmerAnalysisRDD)
writer.write( t._1 + "    " + t._2 +"\n\n");


writer.close();



}


}
