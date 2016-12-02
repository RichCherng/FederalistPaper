import java.util.Scanner;

public class Main {

	static String PATH_ALL 					= "FederalistPaper/ALL";
	static String PATH_MADISON 				= "FederalistPaper/MADISON";
	static String PATH_HAMILTON 			= "FederalistPaper/HAMILTON";
	static String PATH_JAY 					= "FederalistPaper/JAY";
	static String PATH_HAMILTON_AND_MADISON = "FederalistPaper/HAMILTON AND MADISON";
	static String PATH_HAMILTON_OR_MADISON 	= "FederalistPaper/HAMILTON OR MADISON";

	public static void main(String[] args){

		Scanner reader = new Scanner(System.in);
		DirectoryParser aDP = new DirectoryParser();


		Bayesian aB = new Bayesian();
		aDP.parseDirectory(PATH_HAMILTON, new Callback(){
			@Override
			public void func(String pFileName, String pFileContent) {
				// TODO Auto-generated method stub
				aB.learn("HAMILTON", pFileName, pFileContent);
			}
		});

		aDP.parseDirectory(PATH_MADISON, new Callback(){
			@Override
			public void func(String pFileName, String pFileContent) {
				// TODO Auto-generated method stub
				aB.learn("MADISON", pFileName, pFileContent);
			}
		});

		aDP.parseDirectory(PATH_JAY, new Callback(){
			@Override
			public void func(String pFileName, String pFileContent) {
				// TODO Auto-generated method stub
				aB.learn("JAY", pFileName, pFileContent);
			}
		});

		aB.generateTerm(50);

	}
}
