package scalefocus.blogapp.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.platform.commons.annotation.Testable;

import mockit.*;
import scalefocus.blogapp.entities.Blog;
import scalefocus.blogapp.entities.BlogUser;
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

}
