package scalefocus.blogapp.blogappwebflux.domain;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("blogs")
@Data
@Accessors(chain = true)
public class Blog  {
  @Id private String id;
}
