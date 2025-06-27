package org.crm.reachai.service;

import org.crm.reachai.config.RabbitMQConfig;
import org.crm.reachai.dto.*;
import org.crm.reachai.exception.InvalidOtpException;
import org.crm.reachai.exception.UserAlreadyExistException;
import org.crm.reachai.exception.UserNotFoundException;
import org.crm.reachai.mapper.UserMapper;
import org.crm.reachai.model.User;
import org.crm.reachai.repository.UserRepository;
import org.crm.reachai.util.JwtUtil;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

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

    @Autowired
    private OtpCacheService otpCacheService;

    @Autowired
    private RabbitTemplate rabbitTemplate;


    @Autowired
    PasswordEncoder encoder;

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

    public ResponseEntity<Response<String>> sendOtp(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        if(user.isEmpty()) {
            throw new UserNotFoundException("User not found");
        }
        String otp = String.valueOf((int)(Math.random() * 900000) + 100000);

        otpCacheService.saveOtp(email, otp);

        EmailEvent emailEvent = new EmailEvent(
                email,
                "Your OTP Verification Code",
                "Your OTP is: " + otp
        );

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EMAIL_EXCHANGE,
                RabbitMQConfig.EMAIL_ROUTING_KEY,
                emailEvent
        );
        Response<String> response = Response.<String>builder()
                .status(200)
                .message("otp sent successfully")
                .data(otp)
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    public ResponseEntity<Response<Boolean>> verifyOtp(VerifyEmailDto request) {
        String cachedOtp = otpCacheService.getOtp(request.getEmail());
        if (cachedOtp != null && cachedOtp.equals(request.getOtp())) {
            otpCacheService.deleteOtp(request.getEmail());
            otpCacheService.markOtpVerified(request.getEmail());
            Response<Boolean> response = Response.<Boolean>builder()
                    .status(200)
                    .message("otp verified successfully")
                    .data(true)
                    .build();
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        else throw new InvalidOtpException("Invalid OTP");
    }

    public ResponseEntity<Response<String>> updatePassword(UpdatePassword request) {
        if (!otpCacheService.canUpdatePassword(request.getEmail())) {
            throw new InvalidOtpException("Invalid OTP");
        }

        User user = userRepository.findByEmail(request.getEmail()).orElseThrow(
                () -> new UserNotFoundException(request.getEmail()));
        user.setPassword(encoder.encode(request.getPassword()));
        userRepository.save(user);
        otpCacheService.clearOtpVerifiedFlag(request.getEmail());

        Response<String> response = Response.<String>builder()
                .status(200)
                .message("password updated successfully")
                .data(null)
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
