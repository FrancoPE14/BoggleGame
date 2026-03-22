package com.anteaters.boggle.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.Arrays;

import org.springframework.web.bind.annotation.*;
import com.anteaters.boggle.service.BoggleBoard;

@RestController
public class BoardController {

    @GetMapping("/api/generate")
    public String[][] generateBoard(){

        BoggleBoard b = new BoggleBoard();
        return b.boardToStringArray();
    }

}
