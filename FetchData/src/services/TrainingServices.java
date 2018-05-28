package services;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import models.Document;
import models.Neighbour_result;
import sun.misc.BASE64Encoder;

@SuppressWarnings("restriction")

public class TrainingServices {
	final static String username = "dkaiwei";
	final static String password = "oeg2018";
	final static String apiBaseUrl_ES = "http://librairy.linkeddata.es/dkaiwei-es-topics/";
	final static String apiBaseUrl_EN = "http://librairy.linkeddata.es/dkaiwei-en-topics/";
	final static String spaceApiBaseUrl = "http://librairy.linkeddata.es/dkaiwei-space/";
	
	public static int get_documents(String language) throws Exception {
		String url = null;
		int num_docs = -1;
		
		// Se elige el idioma.
		switch(language){
		case "EN":
			url = apiBaseUrl_EN;
			break;
		case "ES":
			url = apiBaseUrl_ES;
			break;
		default:
			url = apiBaseUrl_EN;		
		}
		url += "documents";

	    URL obj = new URL(url);
	    HttpURLConnection con = (HttpURLConnection) obj.openConnection();
	    
	    // Configuracion de la llamada.
	    con.setRequestMethod("GET");
	    con.setRequestProperty("User-Agent", "Mozilla/5.0");
	    con.setRequestProperty("Authorization", "Basic " + setAuthentification(username, password));
	    con.setRequestProperty("accept", "application/json");
	    
	    // Comprobacion de la respuesta.
	    int responseCode = con.getResponseCode();
	    if (responseCode == 200){
	    	JSONObject myResponse = response2JSON(con.getInputStream());
	    	//System.out.println("Size - "+myResponse.get("size"));
		    //System.out.println("Updated - "+myResponse.get("updated"));
	    	int size = (int) myResponse.get("size");
	    	num_docs = size;
	    } 
	    else{
	    	System.err.println("Error: " + con.getRequestMethod());
	    }
	    con.disconnect();
	    return num_docs;
	}
	
	public static void post_documents(String language, Document doc) 
			throws IOException, JSONException {
		String query = null;
		boolean done = false;
		
		// Se elige el idioma.
		switch(language){
		case "EN":
			query = apiBaseUrl_EN;
			break;
		case "ES":
			query = apiBaseUrl_ES;
			break;
		default:
			query = apiBaseUrl_EN;		
		}
		query += "documents";
		
		// Se crea el body JSON
		JSONObject jsonBody = document_JsonBuilder(doc.getId(), doc.getName(), doc.getText(), doc.getLabels());
		if (jsonBody == null) return;
		//System.out.println(jsonBody.toString(2));
		while(!done){
			try{
				// Se construye la llamada POST
				URL url = new URL(query);
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				conn.setConnectTimeout(5000);
				conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
				conn.setRequestProperty("Authorization", "Basic " + setAuthentification(username, password));
				conn.setDoOutput(true);
				conn.setDoInput(true);
				conn.setRequestMethod("POST");
				// BODY
				OutputStream os = conn.getOutputStream();
				os.write(jsonBody.toString().getBytes("UTF-8"));
				os.close();

				// Respuesta
				int responseCode = conn.getResponseCode();
				JSONObject jsonObject = response2JSON(conn.getInputStream());
				String result = (String) jsonObject.get("result");

				// Comprobacion de la respuesta
				if (responseCode == 201 && result.contains("document added")){
					System.out.println(doc.getName() + " - " + jsonObject.get("result"));
					done = true;
				}
				else {
					System.err.println("Error: " + conn.getRequestMethod() + " :" + doc.getName() + " - " + result);
				}
				conn.disconnect();
			} catch(java.io.IOException e){
				e.printStackTrace();
				System.out.println("Retrying");
			}
		}
	}
	
