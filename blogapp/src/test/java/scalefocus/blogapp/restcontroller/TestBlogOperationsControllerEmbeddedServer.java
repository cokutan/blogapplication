package scalefocus.blogapp.restcontroller;

import org.assertj.core.api.Condition;
import org.jetbrains.annotations.NotNull;
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
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import scalefocus.blogapp.containers.OpenSearchTestContainer;
import scalefocus.blogapp.domain.Blog;
import scalefocus.blogapp.domain.BlogTag;
import scalefocus.blogapp.domain.BlogUser;
import scalefocus.blogapp.exceptions.ApiError;
import scalefocus.blogapp.exceptions.BlogAppEntityNotFoundException;
import scalefocus.blogapp.repository.sqldb.BlogUserRepository;
import scalefocus.blogapp.restcontrollers.BlogOperationsRestController;
import scalefocus.blogapp.service.BlogService;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/** The goal of this class is to show how the Embedded Server is used to test the REST service */

// SpringBootTest launch an instance of our application for tests purposes
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("testcontainers")
@Import(BlogOperationsRestController.class)
class TestBlogOperationsControllerEmbeddedServer {
  @Autowired private BlogOperationsRestController blogOperationsRestController;

  // inject the runtime port, it requires the webEnvironment
  @LocalServerPort private int port;

  // we use TestRestTemplate, it's an alternative to RestTemplate specific for
  // tests
  // to use this template a webEnvironment is mandatory
  @Autowired private TestRestTemplate restTemplate;

  @Autowired private BlogService blogService;

  @Autowired private BlogUserRepository blogUserRepository;
  @Autowired private OpenSearchClient client;

  private BlogUser blogUser;

  private String bearerToken;

  private static final OpenSearchTestContainer openSearchTestContainer =
      new OpenSearchTestContainer();

  static {
    openSearchTestContainer.start();
  }

  @DynamicPropertySource
  static void setDynamicProperties(DynamicPropertyRegistry registry) {
    registry.add("${opensearch.host.port}", () -> openSearchTestContainer.getMappedPort(9200));
  }

  @BeforeEach
  public void beforeEachTest() {
    blogUser = blogUserRepository.findFirstByUsername("aliveli").get();
  }

  @Test
  void index() {
    assertThat(blogOperationsRestController).isNotNull();
  }

  @Test
  void getUserBlogs() {
    Map<String, Integer> params = new HashMap<>();
    params.put("page", 0);
    params.put("size", 10);
    List<Blog> body =
        restTemplate
            .exchange(
                "http://localhost:" + port + "/users/aliveli/blogs?page=0&size=10",
                HttpMethod.GET,
                createEntityForRestTemplate(null),
                new ParameterizedTypeReference<List<Blog>>() {},
                params)
            .getBody();
    assertThat(body).isNotEmpty();
  }

  @Test
  void getUserBlogsWithoutPagingParameters() {
    Map<String, Integer> params = new HashMap<>();
    List<Blog> body =
        restTemplate
            .exchange(
                "http://localhost:" + port + "/users/aliveli/blogs",
                HttpMethod.GET,
                createEntityForRestTemplate(null),
                new ParameterizedTypeReference<List<Blog>>() {},
                params)
            .getBody();
    assertThat(body).isNotEmpty();
  }

  @Test
  void getUserBlogsWithNotFoundUser() {
    Map<String, String> params = new HashMap<>();
    params.put("page", "0");
    params.put("size", "10");
    ApiError body =
        restTemplate
            .exchange(
                "http://localhost:" + port + "/users/nonexistinguser/blogs?page=0&size=10",
                HttpMethod.GET,
                createEntityForRestTemplate(null),
                ApiError.class,
                params)
            .getBody();
    assertThat(body).extracting("status").isEqualTo(HttpStatus.NOT_FOUND);
    assertThat(body)
        .extracting("message")
        .isEqualTo("BlogUser was not found for parameters {username=nonexistinguser}");
  }

