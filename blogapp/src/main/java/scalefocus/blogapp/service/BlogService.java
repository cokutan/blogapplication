package scalefocus.blogapp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import scalefocus.blogapp.entities.Blog;
import scalefocus.blogapp.entities.BlogUser;
import scalefocus.blogapp.repository.BlogJPARepository;
import scalefocus.blogapp.repository.BlogUserRepository;

@Service
public class BlogService {
	@Autowired
	private BlogUserRepository blogUserRepository;
	
	@Autowired
	private BlogJPARepository blogJPARepository;

	public Blog createBlog(String username, String title, String body) {
		BlogUser blogUser = blogUserRepository.findFirstByUsername(username);
		Blog blog = Blog.createBlog(blogUser, title, body);
		return blogJPARepository.save(blog);
	}
}
