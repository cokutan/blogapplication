package scalefocus.blogapp.exceptions;

import java.util.function.Supplier;

import scalefocus.blogapp.entities.Blog;

public class BlogNotFoundException extends Throwable implements Supplier<Blog> {

	private static final long serialVersionUID = -6278963955962682212L;

	@Override
	public Blog get() {
		return null;
	}

}
