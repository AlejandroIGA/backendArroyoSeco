package mx.edu.uteq.backend.model;

import java.util.List;
import java.util.Map;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapKeyColumn;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name="property")
@Getter
@Setter
public class Property {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // ⭐ IMPORTANTE: Este es el campo que referencia a User
    // Aunque se llama "ownerId", es de tipo User (no Long)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_profile_id", nullable = false)
    @JsonIgnore  // ⭐ Evita recursión infinita en JSON
    private User ownerId;
    
    private String name;
    private Double pricePerNight;

    @ElementCollection
    @CollectionTable(name = "property_location", joinColumns = @JoinColumn(name = "property_id"))
    @MapKeyColumn(name = "location_key")
    @Column(name = "location_value", columnDefinition = "json")
    private Map<String, Object> location;

    private String type;
    
    @Column(columnDefinition = "TINYINT(1)")
    private Boolean kidsAllowed;
    
    @Column(columnDefinition = "TINYINT(1)")
    private Boolean petsAllowed;
    
    private Integer numberOfGuests;
    
    @Column(columnDefinition = "TINYINT(1)")
    private Boolean showProperty;

    @ElementCollection
    @CollectionTable(name = "property_descriptions", joinColumns = @JoinColumn(name = "property_id"))
    @MapKeyColumn(name = "description_key")
    @Column(name = "description_value", columnDefinition = "json")
    private Map<String, Object> description;

    @ElementCollection
    @CollectionTable(name = "property_image", joinColumns = @JoinColumn(name = "property_id"))
    @Column(name = "image_url")
    private List<String> imagen;
}