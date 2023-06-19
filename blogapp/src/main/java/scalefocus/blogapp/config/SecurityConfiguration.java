package scalefocus.blogapp.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@Profile({"native","default"})
public class SecurityConfiguration {

  private static final String[] AUTH_WHITELIST = {
    // -- Swagger UI v3 (OpenAPI)
    "/v3/api-docs/**", "/v3/api-docs.yaml", "/swagger-ui/**", "/actuator/**", "/validuser/**"
  };

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.authorizeHttpRequests(
            (authz) ->
                authz.requestMatchers(AUTH_WHITELIST).permitAll().anyRequest().authenticated())
        .oauth2ResourceServer()
        .jwt();
    return http.build();
  }
}
