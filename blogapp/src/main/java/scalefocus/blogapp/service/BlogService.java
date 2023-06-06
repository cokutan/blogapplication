package scalefocus.blogapp.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import scalefocus.blogapp.domain.Blog;
import scalefocus.blogapp.domain.BlogUser;
import scalefocus.blogapp.events.BlogCreatedEvent;
import scalefocus.blogapp.events.BlogDeletedEvent;
import scalefocus.blogapp.events.BlogUpdatedEvent;
import scalefocus.blogapp.exceptions.BlogAppEntityNotFoundException;
import scalefocus.blogapp.repository.opensearch.BlogOpenSearchRepository;
import scalefocus.blogapp.repository.sqldb.BlogRepository;
import scalefocus.blogapp.repository.sqldb.BlogUserRepository;

@Service
@RequiredArgsConstructor
public class BlogService {
  private final BlogUserRepository blogUserRepository;
  private final BlogRepository blogRepository;
  private final BlogOpenSearchRepository blogOpenSearchRepository;
  private final ApplicationEventPublisher applicationEventPublisher;

  public Blog createBlog(Blog blog) throws BlogAppEntityNotFoundException {
    String username = blog.getCreatedBy().getUsername();
    blogUserRepository
        .findFirstByUsername(username)
        .ifPresentOrElse(
            blog::setCreatedBy,
            () -> {
              throw new BlogAppEntityNotFoundException(BlogUser.class, "username", username);
            });
    final BlogCreatedEvent event = new BlogCreatedEvent(blog);
    Blog saved = blogRepository.save(blog);
    applicationEventPublisher.publishEvent(event);
    return saved;
  }

  public List<Blog> getBlogSummaryListForUser(String username, int page, int size)
      throws BlogAppEntityNotFoundException {
    if (!blogUserRepository.existsByUsername(username)) {
      throw new BlogAppEntityNotFoundException(BlogUser.class, "username", username);
    }
    return blogRepository.findBlogSummaryByUser(username, Pageable.ofSize(size).withPage(page));
  }

  public List<Blog> getBlogsWithTag(String tag) {
    List<Blog> blogsWithTag = blogRepository.findByTags(tag);
    return blogsWithTag;
  }

  public Blog updateBlog(String id, Blog blogData) throws BlogAppEntityNotFoundException {
    Blog blog =
        blogRepository
            .findById(id)
            .orElseThrow(() -> new BlogAppEntityNotFoundException(Blog.class, "id", id));
    blog.setTitle(blogData.getTitle());
    blog.setBody(blogData.getBody());
    blogRepository.save(blog);
    fireUpdateEvent(blog);
    return blog;
  }

  private void fireUpdateEvent(Blog blog) {
    final BlogUpdatedEvent event = new BlogUpdatedEvent(blog);
    applicationEventPublisher.publishEvent(event);
  }

  public void unattachTag(String blogId, String tag) throws BlogAppEntityNotFoundException {
    Blog blog =
        blogRepository
            .findById(blogId)
            .orElseThrow(
                () -> new BlogAppEntityNotFoundException(Blog.class, "id", blogId));
    blog.getTags().remove(tag);
    blogRepository.save(blog);
    fireUpdateEvent(blog);
  }

  public void attachTag(String blogId, String tag) throws BlogAppEntityNotFoundException {
    Blog blog =
        blogRepository
            .findById(blogId)
            .orElseThrow(
                () -> new BlogAppEntityNotFoundException(Blog.class, "id", blogId));

    blog.getTags().add(tag);
    blogRepository.save(blog);
    fireUpdateEvent(blog);
  }

  public void deleteBlog(String id) throws BlogAppEntityNotFoundException {
    Blog blog =
        blogRepository
            .findById(id)
            .orElseThrow(() -> new BlogAppEntityNotFoundException(Blog.class, "id", id));
    blogRepository.delete(blog);
    applicationEventPublisher.publishEvent(new BlogDeletedEvent(blog));
  }

  public List<Blog> getBlogSearchResults(String term) {
    return blogOpenSearchRepository.search(term);
  }
}
