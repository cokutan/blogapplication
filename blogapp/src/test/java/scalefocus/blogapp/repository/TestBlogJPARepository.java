package scalefocus.blogapp.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import scalefocus.blogapp.entities.Blog;
import scalefocus.blogapp.entities.BlogUser;


@ExtendWith(SpringExtension.class)
@SpringBootTest
class TestBlogJPARepository {

	@Autowired
	private BlogJPARepository repository;
	
	@Test
	void test() {
		Optional<Blog> actual =repository.findById(1l);;
		assertThat(actual).hasValue(new Blog(1,"None","None", new BlogUser()));
		assertThat(actual.get().getCreatedBy().getId()).isSameAs(1l);
	}

}
