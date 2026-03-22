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
     * Usage example: POST /api/register?username=user
     *
     * @param username username of the user who wants to start the game
     * @return
     */
    @PostMapping("/api/start")
    public Map<String, Object> startGame(@RequestParam String username){
        boolean status = service.startGame(); // removed  username for now for the sake of testing
        return Map.of(
                "username", username,
                "status", status
        );
    }

    /**
     * Submits a word for a particular player in the current game session.
     *
     * Usage example: POST /api/submit-word?username=user&word=apple
     *
     * @param username username of the player submitting the word
     * @param word raw word submitted by the player
     * @return WordSubmissionResult containing acceptance status, score update,
     *         and accepted words state
     */
    @PostMapping("/api/submit-word")
    public WordSubmissionResult submitWord(
            @RequestParam String username,
            @RequestParam String word
    ) {
        return service.submitWord(username, word);
    }

    /**
     * End the current game session
     *
     * @return JSON containing info about whether the game has ended successfully
     */
    @PostMapping("/api/end")
    public Map<String, Object> endGame(){
        boolean status = service.endGame();
        return Map.of(
                "status", status
        );
    }
}