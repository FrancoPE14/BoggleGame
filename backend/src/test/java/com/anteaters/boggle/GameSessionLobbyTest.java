package com.anteaters.boggle;

import com.anteaters.boggle.entity.User;
import com.anteaters.boggle.repository.UserRepository;
import com.anteaters.boggle.service.GameSession;
import com.anteaters.boggle.service.Player;
import com.anteaters.boggle.service.ScoreCalculator;
import com.anteaters.boggle.service.WordSubmissionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
        User alice = new User("alice", "password");
        Player player = new Player(alice, calc);

        session.addPlayer(player);

        assertEquals("alice", session.getHostUsername());
    }
}