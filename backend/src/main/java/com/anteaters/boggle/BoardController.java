package main.java.com.anteaters.boggle;

import org.springframework.web.bind.annotation.*;

@RestController
public class BoggleBoardController {

    @GetMapping("[your mapping here]")
    public String[][] generateBoard(){

        BoggleBoard b = new BoggleBoard();
        return b.boardToStringArray();

    }

}
