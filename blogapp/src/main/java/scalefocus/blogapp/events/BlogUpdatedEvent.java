package scalefocus.blogapp.events;


import scalefocus.blogapp.domain.Blog;


public class BlogUpdatedEvent {

    private final Blog blog;

    public BlogUpdatedEvent(Blog blog) {
        this.blog = blog;
    }

    public Blog getBlog() {
        return blog;
    }

    @Override
    public String toString() {
        return "BlogUpdatedEvent{" +
                "Blog=" + blog +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BlogUpdatedEvent that = (BlogUpdatedEvent) o;

        return blog.equals(that.blog);
    }

    @Override
    public int hashCode() {
        return blog.hashCode();
    }
}
