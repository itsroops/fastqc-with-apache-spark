package de.uni_bonn.bit.lsi.groupfive.fastqc_spark;

import java.text.DecimalFormat;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;

public class App {

static SparkConf conf = new SparkConf().setAppName("MySpark").setMaster("local[*]");
static JavaSparkContext sc = new JavaSparkContext(conf);

	 
public static void main( String[] args ) throws Exception {
	
	double start = System.currentTimeMillis() / 60000.0;
    
    DisplaySpark d = new DisplaySpark();  
    
    d.display(args[0], args[1]);
   
    double finish = System.currentTimeMillis() / 60000.0;
    
    DecimalFormat format = new DecimalFormat("##.00");	 
    
    System.out.println("Time elapsed = " +format.format(finish - start) + " mins.");
    
    sc.close();
    
    }
}
