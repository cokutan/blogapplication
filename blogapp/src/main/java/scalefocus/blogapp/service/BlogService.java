package scalefocus.blogapp.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Pageable;
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
        String username = blog.getCreatedBy().getUsername();
        blogUserRepository.findFirstByUsername(username).ifPresentOrElse((value) -> blog.setCreatedBy(value), () ->
                new BlogAppEntityNotFoundException(BlogUser.class, "username", username)
        );
        return blogJPARepository.save(blog);
    }

    public List<Blog> getBlogSummaryListForUser(String username, int page, int size) throws BlogAppEntityNotFoundException {
        blogUserRepository.findFirstByUsername(username).orElseThrow(() ->
                new BlogAppEntityNotFoundException(BlogUser.class, "username", username)
        );
        return blogJPARepository.findBlogSummaryByUser(username, Pageable.ofSize(size).withPage(page));
    }

    @Transactional
    public List<Blog> getBlogsWithTag(String tag) {
        List<Blog> blogsWithTag = blogJPARepository.findByBlogtags_Tag(tag);
        touchTags(blogsWithTag);
        return blogsWithTag;
    }

    private void touchTags(List<Blog> blogsWithTag) {
        blogsWithTag.stream().map(Blog::getBlogtags).flatMap(List::stream).collect(Collectors.toList());
    }

    @Transactional
    public Blog updateBlog(Long id, Blog blogData) throws BlogAppEntityNotFoundException {
        Blog blog = blogJPARepository.findById(id).orElseThrow(() -> new BlogAppEntityNotFoundException(Blog.class, "id", id.toString()));
        blog.setTitle(blogData.getTitle());
        blog.setBody(blogData.getBody());
        touchTags(List.of(blog));
        return blog;
    }

    @Transactional
    public void unattachTag(Long blogId, String tag) throws BlogAppEntityNotFoundException {
        Blog blog = blogJPARepository.findById(blogId).orElseThrow(() -> new BlogAppEntityNotFoundException(Blog.class, "id", blogId.toString()));
        BlogTag blogtag = Optional.ofNullable(blogTagRepository.findByTag(tag))
                .orElseThrow(() -> new BlogAppEntityNotFoundException(BlogTag.class, "tag", tag));

        blog.getBlogtags().remove(blogtag);

        return;
    }

    @Transactional
    public void attachTag(Long blogId, String tag) throws BlogAppEntityNotFoundException {
        Blog blog = blogJPARepository.findById(blogId).orElseThrow(() -> new BlogAppEntityNotFoundException(Blog.class, "id", blogId.toString()));
        BlogTag blogtag = Optional.ofNullable(blogTagRepository.findByTag(tag))
                .orElse(blogTagRepository.save(new BlogTag().setId(0l).setTag(tag)));

        blog.getBlogtags().add(blogtag);

        return;
    }

    @Transactional
    public void deleteBlog(Long id) throws BlogAppEntityNotFoundException {
        Blog blog = blogJPARepository.findById(id).orElseThrow(() -> new BlogAppEntityNotFoundException(Blog.class, "id", id.toString()));
        blogJPARepository.delete(blog);
    }
}
