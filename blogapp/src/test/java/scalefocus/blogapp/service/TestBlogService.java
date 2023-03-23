package scalefocus.blogapp.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.platform.commons.annotation.Testable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.context.ActiveProfiles;

import mockit.Expectations;
import mockit.Injectable;
import mockit.Tested;
import scalefocus.blogapp.domain.Blog;
import scalefocus.blogapp.domain.BlogTag;
import scalefocus.blogapp.domain.BlogUser;
import scalefocus.blogapp.events.BlogCreatedEvent;
import scalefocus.blogapp.events.BlogDeletedEvent;
import scalefocus.blogapp.exceptions.BlogAppEntityNotFoundException;
import scalefocus.blogapp.repository.sqldb.BlogJPARepository;
import scalefocus.blogapp.repository.sqldb.BlogTagRepository;
import scalefocus.blogapp.repository.sqldb.BlogUserRepository;

@Testable
@ActiveProfiles("test")
class TestBlogService {

    @Tested(fullyInitialized = true)
    BlogService blogService;

    @Injectable
    BlogUserRepository blogUserRepository;

    @Injectable
    BlogJPARepository blogJPARepository;

    @Injectable
    BlogTagRepository blogTagRepository;

    @Injectable
    ApplicationEventPublisher applicationEventPublisher;

    private final BlogUser blogUser = new BlogUser().setUsername("aliveli");

    @Test
    void createBlog_shouldReturnCreatedBlogObject() throws BlogAppEntityNotFoundException {
        new Expectations() {
            {
                blogJPARepository.save(withNotNull());
                Blog blog = new Blog();
                blog.setId(2L);
                blog.setCreatedBy(blogUser);
                result = blog;

                blogUserRepository.findFirstByUsername("aliveli");
                result = blogUser;

                applicationEventPublisher.publishEvent(withInstanceOf(BlogCreatedEvent.class));
            }
        };
        Blog createdBlog = blogService.createBlog(new Blog().setCreatedBy(blogUser).setTitle("title_test").setBody("body_test"));

        assertThat(createdBlog).hasFieldOrPropertyWithValue("id", 2L); // implies "data" was persisted
    }

    @Test
    void createBlog_shouldReturnUpdatedBlogObject() throws BlogAppEntityNotFoundException {
        Blog blog = new Blog().setCreatedBy(new BlogUser()).setTitle("title_test").setBody("body_test");
        Blog initialBlog = new Blog().setCreatedBy(new BlogUser()).setTitle("a").setBody("a");
        new Expectations() {
            {
                blogJPARepository.findById(1L);
                result = initialBlog;
            }
        };
        Blog updatedBlog = blogService.updateBlog(1L, blog);

        assertThat(updatedBlog).hasFieldOrPropertyWithValue("body", "body_test");
        assertThat(updatedBlog).hasFieldOrPropertyWithValue("title", "title_test");
    }

    @Test
    void createBlog_shouldThrowBlogNotFoundException() throws BlogAppEntityNotFoundException {
        Blog blog = new Blog().setCreatedBy(new BlogUser()).setTitle("title_test").setBody("body_test");
        new Expectations() {
            {
                blogJPARepository.findById(200L);
                result = Optional.empty();
            }
        };
        assertThatThrownBy(() -> {
            blogService.updateBlog(200L, blog);
        }).isInstanceOf(BlogAppEntityNotFoundException.class);

    }

    @Test
    void deleteBlog_shouldDeleteBlogObject() throws BlogAppEntityNotFoundException {
        Blog blog = new Blog();
        new Expectations() {
            {
                blogJPARepository.findById(1L);
                result =blog;

                blogJPARepository.delete(blog);

                applicationEventPublisher.publishEvent(withInstanceOf(BlogDeletedEvent.class));
            }
        };
        blogService.deleteBlog(1L);

    }

    @Test
    void unattachTag_shouldUnattachTagFromList() throws BlogAppEntityNotFoundException {
        Blog blog = new Blog().setCreatedBy(new BlogUser()).setTitle("title_test").setBody("body_test");
        BlogTag blogtag = new BlogTag().setId(1L).setTag("tag");
        blog.getBlogtags().add(blogtag);
        new Expectations() {
            {
                blogJPARepository.findById(1L);
                result = blog;

                blogTagRepository.findByTag("tag");
                result = blogtag;
            }
        };
        blogService.unattachTag(1L, "tag");

        assertThat(blog.getBlogtags()).isEmpty();

    }

    @Test
    void unattachTag_shouldThrowBlogAppEntityNotFoundExceptionForBlogTag() throws BlogAppEntityNotFoundException {
        Blog blog = new Blog().setCreatedBy(new BlogUser()).setTitle("title_test").setBody("body_test");
        new Expectations() {
            {
                blogJPARepository.findById(1L);
                result = blog;

                blogTagRepository.findByTag("tag");
                result = null;
            }
        };

        assertThatThrownBy(() -> {
            blogService.unattachTag(1L, "tag");
        }).isInstanceOf(BlogAppEntityNotFoundException.class);

    }

    @Test
    void unattachTag_shouldThrowBlogAppEntityNotFoundExceptionForBlog() throws BlogAppEntityNotFoundException {
        new Expectations() {
            {
                blogJPARepository.findById(1L);
                result = Optional.empty();

            }
        };

        assertThatThrownBy(() -> {
            blogService.unattachTag(1L, "tag");
        }).isInstanceOf(BlogAppEntityNotFoundException.class);

    }

    @Test
    void unattachTag_shouldAttachDBTag() throws BlogAppEntityNotFoundException {
        Blog blog = new Blog().setCreatedBy(new BlogUser()).setTitle("title_test").setBody("body_test");
        BlogTag blogtag = new BlogTag().setId(1L).setTag("tag");
        new Expectations() {
            {
                blogJPARepository.findById(1L);
                result = blog;

                blogTagRepository.findByTag("tag");
                result = blogtag;
            }
        };
        blogService.attachTag(1L, "tag");

        assertThat(blog.getBlogtags().get(0).getTag()).isEqualTo(blogtag.getTag());

    }

    @Test
    void unattachTag_shouldAttachNonDBTag() throws BlogAppEntityNotFoundException {
        Blog blog = new Blog().setCreatedBy(new BlogUser()).setTitle("title_test").setBody("body_test");
        BlogTag blogtag = new BlogTag().setId(1L).setTag("tag");
        new Expectations() {
            {
                blogJPARepository.findById(1L);
                result = blog;

                blogTagRepository.findByTag("tag");
                result = null;

                blogTagRepository.save(withNotNull());
                result = blogtag;
            }
        };
        blogService.attachTag(1L, "tag");

        assertThat(blog.getBlogtags().get(0).getTag()).isEqualTo(blogtag.getTag());

    }

    @Test
    void attachTag_shouldThrowBlogAppEntityNotFoundExceptionForBlog() throws BlogAppEntityNotFoundException {
        new Expectations() {
            {
                blogJPARepository.findById(1L);
                result = Optional.empty();

            }
        };

        assertThatThrownBy(() -> {
            blogService.attachTag(1L, "tag");
        }).isInstanceOf(BlogAppEntityNotFoundException.class);

    }
}
