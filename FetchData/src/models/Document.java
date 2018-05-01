package models;
import java.util.List;

public class Document {
	private String id;
	private List<String> labels;
	private String name;
	private String text;
	
	public Document(String id, String name, String text, List<String> labels) {
		super();
		this.id = id;
		this.name = name;
		this.text = text;
		this.labels = labels;
		
	}

	public String getId() {
		return id;
	}

	@Override
	public String toString() {
		return "Document [id=" + id + ", labels=" + labels + ", name=" + name + ", text=" + text + "]";
	}

	public List<String> getLabels() {
		return labels;
	}
	
	public String getName() {
		return name;
	}

	public String getText() {
		return text;
	}
	
}
