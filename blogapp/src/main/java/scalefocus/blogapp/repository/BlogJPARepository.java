package scalefocus.blogapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import scalefocus.blogapp.entities.Blog;

public interface BlogJPARepository extends JpaRepository<Blog,Long> {


}
