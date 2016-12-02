import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class Bayesian{

	private HashMap<String, DocumentClass> 	mClassMap;
	private HashSet<String> 				mDistinctVocab;
	private HashMap<String, Float> 			mTermScore;

	public Bayesian(){

		mClassMap 		= new HashMap<String, DocumentClass>();
		mDistinctVocab 	= new HashSet<String>();
		mTermScore 		= new HashMap<String, Float>();
	}

	public void learn(String pClass,String pFileName, String pFileContent){

		DocumentClass dClass = null;
		if(mClassMap.containsKey(pClass)){
			dClass = mClassMap.get(pClass);
		} else {
			dClass = new DocumentClass(pClass);
			mClassMap.put(pClass, dClass);
		}


		/** Parse File Content **/
		dClass.addFile(pFileName);
		String[] words = pFileContent.split("\\s+");
		for(int i = 0; i < words.length; i++){
			// Normalize word
			words[i] = words[i].replaceAll("[^a-zA-Z0-9]+" , "").toLowerCase();
			mDistinctVocab.add(words[i]);
			dClass.addTerm(words[i]);
		}
	}

	/**
	 * Generate T term list (mutual selection)
	 * @param pTermNum = size of T list
	 */
	public void generateTerm(int pTermNum){

		// For each word in the vocab, calculate i
		for(String term: mDistinctVocab){

			float max = -1;
			String author = "";
			for(Map.Entry<String, DocumentClass> entry: mClassMap.entrySet()){
				float iValue = entry.getValue().calcI(term);
				if (max < iValue){
					max = iValue;
					author = entry.getValue().getName();
				}
			}

			System.out.println(author + ": " + term + " = " + max);

		}

	}

	private class DocumentClass{

		private String mName;
		private HashSet<String> mFileName;
		private HashMap<String, Integer> mTermFreq;

		public DocumentClass(String pName){
			mName 		= pName;
			mFileName 	= new HashSet<String>();
			mTermFreq 	= new HashMap<String, Integer>();
		}

		public String getName(){
			return mName;
		}

		public void addFile(String pFileName){
			mFileName.add(pFileName);
		}

		public void addTerm(String pTerm){

			if(mTermFreq.containsKey(pTerm)){
				mTermFreq.put(pTerm, mTermFreq.get(pTerm) + 1);
			} else {
				mTermFreq.put(pTerm, 1);
			}

		}

		public float calcI(String pTerm){
			return 0.0f;
		}


	}

}
