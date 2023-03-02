package scalefocus.blogapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableAutoConfiguration
public class BlogappApplication {

    public static void main(String[] args) {
	SpringApplication.run(BlogappApplication.class, args);
    }

}
