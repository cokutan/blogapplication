package scalefocus.blogapp.domain;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Accessors(chain = true)
@Document("attachmentfiles")
public class AttachmentFile {
    @Id
    private Long id;
    private byte[] file;
    private Long blogId;
    private String filename;
    private String format;
}
