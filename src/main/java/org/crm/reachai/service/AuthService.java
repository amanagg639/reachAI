package org.crm.reachai.service;

import org.crm.reachai.dto.*;
import org.crm.reachai.exception.UserAlreadyExistException;
import org.crm.reachai.mapper.UserMapper;
import org.crm.reachai.model.User;
import org.crm.reachai.repository.UserRepository;
import org.crm.reachai.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    public ResponseEntity<Response<AuthResponse>> signUp(SignUpRequest signUpRequest) {
        if(userRepository.findByUserName(signUpRequest.getUserName()).isPresent() ||
        userRepository.findByEmail(signUpRequest.getEmail()).isPresent()) {
            throw new UserAlreadyExistException("User already exists");
        }
        User user = userMapper.toEntity(signUpRequest);
        userRepository.save(user);

        Response<AuthResponse> response = Response.<AuthResponse>builder()
                .status(200)
                .message("user registered successfully")
                .data(userMapper.toAuthResponse(user))
                .build();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    public ResponseEntity<Response<JwtResponse>> login(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtil.generateToken(loginRequest.getUsername());
        User usersDetails = (User) authentication.getPrincipal();
        Response<JwtResponse> response = Response.<JwtResponse>builder()
                .status(200)
                .message("logged in successfully")
                .data(userMapper.toJwtResponse(usersDetails, jwt))
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
