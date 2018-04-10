package category;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Categorymember {

	@SerializedName("pageid")
	@Expose
	private Integer pageid;
	@SerializedName("ns")
	@Expose
	private Integer ns;
	@SerializedName("title")
	@Expose
	private String title;

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

}
