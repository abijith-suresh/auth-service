package sh.abijith.authservice.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import sh.abijith.authservice.exception.InvalidRefreshTokenException;
import sh.abijith.authservice.exception.InvalidTokenException;
import sh.abijith.authservice.model.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;

@Service
public class JwtService {
    @Value("${jwt.secret}")
    private String SECRET;

    /**
     * Generates a JWT access token for the given user.
     *
     * @param user the user for whom the token is generated
     * @return a signed JWT access token
     */
    public String generateToken(User user) {
        return Jwts.builder()
                .setSubject(user.getEmail())
                .claim("role", user.getRole())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 86400000)) // 1 day
                .signWith(SignatureAlgorithm.HS256, SECRET.getBytes())
                .compact();
    }


    /**
     * Generates a long-lived refresh token for the given user.
     *
     * @param user the user for whom the refresh token is generated
     * @return a signed JWT refresh token
     */
    public String generateRefreshToken(User user) {
        return Jwts.builder()
                .setSubject(user.getEmail())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 2592000000L)) // 30 days
                .signWith(SignatureAlgorithm.HS256, SECRET.getBytes())
                .compact();
    }

    /**
     * Generates a new access token by verifying and decoding the given refresh token.
     *
     * @param refreshToken the refresh token to validate and extract user info from
     * @return a new access token
     * @throws InvalidRefreshTokenException if the token is invalid or expired
     */
    public String generateTokenFromRefreshToken(String refreshToken) {
        try {
            var claims = Jwts.parser()
                    .setSigningKey(SECRET.getBytes())
                    .parseClaimsJws(refreshToken)
                    .getBody();

            String email = claims.getSubject();

            return Jwts.builder()
                    .setSubject(email)
                    .setIssuedAt(new Date())
                    .setExpiration(new Date(System.currentTimeMillis() + 86400000)) // 1 day
                    .signWith(SignatureAlgorithm.HS256, SECRET.getBytes())
                    .compact();
        } catch (Exception e) {
            throw new InvalidRefreshTokenException("Invalid refresh token");
        }
    }

    /**
     * Validates the structure and expiration of a given JWT token.
     *
     * @param token the JWT token to validate
     * @throws InvalidTokenException if the token is malformed, expired, or has an invalid signature
     */
    public void validateToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(SECRET)
                    .parseClaimsJws(token)
                    .getBody();
            if (claims.getExpiration().before(new java.util.Date())) {
                throw new InvalidTokenException("Token is expired");
            }
        } catch (SignatureException | io.jsonwebtoken.ExpiredJwtException | io.jsonwebtoken.MalformedJwtException e) {
            throw new InvalidTokenException("Invalid token: " + e.getMessage());
        }
    }


    /**
     * Extracts the email (username) from a valid JWT token.
     *
     * @param token the JWT token
     * @return the subject (email) from the token
     */
    public String extractUsername(String token) {
        return Jwts.parser()
                .setSigningKey(SECRET.getBytes())
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
}
