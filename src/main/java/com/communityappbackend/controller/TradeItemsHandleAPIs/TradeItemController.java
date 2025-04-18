package com.communityappbackend.controller.TradeItemsHandleAPIs;

import com.communityappbackend.dto.TradeItemsHandleDTOs.ItemResponse;
import com.communityappbackend.service.TradeItemService.TradeItemService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Handles listing all active tradeable items except from the current user,
 * as well as item detail retrieval.
 */
@RestController
@RequestMapping("/api/trade")
public class TradeItemController {

    private final TradeItemService tradeItemService;

    public TradeItemController(TradeItemService tradeItemService) {
        this.tradeItemService = tradeItemService;
    }

    /**
     * Gets all active items from other users, optionally filtered by category.
     */
    @GetMapping
    public List<ItemResponse> getAllActiveExceptUser(
            @RequestParam(required = false) Long categoryId,
            Authentication auth
    ) {
        return tradeItemService.getAllActiveExceptUser(auth, categoryId);
    }

    /**
     * Fetches detailed info about a specific item.
     */
    @GetMapping("/details/{itemId}")
    public ResponseEntity<ItemResponse> getItemDetails(@PathVariable String itemId) {
        ItemResponse item = tradeItemService.getItemDetails(itemId);
        if (item == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(item);
    }
}
