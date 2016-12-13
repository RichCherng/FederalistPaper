import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;

public class Bayesian{


	private CentralIndex 									aCI;
	private ArrayList<String> 								mTerms;
	private HashMap<String, HashMap<String, Double>> 		mPTCTable; // word -> (class -> ptc value)
	/** For purpose of which words belong to whom **/
	private HashMap<String, String> 						mTermAuthor;

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
					calcPTCTable(mTerms);
					return mTerms;
				}
			}
		}
		return null;
	}


	/**
	 *	Classify document in to existing classes
	 * @param pFileName
	 * @param pFileContent
	 * @return Most likely author
	 */
	public String classify(String pFileName, String pFileContent){

//		System.out.println(pFileName);
		String[] words = pFileContent.split("\\s+");

		double arg_max = 0;
		String max_class = "";
		for(String c: aCI.getClassName()){
			double pi = 1;
			for(int i = 0; i < words.length; i++){
				words[i] = words[i].replaceAll("[^a-zA-Z0-9]+" , "").toLowerCase(); // Normalize Word
				String stem = PorterStemmer.processToken(words[i]);
				if(mPTCTable.containsKey(stem)){
					pi *= mPTCTable.get(stem).get(c) * 10; // Increase by factor of 10 to help reduce number becoming too small
				}
			}

			double arg = ((double)aCI.getDocInClass(c).size() / (double) aCI.getDocList().size()) * pi;
			if( arg_max < arg){
				arg_max = arg;
				max_class 	= c;
			}
//			System.out.println(c + " : " + pi);
		}
//		System.out.println(pFileName + " : " + max_class);

		return max_class;
	}


	/**
	 * Generate PTC table from selected terms, T
	 * @param pTerms - Selected Term
	 */
	private void calcPTCTable(ArrayList<String> pTerms){

		// Calc sum of ftc e T
		HashMap<String, Double> sumFTC = new HashMap<String, Double>(); // Clas -> sum of FTC of that class
		for(String c: aCI.getClassName()){
			double sum = 0;
			for(String term: pTerms){
				sum += aCI.getTermFreqInClass(c, term);
			}
			sumFTC.put(c, sum);
		}

		// Generate PTC table
//		System.out.println("PTC Table");
		mPTCTable = new HashMap<String, HashMap<String, Double>>();
		for(String term: pTerms){
			HashMap<String, Double> row = new HashMap<String, Double>();
//			System.out.print(term + "\t");
			for(String c : aCI.getClassName()){
				// Calculate PTC for each class for the term
				row.put(c, calcPTC(c, term, sumFTC, pTerms.size()));
//				System.out.print(c+":"+row.get(c) + "\t\t");
			}
//			System.out.println();
			mPTCTable.put(term, row);
		}
	}

	private double calcPTC(String pClassName, String pTerm, HashMap<String, Double> pSumFTC, int pTermCount){
		double ftc = aCI.getTermFreqInClass(pClassName, pTerm) + 1;
		double sumFTC = pSumFTC.get(pClassName) + (double)pTermCount;

		return ftc/sumFTC;
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

		double ar1 = (N11/N) * log2( N*N11, N1x*Nx1 );
		double ar2 = (N10/N) * log2( N*N10, N1x*Nx0 );
		double ar3 = (N01/N) * log2( N*N01, N0x*Nx1 );
		double ar4 = (N00/N) * log2( N*N00, N0x*Nx0 );

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

}
