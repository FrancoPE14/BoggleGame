package com.anteaters.boggle;

import com.anteaters.boggle.entity.User;
import com.anteaters.boggle.repository.UserRepository;
import com.anteaters.boggle.service.GameSession;
import com.anteaters.boggle.service.Player;
import com.anteaters.boggle.service.ScoreCalculator;
import com.anteaters.boggle.service.WordSubmissionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

/**
 * Session-level tests for multiplayer lifecycle state that now belongs to GameSession.
 *
 * These tests verify that round-completion and result-tracking state is owned
 * and reset at the session level instead of being managed externally.
 */
public class GameSessionLifecycleStateTest {

    private GameSession session;
    private ScoreCalculator calc;

    /**
     * Rebuilds a fresh session before each test.
     */
    @BeforeEach
    void setUp() {
        GameSession.resetIdCnt();
        calc = new ScoreCalculator();

        UserRepository repo = mock(UserRepository.class);
        WordSubmissionService wordSubmissionService = mock(WordSubmissionService.class);

        session = new GameSession(4, repo, wordSubmissionService);
    }

    /**
     * Verifies that players can be marked as finished and queried individually.
     */
    @Test
    void markPlayerFinished_tracksFinishedPlayers() {
        User alice = new User("alice", "password");
        User bob = new User("bob", "password");

        session.addPlayer(new Player(alice, calc));
        session.addPlayer(new Player(bob, calc));

        session.markPlayerFinished("alice");

        assertTrue(session.isPlayerFinished("alice"));
        assertFalse(session.isPlayerFinished("bob"));
        assertFalse(session.haveAllPlayersFinished());
    }

    /**
     * Verifies that all-finished detection becomes true only when every player
     * in the session has been marked finished.
     */
    @Test
    void haveAllPlayersFinished_returnsTrueOnlyWhenAllPlayersAreMarkedFinished() {
        User alice = new User("alice", "password");
        User bob = new User("bob", "password");

        session.addPlayer(new Player(alice, calc));
        session.addPlayer(new Player(bob, calc));

        session.markPlayerFinished("alice");
        assertFalse(session.haveAllPlayersFinished());

        session.markPlayerFinished("bob");
        assertTrue(session.haveAllPlayersFinished());
    }

    /**
     * Verifies that resultsComputed is session-local lifecycle state that can be
     * updated and queried through the session.
     */
    @Test
    void resultsComputedFlag_canBeUpdated() {
        assertFalse(session.isResultsComputed());

        session.setResultsComputed(true);

        assertTrue(session.isResultsComputed());
    }

    /**
     * Verifies that starting a new game clears old lifecycle state from the previous round.
     */
    @Test
    void startGame_clearsPreviousCompletionState() {
        User alice = new User("alice", "password");
        session.addPlayer(new Player(alice, calc));

        session.markPlayerFinished("alice");
        session.setResultsComputed(true);

        session.startGame();

        assertFalse(session.isPlayerFinished("alice"));
        assertFalse(session.isResultsComputed());
    }

    /**
     * Verifies that ending a game fully resets lifecycle tracking along with the rest
     * of the session state.
     */
    @Test
    void endGame_resetsLifecycleState() {
        User alice = new User("alice", "password");
        session.addPlayer(new Player(alice, calc));

        session.startGame();
        session.markPlayerFinished("alice");
        session.setResultsComputed(true);

        session.endGame();

        assertFalse(session.isStarted());
        assertEquals(0, session.getNumPlayers());
        assertNull(session.getHostUsername());
        assertFalse(session.isResultsComputed());
    }
}