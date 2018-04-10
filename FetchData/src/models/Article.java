package models;

public class Article {
	private int pageid;
	private String title;
	
	public Article(){
		this.pageid = 0;
		this.title = "";
	}
	
	public int getPageid() {
		return pageid;
	}
	public void setPageid(int pageid) {
		this.pageid = pageid;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}

	@Override
	public String toString() {
		return "Article [pageid=" + pageid + ", title=" + title + "]";
	}
	
	
	
	

}