	public static boolean delete_documents(String language){
		String query = null;
		boolean done = false;
		
		// Se elige el idioma.
		switch(language){
		case "EN":
			query = apiBaseUrl_EN;
			break;
		case "ES":
			query = apiBaseUrl_ES;
			break;
		default:
			query = apiBaseUrl_EN;		
		}
		query += "documents";
		
		@SuppressWarnings("resource")
		Scanner input = new Scanner(System.in);
		System.out.print("Se van a borrar los datos anterior de "+query+" desea continuar?(Y/N)");
		String answer = input.nextLine();

		if(answer.toUpperCase().equals("Y")){
			while(!done){
				try{
					URL url = new URL(query);
					HttpURLConnection conn = (HttpURLConnection) url.openConnection();
					conn.setConnectTimeout(5000);
					conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
					conn.setRequestProperty("Authorization", "Basic " + setAuthentification(username, password));
					conn.setDoOutput(true);
					conn.setDoInput(true);
					conn.setRequestMethod("DELETE");
					// BODY
					OutputStream os = conn.getOutputStream();
					os.close();

					// Respuesta
					int responseCode = conn.getResponseCode();
					// Comprobacion de la respuesta
					if (responseCode == 202){
						done = true;
						System.out.println("DELETE /documents, realizado correctamente " );
					}
					else {
						System.err.println("Error "+ responseCode + " - " + conn.getResponseMessage());
					}
					conn.disconnect();
				} catch(java.io.IOException e){
					e.printStackTrace();
					System.out.println("Retrying");
				}
			}
			return true;
		}
		else return false;
	}
	
	public static double[] post_shape(String language, String text){
		String query = null;
		double[] vector = null;
		boolean done = false;
		
		// Se elige el idioma.
		switch(language){
		case "EN":
			query = apiBaseUrl_EN;
			break;
		case "ES":
			query = apiBaseUrl_ES;
			break;
		default:
			query = apiBaseUrl_EN;		
		}
		query += "shape";

		JSONObject jsonBody = shape_JsonBuilder(text);
		if (jsonBody == null) return null;
		
		while(!done){
			try{
				// Se construye la llamada POST
				URL url = new URL(query);
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				conn.setConnectTimeout(5000);
				conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
				conn.setRequestProperty("Authorization", "Basic " + setAuthentification(username, password));
				conn.setDoOutput(true);
				conn.setDoInput(true);
				conn.setRequestMethod("POST");
				// BODY
				OutputStream os = conn.getOutputStream();
				os.write(jsonBody.toString().getBytes("UTF-8"));
				os.close();

				// Respuesta
				int responseCode = conn.getResponseCode();
				JSONObject jsonObject = response2JSON(conn.getInputStream());

				// Comprobacion de la respuesta
				if (responseCode == 200 && jsonObject.getJSONArray("vector") != null){
					done = true;
					JSONArray values = jsonObject.getJSONArray("vector");
					vector = new double[values.length()];
					for(int i = 0; i < values.length(); i++){
						vector[i] = values.optDouble(i);
						//System.out.println(vector[i]);
					}
					System.out.println("POST /shape realizado correctamente");

				}
				else {
					System.err.println("Error");
				}
				conn.disconnect();
			} catch(java.io.IOException e){
				e.printStackTrace();
				System.out.println("Retrying");
			}
		}
		return vector;
	}
	
	public static void post_spaces(int threshold){
		String query = null;
		boolean done = false;
		query = spaceApiBaseUrl + "spaces?threshold="+ threshold;
		
		while(!done){
			try{
				// Se construye la llamada POST
				URL url = new URL(query);
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				conn.setConnectTimeout(5000);
				conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
				conn.setRequestProperty("Authorization", "Basic " + setAuthentification(username, password));
				conn.setDoOutput(true);
				conn.setDoInput(true);
				conn.setRequestMethod("POST");
				// BODY
				OutputStream os = conn.getOutputStream();
				os.close();

				// Respuesta
				int responseCode = conn.getResponseCode();
				// Comprobacion de la respuesta
				if (responseCode == 202){
					done = true;
					System.out.println("POST /spaces, threshold fijado en "+ threshold);
				}
				else {
					System.err.println("Error "+ responseCode + " - " + conn.getResponseMessage());
				}
				conn.disconnect();
			} catch(java.io.IOException e){
				e.printStackTrace();
				System.out.println("Retrying");
			}
		}
	}
	
