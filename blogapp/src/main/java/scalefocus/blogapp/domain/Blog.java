package scalefocus.blogapp.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Accessors(chain = true)
public class Blog implements BlogAppEntity {

    @Id
    @org.springframework.data.annotation.Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "blog_seq")
    @SequenceGenerator(allocationSize = 1, name = "blog_seq")
    private Long id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "body", nullable = false)
    private String body;

    @JoinColumn(foreignKey = @ForeignKey(name = "FK_blog_blog_user"), nullable = false, name = "created_by")
    @ManyToOne(targetEntity = BlogUser.class)
    private BlogUser createdBy;

  //  @ToString.Exclude
    @ManyToMany(cascade = { })
    @JoinTable(name = "BLOG_TAG_MM", joinColumns = @JoinColumn(foreignKey = @ForeignKey(name = "FK_blog_tag_mm_blog"), name = "BLOG_ID", referencedColumnName = "ID"), inverseJoinColumns = @JoinColumn(foreignKey = @ForeignKey(name = "FK_blog_tag_mm_blog_tag"), name = "BLOG_TAG_ID", referencedColumnName = "ID"))
    private List<BlogTag> blogtags = new ArrayList<>();

}
