package com.anteaters.boggle.model;

/**
 * Response DTO returned when a player acknowledges that they have finished
 * the current multiplayer round.
 *
 * @param sessionId id of the session the player belongs to
 * @param username username of the player sending the acknowledgement
 * @param roundEnded whether the session round has already ended
 * @param playerFinished whether this player is now marked as finished
 * @param allPlayersFinished whether all players in the session have finished
 */
public record FinishAckResponse(
        int sessionId,
        String username,
        boolean roundEnded,
        boolean playerFinished,
        boolean allPlayersFinished
) {}