package scalefocus.blogapp.blogappwebflux.domain;

import java.util.Arrays;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document("attachments")
public class AttachmentFile {
    @Id
    private String id;
    private byte[] file;
    private String blogId;
    private String filename;
    private String format;

    public AttachmentFile(byte[] ba, String blogId, String filename, String mimeType) {
        this.file = ba;
        this.blogId = blogId;
        this.filename = filename;
        ImageFormats.as(mimeType).ifPresent(t -> this.format = t.name());
        VideoFormats.as(mimeType).ifPresent(t -> this.format = t.name());
    }

    public boolean isImage() {
        return Arrays.stream(ImageFormats.values()).anyMatch(t -> t.name().equals(format));
    }
}
