package scalefocus.blogapp.blogappwebflux.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {
  private static final String[] AUTH_WHITELIST = {
    // -- Swagger UI v3 (OpenAPI)
    "/v3/api-docs/**", "/v3/api-docs.yaml", "/actuator/**", "/webjars/swagger-ui/**", "/favicon.ico"
  };

  @Bean
  public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
    http.authorizeExchange(
            exchanges ->
                exchanges.pathMatchers(AUTH_WHITELIST).permitAll().anyExchange().authenticated())
        .oauth2ResourceServer()
        .jwt();
    return http.build();
  }
}
