package scalefocus.blogapp.restcontroller;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.assertj.core.api.Condition;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch._types.FieldValue;
import org.opensearch.client.opensearch.core.SearchRequest;
import org.opensearch.client.opensearch.core.SearchResponse;
import org.opensearch.client.opensearch.core.search.Hit;
import org.opensearch.client.opensearch.core.search.HitsMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import scalefocus.blogapp.auth.AuthenticationRequest;
import scalefocus.blogapp.auth.AuthenticationResponse;
import scalefocus.blogapp.containers.OpenSearchTestContainer;
import scalefocus.blogapp.domain.Blog;
import scalefocus.blogapp.domain.BlogUser;
import scalefocus.blogapp.exceptions.ApiError;
import scalefocus.blogapp.exceptions.BlogAppEntityNotFoundException;
import scalefocus.blogapp.repository.sqldb.BlogUserRepository;
import scalefocus.blogapp.restcontrollers.BlogOperationsRestController;
import scalefocus.blogapp.service.BlogService;

/**
 * The goal of this class is to show how the Embedded Server is used to test the
 * REST service
 */

// SpringBootTest launch an instance of our application for tests purposes
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("testcontainers")
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
    @Autowired
    private OpenSearchClient client;

    private BlogUser blogUser;

    private String bearerToken;

    private final static OpenSearchTestContainer openSearchTestContainer = new OpenSearchTestContainer();

    static {
        openSearchTestContainer.start();
    }

    @DynamicPropertySource
    static void setDynamicProperties(DynamicPropertyRegistry registry) {
        registry.add("${opensearch.host.port}", () -> openSearchTestContainer.getMappedPort(9200));
    }
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
    @SuppressWarnings(value = {"unchecked"})
    void getUserBlogs() {
        Map<String, Integer> params = new HashMap<>();
        params.put("page", 0);
        params.put("size", 10);
        List<Blog> body = restTemplate.exchange("http://localhost:" + port + "/api/v2/users/aliveli/blogs?page=0&size=10",
                HttpMethod.GET, createEntityForRestTemplate(null), List.class, params).getBody();
        assertThat(body).isNotEmpty();
    }

    @Test
    @SuppressWarnings(value = {"unchecked"})
    void getUserBlogsWithoutPagingParameters() {
        Map<String, Integer> params = new HashMap<>();
        List<Blog> body = restTemplate.exchange("http://localhost:" + port + "/api/v2/users/aliveli/blogs",
                HttpMethod.GET, createEntityForRestTemplate(null), List.class, params).getBody();
        assertThat(body).isNotEmpty();
    }

    @Test
    void getUserBlogsWithNotFoundUser() {
        Map<String, String> params = new HashMap<>();
        params.put("page", "0");
        params.put("size", "10");
        ApiError body = restTemplate.exchange("http://localhost:" + port + "/api/v2/users/nonexistinguser/blogs?page=0&size=10",
                HttpMethod.GET, createEntityForRestTemplate(null), ApiError.class, params).getBody();
        assertThat(body).extracting("status").isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(body).extracting("message").isEqualTo("BlogUser was not found for parameters {username=nonexistinguser}");
    }

    @Test
    @SuppressWarnings(value = {"unchecked"})
    void getBlogsWithTag() {
        List<Blog> body = restTemplate.exchange("http://localhost:" + port + "/api/v2/blogs/tags/First Tag",
                HttpMethod.GET, createEntityForRestTemplate(null), List.class).getBody();
        assertThat(body).isNotEmpty();
    }

    @Test
    void createBlog() throws IOException {
        Condition<Blog> nonNullID = new Condition<>(m -> m.getId() != null && m.getId() != 0, "nonNullID");

        Blog body = restTemplate.exchange("http://localhost:" + port + "/api/v2/blogs", HttpMethod.POST,
                createEntityForRestTemplate(
                        new Blog().setCreatedBy(blogUser).setTitle("another title").setBody("another body")),
                Blog.class).getBody();
        assertThat(body).isNotNull().has(nonNullID);

        checkOpenSearchForIndexCreation();
    }

    private void checkOpenSearchForIndexCreation() throws IOException {
        SearchRequest searchRequest = new SearchRequest.Builder().query(q -> q.match(m -> m.field("title")
                        .query(FieldValue.of("another title"))))
                .build();
        SearchResponse<Blog> searchResponse = client.search(searchRequest, Blog.class);
        HitsMetadata<Blog> hits = searchResponse.hits();
        List<Hit<Blog>> hitList = hits.hits();
        Hit<Blog> blogHit = hitList.get(0);
        Blog actual = blogHit.source();
        assertThat(actual).hasFieldOrPropertyWithValue("title", "another title");
    }

    @Test
    void updateBlog() throws BlogAppEntityNotFoundException {
        Blog blogU = new Blog().setCreatedBy(blogUser).setTitle("newTitle").setBody("newBody");
        Map<String, String> params = new HashMap<>();
        params.put("id", "1");
        restTemplate.exchange("http://localhost:" + port + "/api/v2/blogs/1", HttpMethod.PUT,
                createEntityForRestTemplate(blogU), Blog.class, params);
        Condition<Blog> updatedBlog = new Condition<>(
                m -> "newTitle".equals(m.getTitle()) && "newBody".equals(m.getBody()), "updatedBlog");
        assertThat(blogService.getBlogSummaryListForUser("aliveli", 0, 10)).areAtLeastOne(updatedBlog);
    }

    @Test
    void deleteBlog() throws BlogAppEntityNotFoundException, IOException {
        Map<String, String> params = new HashMap<>();
        params.put("id", "1");
        restTemplate.exchange("http://localhost:" + port + "/api/v2/blogs/1", HttpMethod.DELETE,
                createEntityForRestTemplate(null), Blog.class, params);
        Condition<Blog> deletedBlog = new Condition<>(m -> Long.valueOf(1).equals(m.getId()), "deletedBlog");
        assertThat(blogService.getBlogSummaryListForUser("aliveli", 0, 10)).areNot(deletedBlog);

        checkOpenSearchForDeletion();
    }

    private void checkOpenSearchForDeletion() throws IOException {
        SearchRequest searchRequest = new SearchRequest.Builder().query(q -> q.match(m -> m.field("id")
                        .query(FieldValue.of(1))))
                .build();
        SearchResponse<Blog> searchResponse = client.search(searchRequest, Blog.class);
        assertThat(searchResponse.hits().hits()).isEmpty();
    }

    @Test
    void attachAndDettachTag() throws BlogAppEntityNotFoundException {
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
        return new HttpEntity<>(body, headers);
    }


    @AfterAll
    public static void afterAll() {
            openSearchTestContainer.stop();
    }
}