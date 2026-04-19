package com.anteaters.boggle.controller;

import com.anteaters.boggle.service.UserRegulationService;
import com.anteaters.boggle.entity.User;
import com.anteaters.boggle.repository.UserRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import java.util.Map;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
public class UserProfileController {
    private final UserRegulationService userService;
    private final UserRepository userRepository;

    public UserProfileController(UserRegulationService userService, UserRepository userRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
    }

    /**
     * Fetch profile info for a logged-in user.
     * Usage: GET /api/user/profile?username=Rae
     */
    @GetMapping("/api/user/profile")
    public ResponseEntity<Map<String, Object>> getProfile(@RequestParam String username) {
        if (!userService.isLoggedIn(username)) {
            return ResponseEntity.status(401).body(Map.of("error", "Not logged in"));
        }
        User user = userService.getUser(username);
        String picture = user.getProfilePicture() != null ? user.getProfilePicture() : "";
        return ResponseEntity.ok(Map.of(
                "username", user.getUsername(),
                "matchesWon", user.getMatchesWon(),
                "highScore", user.getHighScore(),
                "profilePicture", picture
        ));
    }

    /**
     * Upload a profile picture as base64.
     * Usage: POST /api/user/profile/picture?username=Rae
     */
    @PostMapping("/api/user/profile/picture")
    public ResponseEntity<Map<String, Object>> uploadPicture(
            @RequestParam String username,
            @RequestBody Map<String, String> body) {
        if (!userService.isLoggedIn(username)) {
            return ResponseEntity.status(401).body(Map.of("error", "Not authenticated"));
        }
        String imageData = body.get("imageData");
        if (imageData == null || imageData.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "No image data provided"));
        }
        userRepository.findById(username).ifPresent(user -> {
            user.setProfilePicture(imageData);
            userRepository.save(user);
        });
        return ResponseEntity.ok(Map.of("status", true));
    }

    /**
     * Submit a final score; updates highest_score if the new score is higher.
     * Usage: PUT /api/user/score?username=Rae&score=450
     */
    @PutMapping("/api/user/score")
    public ResponseEntity<Map<String, Object>> submitScore(
            @RequestParam String username,
            @RequestParam int score) {
        if (!userService.isLoggedIn(username)) {
            return ResponseEntity.status(401).body(Map.of("error", "Not logged in"));
        }
        userRepository.findById(username).ifPresent(user -> {
            user.updateHighScore(score);
            userRepository.save(user);
        });
        return ResponseEntity.ok(Map.of("status", true));
    }
}