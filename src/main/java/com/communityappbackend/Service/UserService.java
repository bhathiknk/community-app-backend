package com.communityappbackend.Service;

import com.communityappbackend.DTO.SignUpRequest;
import com.communityappbackend.DTO.UpdateUserProfileRequestDTO;
import com.communityappbackend.Exception.EmailAlreadyExistsException;
import com.communityappbackend.Model.User;
import com.communityappbackend.Repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
                       BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User signUp(SignUpRequest request) {
        // Check if email is taken
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException("Email is already in use");
        }

        // Create a new user
        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .address(request.getAddress())
                .city(request.getCity())
                .province(request.getProvince())
                .passwordHash(passwordEncoder.encode(request.getPassword())) // BCrypt
                .isVerified(false)
                .build();

        return userRepository.save(user);
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User findById(String userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }



    // NEW: update user profile fields.
    public User updateUserProfile(String userId, UpdateUserProfileRequestDTO req) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (req.getFullName() != null && !req.getFullName().isEmpty()) {
            user.setFullName(req.getFullName());
        }
        if (req.getPhone() != null) {
            user.setPhone(req.getPhone());
        }
        if (req.getAddress() != null) {
            user.setAddress(req.getAddress());
        }
        if (req.getCity() != null) {
            user.setCity(req.getCity());
        }
        if (req.getProvince() != null) {
            user.setProvince(req.getProvince());
        }

        return userRepository.save(user);
    }

}
