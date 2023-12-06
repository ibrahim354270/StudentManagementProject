package com.project.security.jwt;

import com.project.security.service.UserDetailsImpl;
import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtils { //kullanıcıyı unique yapacak bir veri ile yapılacak

    private static final Logger LOGGER = LoggerFactory.getLogger(JwtUtils.class);//loglama
    @Value("${backendapi.app.jwtSecret}")
    private String jwtSecret; //application.properties e gidiyor ordan alıyor

    @Value("{backendapi.app.jwtExpirationMs}")
    private Long jwtExpirationMs; //application.properties e gidiyor ordan alıyor

 //Not: Generate JWT
    public String generateJwtToken(Authentication authentication){
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        return generateJwtTokenFromUsername(userDetails.getUsername());
    }

public String generateJwtTokenFromUsername(String username){
        return Jwts.builder()
        .setSubject(username)
        .setIssuedAt(new Date())
        .setExpiration(new Date(new Date().getTime() + jwtExpirationMs))
        .signWith(SignatureAlgorithm.HS512, jwtSecret)
        .compact();
        }

        // Not: Validate JWT
    public boolean validateJvtToken(String jwtToken){
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(jwtToken);
            return true;
        } catch (ExpiredJwtException e) {
           LOGGER.error("Jwt token is expired : {}",e.getMessage());
        } catch (UnsupportedJwtException e) {
            LOGGER.error("Jwt token is unsupported : {}",e.getMessage());
        } catch (MalformedJwtException e) {
            LOGGER.error("Jwt token is invalid : {}",e.getMessage());
        } catch (SignatureException e) {
            LOGGER.error("Jwt Signature is invalid : {}",e.getMessage());
        } catch (IllegalArgumentException e) {
            LOGGER.error("Jwt is empty : {}",e.getMessage());
        }
        return false;
    }
    //Not: getUsernameFromJWT
    public String getUserNameFromJwtToken (String token){
        return Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

        }












