package scalefocus.blogapp.auth;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import scalefocus.blogapp.config.JwtService;
import scalefocus.blogapp.domain.BlogUser;
import scalefocus.blogapp.domain.Role;
import scalefocus.blogapp.exceptions.BlogAppEntityNotFoundException;
import scalefocus.blogapp.repository.BlogUserRepository;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
	private final BlogUserRepository blogUserRepository;

	private final PasswordEncoder passwordEncoder;

	private final JwtService jwtService;

	private final AuthenticationManager authenticationManager;

	public AuthenticationResponse register(RegisterRequest request) {
		BlogUser blogUser = new BlogUser().setPassword(passwordEncoder.encode(request.getPassword()))
				.setUsername(request.getUsername()).setDisplayname(request.getDisplayname()).setRole(Role.USER);
		blogUserRepository.save(blogUser);
		var jwtToken = jwtService.generateToken(blogUser);

		return AuthenticationResponse.builder().token(jwtToken).build();
	}

	public AuthenticationResponse authenticate(AuthenticationRequest request) throws BlogAppEntityNotFoundException {
		authenticationManager
				.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
		var user = blogUserRepository.findFirstByUsername(request.getUsername()).orElseThrow(
				() -> new BlogAppEntityNotFoundException(BlogUser.class, "username", request.getUsername()));
		var jwtToken = jwtService.generateToken(user);

		return AuthenticationResponse.builder().token(jwtToken).build();

	}

}
