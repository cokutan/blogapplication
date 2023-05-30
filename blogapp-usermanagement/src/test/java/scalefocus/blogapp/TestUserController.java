package scalefocus.blogapp;

import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;
import scalefocus.blogapp.controller.UserController;
import scalefocus.blogapp.domain.RegisterRequest;
import scalefocus.blogapp.repository.sqldb.BlogUserRepository;

class TestUserController {

  @Mock private BlogUserRepository userRepository;
  @Mock private RestTemplate restTemplate;
  @Mock private RestTemplateBuilder restTemplateBuilder;
  private UserController userController;
  private AutoCloseable closeable;

  @BeforeEach
  void setUp() {
    closeable = MockitoAnnotations.openMocks(this);
    Mockito.when(restTemplateBuilder.build()).thenReturn(restTemplate);
    userController = new UserController(userRepository, restTemplateBuilder);
    ReflectionTestUtils.setField(userController, "keycloakBaseUrl", "http://example.com");
  }

  @AfterEach
  public void releaseMocks() throws Exception {
    closeable.close();
  }

  @Test
  void testRegisterUser_WhenUserDoesNotExist() {
    // Arrange
    RegisterRequest registerRequest = new RegisterRequest();
    registerRequest.setUsername("testuser");
    registerRequest.setPassword("testpassword");
    registerRequest.setDisplayname("Test User");

    Mockito.when(userRepository.existsByUsername("testuser")).thenReturn(false);
    Mockito.when(
            restTemplate.postForEntity(
                Mockito.any(String.class), Mockito.any(HttpEntity.class), Mockito.any()))
        .thenReturn(ResponseEntity.ok(Map.of("access_token", "token")));

    // Act
    ResponseEntity<?> response = userController.registerUser(registerRequest);

    // Assert
    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    Mockito.verify(userRepository, Mockito.times(1)).existsByUsername("testuser");
    Mockito.verify(userRepository, Mockito.times(1)).save(ArgumentMatchers.any());

    // Additional assertions for Keycloak interactions can be added if necessary
  }

  @Test
  void testRegisterUser_WhenUserExists() {
    // Arrange
    RegisterRequest registerRequest = new RegisterRequest();
    registerRequest.setUsername("existinguser");
    registerRequest.setPassword("testpassword");
    registerRequest.setDisplayname("Test User");

    Mockito.when(userRepository.existsByUsername("existinguser")).thenReturn(true);

    // Act
    ResponseEntity<?> response = userController.registerUser(registerRequest);

    // Assert
    Assertions.assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    // Additional assertions for response content can be added if necessary
    Mockito.verify(userRepository, Mockito.times(1)).existsByUsername("existinguser");
    Mockito.verify(userRepository, Mockito.never()).save(ArgumentMatchers.any());
  }
}
