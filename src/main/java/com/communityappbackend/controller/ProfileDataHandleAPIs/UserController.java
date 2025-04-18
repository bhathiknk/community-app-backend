package com.communityappbackend.controller.ProfileDataHandleAPIs;

import com.communityappbackend.dto.UserDataHandleDTOs.UpdateUserProfileRequestDTO;
import com.communityappbackend.dto.UserDataHandleDTOs.UserResponseDTO;
import com.communityappbackend.model.User;
import com.communityappbackend.service.UserService.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * Manages user profile retrieval and updates.
 */
@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Returns the currently authenticated user's profile.
     */
    @GetMapping("/profile")
    public UserResponseDTO getProfile(Authentication authentication) {
        User loggedUser = (User) authentication.getPrincipal();

        return UserResponseDTO.builder()
                .fullName(loggedUser.getFullName())
                .email(loggedUser.getEmail())
                .phone(loggedUser.getPhone())
                .address(loggedUser.getAddress())
                .city(loggedUser.getCity())
                .province(loggedUser.getProvince())
                .build();
    }

    /**
     * Updates the authenticated user's profile fields.
     */
    @PutMapping("/profile/update")
    public UserResponseDTO updateProfile(Authentication authentication,
                                         @RequestBody UpdateUserProfileRequestDTO req) {
        User loggedUser = (User) authentication.getPrincipal();
        User updated = userService.updateUserProfile(loggedUser.getUserId(), req);

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
