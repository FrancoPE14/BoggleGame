package com.anteaters.boggle.service;

import com.anteaters.boggle.entity.User;
import com.anteaters.boggle.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

public class GameSessionLobbyTest {

    private GameSession session;
    private ScoreCalculator calc;

    @BeforeEach
    void setUp() {
        GameSession.resetIdCnt();
        calc = new ScoreCalculator();

        UserRepository repo = mock(UserRepository.class);
        WordSubmissionService wordSubmissionService = mock(WordSubmissionService.class);

        session = new GameSession(3, repo, wordSubmissionService);
    }

    @Test
    void firstAddedPlayerBecomesHost() {
        User user = mock(User.class);
        Player player = new Player(user, calc) {
            @Override
            public String getUsername() {
                return "alice";
            }
        };

        session.addPlayer(player);

        assertEquals("alice", session.getHostUsername());
    }
}