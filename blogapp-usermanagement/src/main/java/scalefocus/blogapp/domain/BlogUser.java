package scalefocus.blogapp.domain;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("users")
@Data
@Accessors(chain = true)
public class BlogUser {

  @Id private String id;
  private String displayname;
  private String username;
}
