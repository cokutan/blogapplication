package scalefocus.blogapp.exceptions;

import java.util.function.Supplier;

import scalefocus.blogapp.domain.Blog;
import scalefocus.blogapp.domain.BlogAppEntity;

public class BlogAppEntityNotFoundException extends Throwable implements Supplier<BlogAppEntity> {

	private static final long serialVersionUID = -6278963955962682212L;

	@Override
	public Blog get() {
		return null;
	}

}
