package com.anteaters.boggle.service;

import com.anteaters.boggle.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * AI-generated test class for GameSession.
 *
 * <p>This test class verifies core GameSession behaviors such as construction,
 * starting a game, adding players, checking membership, and ending a session.
 * Some tests use Mockito mocks for dependencies so the tests can focus on the
 * GameSession logic itself.</p>
 */
public class GameSessionTest {

    private UserRepository repo;
    private WordSubmissionService wordSubmissionService;

    /**
     * Sets up shared mocked dependencies before each test.
     */
    @BeforeEach
    public void setUp() {
        GameSession.resetIdCnt();
        repo = mock(UserRepository.class);
        wordSubmissionService = mock(WordSubmissionService.class);
    }

    /**
     * AI-generated test method.
     *
     * <p>Verifies that the constructor initializes the session with the expected
     * default state.</p>
     */
    @Test
    public void constructorValidArgumentsInitializesFields() {
        GameSession session = new GameSession(2, repo, wordSubmissionService);

        assertEquals(0, session.getId());
        assertEquals(2, session.getMaxPlayers());
        assertFalse(session.isStarted());
        assertNotNull(session.getBoard());
    }

    /**
     * AI-generated test method.
     *
     * <p>Verifies that the constructor throws an exception when maxPlayers is
     * non-positive.</p>
     */
    @Test
    public void constructorNonPositiveMaxPlayersThrowsException() {
        assertThrows(IllegalArgumentException.class,
                () -> new GameSession(0, repo, wordSubmissionService));
    }

    /**
     * AI-generated test method.
     *
     * <p>Verifies that the constructor throws an exception when the repository
     * dependency is null.</p>
     */
    @Test
    public void constructorNullRepositoryThrowsException() {
        assertThrows(IllegalArgumentException.class,
                () -> new GameSession(2, null, wordSubmissionService));
    }

    /**
     * AI-generated test method.
     *
     * <p>Verifies that the constructor throws an exception when the word
     * submission service dependency is null.</p>
     */
    @Test
    public void constructorNullWordSubmissionServiceThrowsException() {
        assertThrows(IllegalArgumentException.class,
                () -> new GameSession(2, repo, null));
    }

    /**
     * AI-generated test method.
     *
     * <p>Verifies that startGame marks the session as started and returns the
     * same board object stored in the session.</p>
     */
    @Test
    public void startGameMarksSessionStartedAndReturnsBoard() {
        GameSession session = new GameSession(2, repo, wordSubmissionService);

        BoggleBoard board = session.startGame();

        assertTrue(session.isStarted());
        assertSame(board, session.getBoard());
    }

    /**
     * AI-generated test method.
     *
     * <p>Verifies that calling startGame twice throws an exception.</p>
     */
    @Test
    public void startGameTwiceThrowsException() {
        GameSession session = new GameSession(2, repo, wordSubmissionService);
        session.startGame();

        assertThrows(IllegalStateException.class, session::startGame);
    }

    /**
     * AI-generated test method.
     *
     * <p>Verifies that adding a null player throws an exception.</p>
     */
    @Test
    public void addPlayerNullThrowsException() {
        GameSession session = new GameSession(2, repo, wordSubmissionService);

        assertThrows(IllegalArgumentException.class, () -> session.addPlayer(null));
    }

    /**
     * AI-generated test method.
     *
     * <p>Verifies that a newly added player can be found in the session by
     * username.</p>
     */
    @Test
    public void addPlayerAddsPlayerToSession() {
        GameSession session = new GameSession(2, repo, wordSubmissionService);
        Player player = mock(Player.class);
        when(player.getUsername()).thenReturn("alice");

        session.addPlayer(player);

        assertTrue(session.isPlayerAdded("alice"));
    }

    /**
     * AI-generated test method.
     *
     * <p>Verifies that adding the same player twice throws an exception.</p>
     */
    @Test
    public void addDuplicatePlayerThrowsException() {
        GameSession session = new GameSession(2, repo, wordSubmissionService);
        Player player = mock(Player.class);
        when(player.getUsername()).thenReturn("alice");

        session.addPlayer(player);

        assertThrows(IllegalStateException.class, () -> session.addPlayer(player));
    }

