package services;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class FetchDataUtils {
	private static final String VALUES = "!#$&'()*+,/:;=?@[] \"%-.<>\\^_`{|}~";

	// Codifica un texto a codigo porciento (Percent-encoding)
	public static String encode(String input) {
	    if (input == null || input.isEmpty()) {
	        return input;
	    }
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
	
	// Transforma un string con unicode a letras normales (ASCII)
	public static String unescapeJava(String escaped) {
	    if(escaped.indexOf("\\u")==-1)
	        return escaped;

	    String processed="";

	    int position=escaped.indexOf("\\u");
	    while(position!=-1) {
	        if(position!=0)
	            processed+=escaped.substring(0,position);
	        String token=escaped.substring(position+2,position+6);
	        escaped=escaped.substring(position+6);
	        processed+=(char)Integer.parseInt(token,16);
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
}
