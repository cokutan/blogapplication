package scalefocus.blogapp.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Testcontainers;
import scalefocus.blogapp.containers.MongoDBTestContainer;
import scalefocus.blogapp.containers.OpenSearchTestContainer;
import scalefocus.blogapp.domain.Blog;
import scalefocus.blogapp.domain.BlogUser;
import scalefocus.blogapp.exceptions.BlogAppEntityNotFoundException;
import scalefocus.blogapp.repository.sqldb.BlogRepository;

@SpringBootTest
@Testcontainers
@ActiveProfiles("testcontainers")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
class TestBlogServiceIntegration {

  public static final String ID_1 = "000000000000000000000001";
  public static final String ID_2 = "000000000000000000000002";
  public static final String ID_3 = "000000000000000000000003";
  @Autowired private BlogService blogService;

  private static final OpenSearchTestContainer openSearchTestContainer =
      new OpenSearchTestContainer();

  static {
    openSearchTestContainer.start();
  }

  @Autowired private BlogRepository blogRepository;

  private final BlogUser blogUser = new BlogUser().setUsername("aliveli");

  static {
    MongoDBTestContainer.startInstance();
  }

  @DynamicPropertySource
  static void mongoDbProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.data.mongodb.uri", MongoDBTestContainer.getInstance()::getReplicaSetUrl);
    registry.add("${opensearch.host.port}", () -> openSearchTestContainer.getMappedPort(9200));
  }

  @Test
  void createBlog_shouldReturnCreatedBlogObject() throws BlogAppEntityNotFoundException {

    Blog createdBlog =
        blogService.createBlog(
            new Blog().setCreatedBy(blogUser).setTitle("title_test").setBody("body_test"));
    Condition<Blog> idGiven =
        new Condition<>(t -> t.getId() != null && !"0".equals(t.getId()), "idGiven");

    assertThat(createdBlog).has(idGiven);
  }

  @Test
  void getBlogSummaryListForUser_shouldReturnSummaryListOfUser()
      throws BlogAppEntityNotFoundException {
    blogService.createBlog(
        new Blog()
            .setCreatedBy(blogUser)
            .setTitle("title_test")
            .setBody(
                "The following table lists the supported databases and their tested versions. ... Changing database locking timeout in a cluster configuration."));
    blogService.createBlog(
        new Blog()
            .setCreatedBy(blogUser)
            .setTitle("title_test")
            .setBody(
                " A convenient way to fix a defective complex sql-query in a service that causes a functional test to fail is to inspect the database state"));
    List<Blog> summaryList = blogService.getBlogSummaryListForUser("aliveli", 0, 10);
    assertThat(summaryList).filteredOn("body", "The following table ").isNotEmpty();
    assertThat(summaryList).filteredOn("body", " A convenient way to").isNotEmpty();
  }

  @Test
  void updateBlog_shouldReturnCreatedBlogObject() throws BlogAppEntityNotFoundException {
    Blog blog =
        new Blog()
            .setCreatedBy(
                new BlogUser().setId(ID_1).setDisplayname("test1").setUsername("username"))
            .setTitle("title updated")
            .setBody("description updated");

    assertThat(blogService.updateBlog(ID_1, blog))
        .hasFieldOrPropertyWithValue("title", "title updated")
        .hasFieldOrPropertyWithValue("body", "description updated");
  }

  @Test
  void deleteBlog_shouldReturnNoObject() throws BlogAppEntityNotFoundException {
    blogService.deleteBlog(ID_2);

    assertThat(blogRepository.findById(ID_2)).isEmpty();
  }

  @Test
  void attachTag_shouldAddTagToBlogObject() throws BlogAppEntityNotFoundException {
    blogService.attachTag(ID_1, "tag1");
    Blog blog = blogRepository.findById(ID_1).get();
    assertThat(blog.getTags()).hasSize(2).contains("First Tag", "tag1").isNotEmpty();
  }

  @Test
  void attachTag_shouldRemoveTagToBlogObject() throws BlogAppEntityNotFoundException {
    blogService.unattachTag(ID_3, "Third Tag");
    Blog blog = blogRepository.findById(ID_3).get();
    assertThat(blog.getTags()).isEmpty();
  }
}
