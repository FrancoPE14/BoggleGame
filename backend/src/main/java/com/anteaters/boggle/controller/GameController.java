package com.anteaters.boggle.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.anteaters.boggle.model.FinishAckResponse;
import com.anteaters.boggle.model.SessionSummary;
import com.anteaters.boggle.model.WordSubmissionResult;
import com.anteaters.boggle.service.GameService;
import com.anteaters.boggle.service.BoggleBoard;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST controller for multiplayer game operations.
 *
 * This controller exposes session listing, lobby join, game start,
 * finish acknowledgement, word submission, and manual game end endpoints.
 */
@RestController
public class GameController{
    private final GameService service; // service layer that owns multiplayer session operations

    /**
     * Creates the controller with the service dependency used by all game endpoints.
     *
     * @param service multiplayer game service
     */
    public GameController(GameService service){
        this.service = service;
    }

    /**
     * Returns summary information for all multiplayer sessions.
     *
     * Usage example: GET /api/sessions
     *
     * This endpoint is intended for the lobby list page so the frontend can display
     * session occupancy, start state, and host ownership before a player joins.
     *
     * @return list of session summaries for lobby rendering
     */
    @GetMapping("/api/sessions")
    public List<SessionSummary> getSessions() {
        return service.getSessionSummaries();
    }

    /**
     * Adds a player to a lobby session before the game starts.
     *
     * Usage example: POST /api/join?sessionId=0&username=user
     *
     * This endpoint is designed for the waiting-room flow. On success, the player is added
     * to the target session, host ownership is assigned if this is the first player, and
     * the updated lobby state is broadcast to connected clients through SSE.
     *
     * @param sessionId the id of the session to join
     * @param username username of the player joining the session
     * @return updated session summary after the join succeeds
     */
    @PostMapping("/api/join")
    public SessionSummary joinSession(@RequestParam int sessionId, @RequestParam String username) {
        return service.joinSession(sessionId, username);
    }

    /**
     * Starts a game session.
     *
     * Usage example: POST /api/start?sessionId=0&username=user
     *
     * Only the current host of the session may start the game. The username is
     * forwarded to the service layer so the backend can enforce host-only start behavior.
     *
     * @param sessionId the id of the session to start
     * @param username username of the user requesting the start action
     * @return JSON representing whether the request succeeded and, if successful, the board state
     */
    @PostMapping("/api/start")
    public Map<String, Object> startGame(@RequestParam int sessionId, @RequestParam String username) {
        boolean succeed = true;
        BoggleBoard board = null;
        String errMsg = null;
        try {
            board = service.startGame(sessionId, username);
        }catch(Exception e){
            succeed = false;
            errMsg = e.getMessage();
        }

        Map<String, Object> response = new HashMap<>();
        response.put("status", succeed);
        response.put("username", username);
        response.put("sessionId", sessionId);
        response.put("board", board == null ? null : board.boardToStringArray());
        response.put("err", errMsg);

        return response;
    }

    /**
     * Records that a player has finished the current round after the timer has ended.
     *
     * Usage example: POST /api/finish?sessionId=0&username=user
     *
     * This endpoint is intended to be called after the client receives the round-ended
     * event and has completed all local round-finalization work.
     *
     * @param sessionId the id of the session
     * @param username username of the player sending the acknowledgement
     * @return structured response describing the updated finish state
     */
    @PostMapping("/api/finish")
    public FinishAckResponse finishRound(@RequestParam int sessionId, @RequestParam String username) {
        return service.acknowledgePlayerFinished(sessionId, username);
    }

    /**
     * Submits a word for a particular player in a game session.
     *
     * Usage example: POST /api/submit-word?sessionId=0&username=user&word=apple
     *
     * @param sessionId the id of the game session
     * @param username username of the player submitting the word
     * @param word raw word submitted by the player
     * @return WordSubmissionResult containing acceptance status, score update,
     *         and accepted words state
     */
    @PostMapping("/api/submit-word")
    public WordSubmissionResult submitWord(
            @RequestParam int sessionId,
            @RequestParam String username,
            @RequestParam String word
    ) {
        return service.submitWord(sessionId, username, word);
    }

    /**
     * Ends a game session.
     *
     * Usage example: POST /api/end?sessionId=0
     *
     * This endpoint is typically not needed during normal gameplay because the session
     * currently ends automatically when later multiplayer endgame flow is completed.
     *
     * @param sessionId the id of the session to end
     * @return JSON containing whether the end request completed successfully
     */
    @PostMapping("/api/end")
    public Map<String, Object> endGame(@RequestParam int sessionId) {
        boolean succeed = true;
        try {
            service.endGame(sessionId);
        }catch(Exception e){
            succeed = false;
        }

        Map<String, Object> response = new HashMap<>();
        response.put("status", succeed);
        response.put("sessionId", sessionId);

        return response;
    }
}