package gr.hua.fitTrack.core.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.Date;

import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;

@Service
public class JwtService {
    private final SecretKey key;
    private final String issuer;
    private final String audience;
    private final Long ttlMinutes; // time-to-live

    public JwtService(@Value("${app.jwt.secret}") final String secret,
                      @Value("${app.jwt.issuer}") final String issuer,
                      @Value("${app.jwt.audience}") final String audience,
                      @Value("${app.jwt.ttl-minutes}") final Long ttlMinutes
    ) {
        if(secret == null){ throw new NullPointerException("secret is null"); }
        if(secret.isBlank()) { throw new IllegalArgumentException("secret is blank"); }
        if(issuer == null){ throw new NullPointerException("issuer is null"); }
        if(issuer.isBlank()){ throw new IllegalArgumentException("issuer is blank"); }
        if(audience == null){ throw new NullPointerException("audience is null"); }
        if(audience.isBlank()){ throw new IllegalArgumentException("issuer is blank"); }
        if(ttlMinutes <= 0){ throw new IllegalArgumentException(); }


        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.issuer = issuer;
        this.audience = audience;
        this.ttlMinutes = ttlMinutes;
    }

    public String issue(final String subject,final Collection<String> roles){
        final Instant now = Instant.now();
        return Jwts.builder()
                .subject(subject)
                .issuer(issuer)
                .audience().add(this.audience)
                .and()
                .claim("roles", roles)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(Duration.ofMinutes(this.ttlMinutes))))
                .signWith(this.key)
                .compact();
    }

    public Claims parse(final String token){
        return Jwts.parser()
                .requireAudience(this.audience)
                .requireIssuer(this.issuer)
                .verifyWith(this.key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

}
