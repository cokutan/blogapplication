package scalefocus.blogapp.blogappwebflux;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(info = @Info(title = "Blog Application File Upload API", version = "v0.3.0", description = "Blog Application File Upload"))
public class BlogappWebfluxApplication {

	public static void main(String[] args) {
		SpringApplication.run(BlogappWebfluxApplication.class, args);
	}

}
