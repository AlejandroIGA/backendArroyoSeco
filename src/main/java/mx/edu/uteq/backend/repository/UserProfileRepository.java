package mx.edu.uteq.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import mx.edu.uteq.backend.model.UserProfile;

public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {

}