package com.communityappbackend.Service;

import com.communityappbackend.DTO.*;
import com.communityappbackend.Model.*;
import com.communityappbackend.Repository.*;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ItemService {

    private final ItemRepository itemRepo;
    private final ItemImageRepository imageRepo;

    private static final String ASSETS_DIR = "C:\\Projects\\Community App\\community-app-backend\\src\\main\\java\\com\\communityappbackend\\Assets";

    public ItemService(ItemRepository itemRepo, ItemImageRepository imageRepo) {
        this.itemRepo = itemRepo;
        this.imageRepo = imageRepo;
    }

    // Existing: addItem
    public ItemResponse addItem(ItemRequest request, List<MultipartFile> files, Authentication auth) {
        User user = (User) auth.getPrincipal();

        Item newItem = Item.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .price(request.getPrice())
                .categoryId(request.getCategoryId())
                .ownerId(user.getUserId())
                .status("ACTIVE") // default
                .build();

        newItem = itemRepo.save(newItem);

        if (files != null && !files.isEmpty()) {
            int count = 0;
            for (MultipartFile file : files) {
                if (count >= 5) break;
                String filePath = saveFileToAssets(file);
                ItemImage img = ItemImage.builder()
                        .imagePath(filePath)
                        .item(newItem)
                        .build();
                imageRepo.save(img);
                count++;
            }
        }

        return toItemResponse(newItem);
    }

    // Existing: get user items
    public List<ItemResponse> getMyItems(Authentication auth) {
        User user = (User) auth.getPrincipal();
        List<Item> items = itemRepo.findByOwnerId(user.getUserId());
        return items.stream()
                .map(this::toItemResponse)
                .collect(Collectors.toList());
    }

    // NEW: get all active items from other users, optionally filter by category
    public List<ItemResponse> getAllActiveExceptUser(Authentication auth, Long categoryId) {
        User user = (User) auth.getPrincipal();
        String currentUserId = user.getUserId();

        // fetch all items that are status=ACTIVE, NOT the current user
        // optionally filter by category if categoryId != null
        List<Item> items;
        if (categoryId != null) {
            items = itemRepo.findByStatusAndOwnerIdNotAndCategoryId("ACTIVE", currentUserId, categoryId);
        } else {
            items = itemRepo.findByStatusAndOwnerIdNot("ACTIVE", currentUserId);
        }

        return items.stream()
                .map(this::toItemResponse)
                .collect(Collectors.toList());
    }

    private ItemResponse toItemResponse(Item item) {
        List<String> imageUrls = item.getImages().stream()
                .map(img -> {
                    String fileName = img.getImagePath().replace("Assets/", "");
                    return "http://10.0.2.2:8080/api/items/image/" + fileName;
                })
                .collect(Collectors.toList());

        // Format created_at as string if not null
        String createdAtStr = item.getCreatedAt() != null
                ? item.getCreatedAt().toLocalDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                : null;

        return ItemResponse.builder()
                .itemId(item.getItemId())
                .title(item.getTitle())
                .description(item.getDescription())
                .price(item.getPrice())
                .categoryId(item.getCategoryId())
                .images(imageUrls)
                .status(item.getStatus())
                .createdAt(createdAtStr)
                .build();
    }

    private String saveFileToAssets(MultipartFile file) {
        try {
            File dir = new File(ASSETS_DIR);
            if (!dir.exists()) {
                boolean created = dir.mkdirs();
                if (!created) {
                    throw new IOException("Failed to create directory: " + ASSETS_DIR);
                }
            }

            String uniqueName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            File dest = new File(dir, uniqueName);
            file.transferTo(dest);
            System.out.println("File saved to: " + dest.getAbsolutePath());

            return "Assets/" + uniqueName;
        } catch (IOException e) {
            throw new RuntimeException("Failed to save file: " + e.getMessage(), e);
        }
    }
}
