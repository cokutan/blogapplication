package scalefocus.blogapp.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import scalefocus.blogapp.domain.BlogUser;
import scalefocus.blogapp.domain.RegisterRequest;
import scalefocus.blogapp.repository.sqldb.BlogUserRepository;

@RestController
@RequiredArgsConstructor
@Slf4j
public class UserController {

  private final BlogUserRepository userRepository;

  @Value("${keycloak.auth-server-url}")
  private String keycloakBaseUrl;

  @Value("${keycloak.realm}")
  private String keycloakRealm;

  @Value("${keycloak.client-id}")
  private String keycloakClientId;

  @Value("${keycloak.client-secret}")
  private String keycloakClientSecret;

  private RestTemplate restTemplate;

  @Autowired
  public UserController(
      BlogUserRepository blogUserRepository, RestTemplateBuilder restTemplateBuilder) {
    this.userRepository = blogUserRepository;
    this.restTemplate = restTemplateBuilder.build();
  }

  @PostMapping("/register")
  @Operation(
      summary = "Register the user for the first time",
      operationId = "register",
      tags = {"authentication"},
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "Succesfully registered",
            content = @Content(schema = @Schema(implementation = ResponseEntity.class)))
      })
  public ResponseEntity<?> registerUser(@RequestBody RegisterRequest registerRequest) {

    if (userRepository.existsByUsername(registerRequest.getUsername())) {
      return ResponseEntity.of(
              ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, "User name already exists!"))
          .build();
    }
    // create the user in Keycloak
    String createUserUrl = keycloakBaseUrl + "/admin/realms/" + keycloakRealm + "/users";
    String accessToken =
        getAccessToken(); // get a Keycloak access token with the "manage-users" scope

    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(accessToken);
    headers.setContentType(MediaType.APPLICATION_JSON);

    Map<String, Object> requestBody = new HashMap<>();
    requestBody.put("username", registerRequest.getUsername());
    requestBody.put("enabled", true);
    requestBody.put(
        "credentials",
        Arrays.asList(
            Map.of(
                "type", "password", "value", registerRequest.getPassword(), "temporary", false)));

    HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

    restTemplate.postForObject(createUserUrl, requestEntity, String.class);

    userRepository.save(
        new BlogUser()
            .setDisplayname(registerRequest.getDisplayname())
            .setUsername(registerRequest.getUsername()));

    return ResponseEntity.ok().build();
  }

  @PostMapping("/logout")
  @Operation(
      summary = "Logout the logged in user",
      operationId = "logout",
      tags = {"logout"},
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "Succesfully logged out",
            content = @Content(schema = @Schema(implementation = ResponseEntity.class)))
      })
  public ResponseEntity<?> logout(HttpServletRequest request) {

    HttpSession session = request.getSession(false);
    if (session != null) {
      session.invalidate();
    }

    String logoutUrl =
        keycloakBaseUrl
            + "/realms/"
            + keycloakRealm
            + "/protocol/openid-connect/logout?redirect_uri="
            + getLogoutRedirectUri(request);

    restTemplate.postForEntity(logoutUrl, null, String.class);

    return ResponseEntity.ok().build();
  }

  private String getLogoutRedirectUri(HttpServletRequest request) {
    String contextPath = request.getContextPath();
    return request.getScheme()
        + "://"
        + request.getServerName()
        + ":"
        + request.getServerPort()
        + contextPath;
  }

  private String getAccessToken() {

    String tokenUrl =
        keycloakBaseUrl + "/realms/" + keycloakRealm + "/protocol/openid-connect/token";

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

    MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
    requestBody.add("grant_type", "client_credentials");
    requestBody.add("client_id", keycloakClientId);
    requestBody.add("client_secret", keycloakClientSecret);
    // requestBody.add("scope", "manage-users");

    HttpEntity<MultiValueMap<String, String>> requestEntity =
        new HttpEntity<>(requestBody, headers);

    ResponseEntity<Map> responseEntity =
        restTemplate.postForEntity(tokenUrl, requestEntity, Map.class);

    return responseEntity.getBody().get("access_token").toString();
  }
}
