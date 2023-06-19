package scalefocus.blogapp.repository;

import java.math.BigInteger;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import scalefocus.blogapp.domain.BlogUser;

public interface BlogUserRepository extends MongoRepository<BlogUser, BigInteger> {
    Optional<BlogUser> findFirstByUsername(String username);
    boolean existsByUsername(String username);
}
