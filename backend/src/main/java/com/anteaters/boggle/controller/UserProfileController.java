package com.anteaters.boggle.controller;

import com.anteaters.boggle.service.UserRegulationService;
import com.anteaters.boggle.entity.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import java.util.Map;

@RestController
public class UserProfileController {
    private final UserRegulationService userService;

    public UserProfileController(UserRegulationService userService) {
        this.userService = userService;
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
        return ResponseEntity.ok(Map.of(
                "username", user.getUsername(),
                "matchesWon", user.getMatchesWon(),
                "highScore", user.getHighScore()
        ));
    }
}