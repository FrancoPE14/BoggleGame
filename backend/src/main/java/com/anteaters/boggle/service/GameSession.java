package com.anteaters.boggle.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import com.anteaters.boggle.model.SessionSummary;
import com.anteaters.boggle.model.WordSubmissionResult;
import com.anteaters.boggle.repository.UserRepository;

/**
 * This class stores the state of a multiplayer game session, including the session id,
 * whether the game is active, the players in the session, the game board, and timing data.
 *
 * For the lobby flow, this class also keeps track of the host player and exposes
 * a compact summary view that can be returned by the lobby session API.
 *
 * For multiplayer lifecycle management, this class owns session-local completion state
 * so later phases can determine when all players have finished and when final results
 * are ready to be computed.
 */
public class GameSession{
    private static int idCnt = 0; // incremented for each session instance so every session id remains unique

    private int numPlayers; // current number of players assigned to this session
    private boolean gameStarted; // whether the game in this session has already started
    private boolean roundEnded; // whether the active round has already ended due to timer expiration
    private Player[] players; // fixed-capacity player storage determined when the session is created
    private BoggleBoard board; // board currently associated with this session
    private String[] settings; // TODO: to be implemented
    private long startTime = -1; // system time when the current game started, measured in milliseconds
    private long endTime = -1; // system time when the current game should end, measured in milliseconds
    private ScheduledExecutorService scheduler = null; // one timer executor per session
    private ScheduledFuture<?> scheduledFuture = null; // scheduled task used to drive per-second timer updates

    private final int sessionId; // unique identifier of the sessions
    private final int maxPlayers; // max number of players that this session can have
    private final WordSubmissionService wordSubmissionService; // centralized word submission pipeline used by this session
    private final UserRepository repo; // repository used to flush player results when the session ends
    private final long duration; // duration of the game in seconds
    private final GameEventService gameEventService; // optional SSE publisher used for session-local round-ended broadcast
    private String hostUsername; // username of the host player, assigned to the first player who joins
    private Set<String> finishedPlayers; // players who have completed the current round
    private boolean resultsComputed; // whether final results have been computed for the current round

    /**
     * Initializes a game session with a fixed player capacity.
     *
     * This overload keeps backward compatibility for callers that do not need
     * session-local round-ended broadcasting.
     *
     * @param maxPlayers the maximum number of players allowed in this session
     * @param repo repository used to persist player data when the session ends
     * @param service word submission service shared by players in this session
     * @throws IllegalArgumentException if maxPlayers is non-positive or if repo/service is null
     */
    public GameSession(int maxPlayers, UserRepository repo, WordSubmissionService service){
        this(maxPlayers, repo, service, null);
    }

    /**
     * Initializes a game session with a fixed player capacity and optional
     * access to the SSE event publisher.
     *
     * The caller determines how many players the session can hold. A fresh session
     * starts in lobby state with no players, no host, and a newly generated board.
     *
     * @param maxPlayers the maximum number of players allowed in this session
     * @param repo repository used to persist player data when the session ends
     * @param service word submission service shared by players in this session
     * @param gameEventService optional SSE publisher for round-ended events
     * @throws IllegalArgumentException if maxPlayers is non-positive or if repo/service is null
     */
    public GameSession(int maxPlayers, UserRepository repo, WordSubmissionService service, GameEventService gameEventService){
        if(maxPlayers<=0 || service==null || repo==null){
            throw new IllegalArgumentException();
        }

        sessionId = idCnt++;

        // initialize fields
        gameStarted = false;
        roundEnded = false;
        numPlayers = 0;
        this.maxPlayers = maxPlayers;
        players = new Player[maxPlayers];
        board = new BoggleBoard();
        hostUsername = null;
        finishedPlayers = new HashSet<>();
        resultsComputed = false;

        wordSubmissionService = service;
        this.repo = repo;
        this.gameEventService = gameEventService;

        // timer related
        duration = 180; // TODO: hard-coded for now, change for future implementation of settings
    }

    /**
     * Returns the unique id assigned to this session.
     *
     * @return session id
     */
    public int getId(){
        return sessionId;
    }

    /**
     * Returns the usernames of all players currently in this session.
     *
     * @return list of player usernames in join order
     */
    public List<String> getPlayerUsernames() {
        List<String> usernames = new ArrayList<>();

        for (Player player : players) {
            if (player != null) {
                usernames.add(player.getUsername());
            }
        }

        return usernames;
    }

