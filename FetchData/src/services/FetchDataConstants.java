package services;

public final class FetchDataConstants {
	private FetchDataConstants(){
		
	}
	public static final int OK = 200;
	public static final int BadResquest = 400;
	public static final int NotFound = 404;
	
	public static final int Min_PAG = 50;
	
	// URL de los ENDPOINTs
	public static final String en_endpoint = "https://en.wikipedia.org/w/api.php";
	public static final String es_endpoint = "https://es.wikipedia.org/w/api.php";
	
	public static final String USER_AGENT = "Mozilla/5.0";
	
	// URL para la query de categorias
	public static final String categoryName = "?action=query&format=json&prop=info&titles=Category:"; 
	public static final String categoryQuery = "?action=query&list=categorymembers&format=json&cmlimit=500&cmsort=timestamp&cmtitle=Category:";
	
	public static final String langLinkEs = "?action=query&format=json&prop=langlinks&lllang=es&pageids=";
	
	public static final String articleName = "?action=query&format=json&prop=extracts&explaintext=true&titles=";
	public static final String articleId = "?action=query&prop=info&format=json&titles=";
	
	// URL Topic Model Base URL
	public static final String TM_ES_BaseURL = "lab4.librairy.linkeddata.es/dkaiwei-es-topics";
	public static final String TM_EN_BaseURL = "lab4.librairy.linkeddata.es/dkaiwei-en-topics";
	
	
	

	
}




