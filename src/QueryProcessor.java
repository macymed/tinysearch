import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class QueryProcessor {

	public static Set<Integer> ProcessQuery(String query, DiskPositionalIndex index){
		Set<Integer> results = new HashSet<Integer>();
		
		//split on not queries
		if(query.contains("-")){
			String[] notQueries = query.split("-");
			results.addAll(ProcessQuery(notQueries[0], index));
			for(int i = 1; i < notQueries.length; i++){
				results.removeAll(ProcessQuery(notQueries[i], index));
			}
		
		//split on or queries	
		}else if(query.contains("+")){
			String[] orQueries = query.split("\\+");
			for(String orQuery : orQueries){
				results.addAll(ProcessQuery(orQuery, index));
			}
		}else{
			
			//phrase queries
			if(query.contains("\"")){
				
				//get the subquery thats the phrase query
				String phraseQuery = query.substring(query.indexOf("\""), query.lastIndexOf("\"") + 1);
				results.addAll(PhraseHandler(phraseQuery, index));
				
				//search the rest of the query
				query = query.replace(phraseQuery, "").replaceAll(" ", "");
				if(!query.isEmpty()){
					System.out.println("QUERY AFTER PHRASE QUERY:" + query);
					results.addAll(ProcessQuery((query.replace(phraseQuery, "")), index));
				}

			}else{
				
				//split on white spaces - "ands"
				String[] andQueries = query.split("\\s+");
				
				//get the results of the first part of the query
				
				PositionalPosting[] result = index.getPostingsWithFreq(PorterStemmer.processToken(andQueries[0].replaceAll("\\W", "")));
				List<PositionalPosting> postings = new ArrayList<PositionalPosting>();
				for(int i = 0; i < result.length; i++){
					postings.add(result[i]);
				}
				//check that word was found
				if(postings != null){						
					for(int i = 0; i < postings.size(); i++){
						results.add(postings.get(i).getdocID());
					}
				}
				
				//get the results of the rest of the queries and use "retain all" to merge lists
				for(int i = 1; i < andQueries.length; i++){
					PositionalPosting[] result2 = index.getPostingsWithFreq(PorterStemmer.processToken(andQueries[i].replaceAll("\\W", "")));
					List<PositionalPosting> postings2 = new ArrayList<PositionalPosting>();
					for(int j = 0; j < result2.length; j++){
						postings2.add(result2[j]);
					}
					//check that words was found
					if(postings2!=null){
						List<Integer> docIDList = new ArrayList<Integer>();
						for(int j = 0; j < postings2.size(); j++){
							docIDList.add(postings2.get(j).getdocID());
						}
						results.retainAll(docIDList);
					}
				}
			}
		}		
		return results;
	}
	
	
	//method for handling phrase queries
	private static Set<Integer> PhraseHandler(String query, DiskPositionalIndex index){
		Set<Integer> phraseResults = new HashSet<Integer>();
		query = query.replaceAll("\"", "");
		String[] phraseParts = query.split("\\s+");

		//retrieve postings for all parts of the query
		List<List<PositionalPosting>> phrasePartPostings = new ArrayList<List<PositionalPosting>>();
		for(int i = 0; i < phraseParts.length; i++){
			//phrasePartPostings.addAll(index.getPostingsWithFreq(PorterStemmer.processToken(phraseParts[i].replaceAll("\\W", ""))));
			PositionalPosting[] result = index.getPostingsWithPos(PorterStemmer.processToken(phraseParts[i].replaceAll("\\W", "")));
			List<PositionalPosting> ppPost = new ArrayList<PositionalPosting>();
			for(int j = 0; j < result.length; j++){
				ppPost.add(result[j]);
			}
			phrasePartPostings.add(ppPost);
		}

		//go through postings

		Set<Integer> postingResults = new HashSet<Integer>();
		for(int i = 0; i < phraseParts.length - 1; i++){
			postingResults.clear();
			//get first two in list
			List<PositionalPosting> postings1 = phrasePartPostings.get(i);
			List<PositionalPosting> postings2 = phrasePartPostings.get(i + 1);
			int j = 0;
			int k = 0;

			while(j < postings1.size() && k < postings2.size()){

				PositionalPosting posting1 = postings1.get(j);
				PositionalPosting posting2 = postings2.get(k);
				
				//if the doc ids are the same, check positions, then advance to next listing
				if(posting1.getdocID() == posting2.getdocID()){
					List<Integer> positions1 = posting1.getPositions();
					List<Integer> positions2 = posting2.getPositions();
					int m = 0;
					int n = 0;

					//go through the positions
					while(m < positions1.size() && n < positions2.size()){
						if((positions1.get(m) + 1) == positions2.get(n)){
							//System.out.println("MATCH FOUND IN DOC "+ posting1.getdocID());
							postingResults.add(posting1.getdocID());
							m = m + 1;
							n = n + 1;
						}else if((positions1.get(m) + 1) < positions2.get(n)){
							m = m + 1;
						}else{
							n = n + 1;
						}
					}
					
					//advance to next listings
					j = j + 1;
					k = k + 1;
				}else if(posting1.getdocID() < posting2.getdocID()){
					j = j + 1;
				}else{
					k = k + 1;
				}


			}
			if(i == 0){
				phraseResults.addAll(postingResults);
			}else{;
				phraseResults.retainAll(postingResults);
			}
		}

		return phraseResults;
	}
	
	
}
