package com.communityappbackend.controller.ProfileDataHandleAPIs;

import com.communityappbackend.dto.UserDataHandleDTOs.AuthResponse;
import com.communityappbackend.dto.UserDataHandleDTOs.SignInRequest;
import com.communityappbackend.dto.UserDataHandleDTOs.SignUpRequest;
import com.communityappbackend.model.User;
import com.communityappbackend.security.JwtUtils;
import com.communityappbackend.service.UserService.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

/**
 * Manages user registration and login.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    @Autowired
    public AuthController(UserService userService,
                          BCryptPasswordEncoder passwordEncoder,
                          JwtUtils jwtUtils) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
    }

    /**
     * Creates a new user account.
     */
    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> signUp(@RequestBody SignUpRequest request) {
        User user = userService.signUp(request);
        String token = jwtUtils.generateToken(user.getUserId());

        AuthResponse response = AuthResponse.builder()
                .message("User created successfully")
                .token(token)
                .build();
        return ResponseEntity.ok(response);
    }

    /**
     * Authenticates a user and returns a JWT.
     * Returns HTTP 401 when credentials are invalid.
     */
    @PostMapping("/signin")
    public ResponseEntity<AuthResponse> signIn(@RequestBody SignInRequest request) {
        try {
            User user = userService.findByEmail(request.getEmail());
            if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
                // Return Unauthorized if password does not match
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(AuthResponse.builder()
                                .message("Invalid email or password")
                                .token("")
                                .build());
            }
            String token = jwtUtils.generateToken(user.getUserId());
            AuthResponse response = AuthResponse.builder()
                    .message("Login successful")
                    .token(token)
                    .build();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            // Catch any exception (e.g., UserNotFoundException) and return 401
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(AuthResponse.builder()
                            .message("Invalid email or password")
                            .token("")
                            .build());
        }
    }
}
