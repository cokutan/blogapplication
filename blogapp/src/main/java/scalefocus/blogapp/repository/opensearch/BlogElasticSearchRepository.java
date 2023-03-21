package scalefocus.blogapp.repository.opensearch;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch._types.Refresh;
import org.opensearch.client.opensearch.core.IndexRequest;
import org.opensearch.client.opensearch.indices.CreateIndexRequest;
import org.opensearch.client.opensearch.indices.ExistsRequest;
import org.springframework.stereotype.Component;
import scalefocus.blogapp.domain.Blog;

import java.io.IOException;

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

    private void createIndexForBlog() {
        try {
            CreateIndexRequest createIndexRequest = new CreateIndexRequest.Builder().index(BLOG).build();
            openSearchClient.indices().create(createIndexRequest);
            log.info(String.format("Creating of index \"%s\"", BLOG));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean indexExists(String indexName) {
        log.info(String.format("Verifying existence of index \"%s\"", indexName));
        ExistsRequest request = new ExistsRequest.Builder().index(BLOG).build();
        try {
            return openSearchClient.indices().exists(request).value();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}