package com.communityappbackend.controller.DonationsHandleAPIs;

import com.communityappbackend.dto.DonationsHandleDTOs.DonationRequest;
import com.communityappbackend.dto.DonationsHandleDTOs.DonationResponse;
import com.communityappbackend.exception.DonationNotFoundException;
import com.communityappbackend.service.DonationService.DonationService;
import org.springframework.core.io.*;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.List;

@RestController
@RequestMapping("/api/donations")
public class DonationController {

    private final DonationService donationService;

    // Path for serving donation images
    private static final String DONATIONS_DIR =
            "C:\\Projects\\Community App\\community-app-backend\\src\\main\\java\\com\\communityappbackend\\Assets\\Donations";

    public DonationController(DonationService donationService) {
        this.donationService = donationService;
    }

    // ------------------- Existing Endpoints ------------------- //

    @PostMapping("/add")
    public DonationResponse addDonation(
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestPart(value = "files", required = false) List<MultipartFile> files,
            Authentication auth
    ) {
        DonationRequest donationRequest = new DonationRequest();
        donationRequest.setTitle(title);
        donationRequest.setDescription(description);
        return donationService.addDonation(donationRequest, files, auth);
    }

    @GetMapping("/my")
    public List<DonationResponse> getMyDonations(Authentication auth) {
        return donationService.getMyDonations(auth);
    }

    @GetMapping("/image/{filename:.+}")
    public ResponseEntity<Resource> getDonationImage(@PathVariable String filename) {
        try {
            File file = new File(DONATIONS_DIR, filename);
            if (!file.exists()) {
                throw new DonationNotFoundException("Donation image not found: " + filename);
            }

            Path path = file.toPath();
            String contentType = Files.probeContentType(path);
            if (contentType == null) {
                contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
            }

            Resource resource = new FileSystemResource(file);
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .body(resource);

        } catch (IOException e) {
            throw new RuntimeException("Failed to read donation image: " + filename);
        }
    }

    @GetMapping("/active")
    public List<DonationResponse> getAllActiveDonations() {
        return donationService.getAllActiveDonations();
    }

    // ------------------- New Endpoints ------------------- //

    /**
     * Get a single donation item by ID (for the "view" popup).
     */
    @GetMapping("/{donationId}")
    public DonationResponse getDonationById(
            @PathVariable String donationId,
            Authentication auth
    ) {
        return donationService.getDonationById(donationId, auth);
    }


    /**
     * Update donation (title, description, status) and manage images.
     * "imageIdsToRemove" -> list of existing DB image IDs to remove.
     * "files" -> new images to add.
     */
    @PutMapping("/{donationId}")
    public DonationResponse updateDonation(
            @PathVariable String donationId,
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam("status") String status,
            @RequestParam(value = "imageIdsToRemove", required = false) List<Long> imageIdsToRemove,
            @RequestPart(value = "files", required = false) List<MultipartFile> newImages,
            Authentication auth
    ) {
        return donationService.updateDonation(
                donationId, title, description, status, imageIdsToRemove, newImages, auth
        );
    }


    /**
     * Returns all ACTIVE donation items except those posted by the current user.
     */
    @GetMapping("/others")
    public List<DonationResponse> getDonationsFromOthers(Authentication auth) {
        return donationService.getAllDonationsExceptMine(auth);
    }
}
