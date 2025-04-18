package com.communityappbackend.service.UserService;

import com.communityappbackend.dto.UserDataHandleDTOs.SignUpRequest;
import com.communityappbackend.dto.UserDataHandleDTOs.UpdateUserProfileRequestDTO;
import com.communityappbackend.exception.EmailAlreadyExistsException;
import com.communityappbackend.exception.UserNotFoundException;
import com.communityappbackend.model.User;
import com.communityappbackend.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Provides sign-up and user profile update logic.
 */
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
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException("Email is already in use: " + request.getEmail());
        }

        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .address(request.getAddress())
                .city(request.getCity())
                .province(request.getProvince())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .isVerified(false)
                .build();

        return userRepository.save(user);
    }

    public User findByEmail(String email) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new UserNotFoundException("User not found with email: " + email);
        }
        return user;
    }

    public User findById(String userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + userId));
    }

    public User updateUserProfile(String userId, UpdateUserProfileRequestDTO req) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + userId));

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
