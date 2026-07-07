package com.hostel.management.security.jwt;

import com.hostel.management.common.enums.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;


@Service
public class JwtService
{
    @Value("${jwt.secret}")
    private String secretKey;
    @Value("${jwt.expiration}")
    private long jwtExpiration;


    private SecretKey getSigningKey()
    {
        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(String username, Role role)
    {
        Instant now = Instant.now();
        Instant expiration = now.plusMillis(jwtExpiration);

        return Jwts.builder()
                .subject(username)
                .claim("role",role.name())
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiration))
                .signWith(getSigningKey())
                .compact();

    }

    private Claims extractAllClaims(String token)
    {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public Role extractRole(String token)
    {
        Claims claims = extractAllClaims(token);
        return Role.valueOf(claims.get("role",String.class));
    }

    public String extractUsername(String token)
    {
        return extractAllClaims(token).getSubject();
    }

    public Date extractExpiration(String token)
    {
        return extractAllClaims(token).getExpiration();
    }
    public boolean isTokenExpired(String token)
    {
        Date expiration = extractExpiration(token);
        return expiration.before(Date.from(Instant.now()));
    }
    public boolean isTokenValid(String token, UserDetails username)
    {
        return !isTokenExpired(token) && username.getUsername().equals(extractUsername(token));
    }
}
