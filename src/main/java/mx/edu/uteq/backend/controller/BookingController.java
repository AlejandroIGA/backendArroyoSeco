package mx.edu.uteq.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/{id}")
    public ResponseEntity<BookingDTO> getById(@PathVariable Long id) {
        if (id == null) {
            return ResponseEntity.badRequest().build();
        }
        return bookingRepository.findById(id)
                .map(this::toDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/by-date")
    public ResponseEntity<?> findByDateRange(@RequestParam("from") String fromIso,
            @RequestParam("to") String toIso) {
        try {
            java.time.Instant from = java.time.Instant.parse(fromIso);
            java.time.Instant to = java.time.Instant.parse(toIso);
            java.util.Date fromDate = java.util.Date.from(from);
            java.util.Date toDate = java.util.Date.from(to);
            var list = bookingRepository.findByStartDateBetween(fromDate, toDate);
            var dtoList = list.stream().map(this::toDto).toList();
            return ResponseEntity.ok(dtoList);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Invalid date format. Use ISO-8601.");
        }
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<java.util.List<BookingDTO>> findByStatus(@PathVariable String status) {
        var list = bookingRepository.findByStatusIgnoreCase(status);
        var dtoList = list.stream().map(this::toDto).toList();
        return ResponseEntity.ok(dtoList);
    }

    // mapper
    private BookingDTO toDto(Booking b) {
        if (b == null)
            return null;
        Long propId = b.getProperty() != null ? b.getProperty().getId() : null;
        Long userId = b.getUser() != null ? b.getUser().getId() : null;
        return new BookingDTO(b.getId(), propId, userId, b.getStatus(), b.getStartDate(), b.getEndDate());
    }
}
