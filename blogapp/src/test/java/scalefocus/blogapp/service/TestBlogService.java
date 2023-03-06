package scalefocus.blogapp.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.platform.commons.annotation.Testable;
import org.springframework.test.context.ActiveProfiles;

import mockit.Expectations;
import mockit.Injectable;
import mockit.Tested;
import scalefocus.blogapp.domain.Blog;
import scalefocus.blogapp.domain.BlogTag;
import scalefocus.blogapp.domain.BlogUser;
import scalefocus.blogapp.exceptions.BlogAppEntityNotFoundException;
import scalefocus.blogapp.repository.BlogJPARepository;
import scalefocus.blogapp.repository.BlogTagRepository;
import scalefocus.blogapp.repository.BlogUserRepository;

@Testable
@ActiveProfiles("test")
class TestBlogService {

	@Tested(fullyInitialized = true)
	BlogService blogService;

	@Injectable
	BlogUserRepository blogUserRepository;

	@Injectable
	BlogJPARepository blogJPARepository;

	@Injectable
	BlogTagRepository blogTagRepository;

	private BlogUser blogUser = new BlogUser().setUsername("aliveli");

	@Test
	void createBlog_shouldReturnCreatedBlogObject() throws BlogAppEntityNotFoundException {
		new Expectations() {
			{
				blogJPARepository.save(withNotNull());
				Blog blog = new Blog();
				blog.setId(2l);
				blog.setCreatedBy(blogUser);
				result = blog;

				blogUserRepository.findFirstByUsername("aliveli");
				result = blogUser;
			}
		};
		Blog createdBlog = blogService
				.createBlog(new Blog().setCreatedBy(blogUser).setTitle("title_test").setBody("body_test"));

		assertThat(createdBlog).hasFieldOrPropertyWithValue("id", 2l); // implies "data" was persisted
	}

	@Test
	void createBlog_shouldReturnUpdatedBlogObject() throws BlogAppEntityNotFoundException {
		Blog blog = new Blog().setCreatedBy(new BlogUser()).setTitle("title_test").setBody("body_test");
		Blog initialBlog = new Blog().setCreatedBy(new BlogUser()).setTitle("a").setBody("a");
		new Expectations() {
			{
				blogJPARepository.findById(1l);
				result = initialBlog;
			}
		};
		Blog updatedBlog = blogService.updateBlog(1l, blog);

		assertThat(updatedBlog).hasFieldOrPropertyWithValue("body", "body_test");
		assertThat(updatedBlog).hasFieldOrPropertyWithValue("title", "title_test");
	}

	@Test
	void createBlog_shouldThrowBlogNotFoundException() throws BlogAppEntityNotFoundException {
		Blog blog = new Blog().setCreatedBy(new BlogUser()).setTitle("title_test").setBody("body_test");
		new Expectations() {
			{
				blogJPARepository.findById(200l);
				result = Optional.empty();
			}
		};
		assertThatThrownBy(() -> {
			blogService.updateBlog(200l, blog);
		}).isInstanceOf(BlogAppEntityNotFoundException.class);

	}

	@Test
	void unattachTag_shouldUnattachTagFromList() throws BlogAppEntityNotFoundException {
		Blog blog = new Blog().setCreatedBy(new BlogUser()).setTitle("title_test").setBody("body_test");
		BlogTag blogtag = new BlogTag().setId(1l).setTag("tag");
		blog.getBlogtags().add(blogtag);
		new Expectations() {
			{
				blogJPARepository.findById(1l);
				result = blog;

				blogTagRepository.findByTag("tag");
				result = blogtag;
			}
		};
		blogService.unattachTag(1l, "tag");

		assertThat(blog.getBlogtags()).isEmpty();

	}

	@Test
	void unattachTag_shouldThrowBlogAppEntityNotFoundExceptionForBlogTag() throws BlogAppEntityNotFoundException {
		Blog blog = new Blog().setCreatedBy(new BlogUser()).setTitle("title_test").setBody("body_test");
		new Expectations() {
			{
				blogJPARepository.findById(1l);
				result = blog;

				blogTagRepository.findByTag("tag");
				result = null;
			}
		};

		assertThatThrownBy(() -> {
			blogService.unattachTag(1l, "tag");
		}).isInstanceOf(BlogAppEntityNotFoundException.class);

	}

	@Test
	void unattachTag_shouldThrowBlogAppEntityNotFoundExceptionForBlog() throws BlogAppEntityNotFoundException {
		new Expectations() {
			{
				blogJPARepository.findById(1l);
				result = Optional.empty();

			}
		};

		assertThatThrownBy(() -> {
			blogService.unattachTag(1l, "tag");
		}).isInstanceOf(BlogAppEntityNotFoundException.class);

	}

	@Test
	void unattachTag_shouldAttachDBTag() throws BlogAppEntityNotFoundException {
		Blog blog = new Blog().setCreatedBy(new BlogUser()).setTitle("title_test").setBody("body_test");
		BlogTag blogtag = new BlogTag().setId(1l).setTag("tag");
		new Expectations() {
			{
				blogJPARepository.findById(1l);
				result = blog;

				blogTagRepository.findByTag("tag");
				result = blogtag;
			}
		};
		blogService.attachTag(1l, "tag");

		assertThat(blog.getBlogtags().get(0).getTag()).isEqualTo(blogtag.getTag());

	}

	@Test
	void unattachTag_shouldAttachNonDBTag() throws BlogAppEntityNotFoundException {
		Blog blog = new Blog().setCreatedBy(new BlogUser()).setTitle("title_test").setBody("body_test");
		BlogTag blogtag = new BlogTag().setId(1l).setTag("tag");
		new Expectations() {
			{
				blogJPARepository.findById(1l);
				result = blog;

				blogTagRepository.findByTag("tag");
				result = null;

				blogTagRepository.save(withNotNull());
				result = blogtag;
			}
		};
		blogService.attachTag(1l, "tag");

		assertThat(blog.getBlogtags().get(0).getTag()).isEqualTo(blogtag.getTag());

	}

	@Test
	void attachTag_shouldThrowBlogAppEntityNotFoundExceptionForBlog() throws BlogAppEntityNotFoundException {
		new Expectations() {
			{
				blogJPARepository.findById(1l);
				result = Optional.empty();

			}
		};

		assertThatThrownBy(() -> {
			blogService.attachTag(1l, "tag");
		}).isInstanceOf(BlogAppEntityNotFoundException.class);

	}
}