    /**
     * Returns whether the game in this session has already started.
     *
     * @return true if gameplay has started, otherwise false
     */
    public boolean isStarted(){
        return gameStarted;
    }

    /**
     * Returns whether the current round has already ended.
     *
     * @return true if the timer has expired and the round is over, otherwise false
     */
    public boolean isRoundEnded() {
        return roundEnded;
    }

    /**
     * Returns the board currently associated with this session.
     *
     * @return current Boggle board
     */
    public BoggleBoard getBoard() {
        return board;
    }

    /**
     * Returns the maximum number of players supported by this session.
     *
     * @return configured player capacity
     */
    public int getMaxPlayers(){
        return maxPlayers;
    }

    /**
     * Returns the current number of players in this session.
     *
     * @return current player count
     */
    public int getNumPlayers() {
        return numPlayers;
    }

    /**
     * Returns the username of the session host.
     *
     * The host is the first player who successfully joins the lobby.
     * This value is null while the session is empty.
     *
     * @return host username, or null if no player has joined yet
     */
    public String getHostUsername() {
        return hostUsername;
    }

    /**
     * Returns whether the provided username currently owns host authority
     * for this session.
     *
     * @param username username to compare against the current host
     * @return true if the provided username matches the host, otherwise false
     * @throws IllegalArgumentException if username is null
     */
    public boolean isHost(String username) {
        if (username == null) {
            throw new IllegalArgumentException("Argument is null");
        }
        return hostUsername != null && hostUsername.equals(username);
    }

    /**
     * Returns whether final results have already been computed for the current round.
     *
     * @return true if results have been computed, otherwise false
     */
    public boolean isResultsComputed() {
        return resultsComputed;
    }

    /**
     * Updates whether final results have been computed for the current round.
     *
     * @param value new results-computed state
     */
    public void setResultsComputed(boolean value) {
        resultsComputed = value;
    }

    /**
     * Records that the specified player has finished the current round.
     *
     * The round must already have ended before players are allowed to acknowledge
     * completion through the backend.
     *
     * @param username username of the finished player
     * @throws IllegalArgumentException if username is null or the player is not part of this session
     * @throws IllegalStateException if the round has not ended yet
     */
    public void markPlayerFinished(String username) {
        if (username == null) {
            throw new IllegalArgumentException("Argument is null");
        }
        if (!isPlayerAdded(username)) {
            throw new IllegalArgumentException("Player is not in this session");
        }
        if (!roundEnded) {
            throw new IllegalStateException("Round has not ended yet");
        }
        finishedPlayers.add(username);
    }

    /**
     * Returns whether the specified player has already been marked as finished
     * for the current round.
     *
     * @param username username to check
     * @return true if the player has been marked finished, otherwise false
     * @throws IllegalArgumentException if username is null
     */
    public boolean isPlayerFinished(String username) {
        if (username == null) {
            throw new IllegalArgumentException("Argument is null");
        }
        return finishedPlayers.contains(username);
    }

    /**
     * Returns whether all current players in the session have been marked as finished.
     *
     * @return true if all players are finished and the session is non-empty, otherwise false
     */
    public boolean haveAllPlayersFinished() {
        return numPlayers > 0 && finishedPlayers.size() == numPlayers;
    }

    /**
     * Clears all round-completion tracking for this session.
     *
     * This is used when a session resets or when a new round is about to begin.
     */
    public void clearFinishedPlayers() {
        finishedPlayers.clear();
    }

    /**
     * Builds a read-only session summary for lobby APIs.
     *
     * This method is intended for the multiplayer lobby list endpoint so the frontend
     * can display high-level session state without depending on internal session fields.
     *
     * @return immutable session summary for lobby display
     */
    public SessionSummary toSummary() {
        return new SessionSummary(
                sessionId,
                gameStarted,
                numPlayers,
                maxPlayers,
                hostUsername
        );
    }

    /**
     * Starts this game session.
     *
     * Once the session starts, the timer begins running and the session transitions
     * from lobby state to active gameplay state.
     *
     * The round-completion state is also reset so the session lifecycle is internally
     * consistent at the beginning of each game.
     *
     * @return the board associated with this session
     * @throws IllegalStateException if the session has already started
     */
    public BoggleBoard startGame(){
        if(gameStarted){
            throw new IllegalStateException("Session has already started");
        }

        clearFinishedPlayers();
        resultsComputed = false;
        roundEnded = false;

        startTime = System.currentTimeMillis();
        endTime = startTime + 1000*duration;
        scheduler = Executors.newScheduledThreadPool(1);
        scheduledFuture = scheduler.scheduleAtFixedRate(() -> updateFrontendTimer(), 0, 1, TimeUnit.SECONDS);
        gameStarted = true;
        return board;
    }

