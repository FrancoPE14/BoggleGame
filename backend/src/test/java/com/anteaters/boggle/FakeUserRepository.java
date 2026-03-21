package com.anteaters.boggle;

import com.anteaters.boggle.entity.User;
import com.anteaters.boggle.repository.UserRepository;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * AI-generated fake in-memory implementation of UserRepository for plain JUnit testing.
 *
 * Purpose:
 * - Replaces the real Spring Data / database-backed repository in unit tests.
 * - Stores User entities in a local HashMap keyed by username.
 *
 * Notes:
 * - AI-generated.
 * - Intended for testing only, not production use.
 */
class FakeUserRepository implements UserRepository {
    private final Map<String, User> users = new HashMap<>();

    /**
     * AI-generated.
     * Saves a single user entity into the in-memory map.
     *
     * @param entity the user entity to save
     * @return the same saved entity
     */
    @Override
    public <S extends User> S save(S entity) {
        users.put(entity.getUsername(), entity);
        return entity;
    }

    /**
     * AI-generated.
     * Finds a user by username.
     *
     * @param username the username to search for
     * @return an Optional containing the user if found, otherwise empty
     */
    @Override
    public Optional<User> findById(String username) {
        return Optional.ofNullable(users.get(username));
    }

    /**
     * AI-generated.
     * Checks whether a user with the given username exists.
     *
     * @param username the username to check
     * @return true if the user exists, false otherwise
     */
    @Override
    public boolean existsById(String username) {
        return users.containsKey(username);
    }

    /**
     * AI-generated.
     * Returns all users currently stored in the in-memory repository.
     *
     * @return an Iterable over all stored users
     */
    @Override
    public Iterable<User> findAll() {
        return users.values();
    }

    /**
     * AI-generated.
     * Returns all users whose usernames appear in the provided iterable.
     *
     * @param usernames the usernames to look up
     * @return an Iterable of matching users
     */
    @Override
    public Iterable<User> findAllById(Iterable<String> usernames) {
        List<User> result = new ArrayList<>();
        for (String username : usernames) {
            if (users.containsKey(username)) {
                result.add(users.get(username));
            }
        }
        return result;
    }

    /**
     * AI-generated.
     * Counts the total number of users currently stored.
     *
     * @return the number of stored users
     */
    @Override
    public long count() {
        return users.size();
    }

    /**
     * AI-generated.
     * Deletes the user associated with the given username.
     *
     * @param username the username of the user to delete
     */
    @Override
    public void deleteById(String username) {
        users.remove(username);
    }

    /**
     * AI-generated.
     * Deletes the given user entity from the in-memory repository.
     *
     * @param entity the user entity to delete
     */
    @Override
    public void delete(User entity) {
        users.remove(entity.getUsername());
    }

    /**
     * AI-generated.
     * Deletes all users whose usernames appear in the provided iterable.
     *
     * @param usernames the usernames of users to delete
     */
    @Override
    public void deleteAllById(Iterable<? extends String> usernames) {
        for (String username : usernames) {
            users.remove(username);
        }
    }

    /**
     * AI-generated.
     * Deletes all provided user entities from the in-memory repository.
     *
     * @param entities the user entities to delete
     */
    @Override
    public void deleteAll(Iterable<? extends User> entities) {
        for (User user : entities) {
            users.remove(user.getUsername());
        }
    }

    /**
     * AI-generated.
     * Removes all users from the in-memory repository.
     */
    @Override
    public void deleteAll() {
        users.clear();
    }

    /**
     * AI-generated.
     * Saves all provided user entities into the in-memory repository.
     *
     * @param entities the user entities to save
     * @return an Iterable containing all saved entities
     */
    @Override
    public <S extends User> Iterable<S> saveAll(Iterable<S> entities) {
        List<S> saved = new ArrayList<>();
        for (S entity : entities) {
            users.put(entity.getUsername(), entity);
            saved.add(entity);
        }
        return saved;
    }
}