package com.communityappbackend.controller.DonationsHandleAPIs;

import com.communityappbackend.dto.DonationsHandleDTOs.DonationCompleteDTO;
import com.communityappbackend.dto.DonationsHandleDTOs.DonationRequestDTO;
import com.communityappbackend.dto.DonationsHandleDTOs.DonationRequestSentViewDTO;
import com.communityappbackend.dto.DonationsHandleDTOs.DonationRequestViewDTO;
import com.communityappbackend.model.DonationRequest;
import com.communityappbackend.service.DonationService.DonationRequestService;
import com.communityappbackend.service.RatingsService.RatingService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/donation-requests")
public class DonationRequestController {

    private final DonationRequestService donationRequestService;
    private final RatingService ratingService;

    public DonationRequestController(DonationRequestService donationRequestService,RatingService ratingService) {
        this.donationRequestService = donationRequestService;
        this.ratingService  = ratingService;
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
    @PostMapping("/{requestId}/complete")
    public ResponseEntity<DonationRequest> complete(
            @PathVariable String requestId,
            Authentication auth
    ) {
        return ResponseEntity.ok(
                donationRequestService.completeDonation(requestId, auth)
        );
    }

    @GetMapping("/complete/{requestId}")
    public ResponseEntity<DonationCompleteDTO> details(
            @PathVariable String requestId
    ) {
        return ResponseEntity.ok(
                donationRequestService.getCompleteDetails(requestId)
        );
    }
}
