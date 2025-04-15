package com.communityappbackend.Controller;

import com.communityappbackend.Service.ProfileImageService;
import com.communityappbackend.Security.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class ProfileImageController {

    @Autowired
    private ProfileImageService profileImageService;

    @Autowired
    private JwtUtils jwtUtils;

    @PostMapping("/uploadProfileImage")
    public ResponseEntity<?> uploadProfileImage(HttpServletRequest request,
                                                @RequestParam("file") MultipartFile file) {
        try {
            String headerAuth = request.getHeader("Authorization");
            String token = (headerAuth != null && headerAuth.startsWith("Bearer "))
                    ? headerAuth.substring(7)
                    : null;
            if (token == null || !jwtUtils.validateToken(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Invalid JWT Token");
            }

            String userId = jwtUtils.getUserIdFromToken(token);
            String message = profileImageService.uploadProfileImage(userId, file);
            return ResponseEntity.ok(message);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Could not upload the file: " + e.getMessage());
        }
    }

    @GetMapping("/getProfileImage")
    public ResponseEntity<?> getProfileImage(HttpServletRequest request) {
        try {
            String headerAuth = request.getHeader("Authorization");
            String token = (headerAuth != null && headerAuth.startsWith("Bearer "))
                    ? headerAuth.substring(7)
                    : null;
            if (token == null || !jwtUtils.validateToken(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid JWT Token");
            }

            String userId = jwtUtils.getUserIdFromToken(token);
            String fileName = profileImageService.getProfileImage(userId);
            if (fileName != null && !fileName.isEmpty()) {
                String imageUrl = "http://10.0.2.2:8080/ProfileImages/" + fileName;
                return ResponseEntity.ok().body("{\"profileImage\": \"" + imageUrl + "\"}");
            }
            return ResponseEntity.ok().body("{\"profileImage\": \"\"}");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error: " + e.getMessage());
        }
    }

}
