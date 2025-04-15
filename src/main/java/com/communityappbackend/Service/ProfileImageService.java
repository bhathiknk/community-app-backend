package com.communityappbackend.Service;

import com.communityappbackend.Model.User;
import com.communityappbackend.Model.UserProfileImage;
import com.communityappbackend.Repository.UserRepository;
import com.communityappbackend.Repository.UserProfileImageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

@Service
public class ProfileImageService {

    private static final String UPLOAD_DIR = "C:\\Projects\\Community App\\community-app-backend\\src\\main\\java\\com\\communityappbackend\\Assets\\ProfileImages";

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserProfileImageRepository userProfileImageRepository;

    public String uploadProfileImage(String userId, MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Please select a file!");
        }

        File uploadDir = new File(UPLOAD_DIR);
        if (!uploadDir.exists() && !uploadDir.mkdirs()) {
            throw new IOException("Could not create upload directory");
        }

        String originalFileName = file.getOriginalFilename();
        if (originalFileName == null) {
            throw new IllegalArgumentException("File must have a name!");
        }

        originalFileName = originalFileName.replace("\\", "/");
        originalFileName = originalFileName.contains("/") ? originalFileName.substring(originalFileName.lastIndexOf("/") + 1) : originalFileName;
        String cleanFileName = StringUtils.cleanPath(originalFileName);
        String newFileName = UUID.randomUUID().toString() + "_" + cleanFileName;
        newFileName = newFileName.replaceAll("[^a-zA-Z0-9._-]", "");

        Path destinationPath = Paths.get(UPLOAD_DIR, newFileName).normalize();
        Files.copy(file.getInputStream(), destinationPath, StandardCopyOption.REPLACE_EXISTING);

        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            throw new IllegalArgumentException("User not found!");
        }

        UserProfileImage profileImage = userProfileImageRepository.findByUserId(userId);
        if (profileImage != null && profileImage.getImagePath() != null) {
            Path oldFilePath = Paths.get(UPLOAD_DIR, profileImage.getImagePath()).normalize();
            File oldFile = oldFilePath.toFile();
            if (oldFile.exists()) {
                oldFile.delete();
            }
        } else {
            if (profileImage == null) {
                profileImage = new UserProfileImage();
                profileImage.setUserId(userId);
            }
        }

        // Save only the filename, not the full path
        profileImage.setImagePath(newFileName);
        userProfileImageRepository.save(profileImage);

        return "Profile image uploaded successfully";
    }

    public String getProfileImage(String userId) {
        UserProfileImage profileImage = userProfileImageRepository.findByUserId(userId);
        if (profileImage != null && profileImage.getImagePath() != null) {
            // return only the filename
            return profileImage.getImagePath();
        }
        return "";
    }

}
