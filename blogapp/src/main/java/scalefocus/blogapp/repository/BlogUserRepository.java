package scalefocus.blogapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import scalefocus.blogapp.domain.BlogUser;

public interface BlogUserRepository extends JpaRepository<BlogUser, Long> {
	BlogUser findFirstByUsername(String username);

}
