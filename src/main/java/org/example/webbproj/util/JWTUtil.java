package org.example.webbproj.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.util.Date;

public class JWTUtil {

    private static final String SECRET = "YOUR_SECRET_KEY"; // Замените на секретный ключ!
    private static final String ISSUER = "YourWebApp";      // Замените на имя вашего приложения
    private static final long EXPIRATION_TIME = 3600000;   // 1 час (в миллисекундах)

    public static String generateToken(String username) {
        Algorithm algorithm = Algorithm.HMAC256(SECRET);
        return JWT.create()
                .withIssuer(ISSUER)
                .withSubject(username)
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .sign(algorithm);
    }

    public static String verifyToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(SECRET);
            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer(ISSUER)
                    .build();
            DecodedJWT jwt = verifier.verify(token);
            return jwt.getSubject(); // Возвращаем имя пользователя, если токен валиден
        } catch (JWTVerificationException e) {
            return null; // Токен не валиден
        }
    }
}