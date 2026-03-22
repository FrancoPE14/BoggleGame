package main.java.com.anteaters.boggle;

import org.springframework.web.bind.annotation.*;
import com.anteaters.boggle.service.BoggleBoard;

@RestController
public class BoardController {

    @GetMapping("[your mapping here]")
    public String[][] generateBoard(){

        BoggleBoard b = new BoggleBoard();
        return b.boardToStringArray();

    }

}
