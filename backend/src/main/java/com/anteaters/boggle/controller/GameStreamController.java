package com.anteaters.boggle.controller;

import com.anteaters.boggle.service.GameEventService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;

@RestController
@RequestMapping("/api/game")
public class GameStreamController {

    private final GameEventService gameEventService;

    public GameStreamController(GameEventService gameEventService) {
        this.gameEventService = gameEventService;
    }

    @GetMapping(path = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter stream(@RequestParam String sessionId) {
        return gameEventService.subscribe(sessionId);
    }

    @PostMapping("/test-event")
    public ResponseEntity<String> sendTestEvent(@RequestParam String sessionId) {
        gameEventService.broadcastToSession(
                sessionId,
                "test",
                Map.of(
                        "message", "hello from backend",
                        "sessionId", sessionId
                )
        );
        return ResponseEntity.ok("sent");
    }
}