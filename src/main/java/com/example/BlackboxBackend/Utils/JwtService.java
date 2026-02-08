package com.example.BlackboxBackend.Utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class JwtService {
    private static int  jwtExpirationTime = 15 * 3600 * 1000;
    private static String jwtSecret = "7f9a3c2e8b4d6a1f9e0c2b7a8d4f6e9c1a3b5d7e9f0a2c4b6d8e1f3a5";

    public String generateToken(Long userId, boolean isSuperAdmin){
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("isSuperAdmin", isSuperAdmin);

        return Jwts.builder()
                .setClaims(claims) // Set the map first
                .setSubject(String.valueOf(userId)) // Then add specific fields
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationTime))
                .signWith(SignatureAlgorithm.HS256, jwtSecret)
                .compact();
    }

    public boolean isValidToken(String token){
        try{
            Jwts.parser()
                    .setSigningKey(jwtSecret)
                    .parseClaimsJws(token)
                    .getBody();

            return true;
        }catch (Exception e){
            System.out.println("ERROR IN THE JWT VALID TOKEN - "+ e.getMessage() );
            return false;
        }
    }

    public Claims getClaims(String token){
        return Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(token)
                .getBody();
    }
}
