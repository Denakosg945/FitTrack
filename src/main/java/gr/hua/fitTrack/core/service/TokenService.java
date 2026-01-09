package gr.hua.fitTrack.core.service;

public interface TokenService {

    /**
     * Creates a signed token for a trainer
     */
    String allocateToken(Long trainerId);

    /**
     * Verifies token and returns trainerId
     * Throws IllegalArgumentException if invalid
     */
    Long verifyAndExtractTrainerId(String token);
}

