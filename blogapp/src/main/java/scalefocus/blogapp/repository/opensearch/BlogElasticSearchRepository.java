package scalefocus.blogapp.repository.opensearch;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch._types.FieldValue;
import org.opensearch.client.opensearch._types.Refresh;
import org.opensearch.client.opensearch.core.DeleteRequest;
import org.opensearch.client.opensearch.core.IndexRequest;
import org.opensearch.client.opensearch.core.SearchRequest;
import org.opensearch.client.opensearch.core.SearchResponse;
import org.opensearch.client.opensearch.core.search.Hit;
import org.opensearch.client.opensearch.core.search.HitsMetadata;
import org.opensearch.client.opensearch.indices.CreateIndexRequest;
import org.opensearch.client.opensearch.indices.ExistsRequest;
import org.springframework.stereotype.Component;
import scalefocus.blogapp.domain.Blog;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class BlogElasticSearchRepository {

    final private OpenSearchClient openSearchClient;

    private final static String BLOG = "blog";

    public void save(Blog blog) {

        if (!indexExists(BLOG)) {
            createIndexForBlog();
        }
        IndexRequest<Blog> indexRequest = new IndexRequest.Builder<Blog>().refresh(Refresh.True).index(BLOG).id(blog.getId().toString()).document(blog).build();
        try {
            openSearchClient.index(indexRequest);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Blog> search(String toBeSearched) {

        SearchRequest searchRequest = new SearchRequest.Builder().query(q -> q.match(m -> m.field(toBeSearched).query(FieldValue.of(toBeSearched)))).build();
        SearchResponse<Blog> searchResponse;
        try {
            searchResponse = openSearchClient.search(searchRequest, Blog.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        HitsMetadata<Blog> hits = searchResponse.hits();
        return hits.hits().stream().map(Hit::source).toList();

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

    private boolean indexExists(String indexName) {
        log.info(String.format("Verifying existence of index \"%s\"", indexName));
        ExistsRequest request = new ExistsRequest.Builder().index(BLOG).build();
        try {
            return openSearchClient.indices().exists(request).value();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public void delete(Blog blog) {
        DeleteRequest deleteRequest = new DeleteRequest.Builder().index(BLOG).id(blog.getId().toString()).refresh(Refresh.True).build();
        try {
            openSearchClient.delete(deleteRequest);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}