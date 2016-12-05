import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;

public class Bayesian{


	private CentralIndex aCI;
	private ArrayList<String> mTerms;

	/** For purpose of which words belong to whom **/
	private HashMap<String, String> mTermAuthor;

	public Bayesian(CentralIndex pCI){
		aCI = pCI;
	}


	/**
	 * Generate T term list (mutual selection)
	 * @param pTermNum = size of T list
	 */
	public ArrayList<String> generateTerm(int pTermNum){

		mTerms 		= new ArrayList<String>();
		mTermAuthor = new HashMap<String, String>();
		ArrayList<String> vocabList = aCI.getVocab(); // List of all vocab

		if(vocabList.size() < pTermNum){
			// Exit because not enough vocab
			return null;
		}

		/** Find top N term with the highest iValues **/
		HashMap<Double, ArrayList<String>> valueMap = new HashMap<Double, ArrayList<String>>();
////		ArrayList<Double> values = new ArrayList<Double>();
		HashSet<Double> values = new HashSet<Double>();
		for(String word: vocabList){
			double iValue = maxCalcI(word);
			values.add(iValue);

			if(valueMap.containsKey(iValue)){
				valueMap.get(iValue).add(word);
				values.add(iValue);
			} else {
				valueMap.put(iValue, new ArrayList<String>());
				valueMap.get(iValue).add(word);
				values.add(iValue);
			}
			values.add(iValue);
		}

		// Sort the iValue
		ArrayList<Double> iValues = new ArrayList<Double>(values);
		Collections.sort(iValues);
		Collections.reverse(iValues);
		for(Double d: iValues){

			for(String w: valueMap.get(d)){
				mTerms.add(w);
				if(mTerms.size() == pTermNum){
					return mTerms;
				}
			}

		}

		return null;

	}

	private double maxCalcI(String pWord){
		ArrayList<String> classNames = aCI.getClassName(); // Get all class name

		double max 			= 0;
		String className 	= "";

		// Loop through class
		for(String c : classNames){
			double i = calcI(c, pWord, classNames);
			if( max < i){
				max = i;
				className = c;
			}
		}

//		System.out.println(pWord + " " + max);
		mTermAuthor.put(pWord, className);
		return max;
	}


	private double calcI(String pClassName, String pWord, ArrayList<String> pClassList){

		double N 	= aCI.getDocList().size(); // # of all documents
		double N11 	= aCI.getDocCountContain(pClassName, pWord); // # of document contain word and in that class, length of fileName in posting

		double N1x 	= N11; // # of document contain the term
		for(String c : pClassList){
			if(!c.equals(pClassName)){
				N1x += aCI.getDocCountContain(c, pWord);
			}
		}

		double Nx1 = aCI.getDocInClass(pClassName).size(); // # of document in the class

		double Nx0 = N - Nx1; // # of documents not in the class
		double N0x = N - N1x; // # of docuemnts doesnt contain the word
		double N01 = Nx1 - N11;
		double N10 = N1x - N11;
		double N00 = Nx0 - N10;


//		BigDecimal  A = new BigDecimal(N * N11);
//		BigDecimal  B = new BigDecimal(N1x*Nx1);
//		System.out.println(A.toString());
//		System.out.println(N*N11);
//		System.out.println(B.toString());
//		System.out.println(N1x*Nx1);



//		double a1 = (N11/N) + ( Math.log( (N*N11) / (N1x*Nx1) ) / Math.log(2) );
//		double a2 = (N10/N) + ( Math.log( (N*N10) / (N1x*Nx0) ) / Math.log(2) );
//		double a3 = (N01/N) + ( Math.log( (N*N01) / (N0x*Nx1) ) / Math.log(2) );
//		double a4 = (N00/N) + ( Math.log( (N*N00) / (N0x*N0x) ) / Math.log(2) );

		double ar1 = (N11/N) + log2( N*N11, N1x*Nx1 );
		double ar2 = (N10/N) + log2( N*N10, N1x*Nx0 );
		double ar3 = (N01/N) + log2( N*N01, N0x*Nx1 );
		double ar4 = (N00/N) + log2( N*N00, N0x*N0x );
//		System.out.println(N00 + " " + (N0x - N01));

//				System.out.println(pWord + " : "  + N11);
//		System.out.println(pWord + "  "+ N11 + " : " + N1x);
//		System.out.println(Nx1);
//		System.out.println(Nx0);
//		System.out.println(N0x);

//		System.out.println(Math.log(0));
//		System.out.println(ar1);
//		System.out.println(ar2);
//		System.out.println(ar3);
//		System.out.println(ar4);
//		System.out.println((float)(N*N11) / (float)(N1x*Nx1));
//		System.out.println((float)(N*N10) / (float)(N1x*Nx0));
//		System.out.println((float)(N*N01) / (float)(N0x*Nx1));
//		System.out.println((float)(N*N00) / (float)(N0x*N0x));
		return ar1 + ar2 + ar3 + ar4;
	}

	private double log2(double top, double bottom){
		if (top * bottom == 0){
			// Contain 0
			return 0.0;
		}

		return Math.log( top/ bottom) / Math.log(2);
	}

	public void printSelectedWord(){
		for(String s: mTerms){
			System.out.println(s + " : " + mTermAuthor.get(s));
		}
	}

	public void debug(){
//		System.out.println()
	}

}
