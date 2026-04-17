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
import static org.mockito.Mockito.mock;

/**
 * Session-level tests for multiplayer lifecycle state that belongs to GameSession.
 *
 * These tests verify that round completion state is tracked at the session level
 * and that it behaves correctly across round start, round end, and full session reset.
 */
public class GameSessionLifecycleStateTest {

    private GameSession session;
    private ScoreCalculator calc;

    @BeforeEach
    void setUp() {
        GameSession.resetIdCnt();
        calc = new ScoreCalculator();

        UserRepository repo = mock(UserRepository.class);
        WordSubmissionService wordSubmissionService = mock(WordSubmissionService.class);
        GameEventService gameEventService = mock(GameEventService.class);

        session = new GameSession(4, repo, wordSubmissionService, gameEventService);
    }

    /**
     * Verifies that players can be marked as finished only after the round has ended,
     * and that finished state is tracked correctly.
     */
    @Test
    void markPlayerFinished_tracksFinishedPlayers() {
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
     * Verifies that all-finished detection becomes true only when every player
     * in the session has been marked finished after the round ends.
     */
    @Test
    void haveAllPlayersFinished_returnsTrueOnlyWhenAllPlayersAreMarkedFinished() {
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

    /**
     * Verifies that the resultsComputed flag is session-local lifecycle state
     * that can be updated independently.
     */
    @Test
    void resultsComputedFlag_canBeUpdated() {
        assertFalse(session.isResultsComputed());

        session.setResultsComputed(true);

        assertTrue(session.isResultsComputed());
    }

    /**
     * Verifies that starting a new game clears previous round completion state
     * and previous results-computed state.
     */
    @Test
    void startGame_clearsPreviousCompletionState() {
        User alice = new User("alice", "password");
        session.addPlayer(new Player(alice, calc));
        session.startGame();
        session.endRound();

        session.markPlayerFinished("alice");
        session.setResultsComputed(true);

        session.endGame();

        session.addPlayer(new Player(alice, calc));
        session.startGame();

        assertFalse(session.isPlayerFinished("alice"));
        assertFalse(session.isResultsComputed());
        assertFalse(session.isRoundEnded());
    }

    /**
     * Verifies that ending the full session resets lifecycle tracking along with
     * the rest of the session state.
     */
    @Test
    void endGame_resetsLifecycleState() {
        User alice = new User("alice", "password");
        session.addPlayer(new Player(alice, calc));

        session.startGame();
        session.endRound();
        session.markPlayerFinished("alice");
        session.setResultsComputed(true);

        session.endGame();

        assertFalse(session.isStarted());
        assertFalse(session.isRoundEnded());
        assertEquals(0, session.getNumPlayers());
        assertNull(session.getHostUsername());
        assertFalse(session.isResultsComputed());
    }
}