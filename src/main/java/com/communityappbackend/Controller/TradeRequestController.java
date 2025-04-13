package com.communityappbackend.Controller;

import com.communityappbackend.DTO.ItemResponse;
import com.communityappbackend.DTO.TradeRequestDTO;
import com.communityappbackend.DTO.TradeRequestDetailedDTO;
import com.communityappbackend.Model.TradeRequest;
import com.communityappbackend.Service.TradeRequestService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/trade/requests")
public class TradeRequestController {

    private final TradeRequestService tradeRequestService;

    public TradeRequestController(TradeRequestService tradeRequestService) {
        this.tradeRequestService = tradeRequestService;
    }

    // 1) Create a new request
    @PostMapping
    public ResponseEntity<TradeRequest> createRequest(
            @RequestBody TradeRequestDTO dto,
            Authentication auth
    ) {
        TradeRequest req = tradeRequestService.createRequest(dto, auth);
        return ResponseEntity.ok(req);
    }

    // 2) View all requests for items that I own
    @GetMapping("/incoming")
    public ResponseEntity<List<TradeRequest>> getIncoming(Authentication auth) {
        List<TradeRequest> requests = tradeRequestService.getIncomingRequests(auth);
        return ResponseEntity.ok(requests);
    }

    // 3) Approve a request (picking an item from the sender)
    @PostMapping("/{requestId}/approve")
    public ResponseEntity<TradeRequest> approveRequest(
            @PathVariable String requestId,
            @RequestParam(required = false) String selectedItemId,
            Authentication auth
    ) {
        TradeRequest updated = tradeRequestService.approveRequest(requestId, selectedItemId, auth);
        return ResponseEntity.ok(updated);
    }

    // 4) Reject a request
    @PostMapping("/{requestId}/reject")
    public ResponseEntity<TradeRequest> rejectRequest(
            @PathVariable String requestId,
            Authentication auth
    ) {
        TradeRequest updated = tradeRequestService.rejectRequest(requestId, auth);
        return ResponseEntity.ok(updated);
    }

    // 5) Get a detailed list of incoming requests with optional filtering by status.
    @GetMapping("/incoming/detailed")
    public List<TradeRequestDetailedDTO> getIncomingDetailed(@RequestParam(required = false) String status,
                                                             Authentication auth) {
        return tradeRequestService.getIncomingRequestsDetailed(auth, status);
    }

    // 6) Get the sender's items (public listings)
    @GetMapping("/sender/{userId}/items")
    public ResponseEntity<List<ItemResponse>> getItemsBySender(
            @PathVariable String userId,
            Authentication auth
    ) {
        List<ItemResponse> items = tradeRequestService.getItemsByOwner(userId);
        return ResponseEntity.ok(items);
    }
}
