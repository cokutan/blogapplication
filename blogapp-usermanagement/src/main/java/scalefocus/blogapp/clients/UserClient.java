package scalefocus.blogapp.clients;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "BLOGAPP")
public interface UserClient {

	@GetMapping("/validuser/{username}")
	Boolean existsByUsername(@PathVariable("username") String username);
	
}
