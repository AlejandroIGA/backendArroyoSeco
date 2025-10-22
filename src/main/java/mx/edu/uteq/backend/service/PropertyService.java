package mx.edu.uteq.backend.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import mx.edu.uteq.backend.dto.PropertyDTO;
import mx.edu.uteq.backend.model.Property;
import mx.edu.uteq.backend.model.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;
import mx.edu.uteq.backend.repository.PropertyRepository;
import mx.edu.uteq.backend.repository.UserRepository;

@Service
public class PropertyService {
    
    @Autowired
    private PropertyRepository propertyRepository;

    @Autowired 
    private UserRepository userRepository;

    public List<PropertyDTO> getProperties() {
        return propertyRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public Optional<PropertyDTO> getPropertyById(Long id){
        return propertyRepository.findById(id)
            .map(this::convertToDto);
    }

    private PropertyDTO convertToDto(Property property){
        PropertyDTO dto = new PropertyDTO();
        dto.setId(property.getId());
        dto.setOwnerId(property.getOwnerId() != null ? property.getOwnerId().getId() : null);
        dto.setName(property.getName());
        dto.setPricePerNight(property.getPricePerNight());
        dto.setLocation(property.getLocation());
        dto.setType(property.getType());
        dto.setKidsAllowed(property.getKidsAllowed());
        dto.setPetsAllowed(property.getPetsAllowed());
        dto.setNumberOfGuests(property.getNumberOfGuests());
        dto.setShowProperty(property.getShowProperty());
        dto.setDescription(property.getDescription());
        dto.setImagen(property.getImagen());
        return dto;
    }

    private Property convertoToEntity(PropertyDTO dto){
        Property property = new Property();
        property.setId(dto.getId());
        if (dto.getOwnerId() != null) {
            userRepository.findById(dto.getOwnerId())
            .ifPresent(property::setOwnerId);
        }
        property.setName(dto.getName());
        property.setPricePerNight(dto.getPricePerNight());
        property.setLocation(dto.getLocation());
        property.setType(dto.getType());
        property.setKidsAllowed(dto.getKidsAllowed());
        property.setPetsAllowed(dto.getPetsAllowed());
        property.setNumberOfGuests(dto.getNumberOfGuests());
        property.setShowProperty(dto.getShowProperty());
        property.setDescription(dto.getDescription());
        property.setImagen(dto.getImagen());
        return property;
    }

    @Transactional
    public void registerProperty(PropertyDTO dto){
        propertyRepository.save(convertoToEntity(dto));
    }

    @Transactional
    public void deleteProperty(Long id){
        Property property = propertyRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Propiedad no encontrada con el ID: " + id));
        propertyRepository.delete(property);
    }

    @Transactional
    public PropertyDTO updateProperty(Long id, PropertyDTO dto){
        Property property = propertyRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Propiedad no encontrada con ID: " + id));
        
        property.setName(dto.getName());
        property.setPricePerNight(dto.getPricePerNight());
        property.setLocation(dto.getLocation());
        property.setType(dto.getType());
        property.setKidsAllowed(dto.getKidsAllowed());
        property.setPetsAllowed(dto.getPetsAllowed());
        property.setNumberOfGuests(dto.getNumberOfGuests());
        property.setShowProperty(dto.getShowProperty());
        property.setDescription(dto.getDescription());
        property.setImagen(dto.getImagen());

        if (dto.getOwnerId() != null) {
            userRepository.findById(dto.getOwnerId())
            .ifPresent(property::setOwnerId);
        }

        Property updatedProperty = propertyRepository.save(property);

        return convertToDto(updatedProperty);
    }
}
