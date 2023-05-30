package scalefocus.blogapp.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.commons.annotation.Testable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.context.ActiveProfiles;

import scalefocus.blogapp.domain.Blog;
import scalefocus.blogapp.domain.BlogTag;
import scalefocus.blogapp.domain.BlogUser;
import scalefocus.blogapp.events.BlogCreatedEvent;
import scalefocus.blogapp.events.BlogDeletedEvent;
import scalefocus.blogapp.exceptions.BlogAppEntityNotFoundException;
import scalefocus.blogapp.repository.opensearch.BlogOpenSearchRepository;
import scalefocus.blogapp.repository.sqldb.BlogJPARepository;
import scalefocus.blogapp.repository.sqldb.BlogTagRepository;
import scalefocus.blogapp.repository.sqldb.BlogUserRepository;

@Testable
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class TestBlogService {

  @InjectMocks BlogService blogService;

  @Mock BlogUserRepository blogUserRepository;

  @Mock BlogJPARepository blogJPARepository;

  @Mock BlogTagRepository blogTagRepository;

  @Mock ApplicationEventPublisher applicationEventPublisher;

  @Mock BlogOpenSearchRepository blogOpenSearchRepository;

  private final BlogUser blogUser = new BlogUser().setUsername("aliveli");

  @Test
  void createBlog_shouldReturnCreatedBlogObject() throws BlogAppEntityNotFoundException {
    Blog blog = new Blog();
    blog.setId(2L);
    blog.setCreatedBy(blogUser);
    when(blogJPARepository.save(any())).thenReturn(blog);

    when(blogUserRepository.findFirstByUsername("aliveli"))
        .thenReturn(Optional.ofNullable(blogUser));

    Blog createdBlog =
        blogService.createBlog(
            new Blog().setCreatedBy(blogUser).setTitle("title_test").setBody("body_test"));

    assertThat(createdBlog).hasFieldOrPropertyWithValue("id", 2L); // implies "data" was persisted
    verify(applicationEventPublisher, atLeastOnce()).publishEvent(any(BlogCreatedEvent.class));
  }

  @Test
  void createBlog_shouldReturnThrowUserNotFoundException() throws BlogAppEntityNotFoundException {
    when(blogUserRepository.findFirstByUsername("aliveli")).thenReturn(Optional.empty());

    assertThatThrownBy(
            () -> {
              blogService.createBlog(
                  new Blog().setCreatedBy(blogUser).setTitle("title_test").setBody("body_test"));
            })
        .isInstanceOf(BlogAppEntityNotFoundException.class);
  }

  @Test
  void createBlog_shouldReturnUpdatedBlogObject() throws BlogAppEntityNotFoundException {
    Blog blog = new Blog().setCreatedBy(new BlogUser()).setTitle("title_test").setBody("body_test");
    Blog initialBlog = new Blog().setCreatedBy(new BlogUser()).setTitle("a").setBody("a");
    when(blogJPARepository.findById(1L)).thenReturn(Optional.ofNullable(initialBlog));
    Blog updatedBlog = blogService.updateBlog(1L, blog);

    assertThat(updatedBlog)
        .hasFieldOrPropertyWithValue("body", "body_test")
        .hasFieldOrPropertyWithValue("title", "title_test");
  }

  @Test
  void createBlog_shouldThrowBlogNotFoundException() throws BlogAppEntityNotFoundException {
    Blog blog = new Blog().setCreatedBy(new BlogUser()).setTitle("title_test").setBody("body_test");
    when(blogJPARepository.findById(200L)).thenReturn(Optional.empty());
    assertThatThrownBy(
            () -> {
              blogService.updateBlog(200L, blog);
            })
        .isInstanceOf(BlogAppEntityNotFoundException.class);
  }

  @Test
  void deleteBlog_shouldDeleteBlogObject() throws BlogAppEntityNotFoundException {
    Blog blog = new Blog();
    when(blogJPARepository.findById(1L)).thenReturn(Optional.of(blog));

    blogJPARepository.delete(blog);

    blogService.deleteBlog(1L);
    verify(applicationEventPublisher, atLeastOnce()).publishEvent(any(BlogDeletedEvent.class));
  }

  @Test
  void unattachTag_shouldUnattachTagFromList() throws BlogAppEntityNotFoundException {
    Blog blog = new Blog().setCreatedBy(new BlogUser()).setTitle("title_test").setBody("body_test");
    BlogTag blogtag = new BlogTag().setId(1L).setTag("tag");
    blog.getBlogtags().add(blogtag);
    when(blogJPARepository.findById(1L)).thenReturn(Optional.of(blog));
    when(blogTagRepository.findByTag("tag")).thenReturn(blogtag);
    blogService.unattachTag(1L, "tag");

    assertThat(blog.getBlogtags()).isEmpty();
  }

  @Test
  void unattachTag_shouldThrowBlogAppEntityNotFoundExceptionForBlogTag()
      throws BlogAppEntityNotFoundException {
    Blog blog = new Blog().setCreatedBy(new BlogUser()).setTitle("title_test").setBody("body_test");
    when(blogJPARepository.findById(1L)).thenReturn(Optional.of(blog));
    when(blogTagRepository.findByTag("tag")).thenReturn(null);

    assertThatThrownBy(
            () -> {
              blogService.unattachTag(1L, "tag");
            })
        .isInstanceOf(BlogAppEntityNotFoundException.class);
  }

  @Test
  void unattachTag_shouldThrowBlogAppEntityNotFoundExceptionForBlog()
      throws BlogAppEntityNotFoundException {
    when(blogJPARepository.findById(1L)).thenReturn(Optional.empty());

    assertThatThrownBy(
            () -> {
              blogService.unattachTag(1L, "tag");
            })
        .isInstanceOf(BlogAppEntityNotFoundException.class);
  }

  @Test
  void unattachTag_shouldAttachDBTag() throws BlogAppEntityNotFoundException {
    Blog blog = new Blog().setCreatedBy(new BlogUser()).setTitle("title_test").setBody("body_test");
    BlogTag blogtag = new BlogTag().setId(1L).setTag("tag");
    when(blogJPARepository.findById(1L)).thenReturn(Optional.of(blog));
    when(blogTagRepository.findByTag("tag")).thenReturn(blogtag);
    blogService.attachTag(1L, "tag");

    assertThat(blog.getBlogtags().get(0).getTag()).isEqualTo(blogtag.getTag());
  }

  @Test
  void unattachTag_shouldAttachNonDBTag() throws BlogAppEntityNotFoundException {
    Blog blog = new Blog().setCreatedBy(new BlogUser()).setTitle("title_test").setBody("body_test");
    BlogTag blogtag = new BlogTag().setId(1L).setTag("tag");
    when(blogJPARepository.findById(1L)).thenReturn(Optional.of(blog));
    when(blogTagRepository.findByTag("tag")).thenReturn(null);
    when(blogTagRepository.save(notNull())).thenReturn(blogtag);
    blogService.attachTag(1L, "tag");

    assertThat(blog.getBlogtags().get(0).getTag()).isEqualTo(blogtag.getTag());
  }

  @Test
  void attachTag_shouldThrowBlogAppEntityNotFoundExceptionForBlog()
      throws BlogAppEntityNotFoundException {
    when(blogJPARepository.findById(1L)).thenReturn(Optional.empty());

    assertThatThrownBy(
            () -> {
              blogService.attachTag(1L, "tag");
            })
        .isInstanceOf(BlogAppEntityNotFoundException.class);
  }
}
