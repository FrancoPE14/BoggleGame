package com.anteaters.boggle.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.stereotype.Controller;

@Controller
public class HomeController {

    @GetMapping("/api/test")
    public String test() {
        return "Backend is running";
    }
}