import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Technical Validation Suite for Boggle Dictionary.
 * Strictly monitors Boolean returns and enforces functional failures for placeholders.
 * * @author James Kang
 */
class DictionaryTest {

    private Dictionary dict;
/*
    @BeforeEach
    void setUp() {
        dict = new Dictionary();
        dict.addWord("APPLE");
        dict.addWord("DOG");
    }
*/
    @BeforeEach
    void setUp() {
        dict = new Dictionary();


        System.gc();
        long before = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

        // data loading
        long startTime = System.currentTimeMillis();
        dict.initialize("temp_dictionary/dictionary.txt", false);
        long endTime = System.currentTimeMillis();

        // memory after loading
        long after = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

        // result (MB)
        System.out.println(">>> [REPORT] Data Fetching Performance:");
        System.out.println(" - Time Taken: " + (endTime - startTime) + "ms");
        System.out.println(" - Memory Used: " + (after - before) / (1024 * 1024) + " MB");
    }

    @Test
    @DisplayName("Edge Case: Exhaustive Length Boundary Testing")
    void testWordLengthBoundaries() {
        System.out.println(">>> CRITICAL PATH: Word Length Boundary Verification");

        String[] testCases = {"", "A", "IT", "DOG"};
        for (String word : testCases) {
            boolean actual = dict.contains(word);
            int len = word.length();
            boolean expected = (len >= 3);

            System.out.printf(" - Input: [%s] (Length: %d) | Expected: %b | Actual: %b\n",
                    word, len, expected, actual);

            assertEquals(expected, actual, "FAIL: Boolean mismatch for input '" + word + "'");
        }
    }

    @Test
    @DisplayName("Edge Case: Multi-variant Case-Insensitivity")
    void testExtensiveCaseInsensitivity() {
        System.out.println(">>> CRITICAL PATH: Case-Insensitivity Verification");

        String[] variations = {"apple", "APPLE", "aPpLe", "ApplE"};
        for (String v : variations) {
            boolean actual = dict.contains(v);
            System.out.printf(" - Input Variation: [%s] | Expected: true | Actual: %b\n", v, actual);
            assertTrue(actual, "FAIL: Case-insensitivity broken for: " + v);
        }
    }

    @Test
    @DisplayName("Functionality: Non-existing Word Verification")
    void testNonExistingWords() {
        System.out.println(">>> CRITICAL PATH: False Positive Verification");

        String[] invalidWords = {"BANANAA", "XYZ", "BOGGLERRR", "AAAAA"};
        for (String word : invalidWords) {
            boolean actual = dict.contains(word);
            // We expect false because these words were never added.
            System.out.printf(" - Searching for: [%s] | Expected: false | Actual: %b\n", word, actual);
            assertFalse(actual, "FAIL: False positive! Method returned true for missing word: " + word);
        }
    }

    @Test
    @DisplayName("Acceptance Criteria: API Connectivity & Data Retrieval")
    void testRemoteDataFetching() {
        System.out.println(">>> CRITICAL PATH: Remote API Functional Verification");

        Dictionary remoteDict = new Dictionary();
        String apiEndpoint = "https://api.example.com/v1/words";

        System.out.println(" - Initializing remote source: " + apiEndpoint);
        remoteDict.initialize(apiEndpoint, true);

        // At this stage, since API logic is NOT implemented, contains() must return false.
        // However, the Acceptance Criteria REQUIRE data to be fetched.
        boolean actual = remoteDict.contains("APPLE");
        System.out.printf(" - Verifying data retrieval: Expected: true (per AC) | Actual: %b\n", actual);

        if (!actual) {
            System.out.println(" [RESULT] FAILED: Remote API integration is a placeholder only.");
            fail("Acceptance Criteria Failure: API fetching is NOT implemented. Expected dictionary to be populated.");
        }
    }

    @Test
    @DisplayName("Large Scale Test: Prefix validation with 100k+ words")
    void testLargeScalePrefix() {
        // dictionary.txt에 있을 법한 단어들의 접두사로 테스트
        assertTrue(dict.isPrefix("APP"), "Should find prefix 'APP' in 100k words");
        assertTrue(dict.isPrefix("BOG"), "Should find prefix 'BOG' in 100k words");
        assertFalse(dict.isPrefix("ZZZXXX"), "Should not find impossible prefix");
    }
}