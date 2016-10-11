
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DiskPositionalIndex {

   private String mPath;
   private RandomAccessFile mVocabList;
   private RandomAccessFile mPostings;
   private RandomAccessFile mDocWeights;
   private long[] mVocabTable;
   private List<String> mFileNames;

   public DiskPositionalIndex(String path) {
      try {
         mPath = path;
         mVocabList = new RandomAccessFile(new File(path, "vocab.bin"), "r");
         mPostings = new RandomAccessFile(new File(path, "postings.bin"), "r");
         mDocWeights = new RandomAccessFile(new File(path, "docWeights.bin"), "r");
         mVocabTable = readVocabTable(path);
         mFileNames = readFileNames(path);
      }
      catch (FileNotFoundException ex) {
         System.out.println(ex.toString());
      }
   }

   private static int[] readPostingsFromFile(RandomAccessFile postings, 
    long postingsPosition) {
      try {
         // seek to the position in the file where the postings start.
         postings.seek(postingsPosition);
         
         // read the 4 bytes for the document frequency
         byte[] buffer = new byte[4];
         postings.read(buffer, 0, buffer.length);
         	//byte[] buffer = VBEncoder.readByte(postings);
         
         // use ByteBuffer to convert the 4 bytes into an int.
         int documentFrequency = ByteBuffer.wrap(buffer).getInt();
         	//int documentFrequency = VBEncoder.decode(buffer);
         
         // initialize the array that will hold the postings. 
         int[] docIDs = new int[documentFrequency];

         int prevID = 0;
         for(int i = 0; i < documentFrequency; i++){
        	 //read the document id        	 
        	 postings.read(buffer, 0, buffer.length);
        	 int gap = ByteBuffer.wrap(buffer).getInt();
        	 docIDs[i] = gap + prevID;
     	 		//buffer = VBEncoder.readByte(postings);
     	 		//docIDs[i] = VBEncoder.decode(buffer) + prevID;
        	 
        	 //read the term freq
        	 	//buffer = VBEncoder.readByte(postings);
        	 	//int termFreq = VBEncoder.decode(buffer);
        	 postings.read(buffer, 0, buffer.length);
        	 int termFreq = ByteBuffer.wrap(buffer).getInt();
        	 
        	 //skip past positions
        	 for(int j = 0; j < termFreq; j++){
        		 postings.read(buffer, 0, buffer.length);
        		 	//VBEncoder.readByte(postings);
        	 }
        	 
        	 prevID = docIDs[i];
         }

         return docIDs;
      }
      catch (IOException ex) {
         System.out.println(ex.toString());
      }
      return null;
   }
   
   private static PositionalPosting[] readPostingsWithFreq(RandomAccessFile postings, long postingsPosition){
	   try{
		   postings.seek(postingsPosition);
		   
		   			//byte[] buffer = VBEncoder.readByte(postings);
		   byte[] buffer = new byte[4];
		   postings.read(buffer, 0, buffer.length);

		   // use ByteBuffer to convert the 4 bytes into an int.
		   			//int documentFrequency = VBEncoder.decode(buffer);
		   int documentFrequency = ByteBuffer.wrap(buffer).getInt();
		   
		   PositionalPosting[] postingList = new PositionalPosting[documentFrequency];
		   int prevID = 0;
		   int docID = 0;
		   for(int i = 0; i < documentFrequency; i++){
			   //read the document id
			   		//buffer = VBEncoder.readByte(postings);
			   		//docID = VBEncoder.decode(buffer) + prevID;
			   postings.read(buffer, 0, buffer.length);
			   int gap = ByteBuffer.wrap(buffer).getInt();
			   docID = gap + prevID;
			   prevID = docID;

			   //read the wdt
			   byte[] buffer2 = new byte[8];
			   postings.read(buffer2, 0, buffer2.length);
			   double w_dt = ByteBuffer.wrap(buffer2).getDouble();
			   
			   //read the term freq
			   		//buffer = VBEncoder.readByte(postings);
			   		//int termFreq = VBEncoder.decode(buffer);
			   postings.read(buffer, 0, buffer.length);
			   int termFreq = ByteBuffer.wrap(buffer).getInt();
	        	 
			   //skip past positions
			   for(int j = 0; j < termFreq; j++){
				   postings.read(buffer, 0, buffer.length);
				   		//VBEncoder.readByte(postings);
			   }
			   
			   postingList[i] = new PositionalPosting(docID, termFreq, w_dt);
			   //postingList[i] = new PositionalPosting(docID, termFreq);
		   }
	         
		   return postingList;
		   
	   }catch (IOException ex) {
		   System.out.println(ex.toString());
	   }
	   
	   return new PositionalPosting[0];
   }
   
   private static PositionalPosting[] readPostingsWithPos(RandomAccessFile postings, long postingsPosition){
	   try{
		   postings.seek(postingsPosition);
		   byte[] buffer = new byte[4];
		   postings.read(buffer, 0, buffer.length);
		   		//byte[] buffer = VBEncoder.readByte(postings);

		   // use ByteBuffer to convert the 4 bytes into an int.
		   int documentFrequency = ByteBuffer.wrap(buffer).getInt();
		   		//int documentFrequency = VBEncoder.decode(buffer);
		   
		   PositionalPosting[] postingList = new PositionalPosting[documentFrequency];
		   int prevID = 0;
		   int docID = 0;
		   for(int i = 0; i < documentFrequency; i++){
			   //read the document id
			   		//buffer = VBEncoder.readByte(postings);
			   		//int gap = VBEncoder.decode(buffer);
			   postings.read(buffer, 0, buffer.length);
			   int gap = ByteBuffer.wrap(buffer).getInt();
			   docID = gap + prevID;
			   prevID = docID;

			   //read the wdt
			   byte[] buffer2 = new byte[8];
			   postings.read(buffer2, 0, buffer2.length);
			   double w_dt = ByteBuffer.wrap(buffer2).getDouble();
			   
			   //read the term freq
			   		//buffer = VBEncoder.readByte(postings);
			   		//int termFreq = VBEncoder.decode(buffer);
			   postings.read(buffer, 0, buffer.length);
			   int termFreq = ByteBuffer.wrap(buffer).getInt();
	        	
			   ArrayList<Integer> positions = new ArrayList<Integer>(termFreq);
			   int prevPos = 0;
			   int pos = 0;
			   //skip past positions
			   for(int j = 0; j < termFreq; j++){
				   		//buffer = VBEncoder.readByte(postings);
				   		//int posGap = VBEncoder.decode(buffer);
				   postings.read(buffer, 0, buffer.length);
				   int posGap = ByteBuffer.wrap(buffer).getInt();
				   pos = posGap + prevPos;
				   positions.add(pos);
				   prevPos = pos;
			   }
			   
			   postingList[i] = new PositionalPosting(docID, termFreq, positions, w_dt);
			   //postingList[i] = new PositionalPosting(docID, termFreq, positions);
		   }
	         
		   return postingList;
		   
	   }catch (IOException ex) {
		   System.out.println(ex.toString());
	   }
	   
	   return null;
   }
   
   

   public int[] GetPostings(String term) {
      long postingsPosition = binarySearchVocabulary(term);
      if (postingsPosition >= 0) {
         return readPostingsFromFile(mPostings, postingsPosition);
      }
      return null;
   }
   
   public PositionalPosting[] getPostingsWithFreq(String term){
	   long postingsPosition = binarySearchVocabulary(term);
	   if(postingsPosition >=0){
		   return readPostingsWithFreq(mPostings, postingsPosition);
	   }
	   return null;
   }
   
   public PositionalPosting[] getPostingsWithPos(String term){
	   long postingsPosition = binarySearchVocabulary(term);
	   if(postingsPosition >=0){
		   return readPostingsWithPos(mPostings, postingsPosition);
	   }
	   return null;
   }

   private long binarySearchVocabulary(String term) {
      // do a binary search over the vocabulary, using the vocabTable and the file vocabList.
      int i = 0, j = mVocabTable.length / 2 - 1;
      while (i <= j) {
         try {
            int m = (i + j) / 2;
            long vListPosition = mVocabTable[m * 2];
            int termLength;
            if (m == mVocabTable.length / 2 - 1){
               termLength = (int)(mVocabList.length() - mVocabTable[m*2]);
            }
            else {
               termLength = (int) (mVocabTable[(m + 1) * 2] - vListPosition);
            }

            mVocabList.seek(vListPosition);

            byte[] buffer = new byte[termLength];
            mVocabList.read(buffer, 0, termLength);
            String fileTerm = new String(buffer, "ASCII");

            int compareValue = term.compareTo(fileTerm);
            if (compareValue == 0) {
               // found it!
               return mVocabTable[m * 2 + 1];
            }
            else if (compareValue < 0) {
               j = m - 1;
            }
            else {
               i = m + 1;
            }
         }
         catch (IOException ex) {
            System.out.println(ex.toString());
         }
      }
      return -1;
   }


   private static List<String> readFileNames(String indexName) {
      try {
         final List<String> names = new ArrayList<String>();
         final Path currentWorkingPath = Paths.get(indexName).toAbsolutePath();

         Files.walkFileTree(currentWorkingPath, new SimpleFileVisitor<Path>() {
            int mDocumentID = 0;

            public FileVisitResult preVisitDirectory(Path dir,
             BasicFileAttributes attrs) {
               // make sure we only process the current working directory
               if (currentWorkingPath.equals(dir)) {
                  return FileVisitResult.CONTINUE;
               }
               return FileVisitResult.SKIP_SUBTREE;
            }

            public FileVisitResult visitFile(Path file,
             BasicFileAttributes attrs) {
               // only process .txt files
               if (file.toString().endsWith(".txt")) {
                  names.add(file.toFile().getName());
               }
               return FileVisitResult.CONTINUE;
            }

            // don't throw exceptions if files are locked/other errors occur
            public FileVisitResult visitFileFailed(Path file,
             IOException e) {

               return FileVisitResult.CONTINUE;
            }

         });
         return names;
      }
      catch (IOException ex) {
         System.out.println(ex.toString());
      }
      return null;
   }

   private static long[] readVocabTable(String indexName) {
      try {
         long[] vocabTable;
         
         RandomAccessFile tableFile = new RandomAccessFile(
          new File(indexName, "vocabTable.bin"),
          "r");
         
         byte[] byteBuffer = new byte[4];
         tableFile.read(byteBuffer, 0, byteBuffer.length);
        
         int tableIndex = 0;
         vocabTable = new long[ByteBuffer.wrap(byteBuffer).getInt() * 2];
         byteBuffer = new byte[8];
         
         while (tableFile.read(byteBuffer, 0, byteBuffer.length) > 0) { // while we keep reading 4 bytes
            vocabTable[tableIndex] = ByteBuffer.wrap(byteBuffer).getLong();
            tableIndex++;
         }
         tableFile.close();
         return vocabTable;
      }
      catch (FileNotFoundException ex) {
         System.out.println(ex.toString());
      }
      catch (IOException ex) {
         System.out.println(ex.toString());
      }
      return null;
   }

   public List<String> getFileNames() {
      return mFileNames;
   }
   
   public int getTermCount() {
      return mVocabTable.length / 2;
   }
   
   //gets the document weight of a given document id
   public double getDocWeight(int docID){
	   try{
		   RandomAccessFile docWeights = new RandomAccessFile(new 
				   File(mPath, "docWeights.bin"), "r");
		   
		   byte[] byteBuffer = new byte[8];
		   docWeights.seek(docID * 8);
		   docWeights.read(byteBuffer, 0 , byteBuffer.length);
		   docWeights.close();
		   return ByteBuffer.wrap(byteBuffer).getDouble();
	   } catch (IOException ex){
		   System.out.println(ex.toString());
		   System.exit(0);
	   }
	   return 0;
   }
   
   
}
