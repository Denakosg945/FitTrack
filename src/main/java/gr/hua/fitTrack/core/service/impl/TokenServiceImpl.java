package gr.hua.fitTrack.core.service.impl;

import gr.hua.fitTrack.core.service.TokenService;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;

@Service
public class TokenServiceImpl implements TokenService {

    private static final String SECRET =
            "fittrack-very-secret-signing-key"; // άλλαξέ το σε prod

    private static final long EXPIRATION_SECONDS = 3600; // 1 ώρα

    // --------------------------------------------------
    // CREATE TOKEN
    // --------------------------------------------------
    @Override
    public String allocateToken(Long trainerId) {

        long expiresAt = Instant.now().getEpochSecond() + EXPIRATION_SECONDS;

        String payload = trainerId + ":" + expiresAt;
        String signature = sign(payload);

        String token = payload + ":" + signature;

        return Base64.getUrlEncoder()
                .encodeToString(token.getBytes(StandardCharsets.UTF_8));
    }


    // --------------------------------------------------
    // VERIFY TOKEN
    // --------------------------------------------------
    @Override
    public Long verifyAndExtractTrainerId(String token) {

        try {
            String decoded =
                    new String(
                            Base64.getUrlDecoder().decode(token),
                            StandardCharsets.UTF_8
                    );

            String[] parts = decoded.split(":");
            if (parts.length != 3) {
                throw new IllegalArgumentException("Invalid token format");
            }

            Long trainerId = Long.valueOf(parts[0]);
            long expiresAt = Long.parseLong(parts[1]);
            String signature = parts[2];

            // ⏰ expiry check
            if (Instant.now().getEpochSecond() > expiresAt) {
                throw new IllegalArgumentException("Token expired");
            }

            // 🔐 signature check
            String expectedSignature = sign(parts[0] + ":" + parts[1]);
            if (!expectedSignature.equals(signature)) {
                throw new IllegalArgumentException("Invalid token signature");
            }

            return trainerId;

        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid trainer token");
        }
    }

    // --------------------------------------------------
    // SIGN HELPER
    // --------------------------------------------------
    private String sign(String data) {

        try {
            Mac mac = Mac.getInstance("HmacSHA256");

            SecretKeySpec key =
                    new SecretKeySpec(
                            SECRET.getBytes(StandardCharsets.UTF_8),
                            "HmacSHA256"
                    );

            mac.init(key);

            byte[] raw =
                    mac.doFinal(data.getBytes(StandardCharsets.UTF_8));

            return Base64.getUrlEncoder().encodeToString(raw);

        } catch (Exception e) {
            throw new RuntimeException("Token signing failed", e);
        }
    }
}

