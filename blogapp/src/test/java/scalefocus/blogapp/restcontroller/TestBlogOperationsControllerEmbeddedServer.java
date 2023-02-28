package scalefocus.blogapp.restcontroller;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpEntity;

import scalefocus.blogapp.domain.Blog;
import scalefocus.blogapp.domain.BlogUser;
import scalefocus.blogapp.exceptions.BlogAppEntityNotFoundException;
import scalefocus.blogapp.restcontrollers.BlogOperationsRestController;
import scalefocus.blogapp.service.BlogService;

/**
 * The goal of this class is to show how the Embedded Server is used to test the
 * REST service
 */

// SpringBootTest launch an instance of our application for tests purposes
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(BlogOperationsRestController.class)
class TestBlogOperationsControllerEmbeddedServer {
	@Autowired
	private BlogOperationsRestController blogOperationsRestController;

	// inject the runtime port, it requires the webEnvironment
	@LocalServerPort
	private int port;

	// we use TestRestTemplate, it's an alternative to RestTemplate specific for
	// tests
	// to use this template a webEnvironment is mandatory
	@Autowired
	private TestRestTemplate restTemplate;

	@Autowired
	private BlogService blogService;

	private BlogUser blogUser = new BlogUser().setUsername("aliveli");

	@Test
	void index() {
		assertThat(blogOperationsRestController).isNotNull();
	}

	@Test
	void getUserBlogs() {
		assertThat(restTemplate.getForObject("http://localhost:" + port + "/blogapp/users/aliveli/blogs", List.class))
				.isNotEmpty();
	}

	@Test
	void getBlogsWithTag() {
		List<Blog> list = restTemplate.getForObject("http://localhost:" + port + "/blogapp/blogs/tags/First Tag",
				List.class);
		assertThat(list).isNotEmpty();
	}

	@Test
	void createBlog() {
		Condition<Blog> nonNullID = new Condition<>(m -> m.getId() != null && m.getId() != 0, "nonNullID");
		assertThat(restTemplate.postForObject("http://localhost:" + port + "/blogs",
				new Blog().setCreatedBy(blogUser).setTitle("another title").setBody("another body"), Blog.class))
				.isNotNull().doesNotHave(nonNullID);
	}

	@Test
	void updateBlog() throws BlogAppEntityNotFoundException {
		Blog blogU = new Blog().setCreatedBy(new BlogUser()).setTitle("newTitle").setBody("newBody");
		Map<String, String> params = new HashMap<>();
		params.put("id", "1");
		restTemplate.put("http://localhost:" + port + "/blogapp/blogs/1", new HttpEntity<>(blogU), params);

		Condition<Blog> updatedBlog = new Condition<>(
				m -> "newTitle".equals(m.getTitle()) && "newBody".equals(m.getBody()), "updatedBlog");
		assertThat(blogService.getBlogSummaryListForUser("aliveli")).areAtLeastOne(updatedBlog);
	}

	@Test
	void attachAndDeatchTag() throws BlogAppEntityNotFoundException {
		Map<String, String> params = new HashMap<>();
		params.put("id", "1");
		params.put("tag", "My RestController Test Tag");
		restTemplate.put("http://localhost:" + port + "/blogapp/blogs/{id}/tags/{tag}", null, params);

		assertThat(blogService.getBlogsWithTag("My RestController Test Tag")).isNotEmpty();

		restTemplate.delete("http://localhost:" + port + "/blogapp/blogs/{id}/tags/{tag}", params);
		assertThat(blogService.getBlogsWithTag("My RestController Test Tag")).isEmpty();
	}
}