    /**
     * AI-generated test method.
     *
     * <p>Verifies that adding a player after the session reaches capacity throws
     * an exception.</p>
     */
    @Test
    public void addPlayerWhenSessionFullThrowsException() {
        GameSession session = new GameSession(1, repo, wordSubmissionService);

        Player player1 = mock(Player.class);
        when(player1.getUsername()).thenReturn("alice");

        Player player2 = mock(Player.class);
        when(player2.getUsername()).thenReturn("bob");

        session.addPlayer(player1);

        assertThrows(IllegalStateException.class, () -> session.addPlayer(player2));
    }

    /**
     * AI-generated test method.
     *
     * <p>Verifies that adding a player after the game has started throws an
     * exception.</p>
     */
    @Test
    public void addPlayerAfterGameStartedThrowsException() {
        GameSession session = new GameSession(2, repo, wordSubmissionService);
        Player player = mock(Player.class);
        when(player.getUsername()).thenReturn("alice");

        session.startGame();

        assertThrows(IllegalStateException.class, () -> session.addPlayer(player));
    }

    /**
     * AI-generated test method.
     *
     * <p>Verifies that checking membership with a null username throws an
     * exception.</p>
     */
    @Test
    public void isPlayerAddedNullUsernameThrowsException() {
        GameSession session = new GameSession(2, repo, wordSubmissionService);

        assertThrows(IllegalArgumentException.class, () -> session.isPlayerAdded(null));
    }

    /**
     * AI-generated test method.
     *
     * <p>Verifies that getScore throws an exception when the username is null.</p>
     */
    @Test
    public void getScoreNullUsernameThrowsException() {
        GameSession session = new GameSession(2, repo, wordSubmissionService);

        assertThrows(IllegalArgumentException.class, () -> session.getScore(null));
    }

    /**
     * AI-generated test method.
     *
     * <p>Verifies that getAcceptedWords throws an exception when the username is
     * null.</p>
     */
    @Test
    public void getAcceptedWordsNullUsernameThrowsException() {
        GameSession session = new GameSession(2, repo, wordSubmissionService);

        assertThrows(IllegalArgumentException.class, () -> session.getAcceptedWords(null));
    }

    /**
     * AI-generated test method.
     *
     * <p>Verifies that submitWord throws an exception if the game has not
     * started yet.</p>
     */
    @Test
    public void submitWordBeforeGameStartedThrowsException() {
        GameSession session = new GameSession(2, repo, wordSubmissionService);

        assertThrows(IllegalStateException.class,
                () -> session.submitWord("alice", "apple"));
    }

    /**
     * AI-generated test method.
     *
     * <p>Verifies that submitWord throws an exception when the player is not in
     * the current session.</p>
     */
    @Test
    public void submitWordForMissingPlayerThrowsException() {
        GameSession session = new GameSession(2, repo, wordSubmissionService);
        session.startGame();

        assertThrows(IllegalArgumentException.class,
                () -> session.submitWord("alice", "apple"));
    }

    /**
     * AI-generated test method.
     *
     * <p>Verifies that ending a session before starting it throws an
     * exception.</p>
     */
    @Test
    public void endGameBeforeStartThrowsException() {
        GameSession session = new GameSession(2, repo, wordSubmissionService);

        assertThrows(IllegalStateException.class, session::endGame);
    }

    /**
     * AI-generated test method.
     *
     * <p>Verifies that endGame resets the session and invokes the expected
     * cleanup methods on each player.</p>
     */
    @Test
    public void endGameResetsSessionAndFlushesPlayers() {
        GameSession session = new GameSession(2, repo, wordSubmissionService);

        Player player1 = mock(Player.class);
        when(player1.getUsername()).thenReturn("alice");

        Player player2 = mock(Player.class);
        when(player2.getUsername()).thenReturn("bob");

        session.addPlayer(player1);
        session.addPlayer(player2);
        session.startGame();
        session.endGame();

        verify(player1).updateHighScore();
        verify(player1).flushToDB(repo);
        verify(player1).reset();

        verify(player2).updateHighScore();
        verify(player2).flushToDB(repo);
        verify(player2).reset();

        assertFalse(session.isStarted());
        assertFalse(session.isPlayerAdded("alice"));
        assertFalse(session.isPlayerAdded("bob"));
    }

    /**
     * AI-generated test method.
     *
     * <p>Verifies that session ids increment across newly created sessions.</p>
     */
    @Test
    public void sessionIdsIncrementAcrossInstances() {
        GameSession session1 = new GameSession(2, repo, wordSubmissionService);
        GameSession session2 = new GameSession(2, repo, wordSubmissionService);

        assertEquals(0, session1.getId());
        assertEquals(1, session2.getId());
    }
}