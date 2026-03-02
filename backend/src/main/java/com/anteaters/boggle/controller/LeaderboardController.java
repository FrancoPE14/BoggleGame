package com.anteaters.boggle.controller;

import com.anteaters.boggle.service.LeaderboardService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * REST API endpoint for retrieving leaderboard data.
 *
 * Returns a ranked list of players by their highest score.
 */
@RestController
public class LeaderboardController {

    private final LeaderboardService leaderboardService;

    public LeaderboardController(LeaderboardService leaderboardService) {
        this.leaderboardService = leaderboardService;
    }


    /**
     * Returns all players sorted by highest score in descending order.
     *
     * Example:
     * GET /api/leaderboard
     *
     * @return list of players with user_name and highest_score
     */
    @GetMapping("/api/leaderboard")
    public List<Map<String, Object>> getLeaderboard() {
        return leaderboardService.getLeaderboard();
    }
}