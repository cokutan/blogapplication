package scalefocus.blogapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchDataAutoConfiguration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import scalefocus.blogapp.repository.opensearch.BlogElasticSearchRepository;

@SpringBootApplication(exclude = {ElasticsearchDataAutoConfiguration.class})
@EnableAutoConfiguration
@EnableJpaRepositories(basePackages = "scalefocus.blogapp.repository.sqldb")
@OpenAPIDefinition(info = @Info(title = "Blog Application API", version = "v0.2.0", description = "Blog Application"))
public class BlogappApplication {

    public static void main(String[] args) {
        SpringApplication.run(BlogappApplication.class, args);
    }

}
