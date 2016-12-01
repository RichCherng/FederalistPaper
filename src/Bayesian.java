import java.util.HashMap;

public class Bayesian{

	private HashMap<String, DocumentClass> mClassList;

	public Bayesian(){

		mClassList = new HashMap<String, DocumentClass>();
	}

	public void learn(String pClass,String pFileName, String pFileContent){
//		System.out.println(file);
		if(mClassList.containsKey(pClass)){
			DocumentClass c = mClassList.get(pClass);
		} else {
			mClassList.put(pClass, new DocumentClass());
		}

		System.out.println(pFileName);
		System.out.println(pFileContent);
	}


	private class DocumentClass{

		public DocumentClass(){
		}
	}

}
