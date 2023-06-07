package scalefocus.blogapp.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Testcontainers;
import scalefocus.blogapp.containers.MongoDBTestContainer;
import scalefocus.blogapp.domain.Blog;
import scalefocus.blogapp.repository.sqldb.BlogRepository;

@SpringBootTest
@ActiveProfiles("testcontainers")
@Testcontainers
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
class TestBlogRepository {

  @Autowired private BlogRepository repository;
  @Autowired private MongoTemplate mongoTemplate;

  static {
    MongoDBTestContainer.startInstance();
  }

  @DynamicPropertySource
  static void mongoDbProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.data.mongodb.uri", MongoDBTestContainer.getInstance()::getReplicaSetUrl);
  }

  @Test
  void test_findBlogSummaryByUser() {
    List<Blog> actual = repository.findBlogSummaryByUser("Zuan", Pageable.ofSize(10));
    Condition<Blog> searchedTitle =
        new Condition<>(m -> "condimentum".equals(m.getTitle()), "searchedTitle");
    Condition<Blog> searchedBody =
        new Condition<>(m -> "Suspendisse potenti.".equals(m.getBody()), "searchedBody");
    assertThat(actual).areAtLeastOne(searchedTitle);
    assertThat(actual).areAtLeastOne(searchedBody);
  }

  @Test
  void test_findByBlogtags_Tag() {
    List<Blog> actual = repository.findByTags("First Tag");
    assertThat(actual).singleElement();
    Blog blog = actual.get(0);
    assertThat(blog.getCreatedBy().getUsername()).isEqualTo("aliveli");
  }
}