    /**
     * Ends only the active round while keeping the session state alive.
     *
     * This is triggered when the round timer expires. The session remains active
     * for finish acknowledgements and later result computation.
     *
     * On success, a `round-ended` SSE event is broadcast to the session if an
     * event publisher is available.
     *
     * @throws IllegalStateException if the game has not started
     */
    public void endRound() {
        if(!gameStarted){
            throw new IllegalStateException("Game have not started");
        }
        if(roundEnded){
            return;
        }

        roundEnded = true;

        if(scheduledFuture != null){
            scheduledFuture.cancel(false);
            scheduledFuture = null;
        }
        if(scheduler != null){
            scheduler.shutdownNow();
            scheduler = null;
        }

        ArrayList<Map<String, Object>> finalScores = new ArrayList<>();
        for (Player player : players) {
            if (player != null) {
                finalScores.add(Map.of("username", player.getUsername(), "score", player.getScore()));
            }
        }

        finalScores.sort((a, b) -> Integer.compare((int) b.get("score"), (int) a.get("score")));

        if(gameEventService != null){
            gameEventService.broadcastToSession(
                    String.valueOf(sessionId),
                    "round-ended",
                    Map.of(
                            "sessionId", sessionId,
                            "finalScores", finalScores
                    )
            );
        }
    }

    /**
     * Advances the server-side timer state for this session.
     *
     * This method currently handles timeout detection only. Frontend timer
     * synchronization is deferred to a later issue.
     */
    private void updateFrontendTimer(){
        long curTime = System.currentTimeMillis();
        long timeLeft = duration - (curTime - startTime)/1000; // remaining time in seconds
        // TODO: send timeLeft to the frontend
        if(curTime >= endTime){
            endGame();
        }
    }

    /**
     * Submits a word for a particular player through the centralized
     * WordSubmissionService pipeline.
     *
     * Flow:
     * - look up the player in the current session
     * - pass that player's tracker to WordSubmissionService
     * - return the result object containing acceptance and score state
     *
     * Word submission is not allowed after the round has already ended.
     *
     * @param username username of the player submitting the word
     * @param word raw submitted word
     * @return result of submission including score state
     * @throws IllegalStateException if the game has not started yet or the round has already ended
     * @throws IllegalArgumentException if the player does not belong to this session
     */
    public WordSubmissionResult submitWord(String username, String word) {
        if (!gameStarted) {
            throw new IllegalStateException("Game has not started yet.");
        }
        if (roundEnded) {
            throw new IllegalStateException("Round has already ended.");
        }

        Player player = getPlayer(username);
        if (player == null) {
            throw new IllegalArgumentException("Player not found in current game.");
        }

        return wordSubmissionService.submitWord(word, player.getTracker());
    }

    /**
     * Computes final results for the session.
     *
     * @return map of username -> score
     */
    public Map<String, Integer> computeFinalScores() {
        Map<String, Integer> scores = new HashMap<>();

        for (Player player : players) {
            if (player != null) {
                scores.put(player.getUsername(), player.getScore());
            }
        }

        return scores;
    }

    /**
     * Determines the winner of the session.
     *
     * @return username of the winner
     */
    public String determineWinner() {
        String winner = null;
        int maxScore = -1;

        for (Player player : players) {
            if (player != null) {
                int score = player.getScore();
                if (score > maxScore) {
                    maxScore = score;
                    winner = player.getUsername();
                }
            }
        }

        return winner;
    }

