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
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.shaded.org.awaitility.Awaitility;
import org.testcontainers.shaded.org.awaitility.Durations;
import org.testcontainers.utility.DockerImageName;
import scalefocus.blogapp.containers.MongoDBTestContainer;
import scalefocus.blogapp.containers.OpenSearchTestContainer;
import scalefocus.blogapp.domain.Blog;
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
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
class TestBlogOperationsControllerEmbeddedServer {
  public static final String ID_1 = "000000000000000000000001";
  public static final String ID_2 = "000000000000000000000002";
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

  @Container
  private static final KafkaContainer kafkaContainer =
      new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.4.0")).withKraft();

  private BlogUser blogUser;

  private static final OpenSearchTestContainer openSearchTestContainer =
      new OpenSearchTestContainer();

  static {
    openSearchTestContainer.start();
    kafkaContainer.start();
  }

  static {
    MongoDBTestContainer.startInstance();
  }

  @DynamicPropertySource
  static void setDynamicProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.data.mongodb.uri", MongoDBTestContainer.getInstance()::getReplicaSetUrl);
    registry.add("${opensearch.host.port}", () -> openSearchTestContainer.getMappedPort(9200));
    registry.add("${spring.kafka.bootstrap-servers}", kafkaContainer::getBootstrapServers);
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

   waitOneSecond();

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
   waitOneSecond();
    blogService.attachTag(blog.getId(), "Sausage");
    blog = blogService.createBlog(createNewBlog().setBody("Aubergine"));
   waitOneSecond();
    blogService.attachTag(blog.getId(), "Sausage");
    blog = blogService.createBlog(createNewBlog().setTitle("Aubergine"));
   waitOneSecond();
    blogService.attachTag(blog.getId(), "Sausage");
    blog = blogService.createBlog(createNewBlog());
   waitOneSecond();
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

   waitOneSecond();

    Condition<Blog> nonNullID =
            new Condition<>(m -> m.getId() != null && !"0".equals(m.getId()) , "nonNullID");
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
            .query(q -> q.match(m -> m.field("tags").query(FieldValue.of(tag))))
            .build();

   waitOneSecond();

    Blog actual = makeOpenSearch(searchRequest);
    Condition<String> searchedTag = new Condition<>(m -> tag.equals(m), "searchedTag");
    assertThat(actual.getTags()).areAtLeastOne(searchedTag);
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
    params.put("id", ID_1);
    restTemplate.exchange(
        "http://localhost:" + port + "/blogs/" + ID_1,
        HttpMethod.PUT,
        createEntityForRestTemplate(blogU),
        Blog.class,
        params);

   waitOneSecond();

    Condition<Blog> updatedBlog =
        new Condition<>(
            m -> "newTitle".equals(m.getTitle()) && "newBody".equals(m.getBody()), "updatedBlog");
    assertThat(blogService.getBlogSummaryListForUser("aliveli", 0, 10)).areAtLeastOne(updatedBlog);

    checkOpenSearch("title", "newTitle");
  }

  @Test
  void deleteBlog() throws BlogAppEntityNotFoundException, IOException {
    Blog blog =
        blogService
            .createBlog(new Blog().setBody("delete").setTitle("delete").setCreatedBy(blogUser))
            .setId(ID_2);
    Map<String, String> params = new HashMap<>();
    String id = blog.getId();
    params.put("id", id);
    restTemplate.exchange(
        "http://localhost:" + port + "/blogs/" + id,
        HttpMethod.DELETE,
        createEntityForRestTemplate(null),
        Blog.class,
        params);

   waitOneSecond();

    Condition<Blog> deletedBlog = new Condition<>(m -> id.equals(m.getId()), "deletedBlog");
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
    params.put("id", ID_1);
    params.put("tag", "My RestController Test Tag");
    restTemplate.exchange(
        "http://localhost:" + port + "/blogs/{id}/tags/{tag}",
        HttpMethod.PUT,
        createEntityForRestTemplate(null),
        ResponseEntity.class,
        params);

   waitOneSecond();
    assertThat(blogService.getBlogsWithTag("My RestController Test Tag")).isNotEmpty();
    checkOpenSearchForBlogTags("My RestController Test Tag");

    restTemplate.exchange(
        "http://localhost:" + port + "/blogs/{id}/tags/{tag}",
        HttpMethod.DELETE,
        createEntityForRestTemplate(null),
        ResponseEntity.class,
        params);

    waitOneSecond();

    assertThat(blogService.getBlogsWithTag("My RestController Test Tag")).isEmpty();
    checkOpenSearchForDeletion("blogtags.tag", "My RestController Test");
  }

  private static void waitOneSecond() {
    Awaitility.await().pollDelay(Durations.ONE_SECOND).until(() -> true);
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
    kafkaContainer.stop();
  }
}
