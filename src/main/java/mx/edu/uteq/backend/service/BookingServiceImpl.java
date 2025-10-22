package mx.edu.uteq.backend.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import mx.edu.uteq.backend.dto.BookingRequestDTO;
import mx.edu.uteq.backend.dto.BookingResponseDTO;
import mx.edu.uteq.backend.model.Booking;
import mx.edu.uteq.backend.model.Property;
import mx.edu.uteq.backend.model.User;
import mx.edu.uteq.backend.repository.BookingRepository;
import mx.edu.uteq.backend.repository.PropertyRepository;
import mx.edu.uteq.backend.repository.UserRepository;

@Service
public class BookingServiceImpl implements BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private PropertyRepository propertyRepository;

    @Autowired
    private UserRepository userRepository;

    // ----- MÉTODOS CRUD -----

    @Override
    public BookingResponseDTO createBooking(BookingRequestDTO requestDTO) {
        // 1. Buscar las entidades relacionadas
    Property property = propertyRepository.findById(requestDTO.getPropertyId())
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Property not found with id: " + requestDTO.getPropertyId()));

    User user = userRepository.findById(requestDTO.getUserId())
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found with id: " + requestDTO.getUserId()));

        // 2. Lógica de negocio (Ej: validar que las fechas no se solapen)
        // ... (Tu lógica aquí) ...

        // 3. Crear la entidad Booking
        Booking booking = new Booking();
        booking.setStatus(requestDTO.getStatus() != null ? requestDTO.getStatus() : "PENDING"); // Valor por defecto
        booking.setStartDate(requestDTO.getStartDate());
        booking.setEndDate(requestDTO.getEndDate());
        booking.setProperty(property);
        booking.setUser(user);

        // 4. Guardar en la BD
        Booking savedBooking = bookingRepository.save(booking);

        // 5. Mapear a DTO de respuesta y devolver
        return convertToResponseDTO(savedBooking);
    }

    @Override
    public BookingResponseDTO getBookingById(Long id) {
        Booking booking = findBookingById(id);
        return convertToResponseDTO(booking);
    }

    @Override
    public List<BookingResponseDTO> getAllBookings() {
        return bookingRepository.findAll()
                .stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public BookingResponseDTO updateBooking(Long id, BookingRequestDTO requestDTO) {
        // 1. Encontrar el booking existente
        Booking existingBooking = findBookingById(id);

        // 2. (Opcional) Buscar las relaciones si pueden cambiar
    Property property = propertyRepository.findById(requestDTO.getPropertyId())
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Property not found"));
    User user = userRepository.findById(requestDTO.getUserId())
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        // 3. Actualizar los campos
        existingBooking.setStatus(requestDTO.getStatus());
        existingBooking.setStartDate(requestDTO.getStartDate());
        existingBooking.setEndDate(requestDTO.getEndDate());
        existingBooking.setProperty(property);
        existingBooking.setUser(user);

        // 4. Guardar
        Booking updatedBooking = bookingRepository.save(existingBooking);

        // 5. Devolver DTO
        return convertToResponseDTO(updatedBooking);
    }

    @Override
    public void deleteBooking(Long id) {
        Booking booking = findBookingById(id); // Asegurarse de que existe
        bookingRepository.delete(booking);
    }


    // ----- MÉTODOS PRIVADOS DE AYUDA -----

    // Mapeo manual de Entidad a DTO de Respuesta
    private BookingResponseDTO convertToResponseDTO(Booking booking) {
        BookingResponseDTO dto = new BookingResponseDTO();
        dto.setId(booking.getId());
        dto.setStatus(booking.getStatus());
        dto.setStartDate(booking.getStartDate());
        dto.setEndDate(booking.getEndDate());
        dto.setPropertyId(booking.getProperty().getId());
        dto.setUserId(booking.getUser().getId());
        // Si usaras DTOs anidados, los mapearías aquí
        return dto;
    }

    // Método reutilizable para encontrar o fallar
    private Booking findBookingById(Long id) {
    return bookingRepository.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Booking not found with id: " + id));
    }
}