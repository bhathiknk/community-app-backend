package com.communityappbackend.controller.TradeItemsHandleAPIs;

import com.communityappbackend.dto.TradeItemsHandleDTOs.ItemResponse;
import com.communityappbackend.dto.TradeItemsHandleDTOs.TradeRequestDTO;
import com.communityappbackend.dto.TradeItemsHandleDTOs.TradeRequestDetailedDTO;
import com.communityappbackend.model.TradeRequest;
import com.communityappbackend.service.TradeItemService.TradeRequestService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Manages creation and handling of trade requests for user items.
 */
@RestController
@RequestMapping("/api/trade/requests")
public class TradeRequestController {

    private final TradeRequestService tradeRequestService;

    public TradeRequestController(TradeRequestService tradeRequestService) {
        this.tradeRequestService = tradeRequestService;
    }

    /**
     * Creates a new trade request (money or item-based).
     */
    @PostMapping
    public ResponseEntity<TradeRequest> createRequest(
            @RequestBody TradeRequestDTO dto,
            Authentication auth
    ) {
        TradeRequest req = tradeRequestService.createRequest(dto, auth);
        return ResponseEntity.ok(req);
    }

    /**
     * Returns all incoming trade requests (for items owned by the authenticated user).
     */
    @GetMapping("/incoming")
    public ResponseEntity<List<TradeRequest>> getIncomingRequests(Authentication auth) {
        List<TradeRequest> requests = tradeRequestService.getIncomingRequests(auth);
        return ResponseEntity.ok(requests);
    }

    /**
     * Approves a request, optionally selecting an item from the sender.
     */
    @PostMapping("/{requestId}/approve")
    public ResponseEntity<TradeRequest> approveRequest(
            @PathVariable String requestId,
            @RequestParam(required = false) String selectedItemId,
            Authentication auth
    ) {
        TradeRequest updated = tradeRequestService.approveRequest(requestId, selectedItemId, auth);
        return ResponseEntity.ok(updated);
    }

    /**
     * Rejects a request.
     */
    @PostMapping("/{requestId}/reject")
    public ResponseEntity<TradeRequest> rejectRequest(
            @PathVariable String requestId,
            Authentication auth
    ) {
        TradeRequest updated = tradeRequestService.rejectRequest(requestId, auth);
        return ResponseEntity.ok(updated);
    }

    /**
     * Returns a detailed list of incoming requests for the current user's items,
     * with optional filtering by status.
     */
    @GetMapping("/incoming/detailed")
    public List<TradeRequestDetailedDTO> getIncomingDetailed(
            @RequestParam(required = false) String status,
            Authentication auth
    ) {
        return tradeRequestService.getIncomingRequestsDetailed(auth, status);
    }

    /**
     * Gets the sender's publicly listed items (by userId).
     */
    @GetMapping("/sender/{userId}/items")
    public ResponseEntity<List<ItemResponse>> getItemsBySender(
            @PathVariable String userId,
            Authentication auth
    ) {
        List<ItemResponse> items = tradeRequestService.getItemsByOwner(userId);
        return ResponseEntity.ok(items);
    }
}
