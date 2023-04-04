package scalefocus.blogapp.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import scalefocus.blogapp.domain.Blog;
import scalefocus.blogapp.domain.BlogTag;
import scalefocus.blogapp.domain.BlogUser;
import scalefocus.blogapp.events.BlogCreatedEvent;
import scalefocus.blogapp.events.BlogDeletedEvent;
import scalefocus.blogapp.events.BlogUpdatedEvent;
import scalefocus.blogapp.exceptions.BlogAppEntityNotFoundException;
import scalefocus.blogapp.repository.opensearch.BlogOpenSearchRepository;
import scalefocus.blogapp.repository.sqldb.BlogJPARepository;
import scalefocus.blogapp.repository.sqldb.BlogTagRepository;
import scalefocus.blogapp.repository.sqldb.BlogUserRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BlogService {
  private final BlogUserRepository blogUserRepository;

  private final BlogJPARepository blogJPARepository;

  private final BlogTagRepository blogTagRepository;

  private final BlogOpenSearchRepository blogOpenSearchRepository;

  private final ApplicationEventPublisher applicationEventPublisher;

  @Transactional()
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
    applicationEventPublisher.publishEvent(event);
    return blogJPARepository.save(blog);
  }

  public List<Blog> getBlogSummaryListForUser(String username, int page, int size)
      throws BlogAppEntityNotFoundException {
    if (!blogUserRepository.existsByUsername(username)) {
      throw new BlogAppEntityNotFoundException(BlogUser.class, "username", username);
    }
    return blogJPARepository.findBlogSummaryByUser(username, Pageable.ofSize(size).withPage(page));
  }

  @Transactional
  public List<Blog> getBlogsWithTag(String tag) {
    List<Blog> blogsWithTag = blogJPARepository.findByBlogtags_Tag(tag);
    touchTags(blogsWithTag);
    return blogsWithTag;
  }

  private void touchTags(List<Blog> blogsWithTag) {
    blogsWithTag.stream().map(Blog::getBlogtags).flatMap(List::stream).toList();
  }

  @Transactional
  public Blog updateBlog(Long id, Blog blogData) throws BlogAppEntityNotFoundException {
    Blog blog =
        blogJPARepository
            .findById(id)
            .orElseThrow(() -> new BlogAppEntityNotFoundException(Blog.class, "id", id.toString()));
    blog.setTitle(blogData.getTitle());
    blog.setBody(blogData.getBody());
    touchTags(List.of(blog));
    fireUpdateEvent(blog);
    return blog;
  }

  private void fireUpdateEvent(Blog blog) {
    final BlogUpdatedEvent event = new BlogUpdatedEvent(blog);
    applicationEventPublisher.publishEvent(event);
  }

  @Transactional
  public void unattachTag(Long blogId, String tag) throws BlogAppEntityNotFoundException {
    Blog blog =
        blogJPARepository
            .findById(blogId)
            .orElseThrow(
                () -> new BlogAppEntityNotFoundException(Blog.class, "id", blogId.toString()));
    BlogTag blogtag =
        Optional.ofNullable(blogTagRepository.findByTag(tag))
            .orElseThrow(() -> new BlogAppEntityNotFoundException(BlogTag.class, "tag", tag));

    blog.getBlogtags().remove(blogtag);
    fireUpdateEvent(blog);
  }

  @Transactional
  public void attachTag(Long blogId, String tag) throws BlogAppEntityNotFoundException {
    Blog blog =
        blogJPARepository
            .findById(blogId)
            .orElseThrow(
                () -> new BlogAppEntityNotFoundException(Blog.class, "id", blogId.toString()));
    BlogTag blogtag =
        Optional.ofNullable(blogTagRepository.findByTag(tag))
            .orElseGet(() -> blogTagRepository.save(new BlogTag().setId(0L).setTag(tag)));

    blog.getBlogtags().add(blogtag);
    fireUpdateEvent(blog);
  }

  @Transactional
  public void deleteBlog(Long id) throws BlogAppEntityNotFoundException {
    Blog blog =
        blogJPARepository
            .findById(id)
            .orElseThrow(() -> new BlogAppEntityNotFoundException(Blog.class, "id", id.toString()));
    blogJPARepository.delete(blog);
    touchTags(List.of(blog));
    applicationEventPublisher.publishEvent(new BlogDeletedEvent(blog));
  }

  public List<Blog> getBlogSearchResults(String term) {
    return blogOpenSearchRepository.search(term);
  }
}
