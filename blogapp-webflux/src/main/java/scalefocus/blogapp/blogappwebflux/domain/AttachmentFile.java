package scalefocus.blogapp.blogappwebflux.domain;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.Arrays;

@Data
@Table
public class AttachmentFile {
    @Id
    @Column("id")
    private Long id;
    @Column
    private byte[] file;
    @Column
    private Long blogId;
    @Column
    private String filename;
    @Column
    private String format;

    public AttachmentFile(byte[] ba, Long blogId, String filename, String mimeType) {
        this.file = ba;
        this.blogId = blogId;
        this.filename = filename;
        ImageFormats.as(mimeType).ifPresent(t -> this.format = t.getType());
        VideoFormats.as(mimeType).ifPresent(t -> this.format = t.getType());
    }

    public boolean isImage() {
        return Arrays.stream(ImageFormats.values()).anyMatch(t -> t.getType() == format);
    }
}
