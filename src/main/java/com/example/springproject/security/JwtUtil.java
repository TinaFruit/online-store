package com.example.springproject.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    // 密钥：服务器自己知道，用来签名和验证
    private final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    // Token 有效期：24小时
    private final long EXPIRATION = 1000 * 60 * 60 * 24;

    // 生成 Token
    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)        // 存入用户名
                .setIssuedAt(new Date())     // 签发时间
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION)) // 过期时间
                .signWith(key)               // 用密钥签名
                .compact();
    }

    // 从 Token 里取出用户名 （Token 里藏着用户名 每次请求都靠它来认出你是谁 ）
    public String getUsername(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    // 验证 Token 是否有效
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}