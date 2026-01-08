package gr.hua.fitTrack.web.rest.model;

/**
 * @see gr.hua.fitTrack.web.rest.AuthResource
 */

public record TokenResponse(
        String accessResource,
        String tokenType,
        long expiresIn
) {
}
