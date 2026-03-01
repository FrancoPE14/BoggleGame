package com.anteaters.boggle.controller;

import com.anteaters.boggle.service.UserRegulationService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

/**
 * Controller for uesr verfication requests, including login and user registery
 */
@RestController
public class UserVerificationController{
    private final UserRegulationService userService;

    /**
     * Create a new user accout
     *
     * Usage example: POST /api/register?username=user&password=12345
     *
     * @param username username of the new user
     * @param password password of the new user
     * @return JSON containing registery result info
     */
    @GetMapping("/api/register")
    public Map<String, Object> createAccount(@RequestParam String username, @RequestParam String password){

    }

    /**
     * Login. Verify user name and password and register them into the list of login users.
     *
     * Usage example: POST /api/login?username=user&password=12345
     *
     * @param username username of the user who wants login
     * @param password password of the user who wants login
     * @return JSON containing login result info
     */
    @GetMapping("/api/login")
    public Map<String, Object> login(@RequestParam String username, @RequestParam String password){

    }
}