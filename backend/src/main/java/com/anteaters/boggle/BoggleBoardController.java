package main.java.com.anteaters.boggle;

import org.springframework.web.bind.annotation.*;


/**
 * REST API endpoint for board generation.
 *
 * Delegates generation logic to BoggleBoard.
 */
@RestController
public class BoggleBoardController {

    /**
     * Generates a Boggle board
     *
     * Example:
     * GET /api/generate
     *
     * @return JSON containing a 2D array of strings representing the board
     */
    @GetMapping("/api/generate")
    public String[][] generateBoard(){

        BoggleBoard b = new BoggleBoard();
        return b.boardToStringArray();

    }

}
