package mx.edu.uteq.backend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import mx.edu.uteq.backend.dto.PropertyDTO;
import mx.edu.uteq.backend.service.PropertyService;
import org.springframework.web.bind.annotation.PutMapping;


@RestController
@RequestMapping("/api/properties")
public class PropertyController {
    
    
    private final PropertyService propertyService;

    public PropertyController(PropertyService propertyService){
        this.propertyService = propertyService;
    }

    

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

    @PostMapping("/register")
    public ResponseEntity<String> registerProperty(@RequestBody PropertyDTO propertyRegister){
        try{
            propertyService.registerProperty(propertyRegister);
            return ResponseEntity.status(HttpStatus.CREATED).body("Propiedad registrada con exito");
        }catch(IllegalArgumentException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }catch(Exception e){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al intentar registrar propiedad");
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteProperty(@RequestBody Long id){
        try{
            propertyService.deleteProperty(id);
            return ResponseEntity.ok("Propiedad eliminada con exito");
        }catch(IllegalArgumentException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }catch(Exception e){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al intentar eliminar propiedad");
        }
    }

    @PutMapping("update/{id}")
    public ResponseEntity<?> updateProperty(@PathVariable String id, @RequestBody PropertyDTO dto) {
        try{
            PropertyDTO updatedProperty = propertyService.updateProperty(id, dto);
            return ResponseEntity.ok(updatedProperty);
        }catch(IllegalArgumentException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }catch(Exception e){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al intentar actualizar propiedad");
        }
}
