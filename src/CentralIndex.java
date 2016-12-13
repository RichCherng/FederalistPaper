import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class CentralIndex {

	private HashSet<String> 				mDocList; // List of document's name
	private HashSet<String> 				mVocab; // List of all vocab
	private HashSet<String>					mClassNames; // List of all classes
	private HashMap<String, DocumentClass> 	mClassMap; // Map of author to their class
	private HashMap<String, Document>		mDocMap; // Map od document name to their class

	public CentralIndex(){
		mVocab 		= new HashSet<String>();
		mDocList 	= new HashSet<String>();
		mClassNames = new HashSet<String>();
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
 			mClassNames.add(pClassName);
 		}

		// Document
		Document doc = new Document(pFileName);
		mDocMap.put(pFileName, doc); // Overwrite the old one if exists


		/** Parse File Content **/
		String[] words = pFileContent.split("\\s+");
		for(int i = 0; i < words.length; i++){

			words[i] = words[i].replaceAll("[^a-zA-Z0-9]+" , "").toLowerCase(); // Normalize Word
			String stem = PorterStemmer.processToken(words[i]);
			mVocab.add(stem); // Add word to vocab list
			dClass.add(stem, pFileName); // put word into class
			doc.addTerm(stem); // Put word into doc info
		}
	}

	public void debug(){

		for(String s: mClassNames){
			System.out.println(s);
		}

		DocumentClass author = mClassMap.get("HAMILTON");
		author.getPosting("hi");
	}

	public ArrayList<String> getTermInDoc(String pDocName){
		return mDocMap.get(pDocName).getTerms();
	}

	public int getTermFreq(String pDocName, String term){
		return mDocMap.get(pDocName).getTermFreq(term);
	}

	/**
	 * List of document indexed
	 * @return
	 */
	public ArrayList<String> getDocList(){
		return new ArrayList<String>(mDocList);
	}


	/**
	 * Get the document count of that word in the specificed class
	 * @param pClassName
	 * @param pWord
	 * @return
	 */
	public int getDocCountContain(String pClassName, String pWord){
		DocumentClass author 	= mClassMap.get(pClassName);
		Posting p 				= author.getPosting(pWord);

		if( p == null){
			return 0;
		} else {
			return p.getFileName().size();
		}
	}

	public int getTermFreqInClass(String pClassName, String pWord){
		DocumentClass author = mClassMap.get(pClassName);
		Posting p = author.getPosting(pWord);

		if(p == null){
			return 0;
		} else {
			return p.getFrequency();
		}
	}

	/**
	 * get Document list in that class
	 * @param pClass
	 * @return
	 */
	public ArrayList<String> getDocInClass(String pClass){
		DocumentClass author = mClassMap.get(pClass);
		return new ArrayList<String>(author.getDocList());
	}

	/**
	 * Get list of vocab that indexed
	 * @return
	 */
	public ArrayList<String> getVocab(){
		return new ArrayList<String>(mVocab);
	}

	/**
	 * Get list of class name
	 * @return
	 */
	public ArrayList<String> getClassName(){
		return new ArrayList<String>(mClassNames);
	}

	private class Document{

		private String 						mName;
		private HashMap<String, Integer> 	termFreq;
		private HashSet<String> 			mTermList;
		public Document(String pName){

			mName 		= pName;
			termFreq 	= new HashMap<String, Integer>();
			mTermList	= new HashSet<String>();
		}

		public void addTerm(String pTerm){

			String stem = PorterStemmer.processToken(pTerm);
			mTermList.add(stem);
			if(termFreq.containsKey(stem)){
				termFreq.put(stem, termFreq.get(stem)+ 1);
			} else {
				termFreq.put(stem, 1);
			}
		}

		public ArrayList<String> getTerms(){
			return new ArrayList<String>(mTermList);
		}

		public int getTermFreq(String pTerm){
			return termFreq.get(pTerm);
		}


	}


	private class DocumentClass{

		private String mName;
		private HashSet<String> mDocList;
		private HashMap<String, Posting> mPosting;

		public DocumentClass(String pName){
			mName 		= pName;
			mDocList 	= new HashSet<String>();
			mPosting 	= new HashMap<String, Posting>();
		}

		public String getName(){
			return mName;
		}

		public void add(String pTerm, String pDocName){

			mDocList.add(pDocName);

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

		public HashSet<String> getDocList(){
			return mDocList;
		}

		public Posting getPosting(String pTerm){
			return mPosting.get(pTerm);
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

//		public ArrayList<String> getPosting(){
//			return new ArrayList<String>(mFileName);
//		}

		public HashSet<String> getFileName(){
			return mFileName;
		}

		public int getFrequency(){
			return freq;
		}

		public void addFreq(){
			freq++;
		}
	}
}