  @Test
  @SuppressWarnings(value = {"unchecked"})
  void getBlogsWithTag() {
    List<Blog> body =
        restTemplate
            .exchange(
                "http://localhost:" + port + "/blogs/tags/First Tag",
                HttpMethod.GET,
                createEntityForRestTemplate(null),
                List.class)
            .getBody();
    assertThat(body).isNotEmpty();
  }

  @Test
  void getSearchedBlogs() {
    insertIndexesForOpenSearch();
    Map<String, Integer> params = new HashMap<>();
    params.put("page", 0);
    params.put("size", 10);
    List<Blog> body =
        restTemplate
            .exchange(
                "http://localhost:" + port + "/blogs/search?term=Sausage",
                HttpMethod.GET,
                createEntityForRestTemplate(null),
                new ParameterizedTypeReference<List<Blog>>() {},
                params)
            .getBody();
    assertThat(body).hasSize(4);

    body =
        restTemplate
            .exchange(
                "http://localhost:" + port + "/blogs/search?term=Aubergine",
                HttpMethod.GET,
                createEntityForRestTemplate(null),
                new ParameterizedTypeReference<List<Blog>>() {},
                params)
            .getBody();
    assertThat(body).hasSize(3);

    HttpStatusCode statusCode =
        restTemplate
            .exchange(
                "http://localhost:" + port + "/blogs/search?term=nonexistent",
                HttpMethod.GET,
                createEntityForRestTemplate(null),
                List.class,
                params)
            .getStatusCode();
    assertThat(statusCode.value()).isEqualTo(HttpStatus.NO_CONTENT.value());
  }

  private void insertIndexesForOpenSearch() {

    Blog blog = blogService.createBlog(createNewBlog());
    blogService.attachTag(blog.getId(), "Sausage");
    blog = blogService.createBlog(createNewBlog().setBody("Aubergine"));
    blogService.attachTag(blog.getId(), "Sausage");
    blog = blogService.createBlog(createNewBlog().setTitle("Aubergine"));
    blogService.attachTag(blog.getId(), "Sausage");
    blog = blogService.createBlog(createNewBlog());
    blogService.attachTag(blog.getId(), "Aubergine");
  }

  @NotNull
  private Blog createNewBlog() {
    Blog blog = new Blog().setBody("Sausage").setTitle("Sausage");
    blog.setCreatedBy(blogUser);
    return blog;
  }

  @Test
  void createBlog() throws IOException {
    Condition<Blog> nonNullID =
        new Condition<>(m -> m.getId() != null && m.getId() != 0, "nonNullID");

    Blog body =
        restTemplate
            .exchange(
                "http://localhost:" + port + "/blogs",
                HttpMethod.POST,
                createEntityForRestTemplate(
                    new Blog()
                        .setCreatedBy(blogUser)
                        .setTitle("another title")
                        .setBody("another body")),
                Blog.class)
            .getBody();
    assertThat(body).isNotNull().has(nonNullID);

    checkOpenSearch("title", "another title");
  }

  private void checkOpenSearch(String field, String fieldValue) throws IOException {
    SearchRequest searchRequest =
        new SearchRequest.Builder()
            .query(q -> q.match(m -> m.field(field).query(FieldValue.of(fieldValue))))
            .build();
    Blog actual = makeOpenSearch(searchRequest);
    assertThat(actual).hasFieldOrPropertyWithValue(field, fieldValue);
  }

  private void checkOpenSearchForBlogTags(String tag) throws IOException {
    SearchRequest searchRequest =
        new SearchRequest.Builder()
            .query(q -> q.match(m -> m.field("blogtags.tag").query(FieldValue.of(tag))))
            .build();
    Blog actual = makeOpenSearch(searchRequest);
    Condition<BlogTag> searchedTag = new Condition<>(m -> tag.equals(m.getTag()), "searchedTag");
    assertThat(actual.getBlogtags()).areAtLeastOne(searchedTag);
  }

