import java.io.BufferedReader;
import java.io.FileReader;
import models.Document;
import services.TrainingServices;

public class Shape {
	
	public static void main(String[] args) {
	     try {
	    	 FileReader en_corpus = new FileReader("src/en-corpus.csv");
	    	 FileReader es_corpus = new FileReader("src/es-corpus.csv");

	    	 BufferedReader en_br = new BufferedReader(en_corpus);
	    	 BufferedReader es_br = new BufferedReader(es_corpus);
	    	 String en_line;
	    	 String es_line;
	    	 Document en_doc = null;
	    	 Document es_doc = null;
	    	 double[] en_vector = null;
	    	 double[] es_vector = null;
	    	 int count_docs = 1;
	    	 
	    	 TrainingServices.post_spaces(50);
	    	 if(TrainingServices.delete_points()){

	    		 // Se empieza a enviar los textos
	    		 while ((en_line = en_br.readLine()) != null && (es_line = es_br.readLine()) != null) {
	    			 System.out.println("--- Linea del fichero: "+ count_docs + " ---");
	    			 en_doc = TrainingServices.parseLine(en_line);
	    			 es_doc = TrainingServices.parseLine(es_line);

	    			 if (en_doc != null && es_doc != null){
	    				 en_vector = TrainingServices.post_shape("EN",en_doc.getText());
	    				 es_vector = TrainingServices.post_shape("ES",es_doc.getText());	
	    			 }

	    			 if (en_vector != null && es_vector != null){
	    				 TrainingServices.post_points("EN", en_doc.getId(), en_doc.getName(), en_vector);
	    				 TrainingServices.post_points("ES", es_doc.getId(), es_doc.getName(), es_vector);
	    				 en_vector = null;
	    				 es_vector = null;
	    				 en_doc = null;
	    				 es_doc = null;
	    				 count_docs++;
	    			 }
	    		 }
	    		 System.out.println("--- COMPLETADO ---");
	    	 }
	    	 en_br.close();
	    	 es_br.close();

	     } catch (Exception e) {
	    	 e.printStackTrace();
	     }
		
		/*TrainingServices.post_spaces(50);
		TrainingServices.delete_points();
		String text = " alternadamente frente a la fuente de calor. Se suele decir que el souvlaki es la variante griega del shish kebab. Etimología y uso del término La terminología de la palabra 'souvlaki' así como de sus variantes es confusa e inconsistente. Dependiendo el contexto, el término 'souvlaki' se puede referir a cualquiera de sus variantes. Es muy parecido al shish kebab que se emplea para denotar una variante de fierritos. En muchas regiones, principalmente Atenas y el sur de Grecia, al sándwich de gyros se le apoda como un 'souvlaki'. La palabra souvlaki es un diminutivo de souvla (pincho), y deriva de la palabra en latín subula. Kalamaki El Kalamaki es un sinónimo de souvlaki en la ciudad de Atenas, para diferenciarlo de otras variedades de souvlaki. El kalamaki emplea trozos de carne de dos a tres centímetros de lado, marinados la noche anterior en zumo de limón y aceite de oliva jun";
		double[] vector = TrainingServices.post_shape("ES", text);
		TrainingServices.post_points("ES", "1", "prueba", vector);*/
		
	}
}
