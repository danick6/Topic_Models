package article;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ArticlePage {

	@SerializedName("pageid")
	@Expose
	private Integer pageid;
	@SerializedName("ns")
	@Expose
	private Integer ns;
	@SerializedName("title")
	@Expose
	private String title;
	@SerializedName("extract")
	@Expose
	private String extract;

	public Integer getPageid() {
		return pageid;
	}

	public void setPageid(Integer pageid) {
		this.pageid = pageid;
	}

	public Integer getNs() {
		return ns;
	}

	public void setNs(Integer ns) {
		this.ns = ns;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getExtract() {
		return extract;
	}

	public void setExtract(String extract) {
		this.extract = extract;
	}

}