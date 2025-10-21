package mx.edu.uteq.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import mx.edu.uteq.backend.dto.BookingDTO;
import mx.edu.uteq.backend.model.Booking;
import mx.edu.uteq.backend.repository.BookingRepository;
import mx.edu.uteq.backend.repository.PropertyRepository;
import mx.edu.uteq.backend.repository.UserRepository;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final PropertyRepository propertyRepository;

    public BookingController(BookingRepository bookingRepository,
                             UserRepository userRepository,
                             PropertyRepository propertyRepository) {
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.propertyRepository = propertyRepository;
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerBooking(@RequestBody BookingDTO bookingRequest) {
        try {
            if (bookingRequest.getPropertyId() == null || bookingRequest.getUserId() == null) {
                return ResponseEntity.badRequest().body("Property ID and User ID are required.");
            }

            // validar existencia
            var prop = propertyRepository.findById(bookingRequest.getPropertyId())
                    .orElseThrow(() -> new IllegalArgumentException("Property not found"));
            var user = userRepository.findById(bookingRequest.getUserId())
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));

            Booking booking = new Booking();
            // no setear id si la BD lo genera
            booking.setProperty(prop);
            booking.setUser(user);
            booking.setStatus(bookingRequest.getStatus());
            booking.setStartDate(bookingRequest.getStartDate());
            booking.setEndDate(bookingRequest.getEndDate());

            bookingRepository.save(booking);

            return ResponseEntity.status(201).body("Booking registered successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error interno del servidor");
        }
    }
}
