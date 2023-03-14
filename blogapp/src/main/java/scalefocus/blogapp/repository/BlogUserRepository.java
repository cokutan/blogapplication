package scalefocus.blogapp.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import scalefocus.blogapp.domain.BlogUser;

public interface BlogUserRepository extends JpaRepository<BlogUser, Long> {
    Optional<BlogUser> findFirstByUsername(String username);

}
