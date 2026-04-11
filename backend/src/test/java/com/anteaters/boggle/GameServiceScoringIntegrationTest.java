package com.anteaters.boggle;

import com.anteaters.boggle.dictionary.DictionaryTrie;
import com.anteaters.boggle.dictionary.InMemoryCustomDictionaryStore;
import com.anteaters.boggle.entity.User;
import com.anteaters.boggle.model.WordSubmissionResult;
import com.anteaters.boggle.repository.UserRepository;
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
 */
public class GameServiceScoringIntegrationTest {

    private static DictionaryTrie trie;

    private InMemoryCustomDictionaryStore store;
    private WordVerificationService verificationService;
    private WordSubmissionService submissionService;

    private UserRepository repo;
    private UserRegulationService userRegulation;
    private GameService gameService;

    @BeforeAll
    static void loadDictionary() {
        trie = new DictionaryTrie();
        trie.loadDictionary();
    }

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
        gameService = new GameService(repo, userRegulation, submissionService);
    }

    @Test
    void submitWord_updatesScoreAndAcceptedWordsThroughGameService() {
        User user = mock(User.class);
        int sessionId = 5;
        when(user.getUsername()).thenReturn("user");
        when(userRegulation.getUser("user")).thenReturn(user);

        // NOTE:
        // This assumes addPlayer()/startGame(username) work in the current GameService implementation.
        gameService.addPlayer(sessionId, "user");
        gameService.startGame(sessionId);

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
    }

    @Test
    void submitWord_duplicateWord_doesNotScoreTwice() {
        User user = mock(User.class);
        when(user.getUsername()).thenReturn("user");
        when(userRegulation.getUser("user")).thenReturn(user);

        int sessionId = 5;

        assertDoesNotThrow(() -> gameService.addPlayer(sessionId, "user"));
        assertNotNull(gameService.startGame(sessionId));

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
    }
}