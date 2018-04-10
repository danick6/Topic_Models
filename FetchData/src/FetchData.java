import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintStream;
import java.util.ArrayList;

import models.Article;
import services.FetchDataConstants;
import services.FetchDataServices;

public class FetchData {

	public static void main (String args[]) throws Exception{
		
		try{
			PrintStream fileStream = new PrintStream(new File("src/log.txt"));
			PrintStream console = System.out;

	        System.setOut(fileStream);
			
			FileReader en = new FileReader("src/selected_categories.txt");
			ArrayList<String> _file = ReadFile.readFile(en);
			
			int sumEN = 0;
			int sumES = 0;
			
			for (int i = 0; i < _file.size() && _file.size() == FetchDataConstants.Min_PAG; i++){
				// Obtenemos la categoria en ingles
				String en_categoryName = _file.get(i);

				// Obtenemos la categoria en espaniol
				String es_categoryName = FetchDataServices.getEs_Category(FetchDataServices.getCategoryId(en_categoryName));
				
				// Obtenemos de la categoria en ingles, sus articulos en ingles
				ArrayList<Article> lista = FetchDataServices.sendGetArticleList(_file.get(i));
				
				// Obtenemos de los articulos en ingles, sus respectivos pero en espaniol
				int es_pages = 0;
				int en_pages = lista.size();
				
				if (lista != null){
					for(int x=0; x < lista.size(); x++){
						String es_Title = FetchDataServices.getEs_Title(lista.get(x).getPageid());
						if (es_Title != null){
							es_pages++;
						}
					}
					sumEN += en_pages;
					sumES += es_pages;
					
					System.out.println("Categoria \"" + es_categoryName + "\" con " + es_pages + " paginas"); 
					System.out.println("Category \"" + en_categoryName + "\" with " + en_pages + " pages");
					System.out.println("-----------------------------------------------------------------------------------");
				}
				
			}
			
			sumEN = sumEN/FetchDataConstants.Min_PAG;
			sumES = sumES/FetchDataConstants.Min_PAG;
			System.out.println("La media de articulos en espaniol es: " + sumES);
			System.out.println("Average pages in english is: " + sumEN);
		}
		catch(FileNotFoundException e){
			System.err.println("Error: Fichero no existe");
		}

		//String categoryName = "Computer arithmetic";
		//String categoryName2 = "Technical drawing";
		
/*		String en_categoryName = "Women archaeologists";
		String es_categoryName = FetchDataServices.getEs_Category(FetchDataServices.getCategoryId(en_categoryName));
		
		ArrayList<Article> lista = FetchDataServices.sendGetArticleList(en_categoryName);
		int j = 0;
		if (lista != null){
			for(int i=0; i < lista.size(); i++){
				//System.out.println(lista.get(i).getTitle());
				String es_Title = FetchDataServices.getEs_Title(lista.get(i).getPageid());
				if (es_Title != null){
					//System.out.println("Titulo en espaniol: " +es_Title);
					j++;
				}
			}
			System.out.println("Categoria \"" + es_categoryName + "\" con " + j + " paginas ------ Category \"" 
					+ en_categoryName + "\" with " + lista.size() + " pages");
		}*/	

	}


}
