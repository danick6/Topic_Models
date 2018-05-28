import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintStream;
import java.util.ArrayList;

import models.Article;
import services.FetchDataConstants;
import services.FetchDataServices;
import services.FetchDataUtils;

public class FetchData {

	public static void main (String args[]) throws Exception{
		
		try{
			FileWriter en_fw = new FileWriter("en-corpus.csv");
			FileWriter es_fw = new FileWriter("es-corpus.csv");
			BufferedWriter en_bw = new BufferedWriter(en_fw);
			BufferedWriter es_bw = new BufferedWriter(es_fw);
			
			PrintStream fileStream = new PrintStream(new File("src/log.txt"));
			//PrintStream console = System.out;

	        //System.setOut(fileStream);
			
			FileReader en = new FileReader("src/selected_categories.txt");
			// Lee el fichero de categorias en ingles y las pone en una lista.
			ArrayList<String> _file = ReadFile.readFile(en);
			
			int sumEN = 0;
			int sumES = 0;
			int num_art = 0;
			
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
					for(int x=0; x < lista.size() && es_pages < 50; x++){
						// Obtenemos los dos titulos de los articulos
						String en_Title = lista.get(x).getTitle();
						String es_Title = FetchDataServices.getEs_Title(lista.get(x).getPageid());
						
						// Se comprueba de que el articulo existe en espaniol
						if (es_Title != null){
							
							// Obtenemos el texto del articulo en ambos idiomas
							String en_Article = FetchDataServices.getArticle(en_Title,"EN");
							String es_Article = FetchDataServices.getArticle(es_Title,"ES");
							
							// Se comprueba que se han obtenido correctamente
							if(en_Article != null && !en_Article.isEmpty() && es_Article != null && !es_Article.isEmpty()){
								
								// Obtenemos los identificadores de las dos articulos
								int en_id = FetchDataServices.getIdPage(en_Title, "EN");
								int es_id = FetchDataServices.getIdPage(es_Title, "ES");
								
								// Se comprueba que se han obtenido correctamente
								if(en_id != -1 && es_id != -1){
									
									// Se prepara las dos lineas para escribirlas en los ficheros.
									String en_line = FetchDataUtils.csvLine(en_id, en_Title, en_categoryName, en_Article);
									String es_line = FetchDataUtils.csvLine(es_id, es_Title, es_categoryName, es_Article);
									//System.out.println(en_line);
									//System.out.println(es_line);
									
									// Se escriben en el fichero
									en_bw.write(en_line);
									es_bw.write(es_line);
									// Nueva linea.
									en_bw.newLine();
									es_bw.newLine();
									
									es_pages++;
									num_art++;
									
								}	
								else {
									System.out.println("Error: Se ha producido un error al obtener el identificador del articulo: " + en_Title + ", " + es_Title);
								}
							}
							
							else {
								System.out.println("Error: Se ha producido un error al obtener el articulo: " + en_Title + ", " + es_Title);
							}
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
			System.out.println("Número de páginas recolectadas: " + num_art);
			
			en_bw.close();
			es_bw.close();
			fileStream.close();
		}
		catch(FileNotFoundException e){
			System.err.println("Error: Fichero no existe");
		}

		//String categoryName = "Computer arithmetic";
		//String categoryName2 = "Technical drawing";
		
/*		String en_categoryName = "Impact craters on the Moon";
		String es_categoryName = FetchDataServices.getEs_Category(FetchDataServices.getCategoryId(en_categoryName));
		
		//FileWriter en_fw = new FileWriter("en-corpus.csv");
		//FileWriter es_fw = new FileWriter("es-corpus.csv");
		//BufferedWriter en_bw = new BufferedWriter(en_fw);
		//BufferedWriter es_bw = new BufferedWriter(es_fw);
		
		ArrayList<Article> lista = FetchDataServices.sendGetArticleList(en_categoryName);
		int j = 0;
		if (lista != null){
			for(int i=0; i < lista.size(); i++){
				String en_Title = lista.get(i).getTitle();
				String es_Title = FetchDataServices.getEs_Title(lista.get(i).getPageid());
				// Si se ha encontrado el articulo en espaniol, obtenemos los articulos en ingles y espaniol.
				if (es_Title != null){
					
					String en_Article = FetchDataServices.getArticle(en_Title,"EN");
					String es_Article = FetchDataServices.getArticle(es_Title,"ES");
					
					if(en_Article != null && es_Article != null){
						int en_id = FetchDataServices.getIdPage(en_Title, "EN");
						int es_id = FetchDataServices.getIdPage(es_Title, "ES");
						
						if(en_id != -1 && es_id != -1){
							String en_line = FetchDataUtils.csvLine(en_id, en_Title, en_categoryName, en_Article);
							String es_line = FetchDataUtils.csvLine(es_id, es_Title, es_categoryName, es_Article);
							System.out.println(en_line);
							System.out.println(es_line);
							//en_bw.write(en_line);
							//es_bw.write(es_line);
							//en_bw.newLine();
							//es_bw.newLine();
							j++;
							
						}
						
						else {
							System.out.println("Error: Se ha producido un error al obtener el identificador del articulo");
						}
					}
					
					else {
						System.out.println("Error: Se ha producido un error al obtener el articulo");
					}
				}
			}
			System.out.println("Categoria \"" + es_categoryName + "\" con " + j + " paginas ------ Category \"" 
					+ en_categoryName + "\" with " + lista.size() + " pages");
			//en_fw.close();
			//es_fw.close();
		}
		*/
/*		
		String body = "<p><b>Lerchfeld</b> is\ta settlement \fin the municipality\r of Dunkelsteinerwald in Melk District, Lower Austria in northeastern Austria.</p>\n<h2><span id=\"References\">References</span></h2>\n\n<p></p>";
		String body2 = "<p><b>Jann Sonya McFarlane</b> (born 22 May 1944), Australian politician, was an Australian Labor Party member of the Australian House of Representatives from October 1998 to October 2004, representing the Division of Stirling, Western Australia. She was born in Sydney, New South Wales, and was educated at Macquarie University, Sydney. She was a clerk, secretary, and community worker before entering politics. She was defeated by Liberal candidate Michael Keenan at the 2004 election.</p>\n<h2><span id=\"References\">References</span></h2>";
		String body3 = "Jann Sonya McFarlane (born 22 May 1944), Australian politician, was an Australian Labor Party member of the Australian House of Representatives from October 1998 to October 2004, representing the Division of Stirling, Western Australia. She was born in Sydney, New South Wales, and was educated at Macquarie University, Sydney. She was a clerk, secretary, and community worker before entering politics. She was defeated by Liberal candidate Michael Keenan at the 2004 election.\n\n\n== References ==";
		String body4 = "sino con la velocidad del afecto emocional, que tambi\u00e9n influye en los latidos del coraz\u00f3n. London escribe que el metro musical \u00abimplica nuestra percepci\u00f3n inicial";
		
		String body5 = "{\\displaystyle 2^{n}-1}n − 1 {\\displaystyle 2^{n}-1} puede ser implementada{\\displaystyle 2^{n}{}{}{}{}-1}"; 
		String body6 = "hola prueba == Notas == hola mundo esto es la nota 1 == Notas y referencias == nota 2.";
		String body7 = "== External links == Eucalyptus orbifolia Brooker, M.I.H. & Kleinig, D.A. (1990). Field Guide to Eucalypts, Volume 2: South-western and Southern Australia. Inkata Press, Melbourne. ISBN 0-909605-59-9 == References ==";
		String body8 = "las ondas ma ===	gnetohid ==== rodinámicas en esto. == ReferenciasEditar ==";
		String body9 = "criptoanálisis.[1] Kahn podría haberse apresurado";
		//System.out.println(FetchDataUtils.cleanText(body4));
		
		//System.out.println(FetchDataServices.getArticle(";;Desbordamiento ar","EN"));
		//System.out.println(FetchDataServices.getEs_Title(117003));
		//System.out.println(FetchDataServices.getCategoryId("Computer arithmetic"));
		//System.out.println(FetchDataUtils.cleanText(body8));
		
		System.out.println(FetchDataUtils.removeCitas(body9));*/
		
		//System.out.println(FetchDataServices.getCategoryId("Ethology"));

	}


}
