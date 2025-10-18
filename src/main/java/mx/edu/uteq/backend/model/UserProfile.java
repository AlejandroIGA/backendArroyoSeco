package mx.edu.uteq.backend.model;

import jakarta.persistence.*; 

@Entity
@Table(name = "user_profile")
public class UserProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) 
    private Long id;

    private String name;

    @Column(name = "last_name")
    private String lastName;
    
    private String cellphone;
    private String country;
    
}