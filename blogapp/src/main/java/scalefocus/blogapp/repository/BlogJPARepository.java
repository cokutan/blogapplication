package scalefocus.blogapp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import scalefocus.blogapp.dto.BlogSummary;
import scalefocus.blogapp.entities.Blog;

public interface BlogJPARepository extends JpaRepository<Blog, Long> {

	@Query("""
			select new scalefocus.blogapp.dto.BlogSummary(
			   b.title,substring(b.body,1,20))
			from Blog b inner join BlogUser bu on b.createdBy.id=bu.id
			where bu.id = :createdBy
			order by b.id
			""")
	List<BlogSummary> findBlogSummaryByUser(@Param("createdBy") Long createdBy);
}
