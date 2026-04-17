package com.anteaters.boggle;

import com.anteaters.boggle.entity.User;
import com.anteaters.boggle.model.SessionSummary;
import com.anteaters.boggle.repository.UserRepository;
import com.anteaters.boggle.service.GameEventService;
import com.anteaters.boggle.service.GameService;
import com.anteaters.boggle.service.GameSession;
import com.anteaters.boggle.service.UserRegulationService;
import com.anteaters.boggle.service.WordSubmissionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.eq;
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

    private User mockUser(String username) {
        User user = mock(User.class);
        when(user.getUsername()).thenReturn(username);
        return user;
    }

    @Test
    void getSessionSummaries_returnsAllSessions() {
        List<SessionSummary> summaries = gameService.getSessionSummaries();

        assertEquals(10, summaries.size());
        assertTrue(summaries.stream().allMatch(summary -> summary.maxPlayers() == 3));
        assertTrue(summaries.stream().allMatch(summary -> !summary.started()));
        assertTrue(summaries.stream().allMatch(summary -> summary.hostUsername() == null));
    }

    @Test
    void joinSession_firstPlayerBecomesHost() {
        User alice = mockUser("alice");
        when(userRegulation.getUser("alice")).thenReturn(alice);

        SessionSummary summary = gameService.joinSession(0, "alice");

        assertEquals(1, summary.playerCount());
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
        User alice = mockUser("alice");
        User bob = mockUser("bob");

        when(userRegulation.getUser("alice")).thenReturn(alice);
        when(userRegulation.getUser("bob")).thenReturn(bob);

        gameService.joinSession(0, "alice");
        SessionSummary summary = gameService.joinSession(0, "bob");

        assertEquals(2, summary.playerCount());
        assertEquals("alice", summary.hostUsername());
    }

    @Test
    void joinSession_samePlayerSameSessionThrows() {
        User alice = mockUser("alice");
        when(userRegulation.getUser("alice")).thenReturn(alice);

        gameService.joinSession(0, "alice");

        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> gameService.joinSession(0, "alice")
        );

        assertEquals("Player is already in this session", ex.getMessage());
    }

    @Test
    void joinSession_samePlayerDifferentSessionThrows() {
        User alice = mockUser("alice");
        when(userRegulation.getUser("alice")).thenReturn(alice);

        gameService.joinSession(0, "alice");

        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> gameService.joinSession(1, "alice")
        );

        assertEquals("Player is already in another session", ex.getMessage());
    }

    @Test
    void joinSession_invalidSessionThrows() {
        User alice = mockUser("alice");
        when(userRegulation.getUser("alice")).thenReturn(alice);

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
        User alice = mockUser("alice");
        User bob = mockUser("bob");
        User charlie = mockUser("charlie");
        User david = mockUser("david");

        when(userRegulation.getUser("alice")).thenReturn(alice);
        when(userRegulation.getUser("bob")).thenReturn(bob);
        when(userRegulation.getUser("charlie")).thenReturn(charlie);
        when(userRegulation.getUser("david")).thenReturn(david);

        gameService.joinSession(0, "alice");
        gameService.joinSession(0, "bob");
        gameService.joinSession(0, "charlie");

        assertThrows(
                IllegalStateException.class,
                () -> gameService.joinSession(0, "david")
        );
    }

    @Test
    void joinSession_startedSessionThrows() {
        User alice = mockUser("alice");
        User bob = mockUser("bob");

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