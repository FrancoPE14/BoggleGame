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
        if(username==null || username.equals("")){
            throw new IllegalArgumentException("Username must not be empty or null.");
        }
        if(password==null || password.equals("")){
            throw new IllegalArgumentException("Password must not be empty or null.");
        }
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
        if(username==null || username.equals("")){
            throw new IllegalArgumentException("Username must not be empty or null.");
        }
        if(password==null || password.equals("")){
            throw new IllegalArgumentException("Password must not be empty or null.");
        }
        if(repo.findById(username).isEmpty()==false){
            return false; // user with that username already exists
        }
        String hashedPwd = hash(password);
        User curUser = new User(username, hashedPwd);
        repo.save(curUser);
        return true;
    }

    /**
     * Perform a linear search on the login user ArrayList to check if a user is logged into their account
     *
     * @param username the username to be checked
     * @return whether the user has logged in
     */
    public boolean isLoggedIn(String username){
        if(username==null || username.equals("")){
            throw new IllegalArgumentException("Username must not be empty or null.");
        }
        for(User user : loginUsers){
            if(user.getUsername().equals(username)){
                return true;
            }
        }
        return false;
    }

    /**
     * Perform a linear search to the login user ArrayList to find the user
     *
     * @param username the username to be found
     * @return the User object representing the user if they have logged in, otherwise return null
     */
    public User getUser(String username){
        if(username==null || username.equals("")){
            throw new IllegalArgumentException("Username must not be empty or null.");
        }
        for(User user : loginUsers){
            if(user.getUsername().equals(username)){
                return user;
            }
        }
        return null;
    }

    /**
     * Remove all loggedin users so no user is logged in
     */
    public void clearAllLogin(){
        loginUsers.clear();
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