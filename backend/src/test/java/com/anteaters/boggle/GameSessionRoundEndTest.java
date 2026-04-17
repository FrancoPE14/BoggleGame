package com.anteaters.boggle;

import com.anteaters.boggle.entity.User;
import com.anteaters.boggle.repository.UserRepository;
import com.anteaters.boggle.service.GameEventService;
import com.anteaters.boggle.service.GameSession;
import com.anteaters.boggle.service.Player;
import com.anteaters.boggle.service.ScoreCalculator;
import com.anteaters.boggle.service.WordSubmissionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Session-level tests for round end and finish acknowledgement state.
 *
 * These tests verify that timer completion is represented as a round-ended state,
 * that players can acknowledge completion only after the round ends, and that
 * all-finished detection works at the session level.
 */
public class GameSessionRoundEndTest {

    private GameSession session;
    private ScoreCalculator calc;
    private GameEventService gameEventService;

    /**
     * Creates a fresh session before each test.
     */
    @BeforeEach
    void setUp() {
        GameSession.resetIdCnt();
        calc = new ScoreCalculator();

        UserRepository repo = mock(UserRepository.class);
        WordSubmissionService wordSubmissionService = mock(WordSubmissionService.class);
        gameEventService = mock(GameEventService.class);

        session = new GameSession(4, repo, wordSubmissionService, gameEventService);
    }

    /**
     * Verifies that ending the round sets roundEnded state and broadcasts
     * a round-ended SSE event to the session.
     */
    @Test
    void endRound_marksRoundEndedAndBroadcastsEvent() {
        User alice = new User("alice", "password");
        session.addPlayer(new Player(alice, calc));
        session.startGame();

        session.endRound();

        assertTrue(session.isRoundEnded());

        verify(gameEventService).broadcastToSession(
                eq("0"),
                eq("round-ended"),
                anyMap()
        );
    }

    /**
     * Verifies that players cannot acknowledge completion before the round has ended.
     */
    @Test
    void markPlayerFinished_requiresRoundEnded() {
        User alice = new User("alice", "password");
        session.addPlayer(new Player(alice, calc));
        session.startGame();

        assertThrows(
                IllegalStateException.class,
                () -> session.markPlayerFinished("alice")
        );
    }

    /**
     * Verifies that finish acknowledgements are tracked correctly after the round ends.
     */
    @Test
    void markPlayerFinished_tracksFinishedPlayersAfterRoundEnds() {
        User alice = new User("alice", "password");
        User bob = new User("bob", "password");

        session.addPlayer(new Player(alice, calc));
        session.addPlayer(new Player(bob, calc));
        session.startGame();
        session.endRound();

        session.markPlayerFinished("alice");

        assertTrue(session.isPlayerFinished("alice"));
        assertFalse(session.isPlayerFinished("bob"));
        assertFalse(session.haveAllPlayersFinished());
    }

    /**
     * Verifies that all-finished detection becomes true only after every player
     * in the session has acknowledged completion.
     */
    @Test
    void haveAllPlayersFinished_returnsTrueWhenAllPlayersAcknowledge() {
        User alice = new User("alice", "password");
        User bob = new User("bob", "password");

        session.addPlayer(new Player(alice, calc));
        session.addPlayer(new Player(bob, calc));
        session.startGame();
        session.endRound();

        session.markPlayerFinished("alice");
        assertFalse(session.haveAllPlayersFinished());

        session.markPlayerFinished("bob");
        assertTrue(session.haveAllPlayersFinished());
    }
}