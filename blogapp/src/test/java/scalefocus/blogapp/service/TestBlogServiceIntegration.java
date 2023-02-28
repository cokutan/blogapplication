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
import scalefocus.blogapp.domain.Blog;
import scalefocus.blogapp.domain.BlogUser;
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

	private BlogUser blogUser = new BlogUser().setUsername("aliveli");

	@Test
	@Transactional
	void createBlog_shouldReturnCreatedBlogObject() throws BlogAppEntityNotFoundException {

		Blog createdBlog = blogService
				.createBlog(new Blog().setCreatedBy(blogUser).setTitle("title_test").setBody("body_test"));
		Condition<Blog> idGiven = new Condition<>(t -> t.getId() != null && t.getId() != 0, "idGiven");

		assertThat(createdBlog).has(idGiven);
	}

	@Test
	void getBlogSummaryListForUser_shouldReturnSummaryListOfUser() throws BlogAppEntityNotFoundException {
		blogService.createBlog(new Blog().setCreatedBy(blogUser).setTitle("title_test").setBody(
				"The following table lists the supported databases and their tested versions. ... Changing database locking timeout in a cluster configuration."));
		blogService.createBlog(new Blog().setCreatedBy(blogUser).setTitle("title_test").setBody(
				" A convenient way to fix a defective complex sql-query in a service that causes a functional test to fail is to inspect the database state"));
		List<Blog> summaryList = blogService.getBlogSummaryListForUser("aliveli");
		assertThat(summaryList).filteredOn("body", "The following table ").isNotEmpty();
		assertThat(summaryList).filteredOn("body", " A convenient way to").isNotEmpty();
	}

	@Test
	@Transactional
	void updateBlog_shouldReturnCreatedBlogObject() throws BlogAppEntityNotFoundException {
		Blog blog = new Blog()
				.setCreatedBy(new BlogUser().setId(1l).setName("test1").setSurname("test2").setUsername("username"))
				.setTitle("title updated").setBody("description updated");

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
