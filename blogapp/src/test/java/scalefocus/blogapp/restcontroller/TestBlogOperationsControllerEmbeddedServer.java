package scalefocus.blogapp.restcontroller;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;

import scalefocus.blogapp.restcontrollers.BlogOperationsRestController;

/** 
 * The goal of this class is to show how the Embedded Server is used to test the REST service 
 */ 
 
// SpringBootTest launch an instance of our application for tests purposes 
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT) 
class TestBlogOperationsControllerEmbeddedServer { 
  @Autowired 
  private BlogOperationsRestController blogOperationsRestController; 
 
  // inject the runtime port, it requires the webEnvironment 
  @LocalServerPort 
  private int port; 
 
  // we use TestRestTemplate, it's an alternative to RestTemplate specific for tests 
  // to use this template a webEnvironment is mandatory 
  @Autowired 
  private TestRestTemplate restTemplate; 
 
  @Test 
  void index() { 
    assertThat(blogOperationsRestController).isNotNull(); 
  } 
 
  @Test 
  void indexResultTest() { 
    assertThat(restTemplate 
      .getForObject("http://localhost:" + port + "/users/aliveli/blogs", List.class)).isNotEmpty(); 
  } 
} 