	public static double[] get_idPoints(String id){
		String query = null;
		double[] vector = null;
		boolean done = false;
		
		query = spaceApiBaseUrl + "points/" + id;
	  
		while(!done){
			try{
				URL obj = new URL(query);
				HttpURLConnection con = (HttpURLConnection) obj.openConnection();

				// Configuracion de la llamada.
				con.setRequestMethod("GET");
				con.setRequestProperty("Authorization", "Basic " + setAuthentification(username, password));
				con.setRequestProperty("Content-type", "application/json; charset=UTF-8");

				int responseCode = con.getResponseCode();
			    if (responseCode == 202){
			    	done = true;
			    	JSONObject jsonObject = response2JSON(con.getInputStream());
			    	//System.out.println(jsonObject.toString(2));
			    	JSONArray values = jsonObject.getJSONArray("shape");
					vector = new double[values.length()];
					for(int i = 0; i < values.length(); i++){
						vector[i] = values.optDouble(i);
						//System.out.println(vector[i]);
					}
			    } 
			    else{
			    	System.err.println("Error: " + con.getRequestMethod() + ", "+ responseCode +" "+ con.getResponseMessage());
			    }
			    con.disconnect();
				

			} catch(java.io.IOException e){
				e.printStackTrace();
				System.out.println("Retrying ... ");
			}
		}
		return vector;
	}
	
	public static boolean delete_points(){
		String query = null;
		boolean done = false;
		query = spaceApiBaseUrl + "points";
		
		Scanner input = new Scanner(System.in);
		System.out.print("Se va a borrar los datos anterior desea continuar?(Y/N)");
		String answer = input.nextLine();

		if(answer.toUpperCase().equals("Y")){
			while(!done){
				try{
					URL url = new URL(query);
					HttpURLConnection conn = (HttpURLConnection) url.openConnection();
					conn.setConnectTimeout(5000);
					conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
					conn.setRequestProperty("Authorization", "Basic " + setAuthentification(username, password));
					conn.setDoOutput(true);
					conn.setDoInput(true);
					conn.setRequestMethod("DELETE");
					// BODY
					OutputStream os = conn.getOutputStream();
					os.close();

					// Respuesta
					int responseCode = conn.getResponseCode();
					// Comprobacion de la respuesta
					if (responseCode == 202){
						done = true;
						System.out.println("DELETE /points, realizado correctamente " );
					}
					else {
						System.err.println("Error "+ responseCode + " - " + conn.getResponseMessage());
					}
					conn.disconnect();
				} catch(java.io.IOException e){
					e.printStackTrace();
					System.out.println("Retrying ... ");
				}
			}
			input.close();
			return true;
		}
		else {
			input.close();
			return false;
		}
	}
	
	public static void post_points(String language, String id, String name, double[] vector){
		String query = null;
		boolean done = false;
		query = spaceApiBaseUrl + "points";

		JSONObject jsonBody = points_JsonBuilder(id, name, vector, language);
		if (jsonBody == null) return ;
		
		while(!done){
			try{
				// Se construye la llamada POST
				URL url = new URL(query);
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				conn.setConnectTimeout(5000);
				conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
				conn.setRequestProperty("Authorization", "Basic " + setAuthentification(username, password));
				conn.setDoOutput(true);
				conn.setDoInput(true);
				conn.setRequestMethod("POST");
				// BODY
				OutputStream os = conn.getOutputStream();
				os.write(jsonBody.toString().getBytes("UTF-8"));
				os.close();

				// Respuesta
				int responseCode = conn.getResponseCode();
				// Comprobacion de la respuesta
				if (responseCode == 200){
					done = true;
					System.out.println("POST /points realizado correctamente");
				}
				else {
					System.err.println("Error "+ responseCode + " - " + conn.getResponseMessage());
				}
				conn.disconnect();
			} catch(java.io.IOException e){
				e.printStackTrace();
				System.out.println("Retrying");
			}
		}

	}

