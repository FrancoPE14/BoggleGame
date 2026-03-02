package com.anteaters.boggle.service;

import org.springframework.stereotype.Service;
import com.anteaters.boggle.repository.UserRepository;
import com.anteaters.boggle.entity.User;
import java.util.ArrayList;
import java.util.Optional;

/**
 * This class provide user authentication and user registeration services
 */
@Service
public class UserRegulationService{
    private final UserRepository repo;
    private final ArrayList<User> loginUsers; // temporary method to implement the login mechanism, should change in futrue

    /**
     * Constructor for UserRegulationService
     * @param repo auto-created by Sprint Boot
     */
    public UserRegulationService(UserRepository repo){
        this.repo = repo;
        this.loginUsers = new ArrayList<User>();
    }

    /**
     * Try to log the user in by verifying username and password and keep them in an ArrayList of login users
     *
     * @param username username of the current user
     * @param password the unhashed password
     * @return true if and only if there is a user with the exact username and password
     */
    public boolean login(String username, String password){
        Optional<User> curUser = repo.findById(username);
        if(curUser.isEmpty()){ // do not find username
            return false;
        }
        String realPwd = curUser.get().getPassword();
        String hashedPwd = hash(password); // insecure, should improve in the future by utlizing Spring Security
        if(realPwd.equals(hashedPwd)==false){
            return false;
        }
        loginUsers.add(curUser.get());
        return true;
    }

    /**
     * Register the new user into the database as an entry of the User table, if a user with that username have not
     * exists yet.
     *
     * @param username the username of the new user
     * @param password the unhashed password of the new user
     * @return true only when the user is successfully registered
     */
    public boolean createNewAccount(String username, String password){
        if(repo.findById(username).isEmpty()==false){
            return false; // user with that username already exists
        }
        String hashedPwd = hash(password);
        User curUser = new User(username, hashedPwd);
        repo.save(curUser);
        return true;
    }

    /**
     * Currently uses the java default hash function
     *
     * @param str the string to be hashed
     * @return the hashed string
     */
    private String hash(String str){
        return "" + str.hashCode();
    }
}