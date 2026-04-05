package com.anteaters.boggle.service;

import org.springframework.stereotype.Service;
import com.anteaters.boggle.repository.UserRepository;
import com.anteaters.boggle.entity.User;
import com.anteaters.boggle.service.Player;
import com.anteaters.boggle.service.UserRegulationService;
import com.anteaters.boggle.service.ScoreCalculator;
import com.anteaters.boggle.service.BoggleBoard;
import com.anteaters.boggle.service.GameSession;
import com.anteaters.boggle.model.WordSubmissionResult;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

/**
 * A service module for the multiplayer feature that will be implemented in the future
 */
@Service
public class GameService {
    private final UserRepository repo;
    private final UserRegulationService userRegulation;
    private final ScoreCalculator calc;
    private final WordSubmissionService wordSubmissionService;
    private static final int MAX_SESSIONS = 100; // the max number of sessions we can create

    private Map<Integer, GameSession> sessions;

    /**
     * The constructor for GameService, this will create all game sessions
     *
     * @param repo           auto-created by Sprint Boot
     * @param userRegulation the service that contains all login information
     */
    public GameService(UserRepository repo, UserRegulationService userRegulation, WordSubmissionService wordSubmissionService) {
        this.repo = repo;
        this.userRegulation = userRegulation;
        this.wordSubmissionService = wordSubmissionService;
        calc = new ScoreCalculator();
        sessions = new HashMap<Integer, GameSession>();
        // TODO: specify the size (number of players) of each game session
        // for now all sessions are 3 players
        for(int i=0; i<MAX_SESSIONS; i++){
            GameSession session = new GameSession(3, repo, wordSubmissionService);
            sessions.put(session.getId(), session);
        }
    }

    /**
     * Find the session id associate with the user
     *
     * @param username from the user to be checked
     * @return a non-negative session id, or -1 if the user is never added to any session
     */
    private int getSessionId(String username){
        // use the map iterator to linearly search through all entries
        for(Map.Entry<Integer, GameSession> e : sessions.entrySet()){
            if(e.getValue().isPlayerAdded(username)){
                return e.getKey();
            }
        }
        return -1;
    }

    /**
     * Checks if the user is logged in, if so, start a game session.
     * A game cannot be started more than once.
     *
     * @return the BoggleBoard object associate with that session
     * @param sessionId the sesssion that we want to start
     */
    public BoggleBoard startGame(int sessionId) {
        if(!sessions.containsKey(sessionId)){
            throw new IllegalArgumentException("The session of this id does not exists");
        }
        GameSession session = sessions.get(sessionId);
        return session.startGame();
    }

    /**
     * Checks if the user is logged in, if so, add them to the session.
     *
     * @param sessionId the id of the game session
     * @param username the username of the player to be added
     */
    public void addPlayer(int sessionId, String username){
        // parameter check - sessionId
        if(!sessions.containsKey(sessionId)){
            throw new IllegalArgumentException("The session of this id does not exists");
        }
        GameSession session = sessions.get(sessionId);

        // parameter check - username
        User user = userRegulation.getUser(username);
        if (user == null) {
            throw new IllegalArgumentException("The user is not logged in");
        }

        session.addPlayer(player);
    }

    /**
     * Submits a word for a particular player through the centralized
     * WordSubmissionService pipeline.
     * <p>
     * Flow:
     * - look up player in current game
     * - pass that player's tracker to WordSubmissionService
     * - receive the result object containing score / accepted words state
     *
     * @param sessionId the id of the game session
     * @param username username of the player submitting the word
     * @param word     raw submitted word
     * @return result of submission including score state
     */
    public WordSubmissionResult submitWord(int sessionId, String username, String word) {
        // parameter check - sessionId
        if(!sessions.containsKey(sessionId)){
            throw new IllegalArgumentException("The session of this id does not exists");
        }
        GameSession session = sessions.get(sessionId);

        return session.submitWord(username, word);
    }

    /**
     * Returns the current score for a player in the active game.
     *
     * @param sessionId the id of the game session
     * @param username username of the player
     * @return current score
     */
    public int getScore(int sessionId, String username) {
        // parameter check - sessionId
        if(!sessions.containsKey(sessionId)){
            throw new IllegalArgumentException("The session of this id does not exists");
        }
        GameSession session = sessions.get(sessionId);

        return session.getScore(username);
    }

    /**
     * Returns the accepted words list for a player in an active game.
     *
     * @param sessionId the id of the game session
     * @param username username of the player
     * @return accepted words in submission order
     */
    public ArrayList<String> getAcceptedWords(int sessionId, String username) {
        // parameter check - sessionId
        if(!sessions.containsKey(sessionId)){
            throw new IllegalArgumentException("The session of this id does not exists");
        }
        GameSession session = sessions.get(sessionId);

        return session.getAcceptedWords(username);
    }

    /**
     * End a game session and flushes all player data to the database
     *
     * @param sessionId the id of the game session
     */
    public void endGame(int sessionId){
        // parameter check - sessionId
        if(!sessions.containsKey(sessionId)){
            throw new IllegalArgumentException("The session of this id does not exists");
        }
        GameSession session = sessions.get(sessionId);

        session.endGame(username);
    }
}