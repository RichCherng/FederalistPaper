import java.util.Scanner;

public class Main {

	public static void main(String[] args){

		Scanner reader = new Scanner(System.in);
		DirectoryParser aDP = new DirectoryParser();
		while(true){
			String line = reader.nextLine();
			aDP.parseDirectory(line);
		}
	}
}
