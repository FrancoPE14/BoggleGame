package com.anteaters.boggle;

import com.anteaters.boggle.entity.User;
import com.anteaters.boggle.repository.UserRepository;
import com.anteaters.boggle.service.BoggleBoard;
import com.anteaters.boggle.service.GameEventService;
import com.anteaters.boggle.service.GameService;
import com.anteaters.boggle.service.GameSession;
import com.anteaters.boggle.service.UserRegulationService;
import com.anteaters.boggle.service.WordSubmissionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Service-level tests for host-authorized game start behavior.
 *
 * These tests verify that only the session host may start the game and that
 * a successful start triggers the expected game-started SSE broadcast.
 */
public class GameServiceStartGameTest {

    private UserRepository repo;
    private UserRegulationService userRegulation;
    private WordSubmissionService wordSubmissionService;
    private GameEventService gameEventService;
    private GameService gameService;

    /**
     * Rebuilds the service graph before each test and resets static session ids
     * so test behavior remains deterministic.
     */
    @BeforeEach
    void setUp() {
        GameSession.resetIdCnt();

        repo = mock(UserRepository.class);
        userRegulation = mock(UserRegulationService.class);
        wordSubmissionService = mock(WordSubmissionService.class);
        gameEventService = mock(GameEventService.class);

        gameService = new GameService(repo, userRegulation, wordSubmissionService, gameEventService);
    }

    /**
     * Creates a real User entity for tests so Player construction receives
     * a stable username value.
     *
     * @param username username for the test user
     * @return real user instance
     */
    private User realUser(String username) {
        return new User(username, "password");
    }

    /**
     * Verifies that the current host can successfully start the game and that
     * a game-started SSE event is broadcast to the session.
     */
    @Test
    void startGame_hostCanStartSuccessfully() {
        User alice = realUser("alice");
        when(userRegulation.getUser("alice")).thenReturn(alice);

        gameService.joinSession(0, "alice");
        BoggleBoard board = gameService.startGame(0, "alice");

        assertNotNull(board);

        verify(gameEventService).broadcastToSession(
                eq("0"),
                eq("game-started"),
                anyMap()
        );
    }

    /**
     * Verifies that a non-host player in the same session cannot start the game.
     */
    @Test
    void startGame_nonHostCannotStart() {
        User alice = realUser("alice");
        User bob = realUser("bob");

        when(userRegulation.getUser("alice")).thenReturn(alice);
        when(userRegulation.getUser("bob")).thenReturn(bob);

        gameService.joinSession(0, "alice");
        gameService.joinSession(0, "bob");

        assertThrows(
                IllegalStateException.class,
                () -> gameService.startGame(0, "bob")
        );
    }

    /**
     * Verifies that a user who is not in the target session cannot start the game.
     */
    @Test
    void startGame_playerNotInSessionCannotStart() {
        User alice = realUser("alice");
        User bob = realUser("bob");

        when(userRegulation.getUser("alice")).thenReturn(alice);
        when(userRegulation.getUser("bob")).thenReturn(bob);

        gameService.joinSession(0, "alice");

        assertThrows(
                IllegalStateException.class,
                () -> gameService.startGame(0, "bob")
        );
    }

    /**
     * Verifies that an unauthenticated user cannot start the game.
     */
//    @Test
//    void startGame_userMustBeLoggedIn() {
//        when(userRegulation.getUser("alice")).thenReturn(null);
//
//        assertThrows(
//                IllegalArgumentException.class,
//                () -> gameService.startGame(0, "alice")
//        );
//    }

    /**
     * Verifies that a session cannot be started more than once.
     */
    @Test
    void startGame_cannotStartAlreadyStartedSession() {
        User alice = realUser("alice");
        when(userRegulation.getUser("alice")).thenReturn(alice);

        gameService.joinSession(0, "alice");
        gameService.startGame(0, "alice");

        assertThrows(
                IllegalStateException.class,
                () -> gameService.startGame(0, "alice")
        );
    }
}