package com.anteaters.boggle.service;

import org.springframework.stereotype.Service;
import com.anteaters.boggle.repository.UserRepository;
import com.anteaters.boggle.entity.User;
import com.anteaters.boggle.model.FinishAckResponse;
import com.anteaters.boggle.model.SessionSummary;
import com.anteaters.boggle.model.WordSubmissionResult;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * Service layer for multiplayer session management.
 *
 * This service owns the registry of available sessions and coordinates
 * session-level operations such as joining a lobby, starting a game,
 * submitting words, and broadcasting SSE updates to connected clients.
 *
 * GameService is intentionally kept as the session registry and request router.
 * Session-local lifecycle state, such as host ownership, completion tracking,
 * and round-local endgame state, belongs to GameSession.
 */
@Service
public class GameService {
    private final UserRepository repo;
    private final UserRegulationService userRegulation;
    private final ScoreCalculator calc;
    private final WordSubmissionService wordSubmissionService;
    private static final int MAX_SESSIONS = 10; // the max number of sessions we can create

    private Map<Integer, GameSession> sessions; // all available multiplayer sessions indexed by session id
    private final GameEventService gameEventService; // SSE event publisher used to notify players within a session

    /**
     * Constructs the multiplayer game service and creates the fixed pool of sessions.
     *
     * For the current implementation, all sessions are created up front and each session
     * is configured to support up to four players.
     *
     * @param repo auto-created by Spring Boot
     * @param userRegulation service that contains login/session user information
     * @param wordSubmissionService centralized word submission pipeline
     * @param gameEventService SSE publisher used for real-time client updates
     */
    public GameService(UserRepository repo,
                       UserRegulationService userRegulation,
                       WordSubmissionService wordSubmissionService,
                       GameEventService gameEventService
    ) {
        this.repo = repo;
        this.userRegulation = userRegulation;
        this.wordSubmissionService = wordSubmissionService;
        this.gameEventService = gameEventService;
        calc = new ScoreCalculator();
        sessions = new HashMap<Integer, GameSession>();

        // for now all sessions are 4 players
        for(int i=0; i<MAX_SESSIONS; i++){
            GameSession session = new GameSession(4, repo, wordSubmissionService, gameEventService); // create the fixed pool of lobby sessions
            sessions.put(session.getId(), session);
        }
    }

    /**
     * Finds the session id currently associated with the specified user.
     *
     * @param username username to search for
     * @return a non-negative session id, or -1 if the user is not assigned to any session
     */
    private int getSessionId(String username){
        for(Map.Entry<Integer, GameSession> e : sessions.entrySet()){
            if(e.getValue().isPlayerAdded(username)){
                return e.getKey();
            }
        }
        return -1;
    }

    /**
     * Returns summary information for all multiplayer sessions.
     *
     * This method supports the lobby list page by converting each session
     * into a compact DTO intended for frontend display.
     *
     * @return list of session summaries for the lobby page
     */
    public List<SessionSummary> getSessionSummaries() {
        return sessions.values()
                .stream()
                .map(GameSession::toSummary)
                .toList();
    }

    /**
     * Adds a logged-in user to a specific multiplayer lobby session.
     *
     * Validation rules:
     * - the target session must exist
     * - the user must be logged in
     * - the user must not already belong to this session
     * - the user must not already belong to another session
     *
     * Additional session-specific validation, such as full-capacity or started-session checks,
     * is delegated to GameSession.addPlayer().
     *
     * On success, a lobby-update SSE event is broadcast to all clients currently subscribed
     * to the session so the waiting room can refresh in real time.
     *
     * @param sessionId the id of the target session
     * @param username the username of the player to be added
     * @return updated session summary after the player has joined
     * @throws IllegalArgumentException if the session does not exist or the user is not logged in
     * @throws IllegalStateException if the player is already assigned to a session
     */
    public SessionSummary joinSession(int sessionId, String username){
        GameSession session = sessions.get(sessionId);
        if(session == null){
            throw new IllegalArgumentException("The session of this id does not exists");
        }


        // TODO: use this once login issue is fixed
        //User user = userRegulation.getUser(username);
        //if (user == null) {
        //    throw new IllegalArgumentException("The user is not logged in");
        //}

        // TODO: delete this and use above once login issue is fixed.
        User user;
        try {
            user = userRegulation.getUser(username);
        } catch (Exception e) {
            user = new User(username, "password");
        }
        if (user == null) {
            user = new User(username, "password");
        }

        int existingSessionId = getSessionId(username);
        if(existingSessionId != -1){
            if(existingSessionId == sessionId){
                throw new IllegalStateException("Player is already in this session");
            }
            throw new IllegalStateException("Player is already in another session");
        }

        Player player = new Player(user, calc);
        session.addPlayer(player);

        SessionSummary summary = session.toSummary();

        gameEventService.broadcastToSession(
                String.valueOf(sessionId),
                "lobby-update",
                Map.of(
                        "sessionId", summary.sessionId(),
                        "started", summary.started(),
                        "playerCount", summary.playerCount(),
                        "maxPlayers", summary.maxPlayers(),
                        "hostUsername", summary.hostUsername(),
                        "joinedUsername", username
                )
        );

        return summary;
    }