	public static Neighbour_result post_points_neighbours(String id, String id_target, int num_neighbours, String language) {
		String query = null;
		boolean done = false;
		Neighbour_result result = new Neighbour_result();
		query = spaceApiBaseUrl + "points/"+id+"/neighbours";
		
		switch(language){
		case "EN":
			language = "wiki-EN";
			break;
		case "ES":
			language = "wiki-ES";
			break;
		default:
			language = "wiki-EN";
			break;	
		}

		JSONObject jsonBody = points_neighbours_JsonBuilder(num_neighbours, language);
		if (jsonBody == null) return null;
		
		while(!done){
			try{
				// Se construye la llamada POST
				URL url = new URL(query);
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				conn.setConnectTimeout(5000);
				conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
				conn.setRequestProperty("Authorization", "Basic " + setAuthentification(username, password));
				conn.setDoOutput(true);
				conn.setDoInput(true);
				conn.setRequestMethod("POST");
				// BODY
				OutputStream os = conn.getOutputStream();
				os.write(jsonBody.toString().getBytes("UTF-8"));
				os.close();

				// Respuesta
				int responseCode = conn.getResponseCode();
				// Comprobacion de la respuesta
				if (responseCode == 202){
					done = true;
					JSONObject jsonObject = response2JSON(conn.getInputStream());
					JSONArray neighbours = jsonObject.getJSONArray("neighbours");
					
					for (int i = 0; i < neighbours.length(); i++) {
			            JSONObject neighbour = neighbours.getJSONObject(i);
			            String id_n = neighbour.getString("id");
			            if(id_target.equals(id_n)){
			            	result.setScore(neighbour.getDouble("score"));
			            	result.setPosition(i);
			            }
			        }
				}
				else {
					System.err.println("Error "+ responseCode);
				}
				conn.disconnect();
			} catch(java.io.IOException e){
				e.printStackTrace();
				System.out.println("Retrying");
			}
		}
		return result;
	}

	public static double post_comparisons(double[] en_vector, double[] es_vector){
		String query = null;
		boolean done = false;
		double result = -1;
		query = spaceApiBaseUrl + "comparisons";

		JSONObject jsonBody = comparisons_JsonBuilder(en_vector, es_vector);
		if (jsonBody == null) 
			return result;
		
		while(!done){
			try{
				// Se construye la llamada POST
				URL url = new URL(query);
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				conn.setConnectTimeout(5000);
				conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
				conn.setRequestProperty("Authorization", "Basic " + setAuthentification(username, password));
				conn.setDoOutput(true);
				conn.setDoInput(true);
				conn.setRequestMethod("POST");
				// BODY
				OutputStream os = conn.getOutputStream();
				os.write(jsonBody.toString().getBytes("UTF-8"));
				os.close();

				// Respuesta
				int responseCode = conn.getResponseCode();
				// Comprobacion de la respuesta
				if (responseCode == 202){
					done = true;
					String s_result = convertStreamToString(conn.getInputStream());
					result = Double.parseDouble(s_result);
					//System.out.println(result);
				}
				else {
					System.err.println("Error "+ responseCode + " - " + conn.getResponseMessage());
				}
				conn.disconnect();
			} catch(java.io.IOException e){
				e.printStackTrace();
				System.out.println("Retrying ...");
			}
		}
		return result;
	}
	
	private static JSONObject comparisons_JsonBuilder(double[] en_vector, double[] es_vector) {
		if(en_vector.length > 0 && es_vector.length > 0 && en_vector.length == es_vector.length){
			JSONObject jsonBody = new JSONObject();
			JSONArray shape1 = new JSONArray();
			JSONArray shape2 = new JSONArray();
			for (int i = 0; i < en_vector.length; i++){
				shape1.put(en_vector[i]);
				shape2.put(es_vector[i]);
			}
			jsonBody.put("shape1", shape1);
			jsonBody.put("shape2", shape2);	
			return jsonBody;
		}
		else
			return null;
	}

