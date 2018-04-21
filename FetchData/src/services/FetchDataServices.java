package services;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import org.apache.commons.io.IOUtils;

import com.google.gson.Gson;

import category.Category;
import category.Categorymember;
import models.Article;

/**
 * @author daniel
 *
 */
public class FetchDataServices {

	// HTTP GET request
	// A partir de un nombre de una categoria, recoge todos los nombres e identificadores
	// de los articulos que pertenecen a esa categoria.
	// Si no encuentra articulos en la categoria devuelve null.
	public static ArrayList<Article> sendGetArticleList(String categoryName) throws Exception {
		ArrayList<Article> articleList = new ArrayList<Article>();
		if (categoryName != null){
			String categoryNameEncoded = FetchDataUtils.encode(categoryName);
			//System.out.println(categoryNameEncoded);
			String url_get = FetchDataConstants.en_endpoint + FetchDataConstants.categoryQuery + categoryNameEncoded;
			//System.out.println("URL: "+ url_get);

			Gson gson = new Gson();
			URL obj = new URL(url_get);

			HttpURLConnection con = (HttpURLConnection) obj.openConnection();

			// GET
			con.setRequestMethod("GET");
			con.setRequestProperty("User-Agent", FetchDataConstants.USER_AGENT);

			int responseCode = con.getResponseCode();

			// Se comprueba la respuesta de la llamada
			if (responseCode == FetchDataConstants.OK){
				BufferedReader br = new BufferedReader(
						new InputStreamReader(con.getInputStream()));
				Category cat = gson.fromJson(br, Category.class);

				// Se comprueba que se ha obtenido por lo menos 50 artículos 
				if (cat != null 
						&& !cat.getQuery().getCategorymembers().isEmpty() 
						&& cat.getQuery().getCategorymembers().size() >= FetchDataConstants.Min_PAG){
					// Construccion de la lista
					for(Categorymember t: cat.getQuery().getCategorymembers()){
						Article a = new Article();
						// Se omiten las subcategorias y los ficheros
						if(t.getTitle() != null 
								&& !t.getTitle().contains("Category:") 
								&& !t.getTitle().contains("File:")
								){
							a.setPageid(t.getPageid());
							a.setTitle(t.getTitle());
							//System.out.println(t.getPageid() + " - " + t.getTitle());
							articleList.add(a);
						}
					}
				}
				else {
					articleList = null;
					System.out.println("Error: La categoria "+categoryName+" no existente o no tiene articulos");
				}
				br.close();
			}
			else {
				System.out.println("Error en la llamada: "+ responseCode);
			}
		}

		return articleList;
	}

	// A partir de un identificador de un artículo, consigue el título del artículo en español
	// Devuelve null si no existe el articulo en espaniol.
	
	/**
	 * @param en_pageid - identificador de la pagina en ingles
	 * @return String - titulo del articulo en espaniol, NULL si no existe.
	 * @throws IOException
	 */
	public static String getEs_Title(int en_pageid) throws IOException{ 
		String es_title = null;
		String url_get = FetchDataConstants.en_endpoint + FetchDataConstants.langLinkEs + en_pageid;
		
		URL obj = new URL(url_get);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();

		// GET
		con.setRequestMethod("GET");
		con.setRequestProperty("User-Agent", FetchDataConstants.USER_AGENT);
		int responseCode = con.getResponseCode();
		//System.out.println("URL: "+ url_get + "  -- Response: " +responseCode);
		// Comprobacion de la respuesta.
		if (responseCode == FetchDataConstants.OK){

			InputStream in = con.getInputStream();
			String encoding = con.getContentEncoding();
			//System.out.println("Encoding es " +encoding);

			// Conversion del contenido de la respuesta a String
			encoding = encoding == null ? "UTF-8" : encoding;
			String body = IOUtils.toString(in, encoding);
			
			// Se comprueba que existe la pagina en Español.
			if (body != null){
				//System.err.println("Error: No se ha encontrado la pagina con id: "+en_pageid+ " en Español.");
				body = FetchDataUtils.unescapeJava(body);
				es_title = FetchDataUtils.getTitle(body);
			}
		}
		return es_title;
	}

	// A partir de un identificador de una categoria, devuelve el nombre en espaniol de la categoria
	// Devuelve null si no existe la categoria en espaniol.
	public static String getEs_Category(int en_pageid) throws IOException{
		String cat_name = null;
		if (en_pageid > 0){
			cat_name = getEs_Title(en_pageid);
			if (cat_name != null && cat_name.contains("Categoría")){
				int start = cat_name.indexOf(":")+1;
				cat_name = cat_name.substring(start, cat_name.length());
			}
			else 
				cat_name = null;
		}
		return cat_name;
	}
	
