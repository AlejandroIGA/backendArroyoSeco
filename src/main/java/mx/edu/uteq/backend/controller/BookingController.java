package mx.edu.uteq.backend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import mx.edu.uteq.backend.dto.BookingRequestDTO;
import mx.edu.uteq.backend.dto.BookingResponseDTO;
import mx.edu.uteq.backend.service.BookingService;

@RestController
@RequestMapping("/api/bookings") // Ruta base para todas las operaciones de bookings
public class BookingController {

    @Autowired
    private BookingService bookingService;

    //GET avanzado busqueda por fechas, propiedad, estatus y usuario
    //requiere los parametros, startDate, endDate, propertyId, status, userId opcionales
    //si no existe ningun parametro, devuelve todos los bookings
    @GetMapping("/search")
    public ResponseEntity<List<BookingResponseDTO>> searchBookings(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) Long propertyId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long userId) {
        List<BookingResponseDTO> bookings = bookingService.searchBookings(startDate, endDate, propertyId, status, userId);
        return ResponseEntity.ok(bookings);
    }

    // CREATE
    @PostMapping
    public ResponseEntity<BookingResponseDTO> createBooking(@RequestBody BookingRequestDTO requestDTO) {
        BookingResponseDTO newBooking = bookingService.createBooking(requestDTO);
        return new ResponseEntity<>(newBooking, HttpStatus.CREATED); 
    }

    // READ (By ID)
    @GetMapping("/{id}")
    public ResponseEntity<BookingResponseDTO> getBookingById(@PathVariable Long id) {
        BookingResponseDTO booking = bookingService.getBookingById(id);
        return ResponseEntity.ok(booking);
    }

    // READ (All)
    @GetMapping
    public ResponseEntity<List<BookingResponseDTO>> getAllBookings() {
        List<BookingResponseDTO> bookings = bookingService.getAllBookings();
        return ResponseEntity.ok(bookings); 
    }

    // UPDATE solo cambia el estado
    @PutMapping("/{id}")
    public ResponseEntity<BookingResponseDTO> updateBooking(@PathVariable Long id, @RequestBody BookingRequestDTO requestDTO) {
        BookingResponseDTO updatedBooking = bookingService.updateBooking(id, requestDTO);
        return ResponseEntity.ok(updatedBooking);
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteBooking(@PathVariable Long id) {
        bookingService.deleteBooking(id);
        //mensaje de respuesta con texto "borrado correctamente"
        return ResponseEntity.ok("Borrado correctamente");
    }
}