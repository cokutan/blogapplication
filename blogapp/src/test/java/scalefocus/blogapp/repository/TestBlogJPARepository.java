package scalefocus.blogapp.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;

import org.hibernate.Hibernate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import scalefocus.blogapp.dto.BlogSummary;
import scalefocus.blogapp.entities.Blog;
import scalefocus.blogapp.entities.BlogUser;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class TestBlogJPARepository {

	@Autowired
	private BlogJPARepository repository;

	@Test
	void test_InitialData() {
		Optional<Blog> actual = repository.findById(1l);
		Blog expectedValue = Blog.createBlog(new BlogUser(), "None", "None");
		expectedValue.setId(1l);
		assertThat(actual).hasValue(expectedValue);
		assertThat(actual.get().getCreatedBy().getId()).isSameAs(1l);
	}

	@Test
	void test_findBlogSummaryByUser() {

		List<BlogSummary> actual = repository.findBlogSummaryByUser(1l);
		assertThat(actual).singleElement();
		BlogSummary blogSummary = actual.get(0);
		assertThat(blogSummary.getShortSummary()).isEqualTo("Lorem ipsum dolor si");
		assertThat(blogSummary.getTitle()).isEqualTo("Blogpost1");
		
	}
}
