package com.communityappbackend.service;

import com.communityappbackend.dto.DonationRequest;
import com.communityappbackend.dto.DonationResponse;
import com.communityappbackend.exception.*;
import com.communityappbackend.model.DonationItem;
import com.communityappbackend.model.DonationItemImage;
import com.communityappbackend.model.User;
import com.communityappbackend.repository.*;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DonationService {

    private final DonationItemRepository donationRepo;
    private final DonationItemImageRepository donationImageRepo;
    private final UserRepository userRepo;

    // Path for saving donation images
    private static final String DONATIONS_DIR =
            "C:\\Projects\\Community App\\community-app-backend\\src\\main\\java\\com\\communityappbackend\\Assets\\Donations";

    public DonationService(DonationItemRepository donationRepo,
                           DonationItemImageRepository donationImageRepo,
                           UserRepository userRepo) {
        this.donationRepo = donationRepo;
        this.donationImageRepo = donationImageRepo;
        this.userRepo = userRepo;
    }

    /**
     * Adds a donation item with up to 5 images.
     */
    public DonationResponse addDonation(
            DonationRequest request,
            List<MultipartFile> files,
            Authentication auth
    ) {
        // 1) Ensure the user is authenticated.
        User user = (User) auth.getPrincipal();
        if (user == null) {
            throw new UserNotFoundException("Authenticated user not found.");
        }

        // 2) Create the donation item record
        DonationItem donation = DonationItem.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .ownerId(user.getUserId())
                .status("ACTIVE")
                .build();
        donation = donationRepo.save(donation);

        // 3) Process up to 5 images
        if (files != null && !files.isEmpty()) {
            // If you want to throw an error if > 5, uncomment below:
            // if (files.size() > 5) {
            //     throw new RuntimeException("Cannot upload more than 5 donation images.");
            // }
            int count = 0;
            for (MultipartFile file : files) {
                if (count >= 5) break; // ignore extras
                String filePath = saveDonationFile(file);
                DonationItemImage img = DonationItemImage.builder()
                        .imagePath(filePath)
                        .donationItem(donation)
                        .build();
                donationImageRepo.save(img);
                count++;
            }
        }

        // 4) Return response
        return toDonationResponse(donation);
    }

    /**
     * Returns the current user's donation items.
     */
    public List<DonationResponse> getMyDonations(Authentication auth) {
        User user = (User) auth.getPrincipal();
        return donationRepo.findByOwnerId(user.getUserId()).stream()
                .map(this::toDonationResponse)
                .collect(Collectors.toList());
    }

    /**
     * Returns all active donation items (status='ACTIVE').
     */
    public List<DonationResponse> getAllActiveDonations() {
        return donationRepo.findByStatus("ACTIVE").stream()
                .map(this::toDonationResponse)
                .collect(Collectors.toList());
    }

    // ------------------ Private Helpers ------------------ //

    private String saveDonationFile(MultipartFile file) {
        try {
            File dir = new File(DONATIONS_DIR);
            if (!dir.exists() && !dir.mkdirs()) {
                throw new IOException("Could not create directory: " + DONATIONS_DIR);
            }

            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            File dest = new File(dir, fileName);
            file.transferTo(dest);

            // e.g. "Donations/<uuid_filename>.jpg"
            return "Donations/" + fileName;

        } catch (IOException e) {
            throw new DonationImageSaveException("Failed to save donation image file.", e);
        }
    }

    private DonationResponse toDonationResponse(DonationItem item) {
        List<String> imageUrls = item.getImages().stream()
                .map(img -> {
                    String fileName = img.getImagePath().replace("Donations/", "");
                    return "http://10.0.2.2:8080/api/donations/image/" + fileName;
                })
                .collect(Collectors.toList());

        String createdAt = null;
        if (item.getCreatedAt() != null) {
            createdAt = item.getCreatedAt().toLocalDateTime()
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        }

        // Optionally fetch user data
        User owner = userRepo.findById(item.getOwnerId()).orElse(null);
        String ownerName = (owner != null) ? owner.getFullName() : "Unknown";
        String ownerEmail = (owner != null) ? owner.getEmail() : "-";

        return DonationResponse.builder()
                .donationId(item.getDonationId())
                .title(item.getTitle())
                .description(item.getDescription())
                .status(item.getStatus())
                .createdAt(createdAt)
                .images(imageUrls)
                .ownerId(item.getOwnerId())
                .ownerFullName(ownerName)
                .ownerEmail(ownerEmail)
                .build();
    }
}
