package scalefocus.blogapp.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;

import scalefocus.blogapp.domain.Blog;
import scalefocus.blogapp.domain.BlogUser;
import scalefocus.blogapp.exceptions.BlogAppEntityNotFoundException;
import scalefocus.blogapp.repository.sqldb.BlogRepository;

@SpringBootTest
@Testcontainers
@ActiveProfiles("testcontainers")
class TestBlogServiceIntegration {
	
	@Autowired
	private BlogService blogService;

	@Autowired
	private BlogRepository blogRepository;

	private BlogUser blogUser = new BlogUser().setUsername("aliveli");

	@Test
	void createBlog_shouldReturnCreatedBlogObject() throws BlogAppEntityNotFoundException {

		Blog createdBlog = blogService
				.createBlog(new Blog().setCreatedBy(blogUser).setTitle("title_test").setBody("body_test"));
		Condition<Blog> idGiven = new Condition<>(t -> t.getId() != null && t.getId() != "0", "idGiven");

		assertThat(createdBlog).has(idGiven);
	}

	@Test
	void getBlogSummaryListForUser_shouldReturnSummaryListOfUser() throws BlogAppEntityNotFoundException {
		blogService.createBlog(new Blog().setCreatedBy(blogUser).setTitle("title_test").setBody(
				"The following table lists the supported databases and their tested versions. ... Changing database locking timeout in a cluster configuration."));
		blogService.createBlog(new Blog().setCreatedBy(blogUser).setTitle("title_test").setBody(
				" A convenient way to fix a defective complex sql-query in a service that causes a functional test to fail is to inspect the database state"));
		List<Blog> summaryList = blogService.getBlogSummaryListForUser("aliveli", 0, 10);
		assertThat(summaryList).filteredOn("body", "The following table ").isNotEmpty();
		assertThat(summaryList).filteredOn("body", " A convenient way to").isNotEmpty();
	}

	@Test
	void updateBlog_shouldReturnCreatedBlogObject() throws BlogAppEntityNotFoundException {
		Blog blog = new Blog().setCreatedBy(new BlogUser().setId("1").setDisplayname("test1").setUsername("username"))
				.setTitle("title updated").setBody("description updated");

		assertThat(blogService.updateBlog("1", blog)).hasFieldOrPropertyWithValue("title", "title updated")
				.hasFieldOrPropertyWithValue("body", "description updated");
	}

	@Test
	void deleteBlog_shouldReturnNoObject() throws BlogAppEntityNotFoundException {
		blogService.deleteBlog("1");

		assertThat(blogRepository.findById("1")).isEmpty();
	}

	@Test
	void attachTag_shouldAddTagToBlogObject() throws BlogAppEntityNotFoundException {
		blogService.attachTag("1", "tag1");
		Blog blog = blogRepository.findById("1").get();
		assertThat(blog.getTags()).hasSize(2).filteredOn("tag", "tag1").isNotEmpty();
	}

	@Test
	void attachTag_shouldRemoveTagToBlogObject() throws BlogAppEntityNotFoundException {
		blogService.unattachTag("1", "First Tag");
		Blog blog = blogRepository.findById("1").get();
		assertThat(blog.getTags()).isEmpty();

	}
}
