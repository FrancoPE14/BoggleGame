package com.anteaters.boggle.controller;

import com.anteaters.boggle.service.WordVerificationService;
import com.anteaters.boggle.service.GameService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.anteaters.boggle.model.WordSubmissionResult;
import com.anteaters.boggle.service.GameService;
import org.springframework.web.bind.annotation.PostMapping;
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
        try {
            service.startGame(sessionId);
        }catch(Exception e){
            succeed = false;
        }
        return Map.of(
                "username", username,
                "sessionId", sessionId,
                "status", succeed
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
     * End a game session
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
                "sessionId", sessionId,
                "status", succeed
        );
    }
}