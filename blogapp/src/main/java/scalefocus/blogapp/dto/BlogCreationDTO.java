package scalefocus.blogapp.dto;

public class BlogCreationDTO {
	private String username;
	private String title;
	private String body;
	
	
	public BlogCreationDTO(String username, String title, String body) {
		super();
		this.username = username;
		this.title = title;
		this.body = body;
	}
	
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
	}
	
	
}
