package cat.cccb.tfg.exposicions.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class JwtService {

    @Value("${app.jwt.secret}")
    private String jwtSecretBase64;

    @Value("${app.jwt.expirationSeconds:86400}")
    private long expirationSeconds;

    public String generateToken(String email, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", role);

        Instant now = Instant.now();
        Instant exp = now.plusSeconds(expirationSeconds);

        return Jwts.builder()
                .claims(claims)
                .subject(email)
                .issuedAt(Date.from(now))
                .expiration(Date.from(exp))
                .signWith(signingKey())
                .compact();
    }

    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    public String extractRole(String token) {
        Object role = extractAllClaims(token).get("role");
        return role == null ? null : role.toString();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return username != null
                && username.equals(userDetails.getUsername())
                && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        Date exp = extractAllClaims(token).getExpiration();
        return exp == null || exp.before(new Date());
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(signingKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey signingKey() {
        String s = jwtSecretBase64 == null ? "" : jwtSecretBase64.trim();
        if (s.isEmpty()) throw new IllegalStateException("app.jwt.secret is empty");
        byte[] keyBytes = Decoders.BASE64.decode(s);
        return Keys.hmacShaKeyFor(keyBytes);
    }


}
