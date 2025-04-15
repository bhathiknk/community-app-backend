package com.communityappbackend.controller;

import com.communityappbackend.dto.ItemResponse;
import com.communityappbackend.service.TradeItemService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/trade")
public class TradeItemController {

    private final TradeItemService tradeItemService;

    public TradeItemController(TradeItemService tradeItemService) {
        this.tradeItemService = tradeItemService;
    }

    // GET /api/trade?categoryId={optional}
    @GetMapping
    public List<ItemResponse> getAllActiveExceptUser(
            @RequestParam(required = false) Long categoryId,
            Authentication auth
    ) {
        return tradeItemService.getAllActiveExceptUser(auth, categoryId);
    }

    // GET /api/trade/details/{itemId}
    @GetMapping("/details/{itemId}")
    public ResponseEntity<ItemResponse> getItemDetails(
            @PathVariable String itemId
    ) {
        ItemResponse item = tradeItemService.getItemDetails(itemId);
        if (item == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(item);
    }
}
