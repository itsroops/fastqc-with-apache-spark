package de.uni_bonn.bit.lsi.groupfive.fastqc_spark;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.compress.compressors.CompressorException;
import org.apache.spark.api.java.JavaRDD;




public class FileLoader {
	
	List<JavaRDD<String>> inputRddList = new ArrayList<>();
	
	public List<JavaRDD<String>> read(String filename) throws IOException, CompressorException { 
	 
		if(filename.endsWith(".fastq.gz") || filename.endsWith(".fastq.bz2") || filename.endsWith(".txt.gz") ||
				   filename.endsWith(".txt.bz2") || filename.endsWith(".fq.gz") || filename.endsWith(".fq.bz2") || 
				   (!(filename.substring(0,filename.length()-3)).contains(".") && filename.endsWith(".gz"))) {
		
		    inputRddList = new FileLoaderFastqCompressed().read(filename);
		 
		 }
		
		else if(filename.endsWith(".fastq") || filename.endsWith(".fq") || filename.endsWith(".txt") || !filename.contains(".")){
			
			inputRddList = new FileLoaderFastq().read(filename);
				
		}
		
		   
		else if(filename.endsWith(".sam.gz") || filename.endsWith(".sam.bz2"))  {
			 
			inputRddList = new FileLoaderSamCompressed().read(filename);
			
		}
			
		else if(filename.endsWith(".sam")) {
			
			inputRddList = new FileLoaderSam().read(filename);
			
					
			}
		
		else
		{
			 System.out.println("Invalid Filename entered...Exiting the JVM.."); 
		     System.exit(0);
	 	 	

		}
		
		return inputRddList;
	
	
	}
}
