package org.yourcompany.reccomendersys;

import java.time.LocalDateTime;

/**
 * Represents a user's interaction with a track
 */
public class Interaction {
    private final int userId;
    private final int trackId;
    private final double rating;
    private final LocalDateTime timestamp;
    private final String interactionType;
    
    public Interaction(int userId, int trackId, double rating, LocalDateTime timestamp, String interactionType) {
        this.userId = userId;
        this.trackId = trackId;
        this.rating = rating;
        this.timestamp = timestamp;
        this.interactionType = interactionType;
    }
    
    public int getUserId() {
        return userId;
    }
    
    public int getTrackId() {
        return trackId;
    }
    
    public double getRating() {
        return rating;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    public String getInteractionType() {
        return interactionType;
    }
    
    @Override
    public String toString() {
        return String.format("Interaction[userId=%d, trackId=%d, rating=%.2f, timestamp=%s, type=%s]",
                userId, trackId, rating, timestamp, interactionType);
    }
} 