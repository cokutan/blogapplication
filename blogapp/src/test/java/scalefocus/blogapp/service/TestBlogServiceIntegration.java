package scalefocus.blogapp.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import scalefocus.blogapp.entities.Blog;
import scalefocus.blogapp.entities.BlogUser;


@ExtendWith(SpringExtension.class)
@SpringBootTest
class TestBlogServiceIntegration {

	@Autowired
	private BlogService blogService;

	@Test
	void createUser_shouldReturnObject() {
		Blog createdBlog = blogService.createBlog("aliveli", "title_test", "body_test");

		assertThat(createdBlog).hasFieldOrPropertyWithValue("id", 2l); // implies "data" was persisted
	}

}
