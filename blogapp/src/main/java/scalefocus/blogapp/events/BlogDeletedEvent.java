package scalefocus.blogapp.events;


import scalefocus.blogapp.domain.Blog;


public class BlogDeletedEvent {

    private final Blog blog;

    public BlogDeletedEvent(Blog blog) {
        this.blog = blog;
    }

    public Blog getBlog() {
        return blog;
    }

    @Override
    public String toString() {
        return "BlogDeletedEvent{" + "Blog=" + blog + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BlogDeletedEvent that = (BlogDeletedEvent) o;

        return blog.equals(that.blog);
    }

    @Override
    public int hashCode() {
        return blog.hashCode();
    }
}
