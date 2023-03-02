package scalefocus.blogapp.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import jakarta.transaction.Transactional;
import scalefocus.blogapp.domain.Blog;

@SpringBootTest
class TestBlogJPARepository {

	@Autowired
	private BlogJPARepository repository;

	@Test
	@Transactional
	void test_InitialData() {
		Optional<Blog> actual = repository.findById(1l);
		Blog expectedValue = new Blog();
		expectedValue.setId(1l);
		assertThat(actual.get().getId()).isEqualTo(1);
		assertThat(actual.get().getCreatedBy().getId()).isEqualTo(1l);
		assertThat(actual.get().getBlogtags().get(0).getTag()).isEqualTo("First Tag");
	}

	@Test
	void test_findBlogSummaryByUser() {

		List<Blog> actual = repository.findBlogSummaryByUser("aliveli");
		assertThat(actual).singleElement();
		Blog blogSummary = actual.get(0);
		assertThat(blogSummary.getBody()).isEqualTo("Lorem ipsum dolor si");
		assertThat(blogSummary.getTitle()).isEqualTo("Blogpost1");

	}

	@Test
	void test_findByBlogtags_Tag() {
		List<Blog> actual = repository.findByBlogtags_Tag("First Tag");
		assertThat(actual).singleElement();
		Blog blog = actual.get(0);
		assertThat(blog.getId()).isEqualTo(1);
	}
}
