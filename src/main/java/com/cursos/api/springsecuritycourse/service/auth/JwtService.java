package com.cursos.api.springsecuritycourse.service.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;
import java.util.Map;

@Service
public class JwtService {

    @Value("${security.jwt.expiration-in-minutes}")
    private Long EXPIRATION_IN_MINUTES;

    @Value("${security.jwt.secret-key}")
    private String SECRET_KEY;
    public String generateToken(UserDetails user, Map<String, Object> extraClaims ) {
        Date issueAt = new Date(System.currentTimeMillis());
        Date expiratation = new Date((EXPIRATION_IN_MINUTES * 60 * 1000) + issueAt.getTime());
        String jwt = Jwts.builder()//Información HEADER
                //Header
                .header()
                .type("JWT")
                .and()

                //Payload
                .claims(extraClaims) //Propiedades extras
                .subject(user.getUsername()) //propiedades obligatorias Propietario
                .issuedAt(issueAt) //propiedades obligatorias del payload fecha creacion
                .expiration(expiratation)  //propiedades obligatorias del payload fecha expitracion

                //Firma
                .signWith(generateKey(), SignatureAlgorithm.HS256)
                .compact();

        return jwt;
    }

    private SecretKey generateKey() {
        byte[] passwordDecoded = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(passwordDecoded);
    }

    public String extractUsername(String jwt) {
        return extractAllClains(jwt).getSubject();
    }

    private Claims extractAllClains(String jwt) {
        return Jwts.parser()
                .verifyWith(generateKey())
                .build()
                .parseSignedClaims(jwt)
                .getPayload();
    }


}
