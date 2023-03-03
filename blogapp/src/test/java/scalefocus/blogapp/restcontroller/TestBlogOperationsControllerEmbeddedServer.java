package scalefocus.blogapp.restcontroller;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.assertj.core.api.Condition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import scalefocus.blogapp.auth.AuthenticationRequest;
import scalefocus.blogapp.auth.AuthenticationResponse;
import scalefocus.blogapp.domain.Blog;
import scalefocus.blogapp.domain.BlogUser;
import scalefocus.blogapp.exceptions.BlogAppEntityNotFoundException;
import scalefocus.blogapp.repository.BlogUserRepository;
import scalefocus.blogapp.restcontrollers.BlogOperationsRestController;
import scalefocus.blogapp.service.BlogService;

/**
 * The goal of this class is to show how the Embedded Server is used to test the
 * REST service
 */

// SpringBootTest launch an instance of our application for tests purposes
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@WithMockUser
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

	@Autowired
	private BlogUserRepository blogUserRepository;

	private BlogUser blogUser;

	private String bearerToken;

	@BeforeEach
	public void login() {
		AuthenticationResponse response = restTemplate.postForObject(
				"http://localhost:" + port + "/api/v2/auth/authenticate",
				AuthenticationRequest.builder().password("Veli").username("aliveli").build(),
				AuthenticationResponse.class);
		blogUser = blogUserRepository.findFirstByUsername("aliveli").get();
		bearerToken = "Bearer " + response.getToken();

	}

	@Test
	void index() {
		assertThat(blogOperationsRestController).isNotNull();
	}

	@Test
	@SuppressWarnings(value = { "unchecked" })
	void getUserBlogs() {
		List<Blog> body = restTemplate.exchange("http://localhost:" + port + "/api/v2/users/aliveli/blogs",
				HttpMethod.GET, createEntityForRestTemplate(null), List.class).getBody();
		assertThat(body).isNotEmpty();
	}

	@Test
	@SuppressWarnings(value = { "unchecked" })
	void getBlogsWithTag() {
		List<Blog> body = restTemplate.exchange("http://localhost:" + port + "/api/v2/blogs/tags/First Tag",
				HttpMethod.GET, createEntityForRestTemplate(null), List.class).getBody();
		assertThat(body).isNotEmpty();
	}

	@Test
	void createBlog() {
		Condition<Blog> nonNullID = new Condition<>(m -> m.getId() != null && m.getId() != 0, "nonNullID");

		Blog body = restTemplate.exchange("http://localhost:" + port + "/api/v2/blogs", HttpMethod.POST,
				createEntityForRestTemplate(
						new Blog().setCreatedBy(blogUser).setTitle("another title").setBody("another body")),
				Blog.class).getBody();
		assertThat(body).isNotNull().has(nonNullID);
	}

	@Test
	void updateBlog() throws BlogAppEntityNotFoundException {
		Blog blogU = new Blog().setCreatedBy(blogUser).setTitle("newTitle").setBody("newBody");
		Map<String, String> params = new HashMap<>();
		params.put("id", "1");
		restTemplate.exchange("http://localhost:" + port + "/api/v2/blogs/1", HttpMethod.PUT,
				createEntityForRestTemplate(blogU), Blog.class, params).getBody();
		Condition<Blog> updatedBlog = new Condition<>(
				m -> "newTitle".equals(m.getTitle()) && "newBody".equals(m.getBody()), "updatedBlog");
		assertThat(blogService.getBlogSummaryListForUser("aliveli")).areAtLeastOne(updatedBlog);
	}

	@Test
	void deleteBlog() throws BlogAppEntityNotFoundException {
		Map<String, String> params = new HashMap<>();
		params.put("id", "1");
		restTemplate.exchange("http://localhost:" + port + "/api/v2/blogs/1", HttpMethod.DELETE,
				createEntityForRestTemplate(null), Blog.class, params).getBody();
		Condition<Blog> deletedBlog = new Condition<>(m -> Long.valueOf(1).equals(m.getId()), "deletedBlog");
		assertThat(blogService.getBlogSummaryListForUser("aliveli")).areNot(deletedBlog);
	}

	@Test
	void attachAndDeatchTag() throws BlogAppEntityNotFoundException {
		Map<String, String> params = new HashMap<>();
		params.put("id", "1");
		params.put("tag", "My RestController Test Tag");
		restTemplate.exchange("http://localhost:" + port + "/api/v2/blogs/{id}/tags/{tag}", HttpMethod.PUT,
				createEntityForRestTemplate(null), ResponseEntity.class, params);

		assertThat(blogService.getBlogsWithTag("My RestController Test Tag")).isNotEmpty();

		restTemplate.exchange("http://localhost:" + port + "/api/v2/blogs/{id}/tags/{tag}", HttpMethod.DELETE,
				createEntityForRestTemplate(null), ResponseEntity.class, params);
		assertThat(blogService.getBlogsWithTag("My RestController Test Tag")).isEmpty();
	}

	private <T> HttpEntity<T> createEntityForRestTemplate(T body) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("authorization", bearerToken);
		return new HttpEntity<T>(body, headers);
	}
}