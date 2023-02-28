package scalefocus.blogapp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import scalefocus.blogapp.domain.Blog;

public interface BlogJPARepository extends JpaRepository<Blog, Long> {

	@Query("""
			select new scalefocus.blogapp.domain.Blog(
			   b.title as title,substring(b.body,1,20) as body)
			from Blog b inner join BlogUser bu on b.createdBy.id=bu.id
			where bu.username = :username
			order by b.id
			""")
	List<Blog> findBlogSummaryByUser(@Param("username") String username);

	List<Blog> findByBlogtags_Tag(String tag);
}
