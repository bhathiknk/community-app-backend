package com.communityappbackend.controller;

import com.communityappbackend.dto.*;
import com.communityappbackend.model.DonationRequest;
import com.communityappbackend.service.DonationRequestService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/donation-requests")
public class DonationRequestController {

    private final DonationRequestService donationRequestService;

    public DonationRequestController(DonationRequestService donationRequestService) {
        this.donationRequestService = donationRequestService;
    }

    @PostMapping
    public ResponseEntity<DonationRequest> createDonationRequest(
            @RequestBody DonationRequestDTO dto,
            Authentication auth
    ) {
        DonationRequest req = donationRequestService.createDonationRequest(dto, auth);
        return ResponseEntity.ok(req);
    }

    /**
     * Return a list of DonationRequestViewDTO with all incoming requests + item + user data.
     */
    @GetMapping("/incoming")
    public ResponseEntity<List<DonationRequestViewDTO>> getIncomingDonationRequests(Authentication auth) {
        List<DonationRequestViewDTO> requests =
                donationRequestService.getIncomingDonationRequestsView(auth);
        return ResponseEntity.ok(requests);
    }

    @PostMapping("/{requestId}/accept")
    public ResponseEntity<DonationRequest> acceptDonationRequest(
            @PathVariable String requestId, Authentication auth
    ) {
        DonationRequest req = donationRequestService.acceptDonationRequest(requestId, auth);
        return ResponseEntity.ok(req);
    }

    @PostMapping("/{requestId}/reject")
    public ResponseEntity<DonationRequest> rejectDonationRequest(
            @PathVariable String requestId, Authentication auth
    ) {
        DonationRequest req = donationRequestService.rejectDonationRequest(requestId, auth);
        return ResponseEntity.ok(req);
    }
}