	// A partir del nombre de una categoria en ingles, devuelve su identificador 
	// Si no existe devuelve -1
	public static int getCategoryId(String category_name) throws IOException{
		int categoryId = -1;
		String url_get = FetchDataConstants.en_endpoint + FetchDataConstants.categoryName 
				+ FetchDataUtils.encode(category_name);

		URL obj = new URL(url_get);

		HttpURLConnection con = (HttpURLConnection) obj.openConnection();

		// GET
		con.setRequestMethod("GET");
		con.setRequestProperty("User-Agent", FetchDataConstants.USER_AGENT);
		int responseCode = con.getResponseCode();
		
		// Comprobacion de la respuesta.
		if (responseCode == FetchDataConstants.OK){
			
			// Conversion del contenido de la respuesta a String
			InputStream in = con.getInputStream();
			String encoding = con.getContentEncoding();
			encoding = encoding == null ? "UTF-8" : encoding;
			String body = IOUtils.toString(in, encoding);
			body = FetchDataUtils.unescapeJava(body);
			
			// Se localiza el campo "pageid" y se obtiene el identificador
			if (body != null && body.contains("pageid\":")){
				
				int start = body.indexOf("pageid\":")+8;
				String id_num = "";

				while(start > -1 && body.charAt(start) != ','){
					id_num = id_num + body.charAt(start);
					start++;
				}
				categoryId = Integer.parseInt(id_num);	
			}

			// Se comprueba que existe la pagina en Español.
			else {
				System.err.println("Error: No se ha encontrado el identificador de la pagina: "+ category_name);
			}
		}
		return categoryId;
	}
	
	public static int getIdPage(String articleName, String language) throws IOException{
		int categoryId = -1;
		String url_get = "";

		switch(language){
		case "EN": 
			url_get = FetchDataConstants.en_endpoint;
			break;
		case "ES":
			url_get = FetchDataConstants.es_endpoint;
			break;
		default:
			url_get = FetchDataConstants.en_endpoint;
		}
		
		url_get += FetchDataConstants.articleId + FetchDataUtils.encode(articleName);
		//System.out.println(url_get);

		URL obj = new URL(url_get);

		HttpURLConnection con = (HttpURLConnection) obj.openConnection();

		// GET
		con.setRequestMethod("GET");
		con.setRequestProperty("User-Agent", FetchDataConstants.USER_AGENT);
		int responseCode = con.getResponseCode();
		
		// Comprobacion de la respuesta.
		if (responseCode == FetchDataConstants.OK){
			
			// Conversion del contenido de la respuesta a String
			InputStream in = con.getInputStream();
			String encoding = con.getContentEncoding();
			encoding = encoding == null ? "UTF-8" : encoding;
			String body = IOUtils.toString(in, encoding);
			body = FetchDataUtils.unescapeJava(body);
			
			// Se localiza el campo "pageid" y se obtiene el identificador
			if (body != null && body.contains("pageid\":")){
				
				int start = body.indexOf("pageid\":")+8;
				String id_num = "";

				while(start > -1 && body.charAt(start) != ','){
					id_num = id_num + body.charAt(start);
					start++;
				}
				categoryId = Integer.parseInt(id_num);	
			}

			// Se comprueba que existe la pagina.
			else {
				System.out.println("Error: No se ha encontrado el identificador de la pagina: "+ articleName);
			}
		}
		return categoryId;
	}
	
	/**
	 * @param articleName - Nombre del articulo, el nombre tiene que estar en el idioma que se desea
	 * @param language - Idioma que se desea
	 * @return String - Devuelve el texto del articulo en el idioma indicado
	 */
	public static String getArticle(String articleName, String language) throws IOException{ 
		
		String article = null;
		String url_get = "";
		
		switch(language){
		case "EN": 
			url_get = FetchDataConstants.en_endpoint;
			break;
		case "ES":
			url_get = FetchDataConstants.es_endpoint;
			break;
		default:
			url_get = FetchDataConstants.en_endpoint;
		}
		url_get += FetchDataConstants.articleName + FetchDataUtils.encode(articleName);
		//System.out.println("URL: "+ url_get);

		URL obj = new URL(url_get);

		HttpURLConnection con = (HttpURLConnection) obj.openConnection();

		// GET
		con.setRequestMethod("GET");
		con.setRequestProperty("User-Agent", FetchDataConstants.USER_AGENT);
		int responseCode = con.getResponseCode();
		// Comprobacion de la respuesta.
		if (responseCode == FetchDataConstants.OK){
			
			// Conversion del contenido de la respuesta a String
			InputStream in = con.getInputStream();
			String encoding = con.getContentEncoding();
			encoding = encoding == null ? "UTF-8" : encoding;
			String body = IOUtils.toString(in, encoding);
			
			// Se obtiene el texto del articulo y se limpia el texto.
			article = FetchDataUtils.getArticle(body);
			if(article != null)
				article = FetchDataUtils.cleanText(article);
			
			// Se comprueba que existe la pagina en Español.
			if (article == null){
				System.err.println("Error: No se ha encontrado la pagina: "+ articleName);
			}
		}
		return article;
	}

}
