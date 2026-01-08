package gr.hua.fitTrack.core.security;

import java.util.Set;

public record APIClientDetails(
        String id,
        String secret,
        Set<String> roles
) {
}
