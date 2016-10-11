import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;

public class Bayesian {
	final static List<String> fileNames = new ArrayList<String>();
	
	private static PriorityQueue<Map.Entry<String, Double>> resultQueue;
	
	public static void main(String[] args) throws IOException{
		
		//build the index
		PositionalInvertedIndex index = indexBuilder();
		ArrayList<String> terms = new ArrayList<String>();
		String[] dictionary = index.getDictionary();
		for(int i = 0; i < dictionary.length; i++){
			terms.add(dictionary[i]);
		}
		
		
		//priority queue weight score, term and class, pull out a term until you have 50 unique terms (set) 
		//tree set of term 
		
		Comparator<Map.Entry<String, Double>> comparator = new IValComparator();
		resultQueue = new PriorityQueue<Map.Entry<String, Double>>(comparator);
		
		
		
		discriminator("Jay", terms, index);		
		discriminator("Hamilton", terms, index);	
		discriminator("Madison", terms, index);
	
		
		//one set of terms 
		int termSetSize = 50;
		
		//System.out.println(resultQueue.size());
		TreeSet<String> definingTerms = new TreeSet<String>();
		while(definingTerms.size() < termSetSize){
			String term = resultQueue.remove().getKey();
			if(!definingTerms.contains(term)){
				definingTerms.add(term);
			}
		}
		//System.out.println(definingTerms.size());
		//System.out.println(definingTerms.toString());
		//train for each class
		double[] jayResults = trainer("Jay", definingTerms, index);
		double[] hamResults = trainer("Hamilton", definingTerms, index);
		double[] madResults = trainer("Madison", definingTerms, index);
		
		

	}
	
	public static double[] trainer(String filename, TreeSet<String> terms, PositionalInvertedIndex index){
		double[] results = new double[terms.size()];
		
		File dir = new File(filename);
		File[] directoryListing = dir.listFiles();
		int counter = 0;
		HashMap<String, Integer> tctMap = new HashMap<String, Integer>();
		
		for(String term : terms){
			List<PositionalPosting> postings = index.getPostings(term);
			
			//get the tct for each term
			int Tct = 0;
			for(File oneFile: directoryListing){
				for(PositionalPosting posting: postings){
					if(posting.getdocID() == fileNames.indexOf(oneFile)){
						Tct += posting.getPositions().size();
						
					}
				}
			}
			tctMap.put(term, Tct);
			//what is number before terms.length???
		}
		int tctTotal = 0;
		for(int i = 0; i < tctMap.size(); i++){
			tctTotal += tctMap.get(i);
		}
		
		for(String term :terms){
			results[counter] = ((double) tctMap.get(counter) + 1)/(tctTotal + terms.size());
			
			counter++;
		}
		
		
		
		return results;
	}
	
	
	public static void discriminator(String filename, ArrayList<String> terms, PositionalInvertedIndex index){
		
		File dir = new File(filename);
		File[] directoryListing = dir.listFiles();
		double N = 85.0;
		double NX1 = dir.listFiles().length;
		for(String term: terms){
			double N1X = index.getPostings(term).size();
			int N11 = 0;			
			for(File oneFile : directoryListing){
				int docID = fileNames.indexOf(oneFile.getName());
				List<PositionalPosting> postings = index.getPostings(term);
				for(int i = 0; i < postings.size(); i++){
					if(postings.get(i).getdocID() == docID){
						N11++;
					}
				}
			}
			
			double N10 = N1X - N11;
			double N0X = N - N1X;
			double N01 = NX1 - N11;
			double N00 = N - N01 - N10 - N11;
			double NX0 = N00 + N10;
			//System.out.println(term + " " + filename);
			//System.out.println(N00 + " " + N01 + " " + N10 + " " + N11);
			//System.out.println(N0X + " " + NX1 + " " + N1X + " " + NX0);

			
			if(N0X != 0){
				double Ival = (double)(N00/N)*Math.log((N*N00)/(N0X*NX0)) + (N01/N)*Math.log((N*N01)/(N0X*NX1))
					+(N10/N)*Math.log(N*N01/(N1X*NX0))+ (N11/N)*Math.log(N*N11/(N1X*NX1));
				
				HashMap<String, Double> mapMaker = new HashMap<String, Double>();
				mapMaker.put(term, Ival);
				//System.out.println("Term: "+ term + ",  "+ Ival);
				for(Map.Entry<String, Double> entry: mapMaker.entrySet()){
					resultQueue.add(entry);
				}
			}
		 
		}		
		
	}
	