    /**
     * Ends the current game session and flushes all player data to the database.
     *
     * This method stops the timer if still running, persists relevant player state,
     * resets in-memory player progress, and returns the session to an empty lobby state.
     *
     * @throws IllegalStateException if the game has not started
     */
    public void endGame(){
        if(!gameStarted){ // game not started
            throw new IllegalStateException("Game have not started");
        }

        if(scheduledFuture != null){
            scheduledFuture.cancel(false);
            scheduledFuture = null;
        }
        if(scheduler != null){
            scheduler.shutdownNow();
            scheduler = null;
        }

        startTime = -1;
        endTime = -1;

        ArrayList<Map<String, Object>> finalScores = new ArrayList<>();
        for (Player player : players) {
            if(player!=null) {
                finalScores.add(Map.of("username", player.getUsername(), "score", player.getScore()));
                player.updateHighScore();
                player.flushToDB(repo);
                player.reset();
            }
        }

        finalScores.sort((a, b) -> Integer.compare((int) b.get("score"), (int) a.get("score")));

        if(gameEventService != null){
            gameEventService.broadcastToSession(
                    String.valueOf(sessionId),
                    "game-ended",
                    Map.of(
                            "sessionId", sessionId,
                            "finalScores", finalScores
                    )
            );
        }

        resetSession();
    }

    /**
     * Checks whether a player with the given username is already assigned to this session.
     *
     * @param username username to search for
     * @return true if the player is already part of this session, otherwise false
     * @throws IllegalArgumentException if username is null
     */
    public boolean isPlayerAdded(String username){
        if(username==null){
            throw new IllegalArgumentException("Argument is null");
        }
        // run a linear search over the players array
        for(Player player : players){
            if(player!=null && player.getUsername().equals(username)){
                return true;
            }
        }
        return false;
    }

    /**
     * Returns the current score for a player in the session.
     *
     * @param username username of the player
     * @return current score
     * @throws IllegalArgumentException if username is null or the player is not found in this session
     */
    public int getScore(String username) {
        if(username==null){
            throw new IllegalArgumentException("Argument is null");
        }
        Player player = getPlayer(username);
        if (player == null) {
            throw new IllegalArgumentException("Player not found in current game.");
        }
        return player.getScore();
    }

    /**
     * Returns the accepted words for a player in the session.
     *
     * A copy is returned so callers cannot mutate the player's internal accepted-word state.
     *
     * @param username username of the player
     * @return accepted words for the specified player
     * @throws IllegalArgumentException if username is null or the player is not found in this session
     */
    public ArrayList<String> getAcceptedWords(String username) {
        if(username==null){
            throw new IllegalArgumentException("Argument is null");
        }
        Player player = getPlayer(username);
        if (player == null) {
            throw new IllegalArgumentException("Player not found in current game.");
        }
        return new ArrayList<>(player.getAcceptedWords());
    }

    /**
     * Adds a player to this session.
     *
     * A player may only be added if the session is still in lobby state, has available capacity,
     * and does not already contain that username. The first player who joins becomes the host.
     *
     * @param newPlayer player to add
     * @throws IllegalArgumentException if newPlayer is null
     * @throws IllegalStateException if the player is already in the session, the session is full,
     *                               or the game has already started
     */
    public void addPlayer(Player newPlayer){
        if(newPlayer==null){
            throw new IllegalArgumentException("Argument is null");
        }
        if(isPlayerAdded(newPlayer.getUsername())){
            throw new IllegalStateException("Player is already added to this session");
        }
        if(numPlayers>=maxPlayers){
            throw new IllegalStateException("Session is full, cannot add more players");
        }
        if(gameStarted){
            throw new IllegalStateException("Session has already started, cannot add more players");
        }

        players[numPlayers++] = newPlayer;

        if(hostUsername == null){
            hostUsername = newPlayer.getUsername(); // assign host ownership to the first player who enters the lobby
        }
    }

    /**
     * Resets the static session id counter.
     *
     * This is primarily useful in tests where deterministic session ids are required.
     */
    public static void resetIdCnt(){
        idCnt = 0;
    }

    /**
     * Returns the player object for the specified username.
     *
     * @param username username to search for
     * @return matching player, or null if the username does not belong to this session
     */
    private Player getPlayer(String username) {
        for (Player player : players) {
            if (player!=null && player.getUsername().equals(username)) {
                return player;
            }
        }
        return null;
    }

    /**
     * Resets this session back to an empty lobby state.
     *
     * This clears the active gameplay state, removes all players, clears host ownership,
     * clears completion tracking, and creates a fresh board for the next game.
     */
    private void resetSession(){
        gameStarted = false;
        roundEnded = false;
        numPlayers = 0;
        board = new BoggleBoard();
        hostUsername = null; // clear host ownership when the session returns to an empty lobby
        clearFinishedPlayers();
        resultsComputed = false;
        startTime = -1;
        endTime = -1;

        for(int i=0; i<maxPlayers; i++){
            players[i] = null;
        }
    }
}