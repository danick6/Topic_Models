package services;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import com.google.gson.Gson;

import category.Category;
import category.Categorymember;
import models.Article;

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
	public static String getEs_Title(int en_pageid) throws IOException{ 
		String es_title = null;
		String url_get = FetchDataConstants.en_endpoint + FetchDataConstants.langLinkEs + en_pageid;
		//System.out.println("URL: "+ url_get);

		URL obj = new URL(url_get);

		HttpURLConnection con = (HttpURLConnection) obj.openConnection();

		// GET
		con.setRequestMethod("GET");
		con.setRequestProperty("User-Agent", FetchDataConstants.USER_AGENT);
		int responseCode = con.getResponseCode();
		// Comprobacion de la respuesta.
		if (responseCode == FetchDataConstants.OK){

			InputStream in = con.getInputStream();
			String encoding = con.getContentEncoding();
			//System.out.println("Encoding es " +encoding);

			// Conversion del contenido de la respuesta a String
			String body = FetchDataUtils.getContent(in, encoding);
			es_title = FetchDataUtils.getTitle(body);

			// Se comprueba que existe la pagina en Español.
			if (es_title == null){
				//System.err.println("Error: No se ha encontrado la pagina con id: "+en_pageid+ " en Español.");
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

			InputStream in = con.getInputStream();
			String encoding = con.getContentEncoding();

			// Conversion del contenido de la respuesta a String
			String body = FetchDataUtils.getContent(in, encoding);
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

}