	public static int[] docInfo(int docID, String[] dictionary, PositionalInvertedIndex index){
		int[] result = new int[dictionary.length];
		int position = 0;
		for(String term: index.getDictionary()){
			int tftd = 0;
			List<PositionalPosting> postings =  index.getPostings(term);
			for(int i =0; i < postings.size(); i++){
				PositionalPosting posting = postings.get(i);
				if(posting.getdocID() == docID){
					//System.out.println(posting.toString());
					tftd = posting.getPositions().size();
					//System.out.println("posting frequency for " + term + "  is " + posting.getTermFreq());
					//System.out.println("tftd for "+ docID + " " +tftd);
					break;
				}
			}
			result[position] = tftd;
			position++;
		}
		return result;
	}
	
	
	public static PositionalInvertedIndex indexBuilder() throws IOException{
	      final Path currentWorkingPath = Paths.get("RealAll").toAbsolutePath();
	      
	      // the inverted index
	      final PositionalInvertedIndex index = new PositionalInvertedIndex();
	      
	      // the list of file names that were processed
	    
	      
	      // This is our standard "walk through all .txt files" code.
	      Files.walkFileTree(currentWorkingPath, new SimpleFileVisitor<Path>() {
	         int mDocumentID  = 0;
	        
	         
	         public FileVisitResult preVisitDirectory(Path dir,
	          BasicFileAttributes attrs) {
	            // make sure we only process the current working directory
	            if (currentWorkingPath.equals(dir)) {
	               return FileVisitResult.CONTINUE;
	            }
	            return FileVisitResult.SKIP_SUBTREE;
	         }

	         public FileVisitResult visitFile(Path file,
	          BasicFileAttributes attrs) throws FileNotFoundException {
	            // only process .txt files
	            if (file.toString().endsWith(".txt")) {
	               // we have found a .txt file; add its name to the fileName list,
	               // then index the file and increase the document ID counter.
	               //System.out.println("Indexing file " + file.getFileName());
	               
	               
	               fileNames.add(file.getFileName().toString());
	               indexFile(file.toFile(), index, mDocumentID);
	               mDocumentID++;
	               
	            }
	            
	            return FileVisitResult.CONTINUE;
	         }

	         // don't throw exceptions if files are locked/other errors occur
	         public FileVisitResult visitFileFailed(Path file,
	          IOException e) {

	            return FileVisitResult.CONTINUE;
	         }

	      });
	     
	      return index;
	}
	

 /**
 Indexes a file by reading a series of tokens from the file, treating each 
 token as a term, and then adding the given document's ID to the inverted
 index for the term.
 @param file a File object for the document to index.
 @param index the current state of the index for the files that have already
 been processed.
 @param docID the integer ID of the current document, needed when indexing
 each term from the document.
* @throws FileNotFoundException 
 */
 private static void indexFile(File file, PositionalInvertedIndex index, 
  int docID) throws FileNotFoundException {
    // TO-DO: finish this method for indexing a particular file.
    // Construct a SimpleTokenStream for the given File.
    // Read each token from the stream and add it to the index.
	   //System.out.println("indexing " + docID);
	  SimpleTokenStream stream = new SimpleTokenStream(file);
	  int position = 0;
    while(stream.hasNextToken()){
  	  String term = stream.nextToken();
  	  TokenProcessor.processToken(term, position, index, docID);
     	  position++;
    }
    
 }

	
}
