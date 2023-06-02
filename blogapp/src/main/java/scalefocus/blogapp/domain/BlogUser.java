package scalefocus.blogapp.domain;



import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigInteger;

@Document("users")
@Data
@Accessors(chain = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class BlogUser implements BlogAppEntity {

	@Id
	private String id;

	private String displayname;

	private String username;

}
