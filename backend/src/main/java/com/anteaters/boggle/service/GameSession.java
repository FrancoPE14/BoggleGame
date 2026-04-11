package com.anteaters.boggle.service;

import com.anteaters.boggle.repository.UserRepository;
import com.anteaters.boggle.entity.User;
import com.anteaters.boggle.service.Player;
import com.anteaters.boggle.service.UserRegulationService;
import com.anteaters.boggle.service.ScoreCalculator;
import com.anteaters.boggle.service.BoggleBoard;
import com.anteaters.boggle.model.WordSubmissionResult;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * This class stores the state of a multiplayer game, including the session id, whether the game is active, the players
 * in the session, the game board, session setting, etc., and supports operations on these fields.
 */
public class GameSession{
    private static int idCnt = 0; // +1 after each session object is created so the id for each object can't repeat

    private int numPlayers; // the current number of players in the game
    private boolean gameStarted;
    private Player[] players; // fixed number of players determined at initialization of object
    private BoggleBoard board;
    private String[] settings; // TODO: to be implemented
    private long startTime = -1; // system time when the game starts in ms
    private long endTime = -1; // system time when the game would end in ms
    private ScheduledFuture<?> scheduledFuture = null;

    private final int sessionId; // unique identifier of the sessions
    private final int maxPlayers; // max number of players that this session can have
    private final WordSubmissionService wordSubmissionService;
    private final UserRepository repo;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1); // one timer thread for each session
    // TODO: maybe a part of settings?
    private final long duration; // duration of the game in seconds

    /**
     * Initializes the session, with caller deciding the number of players
     *
     * @param maxPlayers the number of players for this game session, must be positive
     * @param service a WordSubmissionService object
     * @throws IllegalArgumentException if maxPlayer is non-positive, or service, repo is null
     */
    public GameSession(int maxPlayers, UserRepository repo, WordSubmissionService service){
        if(maxPlayers<=0 || service==null || repo==null){
            throw new IllegalArgumentException();
        }

        sessionId = idCnt++;

        // initialize fields
        gameStarted = false;
        numPlayers = 0;
        this.maxPlayers = maxPlayers;
        players = new Player[maxPlayers];
        board = new BoggleBoard();

        wordSubmissionService = service;
        this.repo = repo;

        // timer related
        duration = 180; // TODO: hard-coded for now, change for future implementation of settings
    }

    /**
     * Get the unique session id
     *
     * @return the session id
     */
    public int getId(){
        return sessionId;
    }

    /**
     * Check if the game has already started
     *
     * @return whether the game has started
     */
    public boolean isStarted(){
        return gameStarted;
    }

    /**
     * Get the boggle board
     *
     * @return the boggle board of the session
     */
    public BoggleBoard getBoard() {
        return board;
    }

    /**
     * Get the max number of players allowed in this game
     *
     * @return the max number of players allowed
     */
    public int getMaxPlayers(){
        return maxPlayers;
    }

    /**
     * Start this game session, must call before calling any in-game methods
     *
     * @return the boggle board of this session
     * @throws IllegalStateException if the game has already started
     */
    public BoggleBoard startGame(){
        if(gameStarted){
            throw new IllegalStateException("Session has already started");
        }
        startTime = System.currentTimeMillis();
        endTime = startTime + 1000*duration;
        scheduledFuture = scheduler.scheduleAtFixedRate(() -> updateFrontendTimer(), 0, 1, TimeUnit.SECONDS);
        gameStarted = true;
        return board;
    }

    /**
     * Run at each second of the session to send time left to frontend and check if the game has ennded
     * TODO: finish the server-side event part
     */
    private void updateFrontendTimer(){
        long curTime = System.currentTimeMillis();
        long timeLeft = duration - (curTime - startTime)/1000;
        // TODO: send timeLeft to the frontend
        if(curTime >= endTime){
            endGame();
        }
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
     * @param username username of the player submitting the word
     * @param word     raw submitted word
     * @return result of submission including score state
     */
    public WordSubmissionResult submitWord(String username, String word) {
        if (!gameStarted) {
            throw new IllegalStateException("Game has not started yet.");
        }

        Player player = getPlayer(username);
        if (player == null) {
            throw new IllegalArgumentException("Player not found in current game.");
        }

        return wordSubmissionService.submitWord(word, player.getTracker());
    }

    /**
     * End the game session and flushes all player data to the database
     *
     * @throws IllegalStateException if the game have not already started
     */
    public void endGame(){
        if(!gameStarted){ // game not started
            throw new IllegalStateException("Game have not started");
        }

        // stop the timer
        scheduledFuture.cancel(false);
        scheduledFuture = null;
        startTime = -1;
        endTime = -1;

        for (Player player : players) {
            if(player!=null) {
                player.updateHighScore();
                player.flushToDB(repo);
                player.reset();
            }
        }
        resetSession();
    }

    /**
     * Check if a player with that username is in this session
     *
     * @param username the username to be checked
     * @return whether the player with that username is in this session
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
     * Returns the current score for a player in the session
     *
     * @param username username of the player
     * @return current score
     * @throws IllegalArgumentException if the username is null or player not found in the session
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
     * Returns the accepted words list for a player in the active game.
     *
     * @param username username of the player
     * @return accepted words in submission order
     * @throws IllegalArgumentException if the username is null or player not found in the session
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
     * Add a player to the session if the session is not already full and not yet started
     *
     * @param newPlayer the new Player object to be added to this session, GameService is responsible of creating it
     * @throws IllegalStateException if the session if already full
     * @throws IllegalArgumentException if newPlayer is null
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
    }

    /**
     * Reset the id counter, only for testing purpose, do not call
     */
    public static void resetIdCnt(){
        idCnt = 0;
    }

    /**
     * Finds a player in the current session by username.
     *
     * @param username username of the player
     * @return the Player object if found, otherwise null
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
     * Reinitailize all non-final fields to the initial state, re-generate a new boggle board
     */
    private void resetSession(){
        gameStarted = false;
        numPlayers = 0;
        board = new BoggleBoard();
        for(int i=0; i<maxPlayers; i++){
            players[i] = null;
        }
    }
}