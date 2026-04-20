package com.anteaters.boggle;

import com.anteaters.boggle.entity.User;
import com.anteaters.boggle.repository.UserRepository;
import com.anteaters.boggle.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for Issue 5: result computation and SSE broadcast logic.
 *
 * Verifies:
 * - final scores are computed correctly
 * - winner is determined correctly
 * - game-results SSE is triggered when all players finish
 * - broadcast is not triggered prematurely
 */
public class GameServiceResultTest {

    private GameService gameService;
    private GameEventService gameEventService;
    private UserRepository repo;
    private UserRegulationService userRegulation;
    private WordSubmissionService submissionService;

    @BeforeEach
    void setUp() {
        repo = new FakeUserRepository();
        userRegulation = mock(UserRegulationService.class);
        submissionService = mock(WordSubmissionService.class);
        gameEventService = mock(GameEventService.class);

        GameSession.resetIdCnt();

        gameService = new GameService(repo, userRegulation, submissionService, gameEventService);
    }

    /**
     * Verifies that winner is correctly determined based on player scores.
     */
    @Test
    void determineWinner_returnsHighestScorePlayer() {
        GameSession session = new GameSession(
                4,
                repo,
                submissionService,
                gameEventService
        );

        Player p1 = new Player(new User("alice", "pw"), new ScoreCalculator());
        Player p2 = new Player(new User("bob", "pw"), new ScoreCalculator());

        session.addPlayer(p1);
        session.addPlayer(p2);

        p1.getTracker().recordWord("CAT", 100);
        p2.getTracker().recordWord("DOG", 200);

        String winner = session.determineWinner();

        assertEquals("bob", winner);
    }

    /**
     * Verifies that game-results is NOT broadcast before all players finish.
     */
    @Test
    void resultsNotBroadcast_ifNotAllPlayersFinished() {
        User user = mock(User.class);
        when(user.getUsername()).thenReturn("alice");
        when(userRegulation.getUser("alice")).thenReturn(user);
        repo.save(user);

        int sessionId = 0;

        gameService.addPlayer(sessionId, "alice");

        gameService.startGame(sessionId, "alice");

        GameSession session = gameService.getSession(sessionId);
        session.endRound();

        // only one player exists → do not mark finished yet

        verify(gameEventService, never()).broadcastToSession(
                anyString(),
                eq("game-results"),
                anyMap()
        );
    }

    /**
     * Verifies that game-results is broadcast when all players finish.
     */
    @Test
    void resultsBroadcast_whenAllPlayersFinished() {
        User user1 = mock(User.class);
        when(user1.getUsername()).thenReturn("alice");

        User user2 = mock(User.class);
        when(user2.getUsername()).thenReturn("bob");

        when(userRegulation.getUser("alice")).thenReturn(user1);
        when(userRegulation.getUser("bob")).thenReturn(user2);

        repo.save(user1);
        repo.save(user2);

        int sessionId = 0;

        gameService.addPlayer(sessionId, "alice");
        gameService.addPlayer(sessionId, "bob");

        gameService.startGame(sessionId, "alice");

        GameSession session = gameService.getSession(sessionId);
        session.endRound();

        gameService.acknowledgePlayerFinished(sessionId, "alice");
        gameService.acknowledgePlayerFinished(sessionId, "bob");

        verify(gameEventService, times(1)).broadcastToSession(
                anyString(),
                eq("game-results"),
                anyMap()
        );
    }
}