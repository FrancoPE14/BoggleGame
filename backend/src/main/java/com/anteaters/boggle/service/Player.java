package com.anteaters.boggle.service;

import com.anteaters.boggle.entity.User;
import com.anteaters.boggle.service.ScoreTracker;
import com.anteaters.boggle.service.ScoreCalculator;
import java.util.List;
import java.util.ArrayList;

/**
 * Used to record player status in a particular game, associated with a user
 */
public class Player{
    private final User user;
    private final ScoreTracker tracker;
    private final ScoreCalculator scoreCalc;

    /**
     * @param user a valid user instance with username initialized
     * @param scoreCalc used to calculate scores for players
     */
    public Player(User user, ScoreCalculator scoreCalc){
        if(user.getUsername()==null || user.getUsername().equals("")){
            throw IllegalArgumentException("Must associate player instance with a valid user with username.");
        }
        tracker = new ScoreTracker();
        this.scoreCalc = scoreCalc;
    }

    /**
     * @return the username associated with this player
     */
    public String getUsername(){
        return user.getUsername();
    }

    /**
     * @return the current score of this player
     */
    public int getScore(){
        return tracker.getScore;
    }

    /**
     * @return a list containing all accepted word strings
     */
    public List<String> getAcceptedWords(){
        return tracker.getAcceptedWords();
    }

    /**
     * Checks if a word is already found by the player
     *
     * @param word the word to be verified
     * @return whether the word is previously found
     */
    public boolean hasWord(String word){
        return tracker.hasWord(word);
    }

    /**
     * If the word is already recorded, the word will not be recorded again and the score will not be upated.
     * Otherwise, this method calculates and updates the score of the new word for the player and record the word in
     * the acccepted words list
     *
     * @param word the word to be added
     * @return whether a new word is being added to the list, and the score increased
     */
    public boolean recordWord(String word){
        int score = scoreCalc.calculateScore(word);
        return tracker.recordWord(word, score);
    }

    /**
     * Reset the tracker to empty
     */
    public void reset(){
        tracker.reset();
    }
}