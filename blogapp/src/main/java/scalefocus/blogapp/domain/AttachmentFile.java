package scalefocus.blogapp.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.experimental.Accessors;

@Entity
@Data
@Accessors(chain = true)
public class AttachmentFile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(length=100000000)
    private byte[] file;
    @Column
    private Long blogId;
    @Column
    private String filename;
    @Column
    private String format;
}
