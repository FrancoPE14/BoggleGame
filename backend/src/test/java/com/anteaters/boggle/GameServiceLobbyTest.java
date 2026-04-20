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

        repo = new FakeUserRepository();
        userRegulation = mock(UserRegulationService.class);
        wordSubmissionService = mock(WordSubmissionService.class);
        gameEventService = mock(GameEventService.class);

        gameService = new GameService(repo, userRegulation, wordSubmissionService, gameEventService);
    }

    /**
     * Uses a real User entity instead of a Mockito mock so Player construction
     * receives a stable username value and does not fail due to missing state.
     */
    private User realUser(String username) {
        User user = new User(username, "password");
        repo.save(user);
        return user;
    }

    @Test
    void getSessionSummaries_returnsAllSessions() {
        List<SessionSummary> summaries = gameService.getSessionSummaries();

        assertEquals(10, summaries.size());
        assertTrue(summaries.stream().allMatch(summary -> summary.maxPlayers() == 4));
        assertTrue(summaries.stream().allMatch(summary -> !summary.started()));
    }

    @Test
    void joinSession_secondPlayerDoesNotReplaceHost() {
        User alice = realUser("alice");
        User bob = realUser("bob");

        when(userRegulation.getUser("alice")).thenReturn(alice);
        when(userRegulation.getUser("bob")).thenReturn(bob);

        gameService.joinSession(0, "alice");
        SessionSummary summary = gameService.joinSession(0, "bob");

        assertEquals(2, summary.playerCount());
        assertEquals("alice", summary.hostUsername());
    }

    @Test
    void joinSession_samePlayerSameSessionThrows() {
        User alice = realUser("alice");
        when(userRegulation.getUser("alice")).thenReturn(alice);

        gameService.joinSession(0, "alice");

        assertThrows(
                IllegalStateException.class,
                () -> gameService.joinSession(0, "alice")
        );
    }

    @Test
    void joinSession_samePlayerDifferentSessionThrows() {
        User alice = realUser("alice");
        when(userRegulation.getUser("alice")).thenReturn(alice);

        gameService.joinSession(0, "alice");

        assertThrows(
                IllegalStateException.class,
                () -> gameService.joinSession(1, "alice")
        );
    }

    @Test
    void joinSession_invalidSessionThrows() {
        User alice = realUser("alice");
        when(userRegulation.getUser("alice")).thenReturn(alice);

        assertThrows(
                IllegalArgumentException.class,
                () -> gameService.joinSession(999, "alice")
        );
    }

//    @Test
//    void joinSession_userNotLoggedInThrows() {
//        when(userRegulation.getUser("alice")).thenReturn(null);
//
//        assertThrows(
//                IllegalArgumentException.class,
//                () -> gameService.joinSession(0, "alice")
//        );
//    }

    @Test
    void joinSession_fullSessionThrows() {
        User alice = realUser("alice");
        User bob = realUser("bob");
        User charlie = realUser("charlie");
        User david = realUser("david");
        User eve = realUser("eve");

        when(userRegulation.getUser("alice")).thenReturn(alice);
        when(userRegulation.getUser("bob")).thenReturn(bob);
        when(userRegulation.getUser("charlie")).thenReturn(charlie);
        when(userRegulation.getUser("david")).thenReturn(david);
        when(userRegulation.getUser("eve")).thenReturn(eve);

        gameService.joinSession(0, "alice");
        gameService.joinSession(0, "bob");
        gameService.joinSession(0, "charlie");
        gameService.joinSession(0, "david");

        assertThrows(
                IllegalStateException.class,
                () -> gameService.joinSession(0, "eve")
        );
    }

    @Test
    void joinSession_startedSessionThrows() {
        User alice = realUser("alice");
        User bob = realUser("bob");

        when(userRegulation.getUser("alice")).thenReturn(alice);
        when(userRegulation.getUser("bob")).thenReturn(bob);

        gameService.joinSession(0, "alice");
        gameService.startGame(0, "alice");

        assertThrows(
                IllegalStateException.class,
                () -> gameService.joinSession(0, "bob")
        );
    }
}