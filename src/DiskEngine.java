
import java.util.ArrayList;
import java.util.*;
import java.util.Scanner;


public class DiskEngine {
	
	private static final int RESULTS_K = 10;

   public static void main(String[] args) {
      Scanner scan = new Scanner(System.in);

      System.out.println("Menu:");
      System.out.println("1) Build index");
      System.out.println("2) Read an index and make a query");
      System.out.println("3) Solve the federalist papers mystery");
      System.out.println("Choose a selection:");
      int menuChoice = scan.nextInt();
      scan.nextLine();

      switch (menuChoice) {
         case 1:
            System.out.println("Enter the name of a directory to index: ");
            String folder = scan.nextLine();

            IndexWriter writer = new IndexWriter(folder);
            writer.buildIndex();
            break;

         case 2:
            System.out.println("Enter the name of an index to read:");
            String indexName = scan.nextLine();

            DiskPositionalIndex index = new DiskPositionalIndex(indexName);

            List<String> FileNames = index.getFileNames();

            while (true) {
            	System.out.println("Select a result type:");
            	System.out.println("1) Ranked");
            	System.out.println("2) Boolean"); 
            	menuChoice = scan.nextInt();
            	scan.nextLine();
               //System.out.println("Enter one or more search terms, separated " +
               // "by spaces:");
            	System.out.println("Enter a search query: ");
                String input = scan.nextLine();

               if (input.equals("EXIT")) {
                  break;
               }
               
               switch(menuChoice){
               		case 1: 
               			//ranked retrieval
               			ArrayList<String> terms = new ArrayList<String>();
               			for(String s: input.split(" ")){
               				terms.add(PorterStemmer.processToken(s.toLowerCase()));
               			}
               			ArrayList<AccumulatorResult> results = RankedRetriever.getResults(RESULTS_K, terms, index);
               			for(AccumulatorResult result : results){
               				System.out.println("Document: " + FileNames.get(result.getDocID()) + " Accumulator Value: " + result.getA_d());
               			}
               			break;
               			
               		case 2:
               			//boolean retrieval
               			Set<Integer> boolResults = QueryProcessor.ProcessQuery(input, index);
               			for(int i: boolResults){
               				System.out.println("Document: " + FileNames.get(i));
               			}
               			System.out.println("Number of documents returned: " + boolResults.size());
               			break;
               }	


            }

            break;
            
         case 3:
        	 System.out.println("You have chosen to solve the federalist paper mystery.");
        	 System.out.println("Choose which method you would like to use: ");
        	 
        	 System.out.println("1) Rocchio Classification");
        	 System.out.println("2) Bayesian Classifcation");
        	 
        	 int menuChoice2 = scan.nextInt();
        	 
        	 switch(menuChoice2){
        	 		case 1:
        	 			
        	 			break;
        	 			
        	 		case 2:
        	 			
        	 			break;
        	 			
        	 }
            

      }
      
      
   }
}
