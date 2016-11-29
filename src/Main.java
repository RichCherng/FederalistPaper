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
//		while(true){
//			String line = reader.nextLine();
//			aDP.parseDirectory(line);
//		}

		Bayesian aB = new Bayesian();
		aDP.parseDirectory(PATH_HAMILTON, new Callback(){
			@Override
			public void func(String pDocPath) {
				// TODO Auto-generated method stub
				aB.learn("HAMILTON", pDocPath);
			}
		});
	}
}
