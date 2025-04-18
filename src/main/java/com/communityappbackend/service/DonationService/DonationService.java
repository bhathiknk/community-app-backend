package com.communityappbackend.service.DonationService;

import com.communityappbackend.dto.DonationsHandleDTOs.DonationRequest;
import com.communityappbackend.dto.DonationsHandleDTOs.DonationResponse;
import com.communityappbackend.exception.*;
import com.communityappbackend.model.*;
import com.communityappbackend.repository.*;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DonationService {

    private final DonationItemRepository donationRepo;
    private final DonationItemImageRepository donationImageRepo;
    private final UserRepository userRepo;
    private final UserProfileImageRepository userProfileImageRepo;

    // Folder path for donation images
    private static final String DONATIONS_DIR =
            "C:\\Projects\\Community App\\community-app-backend\\src\\main\\java\\com\\communityappbackend\\Assets\\Donations";

    public DonationService(
            DonationItemRepository donationRepo,
            DonationItemImageRepository donationImageRepo,
            UserRepository userRepo,
            UserProfileImageRepository userProfileImageRepo
    ) {
        this.donationRepo = donationRepo;
        this.donationImageRepo = donationImageRepo;
        this.userRepo = userRepo;
        this.userProfileImageRepo = userProfileImageRepo;
    }

    // ------------------- Existing Methods ------------------- //

    public DonationResponse addDonation(
            DonationRequest request,
            List<MultipartFile> files,
            Authentication auth
    ) {
        User user = (User) auth.getPrincipal();
        if (user == null) {
            throw new UserNotFoundException("Authenticated user not found.");
        }

        DonationItem donation = DonationItem.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .ownerId(user.getUserId())
                .status("ACTIVE")
                .build();
        donation = donationRepo.save(donation);

        if (files != null && !files.isEmpty()) {
            int count = 0;
            for (MultipartFile file : files) {
                if (count >= 5) break;
                String filePath = saveDonationFile(file);
                DonationItemImage img = DonationItemImage.builder()
                        .imagePath(filePath)
                        .donationItem(donation)
                        .build();
                donationImageRepo.save(img);
                count++;
            }
        }

        return toDonationResponse(donation);
    }

    public List<DonationResponse> getMyDonations(Authentication auth) {
        User user = (User) auth.getPrincipal();
        List<DonationItem> items = donationRepo.findByOwnerId(user.getUserId());
        return items.stream()
                .map(this::toDonationResponse)
                .collect(Collectors.toList());
    }

    public List<DonationResponse> getAllActiveDonations() {
        List<DonationItem> activeList = donationRepo.findByStatus("ACTIVE");
        return activeList.stream()
                .map(this::toDonationResponse)
                .collect(Collectors.toList());
    }

    public DonationResponse getDonationById(String donationId, Authentication auth) {
        User user = (User) auth.getPrincipal();
        DonationItem donation = donationRepo.findById(donationId)
                .orElseThrow(() -> new DonationNotFoundException("Donation not found: " + donationId));

        if (!donation.getOwnerId().equals(user.getUserId())) {
            throw new RuntimeException("Not authorized to view this donation.");
        }

        return toDonationResponse(donation);
    }

    public DonationResponse updateDonation(
            String donationId,
            String title,
            String description,
            String status,
            List<Long> imageIdsToRemove,
            List<MultipartFile> newImages,
            Authentication auth
    ) {
        User user = (User) auth.getPrincipal();
        DonationItem donation = donationRepo.findById(donationId)
                .orElseThrow(() -> new DonationNotFoundException("Donation not found: " + donationId));

        if (!donation.getOwnerId().equals(user.getUserId())) {
            throw new RuntimeException("Not authorized to edit this donation.");
        }

        donation.setTitle(title);
        donation.setDescription(description);
        donation.setStatus(status);

        // Remove images
        if (imageIdsToRemove != null && !imageIdsToRemove.isEmpty()) {
            for (Long imageId : imageIdsToRemove) {
                Optional<DonationItemImage> imgOpt = donationImageRepo.findById(imageId);
                if (imgOpt.isPresent()) {
                    DonationItemImage img = imgOpt.get();
                    donationImageRepo.delete(img);
                    removeDonationFile(img.getImagePath());
                }
            }
        }

        // Add new images
        int existingCount = donation.getImages().size();
        if (newImages != null && !newImages.isEmpty()) {
            for (MultipartFile file : newImages) {
                if (existingCount >= 5) break;
                String filePath = saveDonationFile(file);
                DonationItemImage img = DonationItemImage.builder()
                        .imagePath(filePath)
                        .donationItem(donation)
                        .build();
                donationImageRepo.save(img);
                existingCount++;
            }
        }

        DonationItem updated = donationRepo.save(donation);
        return toDonationResponse(updated);
    }

    // ------------------- NEW METHOD: "Others" Donations ------------------- //
    /**
     * Returns all ACTIVE donations except the current user's own posts.
     *
     * ALGORITHM: Linear filter (O(n)).
     * DATA STRUCTURE: List to store all items, then we skip the user's items.
     */
    public List<DonationResponse> getAllDonationsExceptMine(Authentication auth) {
        User user = (User) auth.getPrincipal();
        String currentUserId = user.getUserId();

        // 1) Fetch all ACTIVE donations
        List<DonationItem> activeList = donationRepo.findByStatus("ACTIVE");

        // 2) Filter out items with the same ownerId
        List<DonationItem> othersList = activeList.stream()
                .filter(donation -> !donation.getOwnerId().equals(currentUserId))
                .collect(Collectors.toList());

        // 3) Convert each to a DonationResponse
        return othersList.stream()
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
            return "Donations/" + fileName;

        } catch (IOException e) {
            throw new DonationImageSaveException("Failed to save donation image file.", e);
        }
    }

    private void removeDonationFile(String imagePath) {
        if (imagePath == null || imagePath.isEmpty()) return;
        try {
            String fileName = imagePath.replace("Donations/", "");
            File file = new File(DONATIONS_DIR, fileName);
            if (file.exists()) {
                file.delete();
            }
        } catch (Exception e) {
            // log or ignore
        }
    }

    /**
     * Convert DonationItem -> DonationResponse. Also fetch the user's phone/address/etc
     * plus their profile image if available.
     */
    private DonationResponse toDonationResponse(DonationItem item) {
        // Simple URLs
        List<String> imageUrls = new ArrayList<>();
        // Detailed map of { imageId, url }
        List<Map<String, Object>> detailedList = new ArrayList<>();

        for (DonationItemImage img : item.getImages()) {
            String fileName = img.getImagePath().replace("Donations/", "");
            String url = "http://10.0.2.2:8080/api/donations/image/" + fileName;
            imageUrls.add(url);

            Map<String, Object> map = new HashMap<>();
            map.put("imageId", img.getImageId());
            map.put("url", url);
            detailedList.add(map);
        }

        String createdAt = null;
        if (item.getCreatedAt() != null) {
            createdAt = item.getCreatedAt().toLocalDateTime()
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        }

        // Fetch user data
        User owner = userRepo.findById(item.getOwnerId()).orElse(null);

        String ownerName = (owner != null) ? owner.getFullName() : "Unknown";
        String ownerEmail = (owner != null) ? owner.getEmail() : "-";
        String ownerPhone = (owner != null) ? owner.getPhone() : "";
        String ownerAddress = (owner != null) ? owner.getAddress() : "";
        String ownerCity = (owner != null) ? owner.getCity() : "";
        String ownerProvince = (owner != null) ? owner.getProvince() : "";

        // Fetch the user's profile image from DB (if any)
        String ownerProfileImageUrl = "";
        if (owner != null) {
            Optional<UserProfileImage> upiOpt = userProfileImageRepo.findByUserId(owner.getUserId());
            if (upiOpt.isPresent()) {
                UserProfileImage upi = upiOpt.get();
                if (upi.getImagePath() != null && !upi.getImagePath().isEmpty()) {
                    // e.g. /image/<filename> or /api/...
                    // For simplicity, let's mirror the approach from donations:
                    String fileName = upi.getImagePath();
                    ownerProfileImageUrl = "http://10.0.2.2:8080/image/" + fileName;
                }
            }
        }

        return DonationResponse.builder()
                .donationId(item.getDonationId())
                .title(item.getTitle())
                .description(item.getDescription())
                .status(item.getStatus())
                .createdAt(createdAt)
                .images(imageUrls)
                .imageList(detailedList)
                .ownerId(item.getOwnerId())
                .ownerFullName(ownerName)
                .ownerEmail(ownerEmail)
                // NEW fields
                .ownerPhone(ownerPhone)
                .ownerAddress(ownerAddress)
                .ownerCity(ownerCity)
                .ownerProvince(ownerProvince)
                .ownerProfileImage(ownerProfileImageUrl)
                .build();
    }
}
