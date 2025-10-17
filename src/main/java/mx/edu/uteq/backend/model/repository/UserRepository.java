package mx.edu.uteq.backend.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import mx.edu.uteq.backend.model.entity.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmailAndPsw(String email, String psw);
}