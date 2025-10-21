package mx.edu.uteq.backend.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import mx.edu.uteq.backend.dto.PropertyDTO;
import mx.edu.uteq.backend.model.Property;
import mx.edu.uteq.backend.model.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        return dto;
    }

    private Property convertoToEntity(PropertyDTO dto){
        Property property = new Property();
        property.setId(dto.getId());
        if(dto.getOwnerId() != null){
            Optional<User> owner = userRepository.findById(dto.getOwnerId());
            owner.ifPresent(property::setOwnerId);
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
        return property;
    }
}
