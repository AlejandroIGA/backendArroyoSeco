package mx.edu.uteq.backend.controller;

import mx.edu.uteq.backend.service.JwtService;
import mx.edu.uteq.backend.service.UserProfileService;

import org.springframework.beans.factory.annotation.Autowired;
import mx.edu.uteq.backend.model.UserProfile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/api/user-profiles")
public class UserProfileController {

    @Autowired
    private UserProfileService userProfileService;

    @Autowired
    private JwtService jwtService;

    // Este es el que trae el perfil de id de usuario especifico
    @GetMapping("/profile")
    public ResponseEntity<UserProfile> getCurrentUserProfile() {
        Long currentUserId = jwtService.getCurrentUserId();
        UserProfile profile = userProfileService.getUserProfileByUserId(currentUserId);
        return ResponseEntity.ok(profile);
    }

    //create
    @PostMapping("/{userId}/profile")
    public ResponseEntity<UserProfile> createProfileForUser(@PathVariable Long userId, @RequestBody UserProfile profileDetails) {
        UserProfile newProfile = userProfileService.createProfileForUser(userId, profileDetails);
        return new ResponseEntity<>(newProfile, HttpStatus.CREATED);
    }
    
    /*read
    @GetMapping("/{userId}/profile")
    public ResponseEntity<UserProfile> getUserProfile(@PathVariable Long userId) {
        UserProfile profile = userProfileService.getUserProfileByUserId(userId);
        return ResponseEntity.ok(profile);
    }
        */

    //update
    @PutMapping("/{userId}/profile")
    public ResponseEntity<UserProfile> updateUserProfile(@PathVariable Long userId, @RequestBody UserProfile profileDetails) {
        UserProfile updatedProfile = userProfileService.updateUserProfileByUserId(userId, profileDetails);
        return ResponseEntity.ok(updatedProfile);
    }
    //delate
    @DeleteMapping("/{userId}/profile")
    public ResponseEntity<Void> deleteUserProfile(@PathVariable Long userId) {
        userProfileService.deleteProfileByUserId(userId);
        return ResponseEntity.noContent().build();
    }

}

