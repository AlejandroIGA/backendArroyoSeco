package mx.edu.uteq.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import mx.edu.uteq.backend.dto.RegisterRequestDTO;
import mx.edu.uteq.backend.service.UserService;

import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

   /*  @PostMapping("/login")
public ResponseEntity<?> login(@RequestBody User user) {
    try {
        User loggedUser = userService.loginUser(user.getEmail(), user.getPassword());
        Map<String, Object> response = new HashMap<>();
        response.put("mensaje", "Bienvenido");
        response.put("id", loggedUser.getId());
        response.put("rol", loggedUser.getRole());

        return ResponseEntity.ok(response);

    } catch (RuntimeException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", e.getMessage()));
    }
} */

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequestDTO registerRequest) {
    try {
        userService.registerUser(registerRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body("Usuario registrado exitosamente.");
    } catch (IllegalArgumentException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    } catch (Exception e) {
        e.printStackTrace(); 
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno al registrar el usuario.");
    }
}

    // -------------------------------------- Restablecer contraseña -----------------------------------------

    @PostMapping("/reset")
    public ResponseEntity<?> sendResetCode(@RequestBody Map<String, String> request) {
        String email = request.get("email");

        if (email == null || email.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", "El email es obligatorio"));
        }

        try {
            String result = userService.sendResetCode(email);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.ok("Código de verificación enviado");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("No se pudo enviar el código de verificación");
        }
    }


    @PostMapping("/verify-code")
    public ResponseEntity<String> verifyCode(@RequestBody Map<String, String> payload) {
    String email = payload.get("email");
    String code = payload.get("code");
    boolean verified = userService.verifyCode(email, code);

    if(verified) {
        return ResponseEntity.ok("Código verificado correctamente");
        } else {
        return ResponseEntity.badRequest().body("Código inválido");
        }
    }


    @PostMapping("/reset-password")
public ResponseEntity<String> resetPassword(@RequestBody Map<String, String> payload) {
    String email = payload.get("email");
    String code = payload.get("code");
    String newPassword = payload.get("newPassword");

    if(email == null || code == null || newPassword == null) {
        return ResponseEntity.badRequest().body("Todos los campos son obligatorios");
    }

    try {
        String mensaje = userService.resetPassword(email, code, newPassword);
        return ResponseEntity.ok(mensaje);
    } catch (RuntimeException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }
}

// --------------------------------------------------------------------------------------------------------------

}