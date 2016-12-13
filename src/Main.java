import java.util.ArrayList;
import java.util.Scanner;

public class Main {

	static String PATH_ALL 					= "FederalistPaper/ALL";
	static String PATH_MADISON 				= "FederalistPaper/MADISON";
	static String PATH_HAMILTON 			= "FederalistPaper/HAMILTON";
	static String PATH_JAY 					= "FederalistPaper/JAY";
	static String PATH_HAMILTON_AND_MADISON = "FederalistPaper/HAMILTON AND MADISON";
	static String PATH_HAMILTON_OR_MADISON 	= "FederalistPaper/HAMILTON OR MADISON";
	static String PATH_TO_TEST              = "FederalistPaper/TEST";
    static String PATH_TO_TEST_UNKNOWN      = "FederalistPaper/TEST_NEW_DOCS";

	public static void main(String[] args){

		Scanner reader 		= new Scanner(System.in);
		DirectoryParser aDP = new DirectoryParser();
		CentralIndex aCI	= new CentralIndex();
		Bayesian aB 		= new Bayesian(aCI);

		RocchioClassification aRC = new RocchioClassification();
		DirectoryParser queryDirectoryParse = new DirectoryParser();
		CentralIndex queryIndex             = new CentralIndex();


		/**** 	Indexing 	****/
		aDP.parseDirectory(PATH_HAMILTON, new Callback(){

			@Override
			public void func(String pFileName, String pFileContent) {
				// TODO Auto-generated method stub
				aCI.index("HAMILTON", pFileName, pFileContent);
			}
		});

		aDP.parseDirectory(PATH_MADISON, new Callback(){

			@Override
			public void func(String pFileName, String pFileContent) {
				// TODO Auto-generated method stub
				aCI.index("MADISON", pFileName, pFileContent);
			}
		});

		aDP.parseDirectory(PATH_JAY, new Callback(){

			@Override
			public void func(String pFileName, String pFileContent) {
				// TODO Auto-generated method stub
				aCI.index("JAY", pFileName, pFileContent);
			}
		});

		/** Query directory parsing and indexing **/
		queryDirectoryParse.parseDirectory(PATH_HAMILTON_OR_MADISON, new Callback() {
            @Override
            public void func(String pFileName, String pFileContent) {
                queryIndex.index("Unkown", pFileName, pFileContent);
            }
        });

//        aDP.parseDirectory(PATH_TO_TEST, new Callback() {
//            @Override
//            public void func(String a, String b) {
//                aCI.index("TEST", a, b);
//            }
//        });
//        queryDirectoryParse.parseDirectory(PATH_TO_TEST_UNKNOWN, new Callback() {
//            @Override
//            public void func(String a, String b) {
//                queryIndex.index("UNKNOWN", a, b);
//            }
//        });

		aRC.setCentralIndex(aCI);
		aRC.setQueryIndex(queryIndex);
		aRC.classifyTrainingData();
		System.out.println("Rocchio: ");
        aRC.classifyUnknownDocument();
        System.out.println("Rocchio finished.\n");

		/************************************************************/

		aB.generateTerm(50);
//		aB.printSelectedWord();

		/** Classify **/
		ArrayList<String> results = new ArrayList<String>();
		aDP.parseDirectory(PATH_HAMILTON_OR_MADISON, new Callback(){

			@Override
			public void func(String pFileName, String pFileContent) {
				// TODO Auto-generated method stub
				results.add("Bayesian : " + pFileName + "  " + aB.classify(pFileName, pFileContent));
			}
		});

		for(String r: results){
			System.out.println(r);
		}

	}
}
