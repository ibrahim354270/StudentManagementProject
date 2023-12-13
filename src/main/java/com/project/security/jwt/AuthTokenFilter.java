package com.project.security.jwt;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@RequiredArgsConstructor
public class AuthTokenFilter extends OncePerRequestFilter {
    private static final Logger LOGGER= LoggerFactory.getLogger(AuthTokenFilter.class);

    @Autowired
    private JwtUtils jwtUtils;//enjekte ettik içindeki metodları burada kullanacağız

    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,//requeste ulaşabiliyoruz
                                    HttpServletResponse response,//request response çevrilince dönen nesneye de ulaşabiliyoruz
                                    FilterChain filterChain) throws ServletException, IOException {

        // !!! requestin icinden tokeni aliyorum
        String jwt = parseJwt(request);//jwttokenin kendisi geldi.

        try {//filtreleme işlemleri
            //null değilse ve validate ettiysek
            if(jwt!=null && jwtUtils.validateToken(jwt)) {
                // !!! Jwt icinden username bilgisini cekiyorum
                String userName = jwtUtils.getUserNameFromJwtToken(jwt);
                // !!! username bilgisi ile userdetail nesnemi getiriyorum
                UserDetails userDetails = userDetailsService.loadUserByUsername(userName);
                //requestin içinden direk uniq(username) alabilmek için
                request.setAttribute("username",userName);
                // !!! buradan itibaren kullaniciyi Security Contexte gondericem
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userDetails,null,userDetails.getAuthorities());
                //requestin içinden ip,tarayıcı gibi bilgileri almak için
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (UsernameNotFoundException e) {
            LOGGER.error("Cannot set user authentication ", e);
        }

        filterChain.doFilter(request, response);//bu filtre hem request hemde response için kullanılıyorsu
    }

    //Not: bu methodda, requestin icinden JWT tokeni cekecegim
    private String parseJwt(HttpServletRequest request){//request içinde header kısmında bulunan jwtTokenı çekicez-

        String header = request.getHeader("Authorization");//baerer ile gelen tokeni aldık
        if(StringUtils.hasText(header) && header.startsWith("Bearer ")) {//null mı?-Token ın başında Bearer var
            return header.substring(7);//bu textin 7. karakterinden itibaren al-salt token i alıyoruz
        }
        return null;
    }

    // Not: shouldNotFilter Metodu ****************************


}