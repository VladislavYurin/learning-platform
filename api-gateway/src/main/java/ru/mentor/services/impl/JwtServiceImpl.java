package ru.mentor.services.impl;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import ru.mentor.entity.UserEntity;
import ru.mentor.services.JwtService;
import io.jsonwebtoken.Claims;
import java.util.function.Function;

/**
 * Реализация {@link JwtService} для генерации, валидации и извлечения данных из JWT-токенов.
 */
@Service
public class JwtServiceImpl implements JwtService {

    @Value("${token.signing.key}")
    private String jwtSigningKey;

    @Value("${token.access.expiration.minutes}")
    private long accessExpirationMinutes;

    @Value("${token.refresh.expiration.day}")
    private long refreshExpirationDays;

    /**
     * Извлечение имени пользователя из токена
     *
     * @param token токен
     * @return имя пользователя
     */
    @Override
    public String extractUserName(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Проверка токена на валидность
     *
     * @param token       токен
     * @param userDetails данные пользователя
     * @return true, если токен валиден
     */
    @Override
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String userName = extractUserName(token);
        return (userName.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    @Override
    public String generateToken(UserDetails userDetails) {
        return buildToken(userDetails, 1000 * 60 * accessExpirationMinutes);
    }

    @Override
    public String generateRefreshToken(UserDetails userDetails) {
        return buildToken(userDetails, refreshExpirationDays * 24 * 60 * 60 * 1000);
    }

    /**
     * Извлечение данных из токена
     *
     * @param token           токен
     * @param claimsResolvers функция извлечения данных
     * @param <T>             тип данных
     * @return данные
     */
    private <T> T extractClaim(String token, Function<Claims, T> claimsResolvers) {
        final Claims claims = extractAllClaims(token);
        return claimsResolvers.apply(claims);
    }

    private String buildToken(UserDetails userDetails, long expirationMs) {
        Map<String, Object> claims = new HashMap<>();
        if (userDetails instanceof UserEntity user) {
            claims.put("id", user.getId());
            claims.put("role", user.getRole().name());
        }
        return Jwts.builder()
                   .setClaims(claims)
                   .setSubject(userDetails.getUsername())
                   .setIssuedAt(new Date())
                   .setExpiration(new Date(System.currentTimeMillis() + expirationMs))
                   .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                   .compact();
    }



    /**
     * Проверка токена на просроченность
     *
     * @param token токен
     * @return true, если токен просрочен
     */
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Извлечение даты истечения токена
     *
     * @param token токен
     * @return дата истечения
     */
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Извлечение всех данных из токена
     *
     * @param token токен
     * @return данные
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parser().setSigningKey(getSigningKey()).build().parseClaimsJws(token)
                   .getBody();
    }

    /**
     * Получение ключа для подписи токена
     *
     * @return ключ
     */
    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSigningKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}