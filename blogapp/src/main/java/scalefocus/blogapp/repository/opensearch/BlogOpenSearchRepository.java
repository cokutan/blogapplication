package scalefocus.blogapp.repository.opensearch;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch._types.FieldValue;
import org.opensearch.client.opensearch._types.Refresh;
import org.opensearch.client.opensearch._types.query_dsl.Query;
import org.opensearch.client.opensearch.core.*;
import org.opensearch.client.opensearch.core.search.Hit;
import org.opensearch.client.opensearch.core.search.HitsMetadata;
import org.opensearch.client.opensearch.indices.CreateIndexRequest;
import org.opensearch.client.opensearch.indices.ExistsRequest;
import org.opensearch.client.util.ObjectBuilder;
import org.opensearch.index.query.QueryBuilders;
import org.springframework.stereotype.Component;
import scalefocus.blogapp.domain.Blog;

import java.io.IOException;
import java.util.List;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
@Slf4j
public class BlogOpenSearchRepository {

  private final OpenSearchClient openSearchClient;

  private static final String BLOG = "blog";

  public void save(Blog blog) {

    createIndexIfNotExists();
    IndexRequest<Blog> indexRequest =
        new IndexRequest.Builder<Blog>()
            .refresh(Refresh.True)
            .index(BLOG)
            .id(blog.getId().toString())
            .document(blog)
            .build();
    try {
      openSearchClient.index(indexRequest);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private void createIndexIfNotExists() {
    if (!indexExists()) {
      createIndexForBlog();
    }
  }

  public void update(Blog blog) {

    createIndexIfNotExists();

    UpdateRequest<Blog, Blog> updateRequest =
        new UpdateRequest.Builder<Blog, Blog>()
            .refresh(Refresh.True)
            .index(BLOG)
            .id(blog.getId().toString())
            .upsert(blog)
            .doc(blog)
            .build();
    try {
      openSearchClient.update(updateRequest, Blog.class);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public List<Blog> search(String toBeSearched) {
    createIndexIfNotExists();

    SearchRequest searchRequest =
        new SearchRequest.Builder()
            .query(
                m ->
                    m.bool(
                        k ->
                            k.should(fieldMatch("title", toBeSearched))
                                .should(fieldMatch("body", toBeSearched))
                                .should(fieldMatch("tags", toBeSearched))))
            .index(BLOG)
            .build();
    SearchResponse<Blog> searchResponse;
    try {
      searchResponse = openSearchClient.search(searchRequest, Blog.class);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    HitsMetadata<Blog> hits = searchResponse.hits();
    return hits.hits().stream().map(Hit::source).toList();
  }

  private static Function<Query.Builder, ObjectBuilder<Query>> fieldMatch(
      String title, String toBeSearched) {
    return t -> t.match(c -> c.field(title).query(FieldValue.of(toBeSearched)));
  }

  private void createIndexForBlog() {
    try {
      CreateIndexRequest createIndexRequest = new CreateIndexRequest.Builder().index(BLOG).build();
      openSearchClient.indices().create(createIndexRequest);
      log.info(String.format("Creating of index \"%s\"", BLOG));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private boolean indexExists() {
    log.info(String.format("Verifying existence of index \"%s\"", BLOG));
    ExistsRequest request = new ExistsRequest.Builder().index(BLOG).build();
    try {
      return openSearchClient.indices().exists(request).value();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public void delete(Blog blog) {
    createIndexIfNotExists();
    DeleteRequest deleteRequest =
        new DeleteRequest.Builder()
            .index(BLOG)
            .id(blog.getId().toString())
            .refresh(Refresh.True)
            .build();
    try {
      openSearchClient.delete(deleteRequest);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
