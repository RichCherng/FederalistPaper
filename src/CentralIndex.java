import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class CentralIndex {

	private HashSet<String> 				mDocList; // List of document's name
	private HashSet<String> 				mVocab; // List of all vocab
	private HashMap<String, DocumentClass> 	mClassMap; // Map of author to their class
	private HashMap<String, Document>		mDocMap; // Map od document name to their class

	public CentralIndex(){
		mVocab 		= new HashSet<String>();
		mDocList 	= new HashSet<String>();
		mClassMap 	= new HashMap<String, DocumentClass>();
		mDocMap		= new HashMap<String, Document>();
	}

	public void index(String pClassName, String pFileName, String pFileContent){

		// Document List
		mDocList.add(pFileName);

		// Class such as HAMILTON, JAY, MADISON
		DocumentClass dClass = null;
		if(mClassMap.containsKey(pClassName)){
			dClass = mClassMap.get(pClassName);
 		} else {
 			dClass = new DocumentClass(pClassName);
 			mClassMap.put(pClassName, dClass);
 		}

		// Document
		Document doc = new Document(pFileName);
		mDocMap.put(pFileName, doc); // Overwrite the old one if exists


		/** Parse File Content **/
		String[] words = pFileContent.split("\\s+");
		for(int i = 0; i < words.length; i++){

			words[i] = words[i].replaceAll("[^a-zA-Z0-9]+" , "").toLowerCase(); // Normalize Word

			mVocab.add(words[i]); // Add word to vocab list
			dClass.add(words[i], pFileName); // put word into class
			doc.addTerm(words[i]); // Put word into doc info
		}
	}

	private class Document{

		private String 						mName;
		private HashMap<String, Integer> 	termFreq;
		public Document(String pName){

			mName 		= pName;
			termFreq 	= new HashMap<String, Integer>();
		}

		public void addTerm(String pTerm){

			String stem = PorterStemmer.processToken(pTerm);
			if(termFreq.containsKey(stem)){
				termFreq.put(stem, termFreq.get(stem)+ 1);
			} else {
				termFreq.put(stem, 1);
			}
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

			p.addDoc(pDocName); // Adding to hashset, no duplicate
			p.addFreq(); // Count the word frequency

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

		public ArrayList<String> getPosting(){
			return new ArrayList<String>(mFileName);
		}

		public int getFrequency(){
			return freq;
		}

		public void addFreq(){
			freq++;
		}
	}
}
