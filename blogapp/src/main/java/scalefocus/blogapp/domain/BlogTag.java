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
public class BlogTag implements BlogAppEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "blog_tag_seq")
    @SequenceGenerator(allocationSize = 1, name = "blog_tag_seq")
    private Long id;

    @Column(name = "tag", unique = true, nullable = false)
    private String tag;

}
