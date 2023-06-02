package scalefocus.blogapp.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import scalefocus.blogapp.domain.Blog;
import scalefocus.blogapp.repository.sqldb.BlogRepository;

@SpringBootTest
@ActiveProfiles("test")
class TestBlogRepository {

	@Autowired
	private BlogRepository repository;

	@Test
	void test_InitialData() {
		Optional<Blog> actual = repository.findById("1");
		Blog expectedValue = new Blog();
		expectedValue.setId("1");
		assertThat(actual.get().getId()).isEqualTo(1);
		assertThat(actual.get().getCreatedBy().getId()).isEqualTo(1l);
		assertThat(actual.get().getTags().get(0)).isEqualTo("First Tag");
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
		assertThat(blog.getId()).isEqualTo(1);
	}
}
