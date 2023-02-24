package scalefocus.blogapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import jakarta.persistence.Id;
import scalefocus.blogapp.entities.BlogTag;

public interface BlogTagRepository extends JpaRepository<BlogTag, Id> {

	BlogTag findByTag(String tag);
	
}
