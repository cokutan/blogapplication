package scalefocus.blogapp.domain;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("blogs")
@Data
@Accessors(chain = true)
public class Blog implements BlogAppEntity {

  @Id private String id;
  private String title;
  private String body;
  private BlogUser createdBy;
  private List<String> tags = new ArrayList<>();
}
