import java.util.*;

public class PositionalPosting {
	
	private int docID;
	private int termFreq;
	private double w_dt;
	private List<Integer> positions;
	
	public PositionalPosting(int docID){
		this.docID = docID;
		positions = new ArrayList<Integer>();
	}
	
	public PositionalPosting(int docID, int termFreq){
		this.docID = docID;
		this.termFreq = termFreq;
		positions = new ArrayList<Integer>();
	}
	
	public PositionalPosting(int docID, int termFreq ,double w_dt){
		this.docID = docID;
		this.termFreq = termFreq;
		this.w_dt = w_dt;
		positions = new ArrayList<Integer>();
	}
	
	public PositionalPosting(int docID, int termFreq, List<Integer> positions){
		this.docID = docID;
		this.termFreq = termFreq;
		this.positions = positions;
	}
	
	public PositionalPosting(int docID, int termFreq, List<Integer> positions, double w_dt){
		this.docID = docID;
		this.termFreq = termFreq;
		this.positions = positions;
		this.w_dt = w_dt;
	}
	
		
	public int getdocID(){
		return docID;
	}
	
	public int getTermFreq(){
		return termFreq;
	}
	
	public double getWdt(){
		return w_dt;
	}
	
	public List<Integer> getPositions(){
		return positions;
	}
	
	public String toString(){
		return docID + ": <" + positions.toString() + ">";
	}

}
