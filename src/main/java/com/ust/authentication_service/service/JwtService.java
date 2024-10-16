package com.ust.authentication_service.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class JwtService {
    public static final String SECRET = "f6d876b2b01d8106fde89bae44be9722e48d83fd8aa8ff17e3e10b78095702f6a1a5efa722652a52a020d548c20a908d3239b4f38f277c0c0731337d95d2d843943f13ddf8cf8690d34c7f915a569605ac2ed71fed889169c3befae73cc68f130d3c7f7250e26c2d93963e61b0104621afaebaf0940bb287d880d9ce12e5a8689aeea52b3222c51d94e5d3ae10bef97acde52e81c3da6381455855e468ffdd471612f2da3a1ada1192a4994cc12abe7e6186a37c6fde6caa2a9c941cb2bbe6b5a18d144c5b006581afec6c9ac655439e0a003b7ec23ed991e9c5ea0f14f66666d6e394f36d60af0ea2a248a6d7f596a8794ba0c73e8b08178100cddbaae6b113";

    public void validateToken(String token){
        Jwts
                .parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token);
    }

    public String generateToken(String username){
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, username);
    }

    private String createToken(Map<String, Object> claims, String username){
        return Jwts
                .builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 30))
                .signWith(getSignKey(), SignatureAlgorithm.HS256).compact();
    }

    private Key getSignKey(){
        byte[] keyBytes = Decoders.BASE64.decode(SECRET);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
