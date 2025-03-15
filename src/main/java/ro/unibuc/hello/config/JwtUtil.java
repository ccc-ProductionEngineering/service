package ro.unibuc.hello.config;

import java.security.Key;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.util.Date;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Date;

import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;

@Component
public class JwtUtil {
    public static final String SECRET_KEY = "mysupersecretkeyforjwt256bitssecurityyyyyyyy";
    public String generateToken(String email, String role) {
        Claims claims = Jwts.claims()
                .add("roles", role)
                .add("sub", email)
                .add("iat", new Date())
                .add("exp", new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10))
                .build();
        return Jwts.builder()
                .claims(claims)
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }

    public String extractEmail(String token) {
        SecretKey secret = Keys.hmacShaKeyFor(Decoders.BASE64.decode(SECRET_KEY));

        return Jwts.parser() // Folosește parserBuilder() pentru a crea un parser
                .verifyWith(secret)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }


    public boolean validateToken(String token, UserDetails userDetails) {
        return extractEmail(token).equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        SecretKey secret = Keys.hmacShaKeyFor(Decoders.BASE64.decode(SECRET_KEY));
        return Jwts.parser()// Folosește parserBuilder()
                .verifyWith(secret)
                .build()
                .parseSignedClaims(token) // Parsează token-ul
                .getPayload()
                .getExpiration() // Obține data expirării
                .before(new Date()); // Verifică dacă token-ul a expirat
    }
}