	private static JSONObject points_neighbours_JsonBuilder(int num_neighbours, String language) {
		if(num_neighbours > 0 && language != null && !language.isEmpty()){
			JSONObject jsonBody = new JSONObject();
			jsonBody.put("force", true);
			jsonBody.put("number", num_neighbours);
			
			JSONArray type = new JSONArray();
			type.put(language);
			jsonBody.put("types", type);	
			
			return jsonBody;
		}
		else
			return null;
	}
	
	public static JSONObject response2JSON(InputStream response) throws IOException{
		BufferedReader in = new BufferedReader(new InputStreamReader(response));
        String inputLine;
        StringBuffer responseBuffer = new StringBuffer();
        while ((inputLine = in.readLine()) != null)
        	responseBuffer.append(inputLine);
        in.close();
        
		JSONObject responseJson = new JSONObject(responseBuffer.toString());
		return responseJson;
	}
	
	public static String setAuthentification(String username, String password){
		String userPassword = username + ":" + password;
		BASE64Encoder enc = new sun.misc.BASE64Encoder();
		return enc.encode(userPassword.getBytes());
	}
	
	public static JSONObject points_JsonBuilder(String id, String name, double[] vector, String language){
		if (id != null && !id.isEmpty() && name != null && !name.isEmpty() && language != null && !language.isEmpty()
				&& vector != null && vector.length > 0){
			JSONObject jsonBody = new JSONObject();
			jsonBody.put("id", id);
			jsonBody.put("name", name);
			JSONArray shape = new JSONArray();
			for (int i = 0; i < vector.length; i++){
				shape.put(vector[i]);
			}
			jsonBody.put("shape", shape);
			jsonBody.put("type", "wiki-"+language);
			//System.out.println(jsonBody.toString(2));
			return jsonBody;
		}
		else {
			return null;
		}
	}
	
	public static JSONObject shape_JsonBuilder(String text){
		if (text != null && !text.isEmpty()){
			JSONObject jsonBody = new JSONObject();
			jsonBody.put("text", text);
			return jsonBody;
		}
		else {
			return null;
		}
	}
	
	public static JSONObject document_JsonBuilder(String id, String name, String text, List<String> labels){
		// Se comprueba todos los parametros
		if (id != null && !id.isEmpty() && name != null && !name.isEmpty() && 
				text != null && !text.isEmpty() && labels != null && !labels.isEmpty()){
			JSONObject jsonBody = new JSONObject();
			jsonBody.put("id", id);
	        jsonBody.put("name", name);
	        jsonBody.put("text", text);
			jsonBody.put("labels", labels);
			return jsonBody;
		}
		else {
			return null;
		}
	}
	
	public static Document parseLine(String line){
		String id = null;
		String name = null;
		String text = null;
		List<String> list_labels = new ArrayList<String>();
		Document doc = null;
		
		int position_1;
		int position_2;
		int position_3;
		String labels;
		
		// Se comprueba que estan los 4 campos, separados por ";;"
		int count = (line.length() - line.replace(";;", "").length())/2;
		if(line.contains(";;") && count == 3){
			position_1 = line.indexOf(";;");
			position_2 = line.indexOf(";;", position_1 + 2);
			position_3 = line.indexOf(";;", position_2 + 2);
			id = line.substring(0, position_1);
			name = line.substring(position_1 + 2, position_2);
			labels = line.substring(position_2 + 2, position_3);
			text = line.substring(position_3 + 2, line.length());
			
			//System.out.println(id);
			//System.out.println(name);
			//System.out.println(labels);
			//System.out.println(text);
			labels = labels.replaceAll("\\s+", "_");
			//System.out.println(labels);
			
			// Se separan las categorias que estan separadas por ";"
			for(String label : labels.split(";")) {
			    list_labels.add(label);
			}
			//System.out.println(list_labels.toString());
			doc = new Document(id, name, text, list_labels);
		}
		return doc;
	}

	private static String convertStreamToString(java.io.InputStream is) {
	    @SuppressWarnings("resource")
		java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
	    return s.hasNext() ? s.next() : "";
	}
	}