    /**
     * Returns the GameSession instance for the given session id.
     *
     * This method is primarily used for testing purposes to access
     * internal session state.
     *
     * @param sessionId id of the session
     * @return GameSession instance
     */
    public GameSession getSession(int sessionId) {
        if (!sessions.containsKey(sessionId)) {
            throw new IllegalArgumentException("The session of this id does not exist");
        }
        return sessions.get(sessionId);
    }

    /**
     * Starts a game for the specified session.
     *
     * Only the session host is allowed to start the game. The caller must be
     * logged in, must belong to the target session, and must match the current host.
     *
     * On success, the generated board is broadcast to every player connected to
     * the session through the game-started SSE event.
     *
     * @param sessionId the session that should start
     * @param username the user requesting the start action
     * @return the BoggleBoard object associated with that session
     * @throws IllegalArgumentException if the session id does not exist or the user is not logged in
     * @throws IllegalStateException if the user is not in the session or is not the host
     */
    public BoggleBoard startGame(int sessionId, String username) {
        if(!sessions.containsKey(sessionId)){
            throw new IllegalArgumentException("The session of this id does not exists");
        }

        // TODO: Use this once login issue is resolved.
        //User user = userRegulation.getUser(username);
        //if (user == null) {
        //    throw new IllegalArgumentException("The user is not logged in");
        //}

        // TODO: delete this and use above once login issue is resolved.
        User user;
        try {
            user = userRegulation.getUser(username);
        } catch (Exception e) {
            user = new User(username, "password");
        }
        if (user == null) {
            user = new User(username, "password");
        }

        GameSession session = sessions.get(sessionId);

        if (!session.isPlayerAdded(username)) {
            throw new IllegalStateException("Player is not in this session");
        }

        if (!session.isHost(username)) {
            throw new IllegalStateException("Only the host can start the game");
        }

        BoggleBoard board = session.startGame();

        gameEventService.broadcastToSession(
                String.valueOf(sessionId),
                "game-started",
                Map.of(
                        "sessionId", sessionId,
                        "board", board.boardToStringArray()
                )
        );

        return board;
    }

    /**
     * Records that a player has finished the current round.
     *
     * The caller must be logged in and must already belong to the target session.
     * The session itself owns the round-ended check and all finished-player tracking.
     *
     * @param sessionId id of the target session
     * @param username username of the player sending the finish acknowledgement
     * @return response describing the resulting session completion state
     * @throws IllegalArgumentException if the session does not exist or the user is not logged in
     * @throws IllegalStateException if the player is not in the session or the round has not ended yet
     */
    public FinishAckResponse acknowledgePlayerFinished(int sessionId, String username) {
        if(!sessions.containsKey(sessionId)){
            throw new IllegalArgumentException("The session of this id does not exists");
        }

        // TODO: use this once login issue is fixed.
        //User user = userRegulation.getUser(username);
        //if (user == null) {
        //    throw new IllegalArgumentException("The user is not logged in");
        //}

        //TODO: delete this and use above once login issue is fixed.
        GameSession session = sessions.get(sessionId);
        User user;
        try {
            user = userRegulation.getUser(username);
        } catch (Exception e) {
            user = new User(username, "password");
        }
        if (user == null) {
            user = new User(username, "password");
        }

        if (!session.isPlayerAdded(username)) {
            throw new IllegalStateException("Player is not in this session");
        }

        session.markPlayerFinished(username);

        FinishAckResponse res = new FinishAckResponse(
                sessionId,
                username,
                session.isRoundEnded(),
                session.isPlayerFinished(username),
                session.haveAllPlayersFinished()
        );

        checkAndBroadcastResults(sessionId);

        return res;
    }

