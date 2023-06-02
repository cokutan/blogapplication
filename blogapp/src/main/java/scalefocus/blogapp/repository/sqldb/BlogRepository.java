package scalefocus.blogapp.repository.sqldb;

import java.math.BigInteger;
import java.util.List;

import org.springframework.data.domain.Pageable;

import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.query.Param;
import scalefocus.blogapp.domain.Blog;

public interface BlogRepository extends MongoRepository<Blog, String> {

  @Aggregation(
      pipeline = {
        "{'$match': { 'createdBy.username': ?0 }},{'$project': { 'title': 1, 'body': { '$substr': ['$body', 0, 20] }},}"
      })
  List<Blog> findBlogSummaryByUser(@Param("username") String username, Pageable pageable);

  List<Blog> findByTags(String tag);
}
