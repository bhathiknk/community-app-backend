package com.communityappbackend.controller;

import com.communityappbackend.dto.UpdateUserProfileRequestDTO;
import com.communityappbackend.dto.UserResponseDTO;
import com.communityappbackend.model.User;
import com.communityappbackend.service.UserService;
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
                .city(loggedUser.getCity())      // include city
                .province(loggedUser.getProvince()) // include province
                .build();
    }


    // method to update user profile fields
    @PutMapping("/profile/update")
    public UserResponseDTO updateProfile(Authentication authentication,
                                         @RequestBody UpdateUserProfileRequestDTO req) {
        User loggedUser = (User) authentication.getPrincipal();
        // Service updates the fields
        User updated = userService.updateUserProfile(loggedUser.getUserId(), req);

        // Return the updated user as a response
        return UserResponseDTO.builder()
                .fullName(updated.getFullName())
                .email(updated.getEmail())
                .phone(updated.getPhone())
                .address(updated.getAddress())
                .city(updated.getCity())
                .province(updated.getProvince())
                .build();
    }
}
