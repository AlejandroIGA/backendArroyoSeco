package mx.edu.uteq.backend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import mx.edu.uteq.backend.dto.PropertyDTO;
import mx.edu.uteq.backend.service.PropertyService;

@RestController
@RequestMapping("/api/properties")
public class PropertyController {
    
    @Autowired
    private PropertyService propertyService;

    @GetMapping
    public List<PropertyDTO> getProperties() {
        return propertyService.getProperties();
    }

    @GetMapping("/id")
    public ResponseEntity<PropertyDTO> getPropertyById(@PathVariable Long id){
        return propertyService.getPropertyById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());

    }
}
