import java.util.*;
import java.io.*;
import java.nio.ByteBuffer;

public class RankedRetriever {

	public static ArrayList<AccumulatorResult> getResults(int numResults, ArrayList<String> terms, DiskPositionalIndex index){
		
		//get distinct set of terms by sorting then adding them if they're different 
		ArrayList<String> distinctTerms = new ArrayList<String>();
		Collections.sort(terms);
		String prevTerm = "";
		for(String term: terms){
			if(prevTerm != term){
				distinctTerms.add(TokenProcessor.processTokenString(term.toLowerCase()));
				//distinctTerms.add(term);
			}
			prevTerm = term;
		}
		
		ArrayList<AccumulatorResult> finalResults = new ArrayList<AccumulatorResult>();
		
		//get "N" number of documents total in the index
		int docSetSize = index.getFileNames().size();
		
		//make an accumulator map
		HashMap<Integer, Double> accumulatorMap = new HashMap<Integer, Double>();


		
		//for each distinctive term t in query
		for(String term: distinctTerms){
			//get array of documents
			
			 PositionalPosting[] postings= index.getPostingsWithFreq(term);
			//calculate Wqt 
			double w_qt = ((double) docSetSize / (double) postings.length);
			w_qt = Math.log(1 + w_qt);
			
			//for each document in t's posting list
			for(PositionalPosting pos: postings){
				int docID = pos.getdocID();
				
				//calc Wdt and multiply by Wqt to get Ad
				//double w_dt = (double) (1 + Math.log((double) pos.getTermFreq()));
				double w_dt = (double) pos.getWdt();
				double A_d = w_qt * w_dt; 
				
				//make accumulator value for document or add to previous 
				if(!accumulatorMap.containsKey(docID)){
					accumulatorMap.put(pos.getdocID(), A_d);
				}else{
					accumulatorMap.put(pos.getdocID(), A_d + accumulatorMap.get(docID));
				}

			}
						
		}
				
		//for nonzero accumulator values divide by Ld
		for(Map.Entry<Integer, Double> entry: accumulatorMap.entrySet()){
			if(entry.getValue() != 0){
				int docID = entry.getKey();
				double docWeight = (double) index.getDocWeight(docID);
				double a_d = (double) entry.getValue();
				
				//divide by document weight and put back in map
				accumulatorMap.put(docID, (a_d / docWeight));
			
			}
		}
		
		//make a priority queue and put the accumulator values into it
		Comparator<Map.Entry<Integer, Double>> comparator = new RankedComparator();
		PriorityQueue<Map.Entry<Integer, Double>> resultQueue = new PriorityQueue<Map.Entry<Integer, Double>>(comparator);
		for(Map.Entry<Integer, Double> entry: accumulatorMap.entrySet()){
			resultQueue.add(entry);
		}
		
		//extract the number results specified by numResults from the queue
		for(int i = 0; i < numResults; i++){
			if(!resultQueue.isEmpty()){
				Map.Entry<Integer, Double> result = resultQueue.remove();
				finalResults.add(new AccumulatorResult(result.getKey(), result.getValue()));
			}
		}
		return finalResults;
	}
	
}
