package gr.hua.fitTrack.core.service.model;

import java.time.Instant;

public record PendingRegistration(CreatePersonRequest request, String code, Instant expiresAt) {
}
