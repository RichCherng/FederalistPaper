import java.util.ArrayList;

public class Bayesian{


	private CentralIndex aCI;

	public Bayesian(CentralIndex pCI){
		aCI = pCI;
	}


	/**
	 * Generate T term list (mutual selection)
	 * @param pTermNum = size of T list
	 */
	public void generateTerm(int pTermNum){

		ArrayList<String> vocabList = aCI.getVocab();

		for(String word: vocabList){
			double iValue = maxCalcI(word);
		}

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

		// Could keep track of whoes word got pick here

		return 0.0;
	}


	private double calcI(String pClassName, String pWord, ArrayList<String> pClassList){

		double N 	= aCI.getDocList().size(); // # of all documents
		double N11 = aCI.getDocCountContain(pClassName, pWord); // # of document contain word and in that class, length of fileName in posting

		double N1x = N11; // # of document contain the term
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

		double ar1 = (N11/N) + ( Math.log( (N*N11) / (N1x*Nx1) ) / Math.log(2) );
		double ar2 = (N10/N) + ( Math.log( (N*N10) / (N1x*Nx0) ) / Math.log(2) );
		double ar3 = (N01/N) + ( Math.log( (N*N01) / (N0x*Nx1) ) / Math.log(2) );
		double ar4 = (N00/N) + ( Math.log( (N*N00) / (N0x*N0x) ) / Math.log(2) );

//		System.out.println(N00 + " " + (N0x - N01));

//				System.out.println(pWord + " : "  + N11);
//		System.out.println(pWord + "  "+ N11 + " : " + N1x);
//		System.out.println(Nx1);
//		System.out.println(Nx0);
//		System.out.println(N0x);
//		System.out.println(pWord + " : " + (ar1 + ar2 + ar3 + ar4));
		return ar1 + ar2 + ar3 + ar4;
	}

	public void debug(){
//		System.out.println()
	}

}
