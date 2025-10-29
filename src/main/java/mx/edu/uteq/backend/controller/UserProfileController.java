package mx.edu.uteq.backend.controller;

import mx.edu.uteq.backend.repository.UserProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import mx.edu.uteq.backend.model.UserProfile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/api/user-profiles")
public class UserProfileController {

    @Autowired
    private UserProfileRepository userProfileRepository;

    @Autowired
    private mx.edu.uteq.backend.service.UserProfileService userProfileService;

    //create
    @PostMapping("/{userId}/profile")
    public ResponseEntity<UserProfile> createProfileForUser(@PathVariable Long userId, @RequestBody UserProfile profileDetails) {
        UserProfile newProfile = userProfileService.createProfileForUser(userId, profileDetails);
        return new ResponseEntity<>(newProfile, HttpStatus.CREATED);
    }
    //read
    @GetMapping("/{userId}/profile")
    public ResponseEntity<UserProfile> getUserProfile(@PathVariable Long userId) {
        UserProfile profile = userProfileService.getUserProfileByUserId(userId);
        return ResponseEntity.ok(profile);
    }
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

