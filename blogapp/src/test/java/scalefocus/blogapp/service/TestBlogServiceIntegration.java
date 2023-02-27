package scalefocus.blogapp.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import jakarta.transaction.Transactional;
import scalefocus.blogapp.dto.BlogCreationDTO;
import scalefocus.blogapp.dto.BlogSummary;
import scalefocus.blogapp.entities.Blog;
import scalefocus.blogapp.entities.BlogUser;
import scalefocus.blogapp.exceptions.BlogAppEntityNotFoundException;
import scalefocus.blogapp.repository.BlogJPARepository;
import scalefocus.blogapp.repository.BlogTagRepository;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class TestBlogServiceIntegration {

	@Autowired
	private BlogService blogService;

	@Autowired
	private BlogJPARepository blogJPARepository;

	@Autowired
	private BlogTagRepository blogTagRepository;

	@Test
	void createBlog_shouldReturnCreatedBlogObject() throws BlogAppEntityNotFoundException {
		Blog createdBlog = blogService.createBlog(new BlogCreationDTO("aliveli", "title_test", "body_test"));
		Condition<Blog> idGiven = new Condition<>(t -> t.getId() != null && t.getId() != 0, "idGiven");

		assertThat(createdBlog).has(idGiven);
	}

	@Test
	void getBlogSummaryListForUser_shouldReturnSummaryListOfUSer()  throws BlogAppEntityNotFoundException{
		blogService.createBlog(new BlogCreationDTO("aliveli", "title_test",
				"The following table lists the supported databases and their tested versions. ... Changing database locking timeout in a cluster configuration."));
		blogService.createBlog(new BlogCreationDTO("aliveli", "title_test",
				" A convenient way to fix a defective complex sql-query in a service that causes a functional test to fail is to inspect the database state"));
		List<BlogSummary> summaryList = blogService.getBlogSummaryListForUser("aliveli");
		assertThat(summaryList).filteredOn("shortSummary", "The following table ").isNotEmpty();
		assertThat(summaryList).filteredOn("shortSummary", " A convenient way to").isNotEmpty();
	}

	@Test
	void updateBlog_shouldReturnCreatedBlogObject() throws BlogAppEntityNotFoundException {
		Blog blog = Blog.createBlog(new BlogUser(1, "test1", "test2", "username"), "title updated",
				"description updated");

		assertThat(blogService.updateBlog(1l, blog)).hasFieldOrPropertyWithValue("title", "title updated")
				.hasFieldOrPropertyWithValue("body", "description updated");
	}

	@Test
	@Transactional
	void attachTag_shouldAddTagToBlogObject() throws BlogAppEntityNotFoundException {
		blogService.attachTag(1l, "tag1");
		Blog blog = blogJPARepository.findById(1l).get();
		assertThat(blog.getBlogtags()).hasSize(2).filteredOn("tag", "tag1").isNotEmpty();
	}

	@Test
	@Transactional
	void attachTag_shouldRemoveTagToBlogObject() throws BlogAppEntityNotFoundException {
		blogService.unattachTag(1l, "First Tag");
		Blog blog = blogJPARepository.findById(1l).get();
		assertThat(blog.getBlogtags()).isEmpty();

		assertThat(blogTagRepository.findByTag("First Tag")).isNotNull();
	}
}
