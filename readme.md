# Implementing Fastqc functionalities using Apache Spark

## About:

FastQC is an application which takes a FastQ file and runs a series
of tests on it to generate a report.  Apache-Spark is an useful way to scale up the processing time of big sequence files in a short period of time.

We have implemented the following functionalities (By referring to the original FastQC textfile output) using the map-reduce approach of Apache Spark in local standalone mode.
`ToDo : Deployment in HPC Clusters `

**1. Basic Statistics** - This module gives the number of sequences in the file and the length of a sequence.

**2. Per Base Sequence Quality** - This module calculates the mean and 10th percentile, lower quartile, median, upper quartile and the 90th percentile per base position of the whole sequence.

**3. Per Sequence Quality Score** - This module calculates the mean of qualities of each sequence and display the frequency of those means. 

**4. Per Base Sequence Content** - This module calculates the percentage of every base (A,T,G,C) in each base position within the whole sequence.

**5. Per Sequence GC content** - This module calculate the percentage of G and C combined for each sequence and calculates the frequencies of those percentages.

**6. Per base N Content** - This module calculates the percentage of 'N' in each base position within the whole sequence.

**7. Duplication Level** - This module calculates the frequencies of each sequence and the percentage of each frequency.

**8. Overrepresented subsequences** - This module calculates the top 6 k-mers(of length 5) and their positions with the frequency of each position.

## Steps to execute:

The project is developed using Maven and the executable JAR is found in the "$projectpath/target". The JAR accepts two command line arguments
namely, the input file and the output filepath to store the results. 

It can be executed as follows:

**java -jar /target/fastqc_spark-0.0.1-SNAPSHOT-jar-with-dependencies.jar *INPUT_FILE* *OUTPUT_FILE***

Please make sure that a stable version of Java 8 is installed in your system. Evenn though the program uses Apache Spark for computation, explicit installation of the same is not required as the JAR file aboe takes care of all the dependencies and download them for the execution of the program.



### Unused Source Codes:

1. SeqChunk.java : This file is no longer used as the approach for development has changed during the course of this project. However, it is kept as a normal pojo class for future developments.

2. FileLoaderv2.java : This file is originally tested which loads the RDDs in a read by read fashion. However, this is now replaced by other loader files which loads the input a textfile in the Spark. 

3. FileLoaderv3.java : This file loads into spark an RDD of objects when SeqChunk was initially used. However, with the implementation of other techiques, this is no longer used. However it might be useful for future developments. 

