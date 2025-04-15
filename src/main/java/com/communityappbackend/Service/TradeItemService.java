package com.communityappbackend.Service;

import com.communityappbackend.DTO.ItemResponse;
import com.communityappbackend.Model.Item;
import com.communityappbackend.Model.User;
import com.communityappbackend.Model.UserProfileImage;
import com.communityappbackend.Repository.ItemRepository;
import com.communityappbackend.Repository.UserProfileImageRepository;
import com.communityappbackend.Repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

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

    public List<ItemResponse> getAllActiveExceptUser(Authentication auth, Long categoryId) {
        User user = (User) auth.getPrincipal();
        String currentUserId = user.getUserId();

        List<Item> items;
        if (categoryId != null) {
            items = itemRepo.findByStatusAndOwnerIdNotAndCategoryId(
                    "ACTIVE", currentUserId, categoryId
            );
        } else {
            items = itemRepo.findByStatusAndOwnerIdNot("ACTIVE", currentUserId);
        }

        return items.stream()
                .map(this::toItemResponseWithOwner)
                .collect(Collectors.toList());
    }

    public ItemResponse getItemDetails(String itemId) {
        Optional<Item> opt = itemRepo.findById(itemId);
        if (opt.isEmpty()) {
            return null;
        }
        return toItemResponseWithOwner(opt.get());
    }

    private ItemResponse toItemResponseWithOwner(Item item) {
        // Convert item images to full URLs
        List<String> imageUrls = item.getImages().stream()
                .map(img -> {
                    String fileName = img.getImagePath().replace("Assets/", "");
                    return "http://10.0.2.2:8080/api/items/image/" + fileName;
                })
                .collect(Collectors.toList());

        // Format created_at
        String createdAtStr = null;
        Timestamp ts = item.getCreatedAt();
        if (ts != null) {
            createdAtStr = ts.toLocalDateTime()
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        }

        // Fetch owner data
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

            // Fetch user's profile image from DB (if exists)
            Optional<UserProfileImage> upiOpt =
                    userProfileImageRepo.findByUserId(owner.getUserId());
            if (upiOpt.isPresent()) {
                UserProfileImage upi = upiOpt.get();
                // Build the fully qualified URL for the profile image
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
