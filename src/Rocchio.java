import java.util.*;
import java.io.*;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;




public class Rocchio {
	
	  final static List<String> fileNames = new ArrayList<String>();

	
		public static void main(String[] args) throws IOException{
			PositionalInvertedIndex index = indexBuilder();
			double[][] centroids = Trainer(index);
			mysterySolver(index, centroids);
			
		}
		
		public static double[][] Trainer(PositionalInvertedIndex index) throws IOException{
			int indexSize = index.getDictionary().length;
			
			//find the centroids
			File dir = new File("Jay");
			double[] jayResults = Classifier(dir, index, indexSize);
			double jayLD = LdValue(jayResults);
			for(int i = 0; i < jayResults.length; i++){
				jayResults[i] =  ((double) jayResults[i] / jayLD);
			}
			//System.out.println("Jay centroid: "+ jayResults.toString());

			
			dir = new File("Hamilton");
			double[] hamResults = Classifier(dir, index, indexSize);
			double hamLD = LdValue(hamResults);
			
			for(int i = 0; i < hamResults.length; i++){
				hamResults[i] = ((double)hamResults[i] / hamLD);
			}
	
			//System.out.println("Ham centroid: "+ hamResults.toString());
			
			dir = new File("Madison");
			double[] madResults = Classifier(dir, index, indexSize);
			double madLD = LdValue(madResults);
			for(int i = 0; i < madResults.length; i++){
				madResults[i] = ((double)madResults[i] / madLD);
			}
			//System.out.println("Mad centroid: "+ madResults.toString());
			
			double[][] results = new double[3][jayResults.length];
			results[0] = jayResults;
			results[1] = hamResults;
			results[2] = madResults;
			
			return results;
		}
		
		public static void mysterySolver(PositionalInvertedIndex index, double[][] centroids){
			
			//compare doc vectors of mystery docs to centroids
			File dir = new File("Hamilton or Madison");
			File[] directoryListing = dir.listFiles();
			
			for(File oneFile : directoryListing){
				if(oneFile.getName().endsWith(".txt")){
					int docID = fileNames.indexOf(oneFile.getName());
				
				double[] mysteryDocVector = getDocVector(docID, index);
				//System.out.println(docID);
				for(int i = 0; i < mysteryDocVector.length; i++){
					//System.out.print(mysteryDocVector[i]);
				}
				//System.out.println();
				ArrayList<Double> distances = new ArrayList<Double>();
				distances.add(EuclideanDistance(mysteryDocVector, centroids[0]));
				distances.add(EuclideanDistance(mysteryDocVector, centroids[1]));
				distances.add(EuclideanDistance(mysteryDocVector, centroids[2]));
				//System.out.println(distances.toString());
				
				
				
				int minIndex = distances.indexOf(Collections.min(distances));
				if(minIndex == 0){
					System.out.println(oneFile.getName() + " belongs to the Jay Class");
				}else if(minIndex == 1){
					System.out.println(oneFile.getName() + " belongs to the Hamilton Class");
				}else{
					System.out.println(oneFile.getName() + " belongs to the Madison Class");
				}
				
				}
				
			}
			
			
		}
		
		public static double EuclideanDistance(double[] a, double[] b){
			double result = 0.00;
			
			for(int i = 0; i < a.length; i++){
				result = (double) result + Math.pow((double)(a[i] - b[i]), 2); 
				//System.out.println(a[i] + " - " + b[i] + "result: "+ result);
			}
			result = (double) Math.sqrt(result);
			
			//System.out.println("result: "+ result);
			
			return result;
		}
		
		public static double[] Classifier(File file, PositionalInvertedIndex index, int indexSize){
			
			File[] directoryListing = file.listFiles();
			ArrayList<double[]> vectors = new ArrayList<double[]>();
			for(File oneFile : directoryListing){
				int docID = fileNames.indexOf(oneFile.getName());
				//System.out.println(docID);
				double[] vector = getDocVector(docID, index);
				vectors.add(vector);
				//System.out.println(vector.toString());
				
			}
			double[] results = new double[indexSize];
			
			for(double[] vector: vectors){
				for(int i = 0; i < vector.length; i++){
					results[i] += vector[i] / directoryListing.length;
				}
				
			}
			
			return results;

		}
		
		public static double[] getDocVector(int docID, PositionalInvertedIndex index){
			double[] vector = new double[index.getDictionary().length];
			int position = 0;
			
			for(String term: index.getDictionary()){
				int tftd = 0;
				List<PositionalPosting> postings =  index.getPostings(term);
				//System.out.println(term + " " + docID);
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
				//System.out.println(tftd);			
				double wdt = (double) Math.log(1 + (double)tftd);
				//System.out.println("WDT for "+ docID + " " +wdt + "at " + position);
				
				vector[position] = wdt;
				position++;
			}
			
			double LdVal = LdValue(vector);
			for(int i = 0; i < vector.length; i++){
				if(vector[i] != (double) 0){
					vector[i] = ((double) vector[i] / LdVal);
				}
			}
		
			return vector;
		}
		
		public static double LdValue(double[] vector){
			double result = 0.00;
	       	 for(int i = 0; i < vector.length; i++){
	        	 result += (Math.pow(vector[i], 2));
	       	 }
	       	 result = Math.sqrt(result);
	       	 //System.out.println("LD VALUE: "+ result);
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
