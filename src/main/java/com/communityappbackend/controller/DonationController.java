package com.communityappbackend.controller;

import com.communityappbackend.dto.DonationRequest;
import com.communityappbackend.dto.DonationResponse;
import com.communityappbackend.exception.DonationNotFoundException;
import com.communityappbackend.service.DonationService;
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

    /**
     * Add new donation item with optional images (limit 5).
     * Must use multipart/form-data in the request.
     * "donation" = JSON (DonationRequest)
     * "files" = up to 5 image files
     */
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


    /**
     * Returns only the authenticated user's donation items.
     */
    @GetMapping("/my")
    public List<DonationResponse> getMyDonations(Authentication auth) {
        return donationService.getMyDonations(auth);
    }

    /**
     * Serve donation images publicly: /api/donations/image/{filename}
     */
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

    /**
     * (Optional) Return all active donations from all users.
     */
    @GetMapping("/active")
    public List<DonationResponse> getAllActiveDonations() {
        return donationService.getAllActiveDonations();
    }
}
