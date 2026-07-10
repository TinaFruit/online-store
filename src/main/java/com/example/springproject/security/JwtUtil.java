package com.example.springproject.security;

import io.jsonwebtoken.JwtParserBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.LocalDateTime;
import java.util.Date;

import static com.example.springproject.service.LogoutSer.blacklist;

@Component
public class JwtUtil {

    // Secret key: known only to the server, used to sign and verify tokens
    private final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    // Token validity: 24 hours
    private final long EXPIRATION = 1000 * 60 * 60 * 24;
    private LocalDateTime localDateTime = LocalDateTime.now();

    // Generate a token
    public String generateToken(String username, String role) {
        return Jwts.builder()
                .setSubject(username)// store username as subject
                .setIssuedAt(new Date())     // issued-at timestamp
                .claim("role", role) // custom claim: user role
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION)) // expiration time
                .signWith(key)               // sign with the secret key
                .compact();
    }

    public String getRole(String token) {
        return Jwts.parserBuilder()// build a parser
                .setSigningKey(key)// set the signing key (used to verify the token)
                .build()// finish building the parser
                .parseClaimsJws(token)// parse the token to get its full claims
                .getBody()// get the claims body (username, role, expiration, etc.)
                .get("role", String.class);  // extract the custom "role" claim
    }

    // Extract the username from the token (used to identify the caller on each request)
    public String getUsername(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    // Validate whether a token is still valid
    public boolean validateToken(String token) {
        try {
            if(blacklist.contains(token)){
                return false;
            }
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    //clear token , remove. make it invalid

}