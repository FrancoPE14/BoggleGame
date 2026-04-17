package com.anteaters.boggle.service;

import com.anteaters.boggle.entity.User;
import com.anteaters.boggle.model.SessionSummary;
import com.anteaters.boggle.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class GameServiceLobbyTest {

    private UserRepository repo;
    private UserRegulationService userRegulation;
    private WordSubmissionService wordSubmissionService;
    private GameEventService gameEventService;
    private GameService gameService;

    @BeforeEach
    void setUp() {
        GameSession.resetIdCnt();

        repo = mock(UserRepository.class);
        userRegulation = mock(UserRegulationService.class);
        wordSubmissionService = mock(WordSubmissionService.class);
        gameEventService = mock(GameEventService.class);

        gameService = new GameService(repo, userRegulation, wordSubmissionService, gameEventService);
    }

    @Test
    void getSessionSummaries_returnsAllSessions() {
        List<SessionSummary> summaries = gameService.getSessionSummaries();

        assertEquals(10, summaries.size());
        assertEquals(0, summaries.get(0).sessionId());
        assertEquals(3, summaries.get(0).maxPlayers());
        assertFalse(summaries.get(0).started());
        assertNull(summaries.get(0).hostUsername());
    }

    @Test
    void joinSession_firstPlayerBecomesHost() {
        User user = mock(User.class);
        when(userRegulation.getUser("alice")).thenReturn(user);

        SessionSummary summary = gameService.joinSession(0, "alice");

        assertEquals(0, summary.sessionId());
        assertEquals(1, summary.playerCount());
        assertEquals(3, summary.maxPlayers());
        assertFalse(summary.started());
        assertEquals("alice", summary.hostUsername());

        verify(gameEventService).broadcastToSession(
                eq("0"),
                eq("lobby-update"),
                anyMap()
        );
    }

    @Test
    void joinSession_secondPlayerDoesNotReplaceHost() {
        User alice = mock(User.class);
        User bob = mock(User.class);

        when(userRegulation.getUser("alice")).thenReturn(alice);
        when(userRegulation.getUser("bob")).thenReturn(bob);

        gameService.joinSession(0, "alice");
        SessionSummary summary = gameService.joinSession(0, "bob");

        assertEquals(2, summary.playerCount());
        assertEquals("alice", summary.hostUsername());
    }

    @Test
    void joinSession_samePlayerSameSessionThrows() {
        User user = mock(User.class);
        when(userRegulation.getUser("alice")).thenReturn(user);

        gameService.joinSession(0, "alice");

        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> gameService.joinSession(0, "alice")
        );

        assertEquals("Player is already in this session", ex.getMessage());
    }

    @Test
    void joinSession_samePlayerDifferentSessionThrows() {
        User user = mock(User.class);
        when(userRegulation.getUser("alice")).thenReturn(user);

        gameService.joinSession(0, "alice");

        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> gameService.joinSession(1, "alice")
        );

        assertEquals("Player is already in another session", ex.getMessage());
    }

    @Test
    void joinSession_invalidSessionThrows() {
        User user = mock(User.class);
        when(userRegulation.getUser("alice")).thenReturn(user);

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> gameService.joinSession(999, "alice")
        );

        assertEquals("The session of this id does not exists", ex.getMessage());
    }

    @Test
    void joinSession_userNotLoggedInThrows() {
        when(userRegulation.getUser("alice")).thenReturn(null);

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> gameService.joinSession(0, "alice")
        );

        assertEquals("The user is not logged in", ex.getMessage());
    }

    @Test
    void joinSession_fullSessionThrows() {
        User alice = mock(User.class);
        User bob = mock(User.class);
        User charlie = mock(User.class);
        User david = mock(User.class);

        when(userRegulation.getUser("alice")).thenReturn(alice);
        when(userRegulation.getUser("bob")).thenReturn(bob);
        when(userRegulation.getUser("charlie")).thenReturn(charlie);
        when(userRegulation.getUser("david")).thenReturn(david);

        gameService.joinSession(0, "alice");
        gameService.joinSession(0, "bob");
        gameService.joinSession(0, "charlie");

        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> gameService.joinSession(0, "david")
        );

        assertEquals("Session is full, cannot add more players", ex.getMessage());
    }

    @Test
    void joinSession_startedSessionThrows() {
        User alice = mock(User.class);
        User bob = mock(User.class);

        when(userRegulation.getUser("alice")).thenReturn(alice);
        when(userRegulation.getUser("bob")).thenReturn(bob);

        gameService.joinSession(0, "alice");
        gameService.startGame(0);

        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> gameService.joinSession(0, "bob")
        );

        assertEquals("Session has already started, cannot add more players", ex.getMessage());
    }
}