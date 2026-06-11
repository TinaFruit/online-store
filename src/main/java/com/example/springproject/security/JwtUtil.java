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

    // 密钥：服务器自己知道，用来签名和验证
    private final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    // Token 有效期：24小时
    private final long EXPIRATION = 1000 * 60 * 60 * 24;
    private LocalDateTime localDateTime = LocalDateTime.now();

    // 生成 Token
    public String generateToken(String username, String role) {
        return Jwts.builder()
                .setSubject(username)// 存入用户名
                .setIssuedAt(new Date())     // 签发时间
                .claim("role", role) //自创的 获取role value
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION)) // 过期时间
                .signWith(key)               // 用密钥签名
                .compact();
    }

    public String getRole(String token) {
        return Jwts.parserBuilder()//→ 创建解析器
                .setSigningKey(key)// → 设置密钥（用来验证token合法性）
                .build()// → 建好解析器
                .parseClaimsJws(token)//→ 解析token，拿到完整数据
                .getBody()// → 拿到数据体（里面装着username、role、过期时间等）
                .get("role", String.class);  // → 从数据体里取出role // 取出自定义的role字段
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