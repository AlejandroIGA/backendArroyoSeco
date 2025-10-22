package mx.edu.uteq.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import mx.edu.uteq.backend.model.Booking;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
}
