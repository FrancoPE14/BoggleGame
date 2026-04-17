package com.anteaters.boggle;

import com.anteaters.boggle.dictionary.DictionaryTrie;
import com.anteaters.boggle.dictionary.InMemoryCustomDictionaryStore;
import com.anteaters.boggle.entity.User;
import com.anteaters.boggle.model.WordSubmissionResult;
import com.anteaters.boggle.repository.UserRepository;
import com.anteaters.boggle.service.GameEventService;
import com.anteaters.boggle.service.GameService;
import com.anteaters.boggle.service.GameSession;
import com.anteaters.boggle.service.ScoreCalculator;
import com.anteaters.boggle.service.UserRegulationService;
import com.anteaters.boggle.service.WordSubmissionService;
import com.anteaters.boggle.service.WordVerificationService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Integration test for scoring flow through GameService.
 *
 * This verifies:
 * - GameService -> WordSubmissionService -> ScoreTracker integration
 * - score updates for a valid word
 * - duplicate words do not score twice
 * - accepted words are stored correctly
 *
 * These tests also respect the current multiplayer start contract:
 * only the host player may start the session.
 */
public class GameServiceScoringIntegrationTest {

    private static DictionaryTrie trie;

    private InMemoryCustomDictionaryStore store;
    private WordVerificationService verificationService;
    private WordSubmissionService submissionService;

    private UserRepository repo;
    private UserRegulationService userRegulation;
    private GameService gameService;

    /**
     * Loads the base dictionary once for all scoring integration tests.
     */
    @BeforeAll
    static void loadDictionary() {
        trie = new DictionaryTrie();
        trie.loadDictionary();
    }

    /**
     * Rebuilds the scoring pipeline and GameService dependencies before each test.
     */
    @BeforeEach
    void setUp() {
        store = new InMemoryCustomDictionaryStore();
        verificationService = new WordVerificationService(trie, store);
        submissionService = new WordSubmissionService(
                verificationService,
                new ScoreCalculator()
        );

        repo = mock(UserRepository.class);
        userRegulation = mock(UserRegulationService.class);

        GameSession.resetIdCnt();
        GameEventService gameEventService = mock(GameEventService.class);
        gameService = new GameService(repo, userRegulation, submissionService, gameEventService);
    }

    /**
     * Verifies that submitting a valid word through GameService updates both score
     * and accepted-word state for the player.
     *
     * The same user is added to the session and starts the game, which makes that
     * user the host under the current Issue 2 rules.
     */
    @Test
    void submitWord_updatesScoreAndAcceptedWordsThroughGameService() {
        User user = mock(User.class);
        int sessionId = 5;

        when(user.getUsername()).thenReturn("user");
        when(userRegulation.getUser("user")).thenReturn(user);

        gameService.addPlayer(sessionId, "user");
        gameService.startGame(sessionId, "user");

        WordSubmissionResult result = gameService.submitWord(sessionId, "user", "CAT");

        assertTrue(result.isAccepted());
        assertTrue(result.isValid());
        assertFalse(result.isDuplicate());
        assertEquals("CAT", result.getNormalizedWord());
        assertEquals(100, result.getPointsAwarded());
        assertEquals(100, result.getCurrentScore());
        assertEquals(100, gameService.getScore(sessionId, "user"));
        assertEquals(1, gameService.getAcceptedWords(sessionId, "user").size());
        assertEquals("CAT", gameService.getAcceptedWords(sessionId, "user").get(0));
        assertDoesNotThrow(() -> gameService.endGame(sessionId));
    }

    /**
     * Verifies that submitting the same valid word twice does not award points twice
     * and does not duplicate the accepted-word entry.
     *
     * The same user is added to the session and starts the game, which makes that
     * user the host under the current Issue 2 rules.
     */
    @Test
    void submitWord_duplicateWord_doesNotScoreTwice() {
        User user = mock(User.class);
        int sessionId = 5;

        when(user.getUsername()).thenReturn("user");
        when(userRegulation.getUser("user")).thenReturn(user);

        assertDoesNotThrow(() -> gameService.addPlayer(sessionId, "user"));
        assertNotNull(gameService.startGame(sessionId, "user"));

        WordSubmissionResult first = gameService.submitWord(sessionId, "user", "CAT");
        WordSubmissionResult second = gameService.submitWord(sessionId, "user", "CAT");

        assertTrue(first.isAccepted());

        assertFalse(second.isAccepted());
        assertTrue(second.isDuplicate());
        assertTrue(second.isValid());
        assertEquals(0, second.getPointsAwarded());
        assertEquals(100, second.getCurrentScore());
        assertEquals(100, gameService.getScore(sessionId, "user"));
        assertEquals(1, gameService.getAcceptedWords(sessionId, "user").size());
        assertDoesNotThrow(() -> gameService.endGame(sessionId));
    }
}