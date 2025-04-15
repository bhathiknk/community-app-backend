package com.communityappbackend.service;

import com.communityappbackend.dto.ItemResponse;
import com.communityappbackend.exception.ItemNotFoundException;
import com.communityappbackend.model.Item;
import com.communityappbackend.model.ItemImage;
import com.communityappbackend.model.User;
import com.communityappbackend.model.UserProfileImage;
import com.communityappbackend.repository.ItemRepository;
import com.communityappbackend.repository.UserProfileImageRepository;
import com.communityappbackend.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Provides specialized trade item listing logic.
 */
@Service
public class TradeItemService {

    private final ItemRepository itemRepo;
    private final UserRepository userRepo;
    private final UserProfileImageRepository userProfileImageRepo;

    public TradeItemService(
            ItemRepository itemRepo,
            UserRepository userRepo,
            UserProfileImageRepository userProfileImageRepo
    ) {
        this.itemRepo = itemRepo;
        this.userRepo = userRepo;
        this.userProfileImageRepo = userProfileImageRepo;
    }

    /**
     * Returns all 'ACTIVE' items owned by users other than the authenticated user,
     * optionally filtered by category.
     */
    public List<ItemResponse> getAllActiveExceptUser(Authentication auth, Long categoryId) {
        User currentUser = (User) auth.getPrincipal();
        String currentUserId = currentUser.getUserId();

        List<Item> items;
        if (categoryId != null) {
            items = itemRepo.findByStatusAndOwnerIdNotAndCategoryId(
                    "ACTIVE", currentUserId, categoryId);
        } else {
            items = itemRepo.findByStatusAndOwnerIdNot("ACTIVE", currentUserId);
        }

        return items.stream().map(this::toItemResponseWithOwner).collect(Collectors.toList());
    }

    /**
     * Fetches full item details including owner info; throws if item not found.
     */
    public ItemResponse getItemDetails(String itemId) {
        Item item = itemRepo.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException("Item not found: " + itemId));
        return toItemResponseWithOwner(item);
    }

    // -------------------------- PRIVATE HELPERS -------------------------- //

    private ItemResponse toItemResponseWithOwner(Item item) {
        List<String> imageUrls = item.getImages().stream()
                .map(img -> {
                    String fileName = img.getImagePath().replace("Assets/", "");
                    return "http://10.0.2.2:8080/api/items/image/" + fileName;
                })
                .collect(Collectors.toList());

        Timestamp ts = item.getCreatedAt();
        String createdAtStr = (ts != null)
                ? ts.toLocalDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                : null;

        User owner = userRepo.findById(item.getOwnerId()).orElse(null);

        String ownerFullName = "";
        String ownerEmail = "";
        String ownerPhone = "";
        String ownerAddress = "";
        String ownerProfileImageUrl = "";

        if (owner != null) {
            ownerFullName = owner.getFullName();
            ownerEmail = owner.getEmail();
            ownerPhone = owner.getPhone();
            ownerAddress = (owner.getAddress() != null) ? owner.getAddress() : "";

            Optional<UserProfileImage> upiOpt = userProfileImageRepo.findByUserId(owner.getUserId());
            if (upiOpt.isPresent()) {
                UserProfileImage upi = upiOpt.get();
                if (upi.getImagePath() != null && !upi.getImagePath().isEmpty()) {
                    String fileName = upi.getImagePath().replace("Assets/", "");
                    ownerProfileImageUrl = "http://10.0.2.2:8080/image/" + fileName;
                }
            }
        }

        return ItemResponse.builder()
                .itemId(item.getItemId())
                .title(item.getTitle())
                .description(item.getDescription())
                .price(item.getPrice())
                .categoryId(item.getCategoryId())
                .images(imageUrls)
                .status(item.getStatus())
                .createdAt(createdAtStr)
                .ownerFullName(ownerFullName)
                .ownerEmail(ownerEmail)
                .ownerPhone(ownerPhone)
                .ownerAddress(ownerAddress)
                .ownerProfileImage(ownerProfileImageUrl)
                .build();
    }
}
