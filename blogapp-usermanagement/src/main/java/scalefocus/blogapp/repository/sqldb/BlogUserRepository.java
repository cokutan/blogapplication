package scalefocus.blogapp.repository.sqldb;

import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import scalefocus.blogapp.domain.BlogUser;

public interface BlogUserRepository extends MongoRepository<BlogUser, Long> {
    Optional<BlogUser> findFirstByUsername(String username);
    boolean existsByUsername(String username);
}
