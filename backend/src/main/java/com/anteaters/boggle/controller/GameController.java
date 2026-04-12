package com.anteaters.boggle.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import com.anteaters.boggle.model.WordSubmissionResult;
import com.anteaters.boggle.service.WordVerificationService;
import com.anteaters.boggle.service.GameService;
import com.anteaters.boggle.service.BoggleBoard;
import java.util.Map;

@RestController
public class GameController{
    private final GameService service;

    public GameController(GameService service){
        this.service = service;
    }

    /**
     *
     * Usage example: POST /api/register?sessionId=0&username=user
     *
     * @param sessionId the id of the session to start
     * @param username username of the user who wants to start the game, currently unused
     * @return JSON representing the call status
     */
    @PostMapping("/api/start")
    public Map<String, Object> startGame(@RequestParam int sessionId, @RequestParam String username) {
        boolean succeed = true;
        BoggleBoard board = null;
        String errMsg = null;
        try {
            board = service.startGame(sessionId);
        }catch(Exception e){
            succeed = false;
            errMsg = e.getMessage();
        }
        return Map.of(
                "status", succeed,
                "username", username,
                "sessionId", sessionId,
                "board", board,
                "err", errMsg
        );
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
     * End a game session, usually should not call because the session now ends automatically 3 minutes after start
     *
     * @return JSON containing info about whether the game has ended successfully
     */
    @PostMapping("/api/end")
    public Map<String, Object> endGame(@RequestParam int sessionId) {
        boolean succeed = true;
        try {
            service.endGame(sessionId);
        }catch(Exception e){
            succeed = false;
        }
        return Map.of(
                "status", succeed,
                "sessionId", sessionId
        );
    }
}