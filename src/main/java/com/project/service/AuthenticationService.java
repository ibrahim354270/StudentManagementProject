package com.project.service;

import com.project.entity.concretes.user.User;
import com.project.payload.mappers.UserMapper;
import com.project.payload.request.LoginRequest;
import com.project.payload.response.AuthResponse;
import com.project.payload.response.UserResponse;
import com.project.repository.UserRepository;
import com.project.security.jwt.JwtUtils;
import com.project.security.service.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final UserMapper userMapper;

    public ResponseEntity<AuthResponse> authenticateUser(LoginRequest loginRequest) {
        String username = loginRequest.getUsername();
        String password = loginRequest.getPassword();
        //!!! authenticationManager uzerinden kullanici valide ediliyor
        Authentication authentication =
                authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));

        //!!! valide edilen kullanici context e atiliyor
        SecurityContextHolder.getContext().setAuthentication(authentication);
        //!!! JWT token olusturuluyor
        String token = "Bearer " + jwtUtils.generateJwtToken(authentication);
        //!!! Response nesnesi olusturuluyor
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        //!!! grantedAuth --> Role ( String )
        Set<String> roles = userDetails.getAuthorities()
                .stream() // Sream<GrantedAuth>
                .map(GrantedAuthority::getAuthority) // Stream<String>
                .collect(Collectors.toSet());

        Optional<String> role = roles.stream().findFirst();

        AuthResponse.AuthResponseBuilder authResponse = AuthResponse.builder(); //builder için 2. yöntem
        //netleşen yerleri setle sonra build et demiş oluyoruz
        authResponse.username(userDetails.getUsername());
        authResponse.token(token.substring(7));
        authResponse.name(userDetails.getName());
        authResponse.ssn(userDetails.getSsn());
        //eğer role bilgisi null degise Authresponse nesnesi içine setleniyor
        role.ifPresent(authResponse::role);

        return ResponseEntity.ok(authResponse.build());

    }

    public UserResponse findByUsername(String username) {
        User user = userRepository.findByUsername(username);
        //!!! Pojo --> DTO
        return userMapper.mapUserToUserResponse(user);
    }
}