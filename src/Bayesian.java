import java.util.HashMap;

public class Bayesian{

	private HashMap<String, DocumentClass> mClassList;

	public Bayesian(){

		mClassList = new HashMap<String, DocumentClass>();
	}

	public void learn(String pClass,String pFile){
//		System.out.println(file);
		if(mClassList.containsKey(pClass)){
			DocumentClass c = mClassList.get(pClass);
		} else {
			mClassList.put(pClass, new DocumentClass());
		}
	}


	private class DocumentClass{

		public DocumentClass(){
		}
	}

}
