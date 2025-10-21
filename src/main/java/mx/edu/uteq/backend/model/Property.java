package mx.edu.uteq.backend.model;


import java.util.Map;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapKeyColumn;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="property")
@Getter
@Setter

public class Property{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "user_profile_id")
    private User ownerId;
    
    private String name;
    private Double pricePerNight;

    @ElementCollection
    @CollectionTable(name = "property_location", joinColumns = @JoinColumn(name = "property_id"))
    @MapKeyColumn(name = "location_key")
    @Column(name = "location_value", columnDefinition = "json")
    private Map<String, Object> location;

    private String type;
    private Boolean kidsAllowed;
    private Boolean petsAllowed;
    private Integer numberOfGuests;
    private Boolean showProperty;

    @ElementCollection
    @CollectionTable(name = "property_descriptions", joinColumns = @JoinColumn(name = "property_id"))
    @MapKeyColumn(name = "description_key")
    @Column(name = "description_value", columnDefinition = "json")
    private Map<String, Object> description;

}

