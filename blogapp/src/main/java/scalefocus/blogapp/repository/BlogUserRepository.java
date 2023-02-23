package scalefocus.blogapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import scalefocus.blogapp.entities.BlogUser;

public interface BlogUserRepository extends JpaRepository<BlogUser, Long> {
	BlogUser findFirstByUsername(String username);

}
