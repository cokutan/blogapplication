package scalefocus.blogapp.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import lombok.Data;
import lombok.experimental.Accessors;

@Entity
@Data
@Accessors(chain = true)
public class BlogUser  {

	private static final long serialVersionUID = -4818698754462896098L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "blog_user_seq")
	@SequenceGenerator(allocationSize = 1, name = "blog_user_seq")
	private Long id;

	@Column(name = "displayname", nullable = false)
	private String displayname;

	@Column(name = "username", unique = true, nullable = false)
	private String username;
}
