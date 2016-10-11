import java.util.Scanner;
import java.util.regex.*;

public class PorterStemmer {

   // a single consonant
   private static final String c = "[^aeiou]";
   // a single vowel
   private static final String v = "[aeiouy]";

   // a sequence of consonants; the second/third/etc consonant cannot be 'y'
   private static final String C = c + "[^aeiouy]*";
   // a sequence of vowels; the second/third/etc cannot be 'y'
   private static final String V = v + "[aeiou]*";

   // this regex pattern tests if the token has measure > 0 [at least one VC].
   private static final Pattern mGr0 = Pattern.compile("^(" + C + ")?" + V + C);
   
   //private static final Pattern mGr0 = Pattern.compile("^(" + C + ")?" +
    //"(" + V + C + ")+(" + V + ")?");

   // add more Pattern variables for the following patterns:
   
   // m equals 1: token has measure == 1
   private static final Pattern mEq1 = Pattern.compile("^(" + C + ")?" + V + C+ "$");
   
   // m greater than 1: token has measure > 1
   private static final Pattern mGr1 = Pattern.compile("^(" + C + ")?" + V + C + V + C);
   
   // vowel: token has a vowel after the first (optional) C
   private static final Pattern vwl = Pattern.compile("^(" + C + ")?" + V + "(.*)");
   
   // double consonant: token ends in two consonants that are the same, 
   //			unless they are L, S, or Z. (look up "backreferencing" to help 
   //			with this)
   private static final Pattern dblC = Pattern.compile("^(.*)([^aeiou])\\2$" );
   
   // m equals 1, Cvc: token is in Cvc form, where the last c is not w, x, 
   //			or y.
   private static final Pattern o = Pattern.compile("^(" + C + ")?" + v + "[^aeiouwxy]");
   
