package mx.edu.uteq.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import mx.edu.uteq.backend.model.User;
import mx.edu.uteq.backend.repository.UserRepository;
import mx.edu.uteq.backend.dto.RegisterRequestDTO;
import mx.edu.uteq.backend.service.UserService;


import java.util.Optional;

@RestController
@RequestMapping("/api/user")
public class UserController {

    // 2. DECLARAR EL SERVICIO COMO UN CAMPO FINAL Y PRIVADO
    private final UserService userService;
    @Autowired
    private UserRepository userRepository;

    // 3. USAR INYECCIÓN POR CONSTRUCTOR (LA MEJOR PRÁCTICA)
    // Spring se encargará de pasar una instancia de UserService aquí.
    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody User user) {
        
        Optional<User> userOptional = userRepository.findByEmailAndPsw(
            user.getEmail(),
            user.getPsw()
        );

        if (userOptional.isPresent()) {
            User foundUser = userOptional.get();
            
            return ResponseEntity.ok("Bienvenido, tu rol es: " + foundUser.getRole());
            
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciales inválidas.");
        }
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequestDTO registerRequest) {
        try {
            // Llama al método en el servicio, que contiene toda la lógica de negocio.
            userService.registerUser(registerRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body("Usuario registrado exitosamente.");
        } catch (IllegalArgumentException e) {
            // Captura errores específicos como "email ya en uso".
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            // Captura cualquier otro error inesperado.
            e.printStackTrace(); // Muy útil para ver el error completo en la consola.
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno al registrar el usuario.");
        }
    }
}