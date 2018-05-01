import java.io.BufferedReader;
import java.io.FileReader;
import services.TrainingServices;
import models.Document;

public class Training {
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
    	 int count_docs = 0;
    	 
    	 // Se empieza a enviar los textos
    	 while ((en_line = en_br.readLine()) != null && (es_line = es_br.readLine()) != null) {
    		 
    		 en_doc = TrainingServices.parseLine(en_line);
    		 es_doc = TrainingServices.parseLine(es_line);
    		 
    		 if (en_doc != null && es_doc != null){
    			 TrainingServices.post_documents("EN", en_doc);
    			 TrainingServices.post_documents("ES", es_doc);
    			 count_docs++;
    		 }
    	 }
    	 
    	 en_br.close();
    	 es_br.close();
    	 
    	 if (count_docs == TrainingServices.get_documents("EN") && count_docs == TrainingServices.get_documents("ES")){
    		 System.out.println("Complete: " + count_docs + " documents added");
    		 //System.out.println("Executing: Building Models ..."); 
    	 }
    	 
    	 /*String line = "27992090;;Animal suicide;;Ethology Categoria3;;Animal suicide refers to any kind of self-destructive";
    	 Document doc = TrainingServices.parseLine(line);
    	 JSONObject jsoon = TrainingServices.jsonBuilder(doc.getId(), doc.getName(), doc.getText(), doc.getLabels());
    	 System.out.println(jsoon.toString(2));
    	*/
    	 /* String id = "2";
    	 String name = "prueba 6";
    	 String text = "texto de prueba 6";
    	 List<String> labels = new ArrayList<String>();
         labels.add("cat6");
         labels.add("cat9");
         
         TrainingServices.post_documents("ES", id, name, text, labels);*/
    	 
        } catch (Exception e) {
         e.printStackTrace();
       }
     }
	   
}