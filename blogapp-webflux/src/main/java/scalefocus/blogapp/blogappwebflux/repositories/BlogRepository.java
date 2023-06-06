package scalefocus.blogapp.blogappwebflux.repositories;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import scalefocus.blogapp.blogappwebflux.domain.Blog;

public interface BlogRepository extends ReactiveMongoRepository<Blog, String> {
}
