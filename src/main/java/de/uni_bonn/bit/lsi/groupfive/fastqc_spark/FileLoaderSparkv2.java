package de.uni_bonn.bit.lsi.groupfive.fastqc_spark;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;

public class FileLoaderSparkv2 {

public List<JavaRDD> read(String filename) throws IOException{
		
SparkConf conf = new SparkConf().setAppName("MySpark").setMaster("local[*]");
JavaSparkContext sc = new JavaSparkContext(conf);
ArrayList<SeqChunk> Reads = new ArrayList<SeqChunk>();
	
BufferedReader br = new BufferedReader(new FileReader(filename)); 
int ln=0;
String line;
String[] chunk = new String[4]; 
List<String> idList= new ArrayList<>();
List<String> seqList= new ArrayList<>();
List<String> qualityList= new ArrayList<>();

List<JavaRDD> listFullRdd = new ArrayList<>();

while ((line = br.readLine()) != null) {
	chunk[ln]=line;
    ln++;
    if(ln==4) {
            	idList.add(chunk[0]);
            	seqList.add(chunk[1]);
            	qualityList.add(chunk[3]);
                     ln=0;
               }
       }
         br.close();
         
        JavaRDD<String> idRdd = sc.parallelize(idList);
        JavaRDD<String> seqRdd = sc.parallelize(seqList);
        JavaRDD<String> qualityRdd = sc.parallelize(qualityList);
        
        JavaRDD<List<Integer>> qualityScoreRdd = qualityRdd.map(val -> qualityScore(val));
  
        listFullRdd.add(idRdd);
        listFullRdd.add(seqRdd);
        listFullRdd.add(qualityRdd);
        
        return listFullRdd;
      
}

private  static List<Integer> qualityScore(String line) {
	
	List<Integer> qualityList = new ArrayList<>();
	
	for(int i=0;i<line.length();i++)
		qualityList.add(line.charAt(i) - 33);
	return qualityList;
}


}
        