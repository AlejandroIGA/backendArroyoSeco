package mx.edu.uteq.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import mx.edu.uteq.backend.model.entity.User;
import mx.edu.uteq.backend.model.repository.UserRepository;

import java.util.Optional;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserRepository userRepository;

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
}