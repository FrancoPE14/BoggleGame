package com.anteaters.boggle.model;

/**
 * Lightweight DTO used by the multiplayer lobby page.
 *
 * This record intentionally exposes only high-level session metadata required
 * to render the lobby list, such as whether the session has started, how many
 * players are currently inside it, and which player currently owns host authority.
 *
 * @param sessionId unique id of the session
 * @param started whether the session is currently in active gameplay
 * @param playerCount number of players currently assigned to the session
 * @param maxPlayers maximum player capacity of the session
 * @param hostUsername username of the host player, or null if the session is empty
 */
public record SessionSummary(
        int sessionId,
        boolean started,
        int playerCount,
        int maxPlayers,
        String hostUsername
) {}