package category;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Query {

	@SerializedName("categorymembers")
	@Expose
	private List<Categorymember> categorymembers = null;

	public List<Categorymember> getCategorymembers() {
		return categorymembers;
	}

	public void setCategorymembers(List<Categorymember> categorymembers) {
		this.categorymembers = categorymembers;
	}

}