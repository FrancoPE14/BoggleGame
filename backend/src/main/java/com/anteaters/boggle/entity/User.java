package com.anteaters.boggle.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.Lob;

/**
 * Each instance of this should corresponds to an entry in the User table of the database
 */
@Entity
@Table(name = "boggle_user")
public class User{
    @Id
    @Column(name = "user_name")
    private String username;
    private String password;
    @Column(name = "matches_won")
    private int matchesWon;
    @Column(name = "highest_score")
    private int highScore;
    @Lob
    @Column(name = "profile_picture")
    private String profilePicture;

    /**
     * @return the profile picture of the user as a base64-encoded string, or null if not set
     */
    public String getProfilePicture() {
        return profilePicture;
    }

    /**
     * @param profilePicture the new profile picture as a base64-encoded string
     */
    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    /**
     * The default constructor, Spring Boot will use this when they read entries from db and fill the private
     * fields automatically
     */
    public User(){
    }

    /**
     * Contructor for the user entity
     *
     * @param username user name of the new user
     * @param password password of the new user
     */
    public User(String username, String password){
        this.username = username;
        this.password = password;
        this.matchesWon = 0;
        this.highScore = 0;
    }

    /**
     * @return the username of this user
     */
    public String getUsername(){
        return username;
    }

    /**
     * @param username the new username
     */
    public void setUsername(String username){
        this.username = username;
    }

    /**
     * @return the hashed password of this user
     */
    public String getPassword(){
        return password;
    }

    /**
     * @return the number of matches won by the user
     */
    public int getMatchesWon(){
        return matchesWon;
    }

    /**
     * Increase the matchWon field of the user by 1
     */
    public void updateMatchesWon(){
        matchesWon++;
    }

    /**
     * @return the highest past score of the user
     */
    public int getHighScore(){
        return highScore;
    }

    /**
     * Update the highScore field of the user only when the new score is higher
     * @param highScore the new highest score
     */
    public void updateHighScore(int highScore){
        this.highScore = Math.max(this.highScore, highScore);
    }

    /**
     * Get a string representing the user, possibly used for debugging
     *
     * @return a string representing the user containing username, number of matches won, and the highest score
     */
    @Override
    public String toString(){
        return "Username: " + username + "; Matches won: " + matchesWon + "; Highest score: " + highScore + ";";
    }
}