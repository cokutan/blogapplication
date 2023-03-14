package scalefocus.blogapp.domain;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import lombok.Data;
import lombok.experimental.Accessors;

@Entity
@Data
@Accessors(chain = true)
public class Blog implements BlogAppEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "blog_seq")
    @SequenceGenerator(allocationSize = 1, name = "blog_seq")
    private Long id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "body", nullable = false)
    private String body;

    @JoinColumn(foreignKey = @ForeignKey(name = "FK_blog_blog_user"), nullable = false, name = "created_by")
    @ManyToOne(cascade = {}, targetEntity = BlogUser.class)
    private BlogUser createdBy;

    @ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH })
    @JoinTable(name = "BLOG_TAG_MM", joinColumns = @JoinColumn(foreignKey = @ForeignKey(name = "FK_blog_tag_mm_blog"), name = "BLOG_ID", referencedColumnName = "ID"), inverseJoinColumns = @JoinColumn(foreignKey = @ForeignKey(name = "FK_blog_tag_mm_blog_tag"), name = "BLOG_TAG_ID", referencedColumnName = "ID"))
    private List<BlogTag> blogtags = new ArrayList<>();

}
