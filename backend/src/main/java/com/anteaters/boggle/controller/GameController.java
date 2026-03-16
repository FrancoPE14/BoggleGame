package com.anteaters.boggle.controller;

import com.anteaters.boggle.service.WordVerificationService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
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
        boolean status = service.startGame(username);
        return Map.of(
                "username", username,
                "status", status
        );
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