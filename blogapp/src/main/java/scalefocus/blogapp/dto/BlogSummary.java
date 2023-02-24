package scalefocus.blogapp.dto;


public class BlogSummary {

	private String title;

	private String shortSummary;

	public BlogSummary(String title, String shortSummary) {
		super();
		this.title = title;
		this.shortSummary = shortSummary;
	}

	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getShortSummary() {
		return shortSummary;
	}
	public void setShortSummary(String shortSummary) {
		this.shortSummary = shortSummary;
	}

}
