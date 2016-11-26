import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

public class DirectoryParser {

	public DirectoryParser(){

	}

	/**
	 * Root is the project folders
	 * Go through all the documents in the directory and parse it.
	 * @return true if successful, otherwise false
	 * @param  directory
	 */
	public boolean parseDirectory(String pDir){

		Path directory = Paths.get(pDir).toAbsolutePath();

		if(!Files.exists(directory)){
			System.out.println("Directory doesn't exists");
			return false;
		}

		try{
			System.out.println(directory.toString());

			Files.walkFileTree(directory, new SimpleFileVisitor<Path>(){
				int docNum = 0;
				public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs){

					// make sure we only process the current working directory
					if(directory.equals(dir)){
						return FileVisitResult.CONTINUE;
					}
					return FileVisitResult.SKIP_SUBTREE;
				}

				public FileVisitResult visitFile(Path file, BasicFileAttributes attr){
					// only process .txt file
					if(file.toString().endsWith(".txt")){
						docNum++;
						System.out.println(file.toString());

						// do something with document
					}

					return FileVisitResult.CONTINUE;
				}

				// don't throw exceptions if files are locked/other errors occur
				public FileVisitResult visitFileFailed(Path file, IOException e){
					return FileVisitResult.CONTINUE;
				}
			});
		}catch (Exception ex){
			return false;
		}



		return true;
	}
}
