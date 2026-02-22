package com.anteaters.boggle.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
public class HomeController {

    @GetMapping("/api/test")
    public String test() {
        return "Backend is running";
    }
}