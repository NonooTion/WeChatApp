package com.backend.util;

import com.backend.entity.User;
import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JWTUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration;

    /**
     * 生成 JWT
     *
     * @param userId 用户 ID
     * @param username 用户名
     * @return JWT 字符串
     */
    public String generateToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getUserId());
        claims.put("username", user.getUsername());
        claims.put("role",user.getRole());

        return Jwts.builder()
                .setClaims(claims)
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }

    /**
     * 验证 JWT 并返回用户信息
     *
     * @param token JWT 字符串
     * @return 用户信息 Map
     */
    public Map<String, Object> validateToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(secret)
                    .parseClaimsJws(token)
                    .getBody();

            Map<String, Object> user = new HashMap<>();
            user.put("userId", claims.get("userId"));
            user.put("username", claims.get("username"));
            user.put("role",claims.get("role"));
            user.put("expiration", claims.getExpiration());

            return user;
        } catch (JwtException | IllegalArgumentException e) {
            throw new RuntimeException("无效的 JWT Token");
        }
    }

    /**
     * 获取用户 ID 从 JWT
     *
     * @param token JWT 字符串
     * @return 用户 ID
     */
    public Integer getUserIdFromToken(String token) {
        if (token == null || token.isEmpty()) {
            throw new RuntimeException("Token不存在");
        }
        Map<String, Object> user = validateToken(token);
        return (Integer) user.get("userId");
    }

    /**
     * 获取用户名从 JWT
     *
     * @param token JWT 字符串
     * @return 用户名
     */
    public String getUsernameFromToken(String token) {
        Map<String, Object> user = validateToken(token);
        return (String) user.get("username");
    }

    public String getRoleFromToken(String token) {
        Map<String, Object> user = validateToken(token);
        return (String) user.get("role");
    }
    /**
     * 检查 JWT 是否过期
     *
     * @param token JWT 字符串
     * @return 是否过期
     */
    public boolean isTokenExpired(String token) {
        Map<String, Object> user = validateToken(token);
        Date expiration = (Date) user.get("expiration");
        return expiration.before(new Date());
    }
}