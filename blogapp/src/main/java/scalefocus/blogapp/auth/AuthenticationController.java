package scalefocus.blogapp.auth;

import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import scalefocus.blogapp.exceptions.BlogAppEntityNotFoundException;

@RestController
@RequestMapping("/api/v3/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthenticationController {

	private final AuthenticationService authenticationService;

	@PostMapping("/register")
	@Operation(summary = "Register the user for the first time", operationId = "register", tags = {
			"authentication" }, responses = {
					@ApiResponse(responseCode = "200", description = "Succesfully registered", content = @Content(examples = {
							@ExampleObject(name = "Token generated with SHA-256 algorithm.", summary = "response", value = "  \"token\": \"eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhbGl2ZWxpIiwiaWF0IjoxNjc4Njk5NjA2LCJleHAiOjE2Nzg3MDEwNDZ9.SNLHaihQeir0yWNZ1gy4gnQM6Z8kXySnueYsouwVXuA\"") }, schema = @Schema(implementation = AuthenticationResponse.class))) })
	public ResponseEntity<AuthenticationResponse> register(
			@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Registration request.", required = true, content = @Content(examples = {
					@ExampleObject(name = "Register request with username and password.", value = "{\r\n"
							+ "  \"displayname\": \"Çağla Boynueğri\",\r\n" + "  \"username\": \"caglaboynuegri\",\r\n"
							+ "  \"password\": \"123456\"\r\n"
							+ "}", summary = "Minimal request") }, schema = @Schema(implementation = RegisterRequest.class))) @RequestBody RegisterRequest request) {
		log.info("Registration started..........");
		ResponseEntity<AuthenticationResponse> responseEntity = ResponseEntity
				.ok(authenticationService.register(request));
		log.info("Registration ended..........");
		return responseEntity;
	}

	@PostMapping("/authenticate")
	@Operation(summary = "Authenticate the user with jwt token", operationId = "authenticate", tags = {
			"authentication" }, responses = {
					@ApiResponse(responseCode = "200", description = "Succesfully authenticated", content = @Content(examples = {
							@ExampleObject(name = "Token generated with SHA-256 algorithm.", summary = "response", value = "  \"token\": \"eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhbGl2ZWxpIiwiaWF0IjoxNjc4Njk5NjA2LCJleHAiOjE2Nzg3MDEwNDZ9.SNLHaihQeir0yWNZ1gy4gnQM6Z8kXySnueYsouwVXuA\"") }, schema = @Schema(implementation = AuthenticationResponse.class))) })
	public ResponseEntity<AuthenticationResponse> authenticate(
			@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Authentication request.", required = true, content = @Content(examples = {
					@ExampleObject(name = "Register request with username and password.", value = "{\r\n"
							+ "  \"username\": \"caglaboynuegri\",\r\n" + "  \"password\": \"123456\"\r\n"
							+ "}", summary = "Minimal request") }, schema = @Schema(implementation = AuthenticationRequest.class))) @RequestBody AuthenticationRequest request) {
		try {
			log.info("Authentication started..........");
			AuthenticationResponse authenticationResponse = authenticationService.authenticate(request);
			ResponseEntity<AuthenticationResponse> ok = ResponseEntity.ok(authenticationResponse);
			log.info("Authentication token generated: " + authenticationResponse.getToken());
			return ok;
		} catch (BlogAppEntityNotFoundException e) {
			return ResponseEntity.of(Optional.empty());
		}
	}
}
