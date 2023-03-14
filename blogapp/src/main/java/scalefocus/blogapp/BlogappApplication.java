package scalefocus.blogapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

@SpringBootApplication
@EnableAutoConfiguration
@OpenAPIDefinition(info = @Info(title = "Blog Application API", version = "v0.2.0", description = "Blog Application"))
public class BlogappApplication {

	public static void main(String[] args) {
		SpringApplication.run(BlogappApplication.class, args);
	}

}
