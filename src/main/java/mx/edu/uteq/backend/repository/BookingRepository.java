package mx.edu.uteq.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Date;

import mx.edu.uteq.backend.model.Booking;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByUserId(Long userId);

    List<Booking> findByPropertyId(Long propertyId);

    List<Booking> findByStatus(String status);

    // case-insensitive lookup convenience
    List<Booking> findByStatusIgnoreCase(String status);

    List<Booking> findByStartDateBetween(Date from, Date to);
}
