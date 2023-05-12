package scalefocus.blogapp.config;

import static org.springframework.security.config.Customizer.withDefaults;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.server.ServerWebExchange;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {
  private static final String[] AUTH_WHITELIST = {
    // -- Swagger UI v3 (OpenAPI)
    "/webjars/swagger-ui/**",
    "/v3/api-docs/**",
    "/v3/api-docs.yaml",
    "/blogapp/v3/api-docs",
    "/file/v3/api-docs",
    "/usermanagement/v3/api-docs",
    // other public endpoints of your API may be appended to this array
    "/register"
  };

  @Bean
  public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
    http.authorizeExchange(
            exchanges ->
                exchanges.pathMatchers(AUTH_WHITELIST).permitAll().anyExchange().authenticated())
        .oauth2Login(withDefaults())
        .csrf(csrf -> csrf.disable());
    return http.build();
  }
}
