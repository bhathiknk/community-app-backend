package com.communityappbackend.Controller;


import com.communityappbackend.DTO.AuthResponse;
import com.communityappbackend.DTO.SignUpRequest;
import com.communityappbackend.Model.User;
import com.communityappbackend.Security.JwtUtils;
import com.communityappbackend.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final AuthenticationManager authManager;
    private final JwtUtils jwtUtils;
    private final BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public AuthController(UserService userService,
                          AuthenticationManager authManager,
                          JwtUtils jwtUtils,
                          BCryptPasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.authManager = authManager;
        this.jwtUtils = jwtUtils;
        this.passwordEncoder = passwordEncoder;
    }

    // ========== SIGN UP ==========
    @PostMapping("/signup")
    public AuthResponse signUp(@RequestBody SignUpRequest request) {
        User user = userService.signUp(request);
        String token = jwtUtils.generateToken(user.getUserId());
        return AuthResponse.builder()
                .message("User created successfully")
                .token(token)
                .build();
    }


}
