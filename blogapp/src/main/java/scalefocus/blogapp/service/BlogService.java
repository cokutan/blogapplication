package scalefocus.blogapp.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import scalefocus.blogapp.dto.BlogCreationDTO;
import scalefocus.blogapp.dto.BlogSummary;
import scalefocus.blogapp.entities.Blog;
import scalefocus.blogapp.entities.BlogTag;
import scalefocus.blogapp.entities.BlogUser;
import scalefocus.blogapp.exceptions.BlogAppEntityNotFoundException;
import scalefocus.blogapp.repository.BlogJPARepository;
import scalefocus.blogapp.repository.BlogTagRepository;
import scalefocus.blogapp.repository.BlogUserRepository;

@Service
public class BlogService {
	@Autowired
	private BlogUserRepository blogUserRepository;

	@Autowired
	private BlogJPARepository blogJPARepository;

	@Autowired
	private BlogTagRepository blogTagRepository;
	
	@Transactional
	public Blog createBlog(BlogCreationDTO blogCreationDTO) throws BlogAppEntityNotFoundException {
		BlogUser blogUser = blogUserRepository.findFirstByUsername(blogCreationDTO.getUsername());
		if(blogUser ==null) {
			throw new BlogAppEntityNotFoundException();
		}
		Blog blog = Blog.createBlog(blogUser, blogCreationDTO.getTitle(), blogCreationDTO.getBody());
		return blogJPARepository.save(blog);
	}

	public List<BlogSummary> getBlogSummaryListForUser(String username) throws BlogAppEntityNotFoundException {
		BlogUser blogUser = blogUserRepository.findFirstByUsername(username);
		if(blogUser == null) {
			throw new BlogAppEntityNotFoundException();
		}
		return blogJPARepository.findBlogSummaryByUser(blogUser.getId());
	}

	public List<Blog> getBlogsWithTag(String tag) {
		return blogJPARepository.findByBlogtags_Tag(tag);
	}

	@Transactional
	public Blog updateBlog(Long id, Blog blogData) throws BlogAppEntityNotFoundException {
		Blog blog = blogJPARepository.findById(id).orElseThrow(() -> new BlogAppEntityNotFoundException());
		blog.setTitle(blogData.getTitle());
		blog.setBody(blogData.getBody());
		return blog;
	}

	@Transactional
	public void unattachTag(Long blogId, String tag) throws BlogAppEntityNotFoundException {
		Blog blog = blogJPARepository.findById(blogId).orElseThrow(() -> new BlogAppEntityNotFoundException());
		BlogTag blogtag = Optional.ofNullable(blogTagRepository.findByTag(tag))
				.orElseThrow(() -> new BlogAppEntityNotFoundException());

		blog.getBlogtags().remove(blogtag);

		return;
	}

	@Transactional
	public void attachTag(Long blogId, String tag) throws BlogAppEntityNotFoundException {
		Blog blog = blogJPARepository.findById(blogId).orElseThrow(() -> new BlogAppEntityNotFoundException());
		BlogTag blogtag = Optional.ofNullable(blogTagRepository.findByTag(tag))
				.orElse(blogTagRepository.save(new BlogTag(0, tag)));

		blog.getBlogtags().add(blogtag);

		return;
	}
}