   public static void main(String[] args) {
	  Scanner user_input = new Scanner(System.in);
      System.out.print("Enter a term to stem: ");
      String token = user_input.next().toLowerCase();
      System.out.println(processToken(token));
      
   }
   public static String processToken(String token) {
      if (token.length() < 3) {
         return token; // token must be at least 3 chars
      }
      // step 1a
      if (token.endsWith("sses")) {
         token = token.substring(0, token.length() - 2);
      }else if (token.endsWith("ies")){
    	  token = token.substring(0, token.length() - 2);
      }else if(token.endsWith("ss")){
    	  
      }else if(token.endsWith("s") && !token.endsWith("ss")){
    	  token = token.substring(0, token.length() - 1);
      }
      // program the other steps in 1a. 
      // note that Step 1a.3 implies that there is only a single 's' as the 
      //	suffix; ss does not count. you may need a regex pattern here for 
      // "not s followed by s".

      // step 1b
      boolean doStep1bb = false;
      //		step 1b
      if (token.endsWith("eed")) { // 1b.1
         // token.substring(0, token.length() - 3) is the stem prior to "eed".
         // if that has m>0, then remove the "d".
         String stem = token.substring(0, token.length() - 3);
         if (mGr0.matcher(stem).find()) { // if the pattern matches the stem
            token = stem + "ee";
         }
      }else if (token.endsWith("ed")){
    	  String stem = token.substring(0, token.length() - 2);
    	  if(vwl.matcher(stem).find()){
    		  token = stem; 
    		  doStep1bb = true;
    	  }
    	  
      }else if (token.endsWith("ing")){
    	  String stem = token.substring(0, token.length()-3);
    	  if(vwl.matcher(stem).find()){
    		  token = stem;
    		  doStep1bb = true;
    	  }
      }
      // program the rest of 1b. set the boolean doStep1bb to true if Step 1b* 
      // should be performed.

      // step 1b*, only if the 1b.2 or 1b.3 were performed.
      if (doStep1bb) {
         if (token.endsWith("at") || token.endsWith("bl")
          || token.endsWith("iz")) {

            token = token + "e";
         }else if(dblC.matcher(token).find()){ 
    		 if(!token.endsWith("l") && !token.endsWith("s") && !token.endsWith("z")){
    			 token = token.substring(0, token.length() - 1);
    		 }
         }else if(o.matcher(token).find()){
        	 token = token + "e";
         }
        
         // use the regex patterns you wrote for 1b*.4 and 1b*.5
      }

      // step 1c
      // program this step. test the suffix of 'y' first, then test the 
      // condition *v*.

      if(token.endsWith("y")){
    	  if(vwl.matcher(token).find() && mGr0.matcher(token).find()){
    		  token = token.substring(0, token.length() - 1) + "i";
    	  }
      }
      
      
      
      

      // step 2
      // program this step. for each suffix, see if the token ends in the 
      // suffix. 
      //		* if it does, extract the stem, and do NOT test any other suffix.
      //    * take the stem and make sure it has m > 0.
      //			* if it does, complete the step. if it does not, do not 
      //				attempt any other suffix.
      // you may want to write a helper method for this. a matrix of 
      // "suffix"/"replacement" pairs might be helpful. It could look like
      // string[][] step2pairs = {  new string[] {"ational", "ate"}, 
      //										new string[] {"tional", "tion"}, ....
      
      if(token.endsWith("ational")){
    	  token = suffixReplacer(token, "ational", "ate");  	  
      } else if(token.endsWith("tional")){
    	  token = suffixReplacer(token, "tional", "tion");
      }else if(token.endsWith("enci")){
    	  token = suffixReplacer(token, "enci", "ence");
      }else if(token.endsWith("anci")){
    	  token = suffixReplacer(token, "anci", "ance");
      }else if(token.endsWith("izer")){
    	  token = suffixReplacer(token, "izer", "ize");
      }else if(token.endsWith("abli")){
    	  token = suffixReplacer(token, "abli", "able");
      }else if(token.endsWith("alli")){
    	  token = suffixReplacer(token, "alli", "al");
      }else if(token.endsWith("entli")){
    	  token = suffixReplacer(token, "entli", "ent");
      }else if(token.endsWith("eli")){
    	  token = suffixReplacer(token, "eli", "e");
      }else if(token.endsWith("ousli")){
    	  token = suffixReplacer(token, "ousli", "ous");
      }else if(token.endsWith("ization")){
    	  token = suffixReplacer(token, "ization", "ize");
      }else if(token.endsWith("ation")){
    	  token = suffixReplacer(token, "ation", "ate");
      }else if(token.endsWith("ator")){
    	  token = suffixReplacer(token, "ator", "ate");
      }else if(token.endsWith("alism")){
    	  token = suffixReplacer(token, "alism", "al");
      }else if(token.endsWith("iveness")){
    	  token = suffixReplacer(token, "iveness", "ive");
      }else if(token.endsWith("fulness")){
    	  token = suffixReplacer(token, "fulness", "ful");
      }else if(token.endsWith("ousness")){
    	  token = suffixReplacer(token, "ousness", "ous");
      }else if(token.endsWith("aliti")){
    	  token = suffixReplacer(token, "aliti", "al");
      }else if(token.endsWith("iviti")){
    	  token = suffixReplacer(token, "iviti", "ive");
      }else if(token.endsWith("biliti")){
    	  token = suffixReplacer(token, "biliti", "ble");
      }
      


      // step 3
      // program this step. the rules are identical to step 2 and you can use
      // the same helper method. you may also want a matrix here.
      if(token.endsWith("icate")){
    	  token = suffixReplacer(token, "icate", "ic");
      }else if(token.endsWith("ative")){
    	  token = suffixReplacer(token, "ative", "");
      }else if(token.endsWith("alize")){
    	  token = suffixReplacer(token, "alize", "al");
      }else if(token.endsWith("iciti")){
    	  token = suffixReplacer(token, "iciti", "ic");
      }else if(token.endsWith("ical")){
    	  token = suffixReplacer(token, "ical", "ic");
      }else if(token.endsWith("ful")){
    	  token = suffixReplacer(token, "ful", "");
      }else if(token.endsWith("ness")){
    	  token = suffixReplacer(token, "ness", "");
      }


      // step 4
      // program this step similar to step 2/3, except now the stem must have
      // measure > 1.
      // note that ION should only be removed if the suffix is SION or TION, 
      // which would leave the S or T.
      // as before, if one suffix matches, do not try any others even if the 
      // stem does not have measure > 1.
      if(token.endsWith("al")){
    	  token = suffixReplacermGr1(token, "al");
      }else if(token.endsWith("ance")){
    	  token = suffixReplacermGr1(token, "ance");
      }else if(token.endsWith("ence")){
    	  token = suffixReplacermGr1(token, "ence");
      }else if(token.endsWith("er")){
    	  token = suffixReplacermGr1(token, "er");
      }else if(token.endsWith("ic")){
    	  token = suffixReplacermGr1(token, "ic");
      }else if(token.endsWith("able")){
    	  token = suffixReplacermGr1(token, "able");
      }else if(token.endsWith("ement")){
    	  token = suffixReplacermGr1(token, "ement");
      }else if(token.endsWith("ment")){
    	  token = suffixReplacermGr1(token, "ment");
      }else if(token.endsWith("ent")){
    	  token = suffixReplacermGr1(token, "ent");
      }else if(token.endsWith("sion") || token.endsWith("tion")){
    	  token = suffixReplacermGr1(token, "ion");
      }else if(token.endsWith("ou")){
    	  token = suffixReplacermGr1(token, "ou");
      }else if(token.endsWith("ism")){
    	  token = suffixReplacermGr1(token, "ism");
      }else if(token.endsWith("ate")){
    	  token = suffixReplacermGr1(token, "ate");
      }else if(token.endsWith("iti")){
    	  token = suffixReplacermGr1(token, "iti");
      }else if(token.endsWith("ous")){
    	  token = suffixReplacermGr1(token, "ous");
      }else if(token.endsWith("ive")){
    	  token = suffixReplacermGr1(token, "ive");
      }else if(token.endsWith("ize")){
    	  token = suffixReplacermGr1(token, "ize");
      }


      // step 5
      // program this step. you have a regex for m=1 and for "Cvc", which
      // you can use to see if m=1 and NOT Cvc.
      // all your code should change the variable token, which represents
      // the stemmed term for the token.
      if(mGr1.matcher(token).find() && token.endsWith("e")){
    	  token = token.substring(0, token.length() - 1);
      }else if(mEq1.matcher(token).find() && !o.matcher(token.substring(0,token.length() - 1)).find() && token.endsWith("e") ){
    	  token = token.substring(0,token.length() - 1);
      }else if(mGr1.matcher(token).find() && token.endsWith("ll")){
    	  token = token.substring(0, token.length() - 1);
	  }
      

      return token;
   }
   
   public static String suffixReplacer(String token, String suffix, String newSuffix){
	   String stem = token.substring(0, token.length() - suffix.length());
	   if(mGr0.matcher(stem).find()){
		   token = stem + newSuffix;
	   }
	   return token;
   }
   
   public static String suffixReplacermGr1(String token, String suffix){
	   String stem = token.substring(0, token.length() - suffix.length());
	   if(mGr1.matcher(stem).find()){
		   token = stem;
	   }
	   return token;
   }
   
}
