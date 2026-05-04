package com.anteaters.boggle.service;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Service for retrieving leaderboard data from the database.
 *
 * Queries the user table and returns players ranked by highest score.
 */
@Service
public class LeaderboardService {

    private final JdbcTemplate jdbcTemplate;

    public LeaderboardService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    /**
     * Retrieves all players sorted by highest score in descending order.
     *
     * @return list of maps each containing user_name and highest_score
     */
    public List<Map<String, Object>> getLeaderboard() {
        String sql = "SELECT user_name, highest_score FROM boggle_user ORDER BY highest_score DESC";
        return jdbcTemplate.queryForList(sql);
    }
}