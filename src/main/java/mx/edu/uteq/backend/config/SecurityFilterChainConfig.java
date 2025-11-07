package mx.edu.uteq.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import org.springframework.context.annotation.Lazy;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityFilterChainConfig {

    private final AuthenticationManager authenticationManager;

    // Esto es CORRECTO. @Lazy rompe el ciclo de dependencia.
    public SecurityFilterChainConfig(@Lazy AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @Bean
    @Order(1) 
    public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http) throws Exception {
        
        OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(http);

        http.getConfigurer(OAuth2AuthorizationServerConfigurer.class)
            .tokenEndpoint(tokenEndpoint -> tokenEndpoint
                .authenticationProvider((AuthenticationProvider) this.authenticationManager) // <-- ¡LA CONEXIÓN!
            );
        
        http.cors(Customizer.withDefaults());
        http.csrf(csrf -> csrf
            .ignoringRequestMatchers("/oauth2/**") // Ignora CSRF para los endpoints de OAuth
        );
        
        http.exceptionHandling(exceptions -> exceptions
            .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
        );
        
        return http.build();
    }

    @Bean
    @Order(2) 
    public SecurityFilterChain apiSecurityFilterChain(HttpSecurity http) throws Exception {
        http
            .cors(Customizer.withDefaults())
            .csrf(AbstractHttpConfigurer::disable) 
            
            .authorizeHttpRequests(authorize -> authorize
                // Endpoints públicos de autenticación
                .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/user/register").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/auth/exchange-code").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/properties/**").permitAll()
                
                // APIs protegidas por roles - requieren JWT
                .requestMatchers("/api/properties/**").hasAuthority("PROPIETARIO") 
                .requestMatchers("/api/bookings/**").hasAnyAuthority("VISITANTE","PROPIETARIO") 
                .requestMatchers("/api/user-profiles/**").hasAnyAuthority("VISITANTE", "PROPIETARIO")
                
                // Permite acceso al /error para debugging
                .requestMatchers("/error").permitAll()
                
                .anyRequest().authenticated()
            )
            
            // Resource Server - Validar JWTs en las peticiones a /api/**
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
            )
            .exceptionHandling(exceptions -> exceptions
                .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
            )
            .logout(logout -> logout
            .logoutUrl("/api/auth/logout") 
            .logoutSuccessUrl("https://alojando.duckdns.org/login") 
            .invalidateHttpSession(true) 
            .deleteCookies("JSESSIONID")
            .permitAll()
        );
        return http.build();
    }

    // Configuración CORS
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList(
            "http://localhost:5173", 
            "http://localhost:4173", 
            "https://alojando.duckdns.org",
            "https://localhost"
        )); 
        
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true); 
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    // Convertidor JWT - Extrae authorities del token
    private JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        grantedAuthoritiesConverter.setAuthoritiesClaimName("authorities");
        grantedAuthoritiesConverter.setAuthorityPrefix("");

        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);
        return jwtAuthenticationConverter;
    }
}
