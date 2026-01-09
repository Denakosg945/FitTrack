package gr.hua.fitTrack.core.security;

import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Component
public class TokenSigner {

    private static final String SECRET = "VERY_SECRET_KEY_CHANGE_ME";
    private static final String ALG = "HmacSHA256";

    public String sign(Long trainerId) {
        try {
            Mac mac = Mac.getInstance(ALG);
            mac.init(new SecretKeySpec(SECRET.getBytes(), ALG));

            String payload = trainerId.toString();
            byte[] signature = mac.doFinal(payload.getBytes());

            return Base64.getUrlEncoder().encodeToString(
                    (payload + "." + Base64.getUrlEncoder().encodeToString(signature)).getBytes()
            );
        } catch (Exception e) {
            throw new RuntimeException("Token signing failed", e);
        }
    }

    public Long verify(String token) {
        try {
            String decoded = new String(Base64.getUrlDecoder().decode(token));
            String[] parts = decoded.split("\\.");

            if (parts.length != 2) throw new IllegalArgumentException();

            Long trainerId = Long.parseLong(parts[0]);

            String expected = sign(trainerId);
            if (!expected.equals(token)) {
                throw new IllegalArgumentException("Invalid token");
            }

            return trainerId;
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid or tampered token");
        }
    }
}
