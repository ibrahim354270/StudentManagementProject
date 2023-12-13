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
public class JwtUtils {

    private static final Logger LOGGER= LoggerFactory.getLogger(JwtUtils.class);//Bu classın loglama işlemlerini istediğimiz yerden yapabiliriz.


    @Value("${backendapi.app.jwtExpirationMs}")//app.pro dosyasından alıyor.
    private long jwtExpirationMs;

    @Value("${backendapi.app.jwtSecret}")
    private String jwtSecret;

    // Not: GENERATE JWT TOKEN ******************

    //Aut. nesnesi üzerinde kullanıcı bilgisine ulaşabiliriz.login olan kullanı
    public String generateJwtToken(Authentication authentication){//token oluşturma
        //generetetoken için kullanıcıya ulaşmamız gerekiyor-username ile-//oto cast yapılmadı biz yaptık.
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();//1)anlık olarak login işlemi kim yapıyorsa onu bize getirir

        return generateJwtTokenFromUsername(userDetails.getUsername());
    }

    //yardımcı method yukarıdakini daha sade yapmak için bu şekilde yaptık
    //token String bir ifade
    public String generateJwtTokenFromUsername(String username){
        return Jwts.builder().//jwt token yapıyor
                setSubject(username).//username ile jwt tokeni oluşturuyoruz.
                setIssuedAt(new Date()).//oluşturma tarihi anlık setlenicek new DAte ile
                setExpiration(new Date(new Date().getTime()+ jwtExpirationMs)).//new date içinde parametre gelinde kullanırsak içinde ekleme yapmamıza izin veriyor.-kısaca tokennin 24 saatlik ömrü var dedik.
                signWith(SignatureAlgorithm.HS512, jwtSecret).//bu hashleme alg. ile benim secretkey(jwt) yi de içine katarak token oluştur. oluştur
                compact();
    }

    // Not: VALIDATE JWT TOKEN *******************
    public boolean validateToken(String jwtToken){//token 3 parçaya bölünüyordu(HEADER,PAYLOAAD,SIGNATURE),bizim sistemdeki ile eşleştirme
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(jwtToken);//secretKey bizim verdiğimiz olan tokenı parçala
            return true;
        } catch (ExpiredJwtException e) {
            LOGGER.error("Jwt token is expired : {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            LOGGER.error("Jwt token is unsupported : {}", e.getMessage());
        } catch (MalformedJwtException e) {
            LOGGER.error("Jwt token is invalid : {}", e.getMessage());
        } catch (SignatureException e) {
            LOGGER.error("Jwt Signature is invalid : {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            LOGGER.error("Jwt is empty : {}", e.getMessage());
        }
//exception fırlarsa false geri dönicek
        return false;
    }

    // Not: JWT TOKENDEN USERNAME  BILGISINI CEKECEGIZ *****
    public String getUserNameFromJwtToken(String token){//username uniq olduğu için bunu almaya çalışıyoruz

        return Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
}
