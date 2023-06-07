package scalefocus.blogapp.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.retry.support.RetryTemplateBuilder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.ContainerLaunchException;
import org.testcontainers.junit.jupiter.Testcontainers;
import scalefocus.blogapp.containers.MongoDBTestContainer;
import scalefocus.blogapp.domain.Blog;
import scalefocus.blogapp.repository.sqldb.BlogRepository;

@SpringBootTest
@ActiveProfiles("testcontainers")
@Testcontainers
class TestBlogRepository {

  @Autowired private BlogRepository repository;
@Autowired private MongoTemplate mongoTemplate;
  private static final MongoDBTestContainer mongoDBTestContainer = new MongoDBTestContainer();

  static {
    new RetryTemplateBuilder()
            .maxAttempts(3)
            .retryOn(ContainerLaunchException.class)
            .build()
            .execute(retryContext -> {
              System.out.printf("Starting MongoDB container attempt {%d}", retryContext.getRetryCount() + 1);
              mongoDBTestContainer.start();
              return null;
            });
    mongoDBTestContainer.start();
  }

  @DynamicPropertySource
  static void mongoDbProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.data.mongodb.uri", mongoDBTestContainer::getReplicaSetUrl);
  }

  @Test
  void test_findBlogSummaryByUser() {
    List<Blog> actual = repository.findBlogSummaryByUser("aliveli", Pageable.ofSize(10));
    assertThat(actual).singleElement();
    Blog blogSummary = actual.get(0);
    assertThat(blogSummary.getBody()).isEqualTo("Lorem ipsum dolor si");
    assertThat(blogSummary.getTitle()).isEqualTo("Blogpost1");
  }

  @Test
  void test_findByBlogtags_Tag() {
    List<Blog> actual = repository.findByTags("First Tag");
    assertThat(actual).singleElement();
    Blog blog = actual.get(0);
    assertThat(blog.getCreatedBy().getUsername()).isEqualTo("aliveli");
  }
}
