package org.crm.reachai.mapper;

import org.crm.reachai.dto.AuthResponse;
import org.crm.reachai.dto.JwtResponse;
import org.crm.reachai.dto.SignUpRequest;
import org.crm.reachai.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    @Autowired
    PasswordEncoder encoder;

    public User toEntity(SignUpRequest signUpRequest) {
        User user = new User();
        user.setUserName(signUpRequest.getUserName());
        user.setPassword(encoder.encode(signUpRequest.getPassword()));
        user.setEmail(signUpRequest.getEmail());
        user.setRole(signUpRequest.getRole());

        return user;
    }

    public AuthResponse toAuthResponse(User user) {
        AuthResponse authResponse = new AuthResponse();
        authResponse.setId(user.getId());
        authResponse.setUserName(user.getUsername());
        authResponse.setRole(user.getRole());
        authResponse.setEmail(user.getEmail());

        return authResponse;
    }

    public JwtResponse toJwtResponse(User user, String token) {
         JwtResponse jwtResponse = new JwtResponse();
         jwtResponse.setToken(token);
         jwtResponse.setId(user.getId());
         jwtResponse.setUserName(user.getUsername());
         jwtResponse.setRole(user.getRole());
         jwtResponse.setEmail(user.getEmail());
         return jwtResponse;
    }
}
