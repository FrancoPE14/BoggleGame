package com.anteaters.boggle.service;

import com.anteaters.boggle.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import com.anteaters.boggle.entity.User;

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
    private final List<GameSession> sessionsToCleanUp = new ArrayList<>();

    /**
     * Sets up shared mocked dependencies before each test.
     */
    @BeforeEach
    public void setUp() {
        GameSession.resetIdCnt();
        repo = mock(UserRepository.class);
        wordSubmissionService = mock(WordSubmissionService.class);
        sessionsToCleanUp.clear();
    }

    /**
     * Cleans up any started sessions after each test so timer threads do not
     * remain alive after test execution.
     */
    @AfterEach
    public void tearDown() {
        for (GameSession session : sessionsToCleanUp) {
            try {
                if (session.isStarted()) {
                    session.endGame();
                }
            } catch (Exception ignored) {
            }
        }
        sessionsToCleanUp.clear();
    }

    /**
     * AI-generated helper method.
     *
     * <p>Creates a new session and registers it for automatic cleanup after the
     * test completes.</p>
     */
    private GameSession newSession(int maxPlayers) {
        GameSession session = new GameSession(maxPlayers, repo, wordSubmissionService);
        sessionsToCleanUp.add(session);
        return session;
    }

    /**
     * AI-generated test method.
     *
     * <p>Verifies that the constructor initializes the session with the expected
     * default state.</p>
     */
    @Test
    public void constructorValidArgumentsInitializesFields() {
        GameSession session = newSession(2);

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
        GameSession session = newSession(2);

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
        GameSession session = newSession(2);
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
        GameSession session = newSession(2);

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
        GameSession session = newSession(2);
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
        GameSession session = newSession(2);
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
        GameSession session = newSession(1);

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
        GameSession session = newSession(2);
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
        GameSession session = newSession(2);

        assertThrows(IllegalArgumentException.class, () -> session.isPlayerAdded(null));
    }

    /**
     * AI-generated test method.
     *
     * <p>Verifies that getScore throws an exception when the username is null.</p>
     */
    @Test
    public void getScoreNullUsernameThrowsException() {
        GameSession session = newSession(2);

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
        GameSession session = newSession(2);

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
        GameSession session = newSession(2);

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
        GameSession session = newSession(2);
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
        GameSession session = newSession(2);

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
        GameSession session = newSession(2);

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
        GameSession session1 = newSession(2);
        GameSession session2 = newSession(2);

        assertEquals(0, session1.getId());
        assertEquals(1, session2.getId());
    }

    /**
     * AI-generated test method.
     *
     * <p>Verifies that startGame initializes the timer-related fields and stores
     * a scheduled task handle.</p>
     */
    @Test
    public void startGameInitializesTimerFieldsAndScheduledTask() throws Exception {
        GameSession session = newSession(2);

        session.startGame();

        long startTime = (long) getPrivateField(session, "startTime");
        long endTime = (long) getPrivateField(session, "endTime");
        ScheduledFuture<?> future =
                (ScheduledFuture<?>) getPrivateField(session, "scheduledFuture");

        assertTrue(startTime > 0);
        assertTrue(endTime > startTime);
        assertNotNull(future);
    }

    /**
     * AI-generated test method.
     *
     * <p>Verifies that endGame cancels the scheduled timer task and clears the
     * timer-related fields.</p>
     */
    @Test
    public void endGameCancelsTimerAndClearsTimerFields() throws Exception {
        GameSession session = newSession(2);

        session.startGame();

        ScheduledFuture<?> future =
                (ScheduledFuture<?>) getPrivateField(session, "scheduledFuture");

        session.endGame();

        assertTrue(future.isCancelled());
        assertNull(getPrivateField(session, "scheduledFuture"));
        assertEquals(-1L, getPrivateField(session, "startTime"));
        assertEquals(-1L, getPrivateField(session, "endTime"));
        assertFalse(session.isStarted());
    }

    /**
     * AI-generated test method.
     *
     * <p>Verifies that updateFrontendTimer ends the game when the current time
     * has passed the session end time.</p>
     *
     * This got commented because we are now disabling the endRound functionality
     */
    /*
    @Test
    void updateFrontendTimerAfterExpirationEndsRound() throws Exception {
        UserRepository repo = mock(UserRepository.class);
        WordSubmissionService wordSubmissionService = mock(WordSubmissionService.class);
        GameEventService gameEventService = mock(GameEventService.class);

        GameSession.resetIdCnt();
        GameSession session = new GameSession(4, repo, wordSubmissionService, gameEventService);

        User alice = new User("alice", "password");
        session.addPlayer(new Player(alice, new ScoreCalculator()));
        session.startGame();

        Method updateTimer = GameSession.class.getDeclaredMethod("updateFrontendTimer");
        updateTimer.setAccessible(true);

        Field endTimeField = GameSession.class.getDeclaredField("endTime");
        endTimeField.setAccessible(true);
        endTimeField.setLong(session, System.currentTimeMillis() - 1000);

        updateTimer.invoke(session);

        assertTrue(session.isStarted());
        assertTrue(session.isRoundEnded());
        assertEquals(1, session.getNumPlayers());
        assertEquals("alice", session.getHostUsername());
    }
    */

    /**
     * AI-generated helper method.
     *
     * <p>Returns the value of a private field by reflection.</p>
     */
    private Object getPrivateField(Object target, String fieldName) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.get(target);
    }

    /**
     * AI-generated helper method.
     *
     * <p>Sets the value of a private field by reflection.</p>
     */
    private void setPrivateField(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }

    /**
     * AI-generated helper method.
     *
     * <p>Invokes a private no-argument method by reflection.</p>
     */
    private void invokePrivateNoArgMethod(Object target, String methodName) throws Exception {
        Method method = target.getClass().getDeclaredMethod(methodName);
        method.setAccessible(true);
        method.invoke(target);
    }
}