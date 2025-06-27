package org.crm.reachai.controller;

import org.crm.reachai.dto.*;
import org.crm.reachai.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class
AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/signUp")
    public ResponseEntity<Response<AuthResponse>> signUp(@RequestBody SignUpRequest signUpRequest) {
        return authService.signUp(signUpRequest);
    }

    @PostMapping("/login")
    public ResponseEntity<Response<JwtResponse>> login(@RequestBody LoginRequest loginRequest) {
        return authService.login(loginRequest);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<Response<String>> forgotPassword(@RequestParam String email){
        return authService.sendOtp(email);
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<Response<Boolean>> verifyOtp(@RequestBody VerifyEmailDto request){
       return authService.verifyOtp(request);
    }

    @PostMapping("/update-password")
    public ResponseEntity<Response<String>> updatePassword(@RequestBody UpdatePassword request){
        return authService.updatePassword(request);
    }

}
