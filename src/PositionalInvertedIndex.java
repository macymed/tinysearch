import java.util.*;

public class PositionalInvertedIndex {

   private HashMap<String, List<PositionalPosting>> mIndex;
   
   public PositionalInvertedIndex() {
      mIndex = new HashMap<String, List<PositionalPosting>>();
   }
   
   public void addTerm(String term, int docID, int position) {
      // TO-DO: add the term to the index hashtable. If the table does not have
      // an entry for the term, initialize a new ArrayList<Integer>, add the 
      // docID to the list, and put it into the map. Otherwise add the docID
      // to the list that already exists in the map, but ONLY IF the list does
      // not already contain the docID.
	   //term = TokenProcessor.processTokenString(term);
	   if(mIndex.containsKey(term)){
		   boolean flag = false;
		   //check for doc id already in postings, then add if found
		   for(int i = 0; i < mIndex.get(term).size(); i++){
			   if(mIndex.get(term).get(i).getdocID() == docID){
				   mIndex.get(term).get(i).getPositions().add(position);
				   flag = true;
				   break;
			   }
		   }
		   //doc id wasn't already in list of positional postings
		   if(flag == false){
			   PositionalPosting result = new PositionalPosting(docID);
			   result.getPositions().add(position);
			   mIndex.get(term).add(result);
		   }
	   //term not in vocab yet, add it and add new positional posting 
	   }else{
		   PositionalPosting result = new PositionalPosting(docID);
		   result.getPositions().add(position);
		   List<PositionalPosting> list = new ArrayList<PositionalPosting>();
		   list.add(result);
		   mIndex.put(term, list);
	   }
	   
      
   }
   
   public List<PositionalPosting> getPostings(String term) {
      // TO-DO: return the postings list for the given term from the index map.
      
      return mIndex.get(term);
   }
   
   public int getTermCount() {
      // TO-DO: return the number of terms in the index.
      
      return mIndex.size();
   }
   
   public String[] getDictionary() {
      // TO-DO: fill an array of Strings with all the keys from the hashtable.
      // Sort the array and return it.
	  //String[] dictionary = new String[mIndex.size()];
	  String[] dictionary =  mIndex.keySet().toArray(new String[mIndex.size()]);
	  Arrays.sort(dictionary);
      return dictionary;
   }

	
	
}
