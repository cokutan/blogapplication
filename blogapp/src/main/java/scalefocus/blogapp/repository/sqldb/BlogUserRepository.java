package scalefocus.blogapp.repository.sqldb;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import scalefocus.blogapp.domain.BlogUser;

public interface BlogUserRepository extends JpaRepository<BlogUser, Long> {
    Optional<BlogUser> findFirstByUsername(String username);
    boolean existsByUsername(String username);
}
