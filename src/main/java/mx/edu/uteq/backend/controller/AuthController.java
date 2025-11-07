package mx.edu.uteq.backend.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import mx.edu.uteq.backend.dto.LoginRequest;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException; 
import org.slf4j.Logger;                // <-- AÑADIR
import org.slf4j.LoggerFactory;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class); // <-- AÑADIR

    private final AuthenticationManager authenticationManager;
    private final JwtDecoder jwtDecoder; 

    @Value("${oauth.client-id}")
    private String clientId;

    @Value("${oauth.client-secret}")
    private String clientSecret;
    
    @Value("${oauth.token-uri}")
    private String tokenUri;

    @Value("${oauth.redirect-uri}")
    private String redirectUri;

    public AuthController(AuthenticationManager authenticationManager, JwtDecoder jwtDecoder) {
        this.authenticationManager = authenticationManager;
        this.jwtDecoder = jwtDecoder;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest, HttpServletRequest request) {
        logger.info("Procesando solicitud de login para: {}", loginRequest.getEmail()); // <-- AÑADIR
        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    loginRequest.getEmail(),
                    loginRequest.getPassword()
                )
            );
            logger.info("PASO 1: Autenticación de USUARIO exitosa para: {}", authentication.getName()); // <-- AÑADIR
            SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
            securityContext.setAuthentication(authentication);
            SecurityContextHolder.setContext(securityContext);

            HttpSession session = request.getSession(true);
            session.setAttribute(
                HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                securityContext
            );

            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            
            String auth = clientId + ":" + clientSecret;
            String encodedAuth = java.util.Base64.getEncoder().encodeToString(auth.getBytes());
            headers.setBasicAuth(encodedAuth); 

            MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
            body.add("grant_type", "password");
            body.add("username", loginRequest.getEmail());   // Usamos el email/pass del request
            body.add("password", loginRequest.getPassword());
            body.add("scope", "read write"); // O los scopes que necesites
            
            HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(body, headers);
            logger.info("PASO 2: Solicitando token de OAuth2 a [{}] para el cliente [{}]", tokenUri, clientId); // <-- AÑADIR
            ResponseEntity<Map> tokenResponse = restTemplate.exchange(
                tokenUri,
                HttpMethod.POST,
                entity,
                Map.class
            );
            logger.info("PASO 2: Token OAuth2 obtenido exitosamente."); // <-- AÑADIR
            Map<String, Object> responseBody = tokenResponse.getBody();
            if (responseBody == null) {
                throw new RuntimeException("El cuerpo de la respuesta de tokens está vacío.");
            }

            // Extraer rol
            String accessToken = (String) responseBody.get("access_token");
            String userRole = extractRoleFromJwt(accessToken);
            responseBody.put("user_role", userRole);
            
            // Añadir info de sesión (útil para la web)
            responseBody.put("authenticated", true);
            responseBody.put("sessionId", session.getId());

            return ResponseEntity.ok(responseBody);
            
        } catch (AuthenticationException | HttpClientErrorException e) {
            // Captura si la autenticación falla O si el "password grant" falla
            logger.error("FALLO EL LOGIN para {}: {}", loginRequest.getEmail(), e.getMessage()); // <-- AÑADIR

            // Esta lógica nos dirá exactamente qué falló
            if (e instanceof HttpClientErrorException) {
                HttpClientErrorException hce = (HttpClientErrorException) e;
                logger.error("Detalle del fallo (PASO 2 - Cliente): Status Code: {}", hce.getStatusCode());
                // ESTE ES EL LOG MÁS IMPORTANTE:
                logger.error("Detalle del fallo (PASO 2 - Cliente): Response Body: {}", hce.getResponseBodyAsString()); // <-- AÑADIR
            } else if (e instanceof AuthenticationException) {
                logger.warn("Detalle del fallo (PASO 1 - Usuario): Autenticación de usuario fallida."); // <-- AÑADIR
            }
            Map<String, Object> error = new HashMap<>();
            error.put("authenticated", false);
            error.put("message", "Credenciales inválidas");
            
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        } catch (Exception e) {
            logger.error("Error inesperado durante el login para {}", loginRequest.getEmail(), e); // <-- MEJORAR
            // Captura cualquier otro error (ej. RestTemplate fallando)
             Map<String, Object> error = new HashMap<>();
             error.put("message", "Error al procesar el login: " + e.getMessage());
             return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @GetMapping("/exchange-code") 
    public ResponseEntity<?> exchangeCode(@RequestParam String code, HttpServletRequest request) {
        RestTemplate restTemplate = new RestTemplate();
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        
        String auth = clientId + ":" + clientSecret;
        String encodedAuth = java.util.Base64.getEncoder().encodeToString(auth.getBytes());
        headers.setBasicAuth(encodedAuth); 

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("code", code);
        body.add("redirect_uri", redirectUri);
        
        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(body, headers);
        
        try {
            ResponseEntity<Map> response = restTemplate.exchange(
                tokenUri,
                HttpMethod.POST,
                entity,
                Map.class
            );
            
            Map<String, Object> responseBody = response.getBody();
            String accessToken = (String) responseBody.get("access_token");
            String userRole = extractRoleFromJwt(accessToken);
            responseBody.put("user_role", userRole); 
            
            return ResponseEntity.ok(responseBody);

        } catch (Exception e) {
            System.err.println("Error al canjear código o decodificar token: " + e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to exchange code for token or get role");
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }
    }

    private String extractRoleFromJwt(String accessToken) {
        try {
            Jwt jwt = jwtDecoder.decode(accessToken);
            List<String> authorities = jwt.getClaimAsStringList("authorities");

            if (authorities != null && !authorities.isEmpty()) {
                return authorities.get(0);
            }
            return "DEFAULT_USER"; 

        } catch (Exception e) {
            System.err.println("Error al decodificar JWT: " + e.getMessage());
            return "UNKNOWN";
        }
    }
}
