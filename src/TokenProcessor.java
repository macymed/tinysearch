
public class TokenProcessor {

	public static void processToken(String term, int position, PositionalInvertedIndex index, int docID){		
  	  if(term!=null){
  		  //remove non-alphanumeric characters from beginning and end
  		  term = term.replaceAll("[^a-zA-Z0-9]$", "").replaceAll("^[^a-zA-Z0-9]", "").toLowerCase();
    	  //for hyphens, have to stem and index three times
    	  if(term.contains("-")){
    		  //split on hyphens
    		  String[] hyphens = term.split("-");
    		  //process each part individually
    		  for(int i = 0; i < hyphens.length; i++){
    			  //stem and index each part of the hyphenated word
    			  String termHalf = PorterStemmer.processToken(hyphens[i].replaceAll("\\W", ""));
    			  index.addTerm(termHalf,  docID, position);
    		  }
    	  }
    	  //remove non alphanumeric characters
    	  term = term.replaceAll("\\W", "");
    	  //stem and index the term (including hyphened words without the hyphen)
    	  term = PorterStemmer.processToken(term);
    	  index.addTerm(term, docID,position);
	  }
	}
	
	public static String processTokenString(String word){		
	  	String term = word;  
		if(term!=null){
	  		  //remove non-alphanumeric characters from beginning and end
	  		  term = term.replaceAll("[^a-zA-Z0-9]$", "").replaceAll("^[^a-zA-Z0-9]", "").toLowerCase();
	    	  //for hyphens, have to stem and index three times
//	    	  if(term.contains("-")){
//	    		  //split on hyphens
//	    		  String[] hyphens = term.split("-");
//	    		  //process each part individually
//	    		  for(int i = 0; i < hyphens.length; i++){
//	    			  //stem and index each part of the hyphenated word
//	    			  String termHalf = PorterStemmer.processToken(hyphens[i].replaceAll("\\W", ""));
//	    		  }
//	    	  }
	    	  //remove non alphanumeric characters
	    	  term = term.replaceAll("\\W", "");
	    	  //stem and index the term (including hyphened words without the hyphen)
	    	  term = PorterStemmer.processToken(term);
	    	  return term;
		  }
		return term;
	}
}
