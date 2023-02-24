package scalefocus.blogapp.entities;


import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class BlogTag implements BlogAppEntity{

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(name = "tag", unique = true, nullable = false)
	private String tag;

	public BlogTag() {
	}

	public BlogTag(long id, String tag) {
		this.id = id;
		this.tag = tag;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, tag);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if ((obj == null) || (getClass() != obj.getClass()))
			return false;
		BlogTag other = (BlogTag) obj;
		return Objects.equals(id, other.id) && Objects.equals(tag, other.tag);
	}

}
