
public class AccumulatorResult {
	private int docID;
	private double a_d;
	
	public AccumulatorResult(int docID, double a_d){
		this.docID = docID;
		this.a_d = a_d;
	}
	
	public int getDocID(){
		return docID;
	}
	
	public double getA_d(){
		return a_d;
	}
	
}
