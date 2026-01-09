package gr.hua.fitTrack.core.security;
import org.springframework.security.core.token.Sha512DigestUtils;

public final class TokenUtils {

    // ⚠️ ίδιο secret παντού
    private static final String SECRET = "FITTRACK_TRAINER_TOKEN_SECRET";

    private TokenUtils() {
    }

    // =========================
    // CREATE TOKEN
    // =========================
    public static String generateTrainerToken(Long trainerId) {
        String raw = trainerId + ":" + SECRET;
        return Sha512DigestUtils.shaHex(raw);
    }

    // =========================
    // VERIFY & EXTRACT
    // =========================
    public static Long verifyAndExtractTrainerId(String token) {

        if (token == null || token.isBlank()) {
            throw new IllegalArgumentException("Empty token");
        }

        // brute-force check (OK για assignment)
        for (long id = 1; id <= 10_000; id++) {
            if (generateTrainerToken(id).equals(token)) {
                return id;
            }
        }

        throw new IllegalArgumentException("Invalid trainer token");
    }
}

