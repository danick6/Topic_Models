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

import org.json.JSONException;
import org.json.JSONObject;

import models.Document;
import sun.misc.BASE64Encoder;

@SuppressWarnings("restriction")

public class TrainingServices {
	final static String username = "dkaiwei";
	final static String password = "oeg2018";
	final static String apiBaseUrl_ES = "http://lab4.librairy.linkeddata.es/dkaiwei-es-topics/";
	final static String apiBaseUrl_EN = "http://lab4.librairy.linkeddata.es/dkaiwei-en-topics/";
	
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
	    	String size = (String) myResponse.get("size");
	    	num_docs = Integer.getInteger(size);
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
		JSONObject jsonBody = jsonBuilder(doc.getId(), doc.getName(), doc.getText(), doc.getLabels());
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
	
	public static void post_dimensions(String language){
		String url = null;
		
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
		url += "dimensions";
		// TODO
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
	
	public static JSONObject jsonBuilder(String id, String name, String text, List<String> labels){
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
}