  private Blog makeOpenSearch(SearchRequest searchRequest) throws IOException {
    SearchResponse<Blog> searchResponse = client.search(searchRequest, Blog.class);

    HitsMetadata<Blog> hits = searchResponse.hits();
    List<Hit<Blog>> hitList = hits.hits();
    Hit<Blog> blogHit = hitList.get(0);
    return blogHit.source();
  }

  @Test
  void updateBlog() throws BlogAppEntityNotFoundException, IOException {
    Blog blogU = new Blog().setCreatedBy(blogUser).setTitle("newTitle").setBody("newBody");
    Map<String, String> params = new HashMap<>();
    params.put("id", "1");
    restTemplate.exchange(
        "http://localhost:" + port + "/blogs/1",
        HttpMethod.PUT,
        createEntityForRestTemplate(blogU),
        Blog.class,
        params);
    Condition<Blog> updatedBlog =
        new Condition<>(
            m -> "newTitle".equals(m.getTitle()) && "newBody".equals(m.getBody()), "updatedBlog");
    assertThat(blogService.getBlogSummaryListForUser("aliveli", 0, 10)).areAtLeastOne(updatedBlog);

    checkOpenSearch("title", "newTitle");
  }

  @Test
  void deleteBlog() throws BlogAppEntityNotFoundException, IOException {
    Blog blog =
        blogService.createBlog(
            new Blog().setBody("delete").setTitle("delete").setCreatedBy(blogUser));
    Map<String, String> params = new HashMap<>();
    String id = blog.getId().toString();
    params.put("id", id);
    restTemplate.exchange(
        "http://localhost:" + port + "/blogs/" + id,
        HttpMethod.DELETE,
        createEntityForRestTemplate(null),
        Blog.class,
        params);
    Condition<Blog> deletedBlog =
        new Condition<>(m -> Long.valueOf(id).equals(m.getId()), "deletedBlog");
    assertThat(blogService.getBlogSummaryListForUser("aliveli", 0, 10)).areNot(deletedBlog);

    checkOpenSearchForDeletion("id", id);
  }

  private void checkOpenSearchForDeletion(String field, String value) throws IOException {
    SearchRequest searchRequest =
        new SearchRequest.Builder()
            .query(q -> q.match(m -> m.field(field).query(FieldValue.of(value))))
            .requestCache(false)
            .index("blog")
            .build();
    SearchResponse<Blog> searchResponse = client.search(searchRequest, Blog.class);
    assertThat(searchResponse.hits().hits()).isEmpty();
  }

  @Test
  void attachAndDettachTag() throws BlogAppEntityNotFoundException, IOException {
    Map<String, String> params = new HashMap<>();
    params.put("id", "1");
    params.put("tag", "My RestController Test Tag");
    restTemplate.exchange(
        "http://localhost:" + port + "/blogs/{id}/tags/{tag}",
        HttpMethod.PUT,
        createEntityForRestTemplate(null),
        ResponseEntity.class,
        params);

    assertThat(blogService.getBlogsWithTag("My RestController Test Tag")).isNotEmpty();
    checkOpenSearchForBlogTags("My RestController Test Tag");

    restTemplate.exchange(
        "http://localhost:" + port + "/blogs/{id}/tags/{tag}",
        HttpMethod.DELETE,
        createEntityForRestTemplate(null),
        ResponseEntity.class,
        params);
    assertThat(blogService.getBlogsWithTag("My RestController Test Tag")).isEmpty();
    checkOpenSearchForDeletion("blogtags.tag", "My RestController Test");
  }

  private <T> HttpEntity<T> createEntityForRestTemplate(T body) {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setBasicAuth("aliveli", "secret");
    return new HttpEntity<>(body, headers);
  }

  @AfterAll
  public static void afterAll() {
    openSearchTestContainer.stop();
  }
}
