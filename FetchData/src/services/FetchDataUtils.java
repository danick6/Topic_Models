package services;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class FetchDataUtils {
	private static final String VALUES = "!#$&'()*+,/:;=?@[] \"%-.<>\\^_`{|}~";

	/** Codifica un texto a codigo porciento (Percent-encoding)
	 * @param input - String Texto a codificar
	 * @return String - Texto codificado en Percent-encoding, NULL - si input es NULL
	 */
	public static String encode(String input) {
	    if (input == null || input.isEmpty())
	        return input;
	    
	    StringBuilder result = new StringBuilder(input);
	    for (int i = input.length() - 1; i >= 0; i--) {
	        if (VALUES.indexOf(input.charAt(i)) != -1) {
	            result.replace(i, i + 1, 
	                    "%" + Integer.toHexString(input.charAt(i)).toUpperCase());
	        }
	    }
	    return result.toString();
	}
	
	// Devuelve el contenido de un body en un String
	public static String getContent(InputStream in, String encoding) throws IOException{ 
		encoding = encoding == null ? "UTF-8" : encoding;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] buf = new byte[8192];
		int len = 0;
		while ((len = in.read(buf)) != -1) {
		    baos.write(buf, 0, len);
		}
		String body = new String(baos.toByteArray(),encoding);
		String decodedToISO88591 = unescapeJava(body);
		// System.out.println(body);
		return decodedToISO88591;
	}
	
	/** Transforma un string con unicode a letras normales (ASCII)
	 * @param escaped - String Texto en unicode
	 * @return String - Texto en ASCII, NULL si escaped es NULL
	 */
	public static String unescapeJava(String escaped) {
	    if(escaped == null || escaped.indexOf("\\u")==-1)
	        return escaped;

	    String processed="";

	    int position=escaped.indexOf("\\u");
	    while(position!=-1) {
	        if(position!=0)
	            processed+=escaped.substring(0,position);
	        String token=escaped.substring(position+2,position+6);
	        escaped=escaped.substring(position+6);
	        try{
	        	processed+=(char)Integer.parseInt(token,16);
	        } catch(NumberFormatException e){
	        	
	        }
	        
	        position=escaped.indexOf("\\u");
	    }
	    processed+=escaped;

	    return processed;
	}
	
	// Extrae el texto contenido en el campo *, si no existe el campo devuelve null.
	// p.e. "*":"Sumador"} ==> Sumador
	public static String getTitle(String body){
		String title = null;
		if(body.contains("*")){
			int _start = body.indexOf("*")+4;
			int _end = body.indexOf("}", _start)-1;
			title = body.substring(_start, _end);
		}
		return title;
	}
	
	/** Extrae del body el texto del articulo que esta en el campo "extract"
	 * @param body - String Body de la llamada GET a un articulo
	 * @return String - Texto del articulo, NULL si el body es NULL
	 */
	public static String getArticle(String body){
		String article = null;
		if(body != null && body.contains("\"extract\"")){
			int _start = body.indexOf("extract\"")+10;
			int _end = body.length()-5;
			article = body.substring(_start, _end);
			//System.out.println(article);
		}
		return article;
	}
	
	/** Quita todos los tags de un String
	 * @param source - String Texto a filtrar
	 * @return String - Devuelve source sin html tags
	 */
	public static String stripHtmlRegex(String source) {
        return source.replaceAll("<.*?>", "");
    }
	
	/** Quita tabuladores, saltos de linea, ... de un String para que quede todo en una linea.
	 * @param source - String Texto a filtrar
	 * @return String - Texto filtrado sin los caracteres especiales 
	 */
	public static String removeSpecialChracters(String source) {
		source = source.replace("\\n", " ");
		source = source.replace("\\t", " ");
		source = source.replace("\\f", "");
		source = source.replace("\\r", " ");
		source = source.replace("\\\"", "\"");
		
		return source;
	}
	
	/** Quita los dobles espacios en blanco seguidos
	 * @param source - Texto a filtrar
	 * @return String - Texto filtrado sin dos o mas espacios en blanco seguidos
	 */
	public static String removeSpace(String source){
		return source.replaceAll("\\s+", " ");
	}
	
	public static String removeDisplayStyle(String source){
		if(source == null || source.indexOf("{\\\\displaystyle")== -1)
			return source;
		
	    int position_1 = source.indexOf("{\\\\displaystyle");
	    
	    while(position_1!=-1) {
	        int brakets = 1;
	        int position_2 = position_1+1;
	        while(brakets > 0 && position_2 < source.length()-1 ){
	        	//System.out.println(source.charAt(position_2) + " --- brakets --> " + brakets);
	        	if (source.charAt(position_2) == '{')
	        		brakets++;
	        	if (source.charAt(position_2) == '}')
	        		brakets--;
	        	position_2++;
	        }
	        source = source.replace(source.substring(position_1, position_2), "");
	        
	        position_1=source.indexOf("{\\\\displaystyle");
	    }
	    return source;	
	}
	
	public static String removeNotesEdits(String source){
		if (source != null && !source.isEmpty()){
			source = source.replaceAll("== Notas[^=]* ==", "");
			source = source.replaceAll("== Notes[^=]* ==", "");
			source = source.replaceAll("Editar(\\s|$)", " ");
			source = source.replaceAll("Edit(\\s|$)", " ");
		}
		return source;
	}
	
	public static String removeExtra(String source){
		if (source != null && !source.isEmpty()){
			// Se obtiene las posiciones de las secciones
			int see_also = source.indexOf("== Véase también ==") == -1? source.indexOf("== See also ==") : 
				source.indexOf("== Véase también ==");
			int references = source.indexOf("== Referencias ==") == -1? source.indexOf("== References ==") : 
				source.indexOf("== Referencias ==");
			int external_links = source.indexOf("== Enlaces externos ==") == -1? source.indexOf("== External links ==") : 
				source.indexOf("== Enlaces externos ==");
			int bibliography = source.indexOf("== Bibliografía ") == -1? source.indexOf("== Bibliography "):
				source.indexOf("== Bibliografía ");
			
			//System.out.println("See_also: "+ see_also);
			//System.out.println("References: " + references);
			//System.out.println("Extenal_links: " + external_links);
			
			// Se insertan los valores en el array
			int[] arrayPosition = new int[4];
			arrayPosition[0] = see_also;
			arrayPosition[1] = references;
			arrayPosition[2] = external_links;
			arrayPosition[3] = bibliography;
			
			// Se mira cual de las secciones es la primera que aparece.
			int position = Integer.MAX_VALUE;
			for(int i = 0; i < arrayPosition.length; i++){
				if(arrayPosition[i] != -1 && arrayPosition[i] < position) position = arrayPosition[i];
			}
			
			if (position != Integer.MAX_VALUE){
				source = source.replace(source.substring(position, source.length()), "");
			}
		}
		return source;
	}
	public static String removeEquals(String source){
		return source.replaceAll("\\s=+(\\s|$)", " ");
		
	}

	public static String removeCitas(String source){
		source = source.replace("[cita requerida]", "");
		return source.replaceAll("\\[\\d+\\]", " ");
	}
	
	
	// Limpia un String.
	public static String cleanText(String source){
		source = unescapeJava(source);
		source = stripHtmlRegex(source);
		source = removeSpecialChracters(source);
		source = removeDisplayStyle(source);
		source = removeNotesEdits(source);
		source = removeExtra(source);
		source = removeCitas(source);
		source = removeEquals(source);
		source = removeSpace(source);
		
		return source;
	}
	
	public static String csvLine(int id, String title, String category, String text){
		String semicolon = ";;";
		return id + semicolon + title + semicolon + category + semicolon + text;
	}
	
	public static boolean isNumeric(String s) {  
	    return s != null && s.matches("[-+]?\\d*\\.?\\d+");  
	}  
}