    /**
     * Checks if the user is logged in and, if so, adds them to the session.
     *
     * This method predates the dedicated lobby join flow. The newer joinSession()
     * method should be preferred for the waiting-room flow because it also returns
     * a structured session summary and enforces cross-session membership checks.
     *
     * @param sessionId the id of the game session
     * @param username the username of the player to be added
     */
    public void addPlayer(int sessionId, String username){
        if(!sessions.containsKey(sessionId)){
            throw new IllegalArgumentException("The session of this id does not exists");
        }
        GameSession session = sessions.get(sessionId);

        // TODO: use this once login issue is fixed.
        //User user = userRegulation.getUser(username);
        //if (user == null) {
        //    throw new IllegalArgumentException("The user is not logged in");
        //}

        // TODO: delete this and use above once login issue is fixed.
        User user;
        try {
            user = userRegulation.getUser(username);
        } catch (Exception e) {
            user = new User(username, "password");
        }
        if (user == null) {
            user = new User(username, "password");
        }
        Player player = new Player(user, calc);
        session.addPlayer(player);

        gameEventService.broadcastToSession(
                String.valueOf(sessionId),
                "lobby-update",
                Map.of(
                        "sessionId", sessionId,
                        "username", username,
                        "playerCount", session.getNumPlayers(),
                        "maxPlayers", session.getMaxPlayers()
                )
        );
    }

    /**
     * Submits a word for a particular player through the centralized
     * WordSubmissionService pipeline.
     *
     * Flow:
     * - look up the session
     * - delegate submission to the session
     * - broadcast the updated player state to the session over SSE
     *
     * @param sessionId the id of the game session
     * @param username username of the player submitting the word
     * @param word raw submitted word
     * @return result of submission including score and accepted-word state
     */
    public WordSubmissionResult submitWord(int sessionId, String username, String word) {
        if(!sessions.containsKey(sessionId)){
            throw new IllegalArgumentException("The session of this id does not exists");
        }
        GameSession session = sessions.get(sessionId);
        WordSubmissionResult result = session.submitWord(username, word);

        gameEventService.broadcastToSession(
                String.valueOf(sessionId),
                "game-state",
                Map.of(
                        "sessionId", sessionId,
                        "username", username,
                        "originalWord", result.getOriginalWord(),
                        "normalizedWord", result.getNormalizedWord(),
                        "accepted", result.isAccepted(),
                        "duplicate", result.isDuplicate(),
                        "valid", result.isValid(),
                        "pointsAwarded", result.getPointsAwarded(),
                        "currentScore", result.getCurrentScore(),
                        "acceptedWords", result.getAcceptedWords()
                )
        );

        return result;
    }

    /**
     * Returns the current score for a player in the active game.
     *
     * @param sessionId the id of the game session
     * @param username username of the player
     * @return current score
     */
    public int getScore(int sessionId, String username) {
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
        if(!sessions.containsKey(sessionId)){
            throw new IllegalArgumentException("The session of this id does not exists");
        }
        GameSession session = sessions.get(sessionId);

        return session.getAcceptedWords(username);
    }

    private void checkAndBroadcastResults(int sessionId) {
        GameSession session = sessions.get(sessionId);

        if (!session.haveAllPlayersFinished()) return;
        if (session.isResultsComputed()) return;

        Map<String, Integer> scores = session.computeFinalScores();
        String winner = session.determineWinner();

        session.setResultsComputed(true);

        gameEventService.broadcastToSession(
                String.valueOf(sessionId),
                "game-results",
                Map.of(
                        "sessionId", sessionId,
                        "scores", scores,
                        "winner", winner
                )
        );
    }

    /**
     * Ends a game session and flushes all player data to the database.
     *
     * GameService intentionally remains the orchestration layer that triggers
     * session shutdown and broadcasts the resulting game-ended event. The session-local
     * lifecycle state itself is still owned by GameSession.
     *
     * @param sessionId the id of the game session
     */
    public void endGame(int sessionId){
        if(!sessions.containsKey(sessionId)){
            throw new IllegalArgumentException("The session of this id does not exists");
        }
        GameSession session = sessions.get(sessionId);

        session.endGame();
    }
}