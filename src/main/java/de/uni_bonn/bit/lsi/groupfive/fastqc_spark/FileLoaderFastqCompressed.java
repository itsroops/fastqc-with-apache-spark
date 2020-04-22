package de.uni_bonn.bit.lsi.groupfive.fastqc_spark;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import org.apache.spark.api.java.JavaRDD;

public class FileLoaderFastqCompressed {
	
	public List<JavaRDD<String>> read(String filename) throws IOException{
	
		List<JavaRDD<String>> listFullRdd = new ArrayList<>();
		
		String decompressed_file = null;
		
		if(filename.contains(".gz"))
			decompressed_file = convert_gz(filename);
		else if(filename.contains(".bz2"))
			decompressed_file = convert_bz2(filename);

	      
	     JavaRDD<String> fullRdd = App.sc.textFile(decompressed_file);
	 	
	 				
	 		  
	     // Sequence Rdd
	 		
	 		JavaRDD<String> seqRdd = fullRdd.filter(line -> 
	 		line.matches("[ATGC]*") && !(line.startsWith("@") || line.startsWith("+")));
	 		
	 		listFullRdd.add(seqRdd);
	         
	 		//int length = seqRdd.first().length();
	 		   
	 	// Quality Rdd 
	 	   
	 		JavaRDD<String> qualityRdd = fullRdd.filter(line -> 
	 		!line.matches("[ATGC]*") && !(line.startsWith("@") || line.startsWith("+")));
	 		
	 		
	 		listFullRdd.add(qualityRdd);
	 		
	        
	 		return listFullRdd;
	 	
	 	
	 	}	
	 	

	 	private String convert_bz2(String filename) throws IOException {
	 		FileInputStream in = new FileInputStream(filename);
	 		String decompressed_file = filename.replace(".bz2", ".txt");
	 		FileOutputStream out = new FileOutputStream(decompressed_file);
	 		BZip2CompressorInputStream bzIn = new BZip2CompressorInputStream(in);
	 		final byte[] buffer = new byte[1024];
	 		int n = 0;
	 		while (-1 != (n = bzIn.read(buffer))) {
	 		 out.write(buffer, 0, n);
	 		}
	 		out.close();
	 		bzIn.close();
	
		return decompressed_file;
	}


		private String convert_gz(String filename) throws IOException {
		
	 		String decompressed_file = filename.replace(".gz",".txt");
	 		  
			byte[] buffer = new byte[1024];
	       
			FileInputStream fileIn = new FileInputStream(filename);
			 
	        GZIPInputStream gZIPInputStream = new GZIPInputStream(fileIn);

	        FileOutputStream fileOutputStream = new FileOutputStream(decompressed_file);

	        int bytes_read;

	        while ((bytes_read = gZIPInputStream.read(buffer)) > 0) {

	            fileOutputStream.write(buffer, 0, bytes_read);
	        }

	        gZIPInputStream.close();
	        fileOutputStream.close();
	 		
	 		
		return decompressed_file;
	}


	 }

		
		
		
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

