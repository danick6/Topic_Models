package category;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Continue {

	@SerializedName("cmcontinue")
	@Expose
	private String cmcontinue;
	@SerializedName("continue")
	@Expose
	private String _continue;

	public String getCmcontinue() {
		return cmcontinue;
	}

	public void setCmcontinue(String cmcontinue) {
		this.cmcontinue = cmcontinue;
	}

	public String getContinue() {
		return _continue;
	}

	public void setContinue(String _continue) {
		this._continue = _continue;
	}

}