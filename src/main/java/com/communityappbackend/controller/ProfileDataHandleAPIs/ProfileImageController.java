package com.communityappbackend.controller.ProfileDataHandleAPIs;

import com.communityappbackend.security.JwtUtils;
import com.communityappbackend.service.UserService.ProfileImageService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.UrlResource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Handles user profile image upload and retrieval.
 */
@RestController
public class ProfileImageController {

    private static final String UPLOAD_DIR =
            "C:\\Projects\\Community App\\community-app-backend\\src\\main\\java\\com\\communityappbackend\\Assets\\ProfileImages";

    @Autowired
    private ProfileImageService profileImageService;

    @Autowired
    private JwtUtils jwtUtils;

    @PostMapping("/uploadProfileImage")
    public ResponseEntity<?> uploadProfileImage(
            HttpServletRequest request,
            @RequestParam("file") MultipartFile file
    ) {
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
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
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

    /**
     * Serves the actual user profile image file by file name.
     */
    @GetMapping("/image/{fileName}")
    public ResponseEntity<?> getUserImageFile(@PathVariable("fileName") String fileName) {
        try {
            Path filePath = Paths.get(UPLOAD_DIR).resolve(fileName).normalize();
            if (!Files.exists(filePath)) {
                return ResponseEntity.notFound().build();
            }

            UrlResource resource = new UrlResource(filePath.toUri());
            if (!resource.exists() || !resource.isReadable()) {
                return ResponseEntity.notFound().build();
            }

            String contentType = Files.probeContentType(filePath);
            if (contentType == null) {
                contentType = "application/octet-stream";
            }

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(contentType));

            return ResponseEntity.ok().headers(headers).body(resource);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error reading file: " + e.getMessage());
        }
    }
}
