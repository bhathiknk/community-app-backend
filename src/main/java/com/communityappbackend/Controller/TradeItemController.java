package com.communityappbackend.Controller;

import com.communityappbackend.DTO.ItemResponse;
import com.communityappbackend.Service.ItemService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/trade")
public class TradeItemController {

    private final ItemService itemService;

    public TradeItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    // GET /api/trade?categoryId={optional}
    @GetMapping
    public List<ItemResponse> getAllActiveExceptUser(
            @RequestParam(required = false) Long categoryId,
            Authentication auth
    ) {
        return itemService.getAllActiveExceptUser(auth, categoryId);
    }
}
