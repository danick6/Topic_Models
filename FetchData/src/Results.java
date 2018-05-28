import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import models.Document;
import models.Neighbour_result;
import services.TrainingServices;

public class Results {
	
	public static void main(String[] args) {
		// ---- MAIN ----
	     try {
	    	 FileWriter results_fw = new FileWriter("results.csv");
	    	 BufferedWriter results_bw = new BufferedWriter(results_fw);
	    	 
	    	 FileReader en_corpus = new FileReader("src/en-corpus.csv");
	    	 FileReader es_corpus = new FileReader("src/es-corpus.csv");

	    	 BufferedReader en_br = new BufferedReader(en_corpus);
	    	 BufferedReader es_br = new BufferedReader(es_corpus);
	    	 String en_line;
	    	 String es_line;
	    	 Document en_doc = null;
	    	 Document es_doc = null;
	    	 
	    	 double[] en_points = null;
	    	 double[] es_points = null;
	    	 
	    	 double comparisons_result = -1;
	    	 
	    	 Neighbour_result result_en_es = new Neighbour_result();
	    	 Neighbour_result result_es_en = new Neighbour_result();
	    	 
	    	 int count_docs = 1;
	    	 
	    	 // Se empieza a enviar los textos
	    	 while ((en_line = en_br.readLine()) != null && (es_line = es_br.readLine()) != null) {
	    		 en_doc = TrainingServices.parseLine(en_line);
	    		 es_doc = TrainingServices.parseLine(es_line);
	    		 
	    		 System.out.println("--- Linea del fichero: "+ count_docs + " --- " + en_doc.getName() + " / " + es_doc.getName());

	    		 if (en_doc != null && es_doc != null){
	    			en_points = TrainingServices.get_idPoints(en_doc.getId());
	    			es_points = TrainingServices.get_idPoints(es_doc.getId());	
	    		 }
	    		 
	    		 if (en_points != null && es_points != null){
	    			comparisons_result = TrainingServices.post_comparisons(en_points, es_points);
	    			result_en_es = TrainingServices.post_points_neighbours(en_doc.getId(), es_doc.getId(), 2500, "ES");
	    			result_es_en = TrainingServices.post_points_neighbours(es_doc.getId(), en_doc.getId(), 2500, "EN");
	    			//System.out.println("Comparisons: "+ comparisons_result);
	    		 }
	    		 
	    		 if(result_en_es != null && result_es_en != null && comparisons_result > -1){
	    			 String line = en_doc.getId() + ";" + es_doc.getId() + ";" + comparisons_result + ";" +
	    					 result_en_es.getPosition() + ";" + result_es_en.getPosition(); 
	    			 System.out.println(line);
	    			 results_bw.write(line);
	    			 results_bw.newLine();
	    			 results_bw.flush();
	    			 
	    			 result_en_es = null;
	    			 result_es_en = null;
	    			 comparisons_result = -1;
	    		 }
	    		 
	    		 count_docs ++;
	    		 
	    	 }
	    	 results_bw.close();
	    	 en_br.close();
	    	 es_br.close();
	    	 
	     }catch (Exception e) {
	    	 e.printStackTrace();
	     }
	     // ------------------------
		
		/*String id_en = "7645050";
		String id_es = "239729";
		
		double[] en_points = TrainingServices.get_idPoints("7645050");
		double[] es_points = TrainingServices.get_idPoints("239729");
		
		double comparisons_result = TrainingServices.post_comparisons(en_points, es_points);
		System.out.println("Comparisons: "+ comparisons_result);
		
		Neighbour_result neighbour_2500en_es = TrainingServices.post_points_neighbours(id_en, id_es, 2500, "ES");
		System.out.println("Neighbour position: "+neighbour_2500en_es.getPosition()+" ------- "+"Score: "+ neighbour_2500en_es.getScore());
		
		Neighbour_result neighbour_2500es_en = TrainingServices.post_points_neighbours(id_es, id_en, 2500, "EN");
		System.out.println("Neighbour position: "+neighbour_2500es_en.getPosition()+" ------- "+"Score: "+ neighbour_2500es_en.getScore());
		*/
	}
}
	     
