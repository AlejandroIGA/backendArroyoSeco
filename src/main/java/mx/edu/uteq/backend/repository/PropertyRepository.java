package mx.edu.uteq.backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import mx.edu.uteq.backend.model.Property;

@Repository
public interface PropertyRepository extends JpaRepository<Property, Long> {
    Optional<Property> findByNombre(String nombre);
    Optional<Property> findById(Long id);
}
