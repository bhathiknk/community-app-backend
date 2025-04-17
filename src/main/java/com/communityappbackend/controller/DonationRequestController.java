package com.communityappbackend.controller;

import com.communityappbackend.dto.DonationRequestDTO;
import com.communityappbackend.dto.DonationRequestSentViewDTO;
import com.communityappbackend.dto.DonationRequestViewDTO;
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

    /** Create a new donation request */
    @PostMapping
    public ResponseEntity<DonationRequest> createDonationRequest(
            @RequestBody DonationRequestDTO dto,
            Authentication auth
    ) {
        DonationRequest req = donationRequestService.createDonationRequest(dto, auth);
        return ResponseEntity.ok(req);
    }

    /** All incoming requests for items I own (with full item + requester data) */
    @GetMapping("/incoming")
    public ResponseEntity<List<DonationRequestViewDTO>> getIncomingRequestsView(Authentication auth) {
        List<DonationRequestViewDTO> requests =
                donationRequestService.getIncomingDonationRequestsView(auth);
        return ResponseEntity.ok(requests);
    }

    /** All requests I’ve sent (with full item + owner data) */
    @GetMapping("/sent")
    public ResponseEntity<List<DonationRequestSentViewDTO>> getSentRequestsView(Authentication auth) {
        List<DonationRequestSentViewDTO> sent =
                donationRequestService.getSentDonationRequestsView(auth);
        return ResponseEntity.ok(sent);
    }

    /** Accept a request */
    @PostMapping("/{requestId}/accept")
    public ResponseEntity<DonationRequest> acceptDonationRequest(
            @PathVariable String requestId,
            Authentication auth
    ) {
        DonationRequest req = donationRequestService.acceptDonationRequest(requestId, auth);
        return ResponseEntity.ok(req);
    }

    /** Reject a request */
    @PostMapping("/{requestId}/reject")
    public ResponseEntity<DonationRequest> rejectDonationRequest(
            @PathVariable String requestId,
            Authentication auth
    ) {
        DonationRequest req = donationRequestService.rejectDonationRequest(requestId, auth);
        return ResponseEntity.ok(req);
    }
}
