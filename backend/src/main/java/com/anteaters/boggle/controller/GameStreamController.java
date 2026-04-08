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

    // SSE connection endpoint
    @GetMapping(path = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter stream() {
        return gameEventService.subscribe();
    }

    // test purpose event
    @PostMapping("/test-event")
    public ResponseEntity<String> sendTestEvent() {
        gameEventService.broadcast(
                "test",
                Map.of("message", "hello from backend")
        );
        return ResponseEntity.ok("sent");
    }
}