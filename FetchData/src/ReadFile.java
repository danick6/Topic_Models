import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class ReadFile {
	
	//Readfile, lee de un fichero una serie de categorias separadas por saltos de linea
	public static ArrayList<String> readFile(FileReader file) throws IOException{
		ArrayList<String> list = new ArrayList<String>();
		
		try (BufferedReader br = new BufferedReader(file)) {
		    String line;
		    while ((line = br.readLine()) != null) {
		       list.add(line);
		    }   
		}
		return list;
	}
}
