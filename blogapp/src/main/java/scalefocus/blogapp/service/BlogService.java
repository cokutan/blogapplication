package scalefocus.blogapp.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import scalefocus.blogapp.domain.Blog;
import scalefocus.blogapp.domain.BlogTag;
import scalefocus.blogapp.domain.BlogUser;
import scalefocus.blogapp.exceptions.BlogAppEntityNotFoundException;
import scalefocus.blogapp.repository.BlogJPARepository;
import scalefocus.blogapp.repository.BlogTagRepository;
import scalefocus.blogapp.repository.BlogUserRepository;

@Service
@RequiredArgsConstructor
public class BlogService {
	final private BlogUserRepository blogUserRepository;

	final private BlogJPARepository blogJPARepository;

	final private BlogTagRepository blogTagRepository;

	@Transactional
	public Blog createBlog(Blog blog) throws BlogAppEntityNotFoundException {
		BlogUser blogUser = blogUserRepository.findFirstByUsername(blog.getCreatedBy().getUsername());
		if (blogUser == null) {
			throw new BlogAppEntityNotFoundException();
		}
		blog.setCreatedBy(blogUser);
		return blogJPARepository.save(blog);
	}

	public List<Blog> getBlogSummaryListForUser(String username) throws BlogAppEntityNotFoundException {
		BlogUser blogUser = blogUserRepository.findFirstByUsername(username);
		if (blogUser == null) {
			throw new BlogAppEntityNotFoundException();
		}
		return blogJPARepository.findBlogSummaryByUser(username);
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
				.orElse(blogTagRepository.save(new BlogTag().setId(0l).setTag(tag)));

		blog.getBlogtags().add(blogtag);

		return;
	}
}
