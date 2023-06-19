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
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import scalefocus.blogapp.domain.Blog;
import scalefocus.blogapp.domain.BlogUser;
import scalefocus.blogapp.exceptions.BlogAppEntityNotFoundException;
import scalefocus.blogapp.repository.BlogRepository;
import scalefocus.blogapp.repository.BlogUserRepository;

@Testable
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class TestBlogService {

  @InjectMocks BlogService blogService;

  @Mock BlogUserRepository blogUserRepository;

  @Mock BlogRepository blogRepository;

  @Mock KafkaProducerService kafkaProducerService;

  private final BlogUser blogUser = new BlogUser().setUsername("aliveli");

  @Test
  void createBlog_shouldReturnCreatedBlogObject() throws BlogAppEntityNotFoundException {
    Blog blog = new Blog();
    blog.setId("2");
    blog.setCreatedBy(blogUser);
    when(blogRepository.save(any())).thenReturn(blog);
    when(blogUserRepository.findFirstByUsername("aliveli"))
        .thenReturn(Optional.ofNullable(blogUser));

    Blog createdBlog =
        blogService.createBlog(
            new Blog().setCreatedBy(blogUser).setTitle("title_test").setBody("body_test"));

    assertThat(createdBlog).hasFieldOrPropertyWithValue("id", "2"); // implies "data" was persisted
    verify(kafkaProducerService, atLeastOnce()).sendCreateMessage(createdBlog);
  }

  @Test
  void createBlog_shouldReturnThrowUserNotFoundException() throws BlogAppEntityNotFoundException {
    when(blogUserRepository.findFirstByUsername("aliveli")).thenReturn(Optional.empty());

    assertThatThrownBy(
            () ->
                blogService.createBlog(
                    new Blog().setCreatedBy(blogUser).setTitle("title_test").setBody("body_test")))
        .isInstanceOf(BlogAppEntityNotFoundException.class);
  }

  @Test
  void createBlog_shouldReturnUpdatedBlogObject() throws BlogAppEntityNotFoundException {
    Blog blog = new Blog().setCreatedBy(new BlogUser()).setTitle("title_test").setBody("body_test");
    Blog initialBlog = new Blog().setCreatedBy(new BlogUser()).setTitle("a").setBody("a");
    when(blogRepository.findById("1")).thenReturn(Optional.ofNullable(initialBlog));
    Blog updatedBlog = blogService.updateBlog("1", blog);

    assertThat(updatedBlog)
        .hasFieldOrPropertyWithValue("body", "body_test")
        .hasFieldOrPropertyWithValue("title", "title_test");
    verify(kafkaProducerService, atLeastOnce()).sendUpdateMessage(updatedBlog);
  }

  @Test
  void createBlog_shouldThrowBlogNotFoundException() throws BlogAppEntityNotFoundException {
    Blog blog = new Blog().setCreatedBy(new BlogUser()).setTitle("title_test").setBody("body_test");
    when(blogRepository.findById("200")).thenReturn(Optional.empty());
    assertThatThrownBy(() -> blogService.updateBlog("200", blog))
        .isInstanceOf(BlogAppEntityNotFoundException.class);
  }

  @Test
  void deleteBlog_shouldDeleteBlogObject() throws BlogAppEntityNotFoundException {
    Blog blog = new Blog();
    when(blogRepository.findById("1")).thenReturn(Optional.of(blog));

    blogRepository.delete(blog);

    blogService.deleteBlog("1");
    verify(kafkaProducerService, atLeastOnce()).sendDeleteMessage(any());
  }

  @Test
  void unattachTag_shouldUnattachTagFromList() throws BlogAppEntityNotFoundException {
    Blog blog = new Blog().setCreatedBy(new BlogUser()).setTitle("title_test").setBody("body_test");
    blog.getTags().add("tag");
    when(blogRepository.findById("1")).thenReturn(Optional.of(blog));
    blogService.unattachTag("1", "tag");

    assertThat(blog.getTags()).isEmpty();
    verify(kafkaProducerService, atLeastOnce()).sendUpdateMessage(any());
  }

  @Test
  void unattachTag_shouldThrowBlogAppEntityNotFoundExceptionForBlog()
      throws BlogAppEntityNotFoundException {
    when(blogRepository.findById("1")).thenReturn(Optional.empty());

    assertThatThrownBy(() -> blogService.unattachTag("1", "tag"))
        .isInstanceOf(BlogAppEntityNotFoundException.class);
  }

  @Test
  void unattachTag_shouldAttachDBTag() throws BlogAppEntityNotFoundException {
    Blog blog = new Blog().setCreatedBy(new BlogUser()).setTitle("title_test").setBody("body_test");
    blog.getTags().add("tag");
    when(blogRepository.findById("1")).thenReturn(Optional.of(blog));
    blogService.attachTag("1", "tag");

    assertThat(blog.getTags().get(0)).isEqualTo("tag");
    verify(kafkaProducerService, atLeastOnce()).sendUpdateMessage(any());
  }

  @Test
  void attachTag_shouldThrowBlogAppEntityNotFoundExceptionForBlog()
      throws BlogAppEntityNotFoundException {
    when(blogRepository.findById("1")).thenReturn(Optional.empty());

    assertThatThrownBy(() -> blogService.attachTag("1", "tag"))
        .isInstanceOf(BlogAppEntityNotFoundException.class);
  }
}
