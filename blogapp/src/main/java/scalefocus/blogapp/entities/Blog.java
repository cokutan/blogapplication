package scalefocus.blogapp.entities;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;

@Entity
public class Blog implements BlogAppEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(name = "title")
	private String title;

	@Column(name = "body")
	private String body;

	@JoinColumn(foreignKey = @ForeignKey(name = "FK_blog_blog_user"), nullable = false, name = "created_by")
	@ManyToOne(cascade = CascadeType.ALL, targetEntity = BlogUser.class)
	private BlogUser createdBy;

	@ManyToMany
	@JoinTable(name = "BLOG_TAG_MM", joinColumns = @JoinColumn(foreignKey = @ForeignKey(name = "FK_blog_tag_mm_blog"), name = "BLOG_ID", referencedColumnName = "ID"), inverseJoinColumns = @JoinColumn(foreignKey = @ForeignKey(name = "FK_blog_tag_mm_blog_tag"), name = "BLOG_TAG_ID", referencedColumnName = "ID"))
	private List<BlogTag> blogtags = new ArrayList<>();

	public Blog() {
	}

	private Blog(long id, String title, String body, BlogUser user) {
		this.id = id;
		this.title = title;
		this.body = body;
		this.createdBy = user;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public BlogUser getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(BlogUser createdBy) {
		this.createdBy = createdBy;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if ((obj == null) || (getClass() != obj.getClass()))
			return false;
		Blog other = (Blog) obj;
		return Objects.equals(id, other.id);
	}

	public static Blog createBlog(BlogUser blogUser, String title, String body) {

		return new Blog(0, title, body, blogUser);
	}

	public List<BlogTag> getBlogtags() {
		return blogtags;
	}

	public void setBlogtags(List<BlogTag> blogtags) {
		this.blogtags = blogtags;
	}

}
