import java.util.HashMap;
import java.util.HashSet;

public class CentralIndex {

	private HashSet<String> 				mVocab;
	private HashMap<String, DocumentClass> 	mClassMap;

	public CentralIndex(){
		mVocab 		= new HashSet<String>();
		mClassMap 	= new HashMap<String, DocumentClass>();
	}

	public void index(String pClassName, String pFileName, String pFileContent){

		DocumentClass dClass = null;
		if(mClassMap.containsKey(pClassName)){
			dClass = mClassMap.get(pClassName);
 		} else {
 			dClass = new DocumentClass(pClassName);
 			mClassMap.put(pClassName, dClass);
 		}

		/** Parse File Content **/
		String[] words = pFileContent.split("\\s+");
		for(int i = 0; i < words.length; i++){
			// Normalize Word
			words[i] = words[i].replaceAll("[^a-zA-Z0-9]+" , "").toLowerCase();
			mVocab.add(words[i]);
			dClass.add(words[i], pFileName);
		}
	}


	private class DocumentClass{

		private String mName;
		private HashMap<String, Posting> mPosting;

		public DocumentClass(String pName){
			mName 		= pName;
			mPosting 	= new HashMap<String, Posting>();
		}

		public String getName(){
			return mName;
		}

		public void add(String pTerm, String pDocName){

			Posting p = null;
			if(mPosting.containsKey(pTerm)){
				// Term already exists
				p = mPosting.get(pTerm);
			} else {
				// Doesn't exist, create new posting
				p = new Posting();
				mPosting.put(pTerm, p);
			}

			p.addDoc(pDocName);
			p.addFreq();

		}
	}

	private class Posting{


		private HashSet<String> mFileName;
		private int freq;

		public Posting(){
			mFileName 	= new HashSet<String>();
			freq 		= 0;
		}

		public void addDoc(String pDocName){
			mFileName.add(pDocName);
		}

		public int getFrequency(){
			return freq;
		}

		public void addFreq(){
			freq++;
		}
	}
}
