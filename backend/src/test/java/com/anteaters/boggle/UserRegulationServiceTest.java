package com.anteaters.boggle;

import com.anteaters.boggle.service.UserRegulationService;
import com.anteaters.boggle.repository.UserRepository;
import com.anteaters.boggle.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the bcakend UserRegulationService
 * @see UserRegulationService
 */
@SpringBootTest
public class UserRegulationServiceTest{
    @Autowired
    private UserRepository repo;
    @Autowired
    private UserRegulationService service;

    /**
     * Initialize instance of the service to be tested
     */
    @BeforeEach
    void setup(){
        repo.deleteAll();
        service.clearAllLogin();
    }

    /**
     * Registering a user with a new username should return true
     */
    @Test
    void newUserRegistered(){
        boolean status = service.createNewAccount("username", "12345");
        assertTrue(status);
    }

    /**
     * For invalid usernames such as empty string, or null, the service should throw an exception
     */
    @Test
    void registerInvalidUesrnameThrowsException(){
        assertThrows(IllegalArgumentException.class, () -> service.createNewAccount("", "12345"));
        assertThrows(IllegalArgumentException.class, () -> service.createNewAccount(null, "12345"));
    }

    /**
     * For invalid password such as empty string, or null, the service should throw an exception
     */
    @Test
    void registerInvalidPasswordThrowsException(){
        assertThrows(IllegalArgumentException.class, () -> service.createNewAccount("username", ""));
        assertThrows(IllegalArgumentException.class, () -> service.createNewAccount("username", null));
    }

    /**
     * The service should not register a user twice
     */
    @Test
    void registerOldUserRejected(){
        service.createNewAccount("username", "12345");
        boolean status = service.createNewAccount("username", "pwd");
        assertFalse(status);

        status = service.createNewAccount("username", "12345");
        assertFalse(status);
    }

    /**
     * A new user with the correct credential who is registered but not yet loggedin should be accepted
     */
    @Test
    void newLoginAccepted(){
        service.createNewAccount("username", "12345");
        boolean status = service.login("username", "12345");
        assertTrue(status);
    }

    /**
     * A new user with the wrong password who is registered but not yet loggedin should not be accepted
     */
    @Test
    void incorrectLoginPasswordRejected(){
        service.createNewAccount("username", "12345");
        boolean status = service.login("username", "wrongpwd");
        assertFalse(status);
    }

    /**
     * A user who never registered should be rejected
     */
    @Test
    void unregisteredUserRejected(){
        boolean status = service.login("username", "12345");
        assertFalse(status);
    }

    /**
     * Empty or null userrname or password should throws exception
     */
    @Test
    void invalidLoginInputThrowsException(){
        assertThrows(IllegalArgumentException.class, () -> service.login(null, "12345"));
        assertThrows(IllegalArgumentException.class, () -> service.login("", "12345"));
        assertThrows(IllegalArgumentException.class, () -> service.login("username", null));
        assertThrows(IllegalArgumentException.class, () -> service.login("username", ""));
    }

    /**
     * isLoggedIn should not return true until we have correctly logged a user in
     */
    @Test
    void isLoggedInTest(){
        assertFalse(service.isLoggedIn("username"));

        service.createNewAccount("username", "12345");
        assertFalse(service.isLoggedIn("username"));

        service.login("username", "wrongpwd");
        assertFalse(service.isLoggedIn("username"));

        service.login("username", "12345");
        assertTrue(service.isLoggedIn("username"));
    }

    /**
     * The getUser() method should return a valid user entity with the correct fields
     */
    @Test
    void getLoggedInUserReturned(){
        String username = "username", pwd = "password";
        assertNull(service.getUser(username));

        service.createNewAccount(username, pwd);
        service.login(username, pwd);

        User user = service.getUser(username);
        assertNotNull(user);
        assertEquals(user.getUsername(), username);
        assertEquals(user.getPassword(), pwd);
        assertEquals(user.getMatchesWon(), 0);
        assertEquals(user.getHighScore(), 0);
    }

    /**
     * getUser() should throw exception when passed with null or empty string
     */
    @Test
    void geUserInvalidInputThrowsException(){
        assertThrows(IllegalArgumentException.class, () -> service.getUser(null));
        assertThrows(IllegalArgumentException.class, () -> service.getUser(""));
    }
}