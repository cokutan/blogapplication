package scalefocus.blogapp.config;

import static org.springframework.security.config.Customizer.withDefaults;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.gateway.config.GlobalCorsProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebFluxSecurity
@EnableConfigurationProperties(value = {GlobalCorsProperties.class})
@Slf4j
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
        .csrf(csrf -> csrf.disable())
        .cors();
    http.oauth2ResourceServer().jwt();
    return http.build();
  }

  @Bean
  @Order(Ordered.HIGHEST_PRECEDENCE)
  @RefreshScope
  public CorsWebFilter corsWebFilter(CorsConfigurationSource corsConfigurationSource) {
    return new CorsWebFilter(corsConfigurationSource);
  }

  @Bean
  public CorsConfigurationSource corsConfigurationSource(
      GlobalCorsProperties globalCorsProperties) {
    var source = new UrlBasedCorsConfigurationSource();
    globalCorsProperties.getCorsConfigurations().forEach(source::registerCorsConfiguration);
    return source;
  }

  @PostConstruct
  public void postConstruct() {
    log.info("Starting Gateway Security Auto Configuration");
  }
}
