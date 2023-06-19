package scalefocus.blogapp;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
@OpenAPIDefinition(info = @Info(title = "Blog Application UserManagement API", version = "v0.3.0", description = "Blog Application"))
public class BlogappUserManagementApplication {
    public static void main(String[] args) {
        SpringApplication.run(BlogappUserManagementApplication.class, args);
    }

}
