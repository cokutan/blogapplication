package scalefocus.blogapp.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.platform.commons.annotation.Testable;

import mockit.Expectations;
import mockit.Injectable;
import mockit.Tested;
import scalefocus.blogapp.entities.Blog;
import scalefocus.blogapp.entities.BlogUser;
import scalefocus.blogapp.exceptions.BlogNotFoundException;
import scalefocus.blogapp.repository.BlogJPARepository;
import scalefocus.blogapp.repository.BlogUserRepository;

@Testable
class TestBlogService {

	@Tested(fullyInitialized = true)
	BlogService blogService;

	@Injectable
	BlogUserRepository blogUserRepository;

	@Injectable
	BlogJPARepository blogJPARepository;

	@Test
	void createBlog_shouldReturnCreatedBlogObject() {
		new Expectations() {
			{
				blogJPARepository.save(withNotNull());
				Blog blog = new Blog();
				blog.setId(2l);
				result = blog;

				blogUserRepository.findFirstByUsername("aliveli");
				result = new BlogUser();
			}
		};
		Blog createdBlog = blogService.createBlog("aliveli", "title_test", "body_test");

		assertThat(createdBlog).hasFieldOrPropertyWithValue("id", 2l); // implies "data" was persisted
	}

	@Test
	void createBlog_shouldReturnUpdatedBlogObject() throws BlogNotFoundException {
		Blog blog = Blog.createBlog(new BlogUser(), "title_test", "body_test");
		Blog initialBlog = Blog.createBlog(new BlogUser(), "a", "a");
		new Expectations() {
			{
				blogJPARepository.save(withNotNull());
				result = blog;

				blogJPARepository.findById(1l);
				result = initialBlog;
			}
		};
		Blog updatedBlog = blogService.updateBlog(1l, blog);

		assertThat(updatedBlog).hasFieldOrPropertyWithValue("body", "body_test");
		assertThat(updatedBlog).hasFieldOrPropertyWithValue("title", "title_test");
	}

	@Test
	void createBlog_shouldThrowBlogNotFoundException() throws BlogNotFoundException {
		Blog blog = Blog.createBlog(new BlogUser(), "title_test", "body_test");
		new Expectations() {
			{
				blogJPARepository.findById(200l);
				result =  Optional.empty();
			}
		};
		assertThatThrownBy(() -> {
			blogService.updateBlog(200l, blog);
		}).isInstanceOf(BlogNotFoundException.class);

	}
}
