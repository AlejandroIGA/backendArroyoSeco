package mx.edu.uteq.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import mx.edu.uteq.backend.dto.RegisterRequestDTO;
import mx.edu.uteq.backend.model.User;
import mx.edu.uteq.backend.repository.UserRepository;
import java.util.Date;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public void registerUser(RegisterRequestDTO request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("El correo electrónico ya está en uso.");
        }

        User newUser = new User();
        newUser.setEmail(request.getEmail());

        // Guardamos la contraseña en texto plano (SOLO PARA ESTA PRUEBA)
        newUser.setPsw(request.getPassword());

        newUser.setRole(request.getRole() != null ? request.getRole() : "USER");
        newUser.setCreationDate(new Date());
        newUser.setLogDate(null);
        newUser.setUserProfile(null);

        userRepository.save(newUser);
    }
}
