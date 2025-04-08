package com.communityappbackend.Controller;

import com.communityappbackend.DTO.UserResponseDTO;
import com.communityappbackend.Model.User;
import com.communityappbackend.Service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/profile")
    public UserResponseDTO getProfile(Authentication authentication) {
        User loggedUser = (User) authentication.getPrincipal();

        return UserResponseDTO.builder()
                .fullName(loggedUser.getFullName())
                .email(loggedUser.getEmail())
                .phone(loggedUser.getPhone())
                .address(loggedUser.getAddress())
                .build();
    }
}
