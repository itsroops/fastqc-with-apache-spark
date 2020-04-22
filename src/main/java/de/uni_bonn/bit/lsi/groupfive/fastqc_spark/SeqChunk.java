// This is a POJO class containing a read as an object having id, sequence and quality information.
// This is not used in our program but kept as a normal class for future requirements. 

package de.uni_bonn.bit.lsi.groupfive.fastqc_spark;


public class SeqChunk {

  private String id;
  private String seq;
  private String quality;

 public SeqChunk(String id, String seq, String quality) {
	super();
	this.id = id;
	this.seq = seq;
	this.quality = quality;
}


public String getId() {
	return id;
}


public void setId(String id) {
	this.id = id;
}


public String getSeq() {
	return seq;
}


public void setSeq(String seq) {
	this.seq = seq;
}


public String getQuality() {
	return quality;
}


public void setQuality(String quality) {
	this.quality = quality;
}

}
