package com.anteaters.boggle.service;

import org.springframework.stereotype.Service;
import com.anteaters.boggle.repository.UserRepository;
import com.anteaters.boggle.entity.User;
import com.anteaters.boggle.service.Player;
import com.anteaters.boggle.service.UserRegulationService;
import com.anteaters.boggle.service.ScoreCalculator;
import com.anteaters.boggle.service.BoggleBoard;
import java.util.ArrayList;

/**
 * A service module for the multiplayer feature that will be implemented in the future
 */
@Service
public class GameService{
    private final UserRepository repo;
    private final UserRegulationService userRegulation;
    private final ScoreCalculator calc;
    private final ArrayList<Player> players;
    private BoggleBoard board;

    /**
     * The constructor for GameService
     * @param repo auto-created by Sprint Boot
     * @param userRegulation the service that contains all login information
     */
    public GameService(UserRepository repo, UserRegulationService userRegulation){
        this.repo = repo;
        this.userRegulation = userRegulation;
        calc = new ScoreCalculator();
        players = new ArrayList<Player>();
    }

    /**
     * Checks if the user is logged in, if so, start a game session.
     * A game cannot be started more than once.
     *
     * @return whether the game session is successfully started
     */
    public boolean startGame(){
        if(!players.isEmpty()) {
            return false;
        }
        // create gameboard logic
        return true;
    }

    /**
     * Checks if the user is logged in, if so, add them to the game.
     *
     * @param username the username of the player to be added
     * @return whether the player is successfully added
     */
    public boolean addPlayer(String username){
        User user = userRegulation.getUser(username);
        if(user==null){
            return false;
        }
        Player player = new Player(user, calc);
        players.add(player);
        return true;
    }

    /**
     * End the game session and flushes all player data to the database
     *
     * @return whether the game ends successfully
     */
    public boolean endGame(){
        if(players.isEmpty()){ // game not started
            return false;
        }
        for(Player player : players){
            player.updateHighScore();
            player.flushToDB(repo);
        }
        return true;
    }
}