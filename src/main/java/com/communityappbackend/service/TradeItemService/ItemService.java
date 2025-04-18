package com.communityappbackend.service.TradeItemService;

import com.communityappbackend.dto.TradeItemsHandleDTOs.ItemRequest;
import com.communityappbackend.dto.TradeItemsHandleDTOs.ItemResponse;
import com.communityappbackend.exception.ItemNotFoundException;
import com.communityappbackend.model.Item;
import com.communityappbackend.model.ItemImage;
import com.communityappbackend.model.User;
import com.communityappbackend.repository.ItemImageRepository;
import com.communityappbackend.repository.ItemRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Provides CRUD and business logic for items.
 */
@Service
public class ItemService {

    private final ItemRepository itemRepo;
    private final ItemImageRepository imageRepo;

    private static final String ASSETS_DIR =
            "C:\\Projects\\Community App\\community-app-backend\\src\\main\\java\\com\\communityappbackend\\Assets";

    public ItemService(ItemRepository itemRepo, ItemImageRepository imageRepo) {
        this.itemRepo = itemRepo;
        this.imageRepo = imageRepo;
    }

    /**
     * Creates a new item and optionally persists up to 5 images.
     */
    public ItemResponse addItem(ItemRequest request, List<MultipartFile> files, Authentication auth) {
        User user = (User) auth.getPrincipal();

        Item newItem = Item.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .price(request.getPrice())
                .categoryId(request.getCategoryId())
                .ownerId(user.getUserId())
                .status("ACTIVE")
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

    /**
     * Fetches items belonging to the authenticated user.
     */
    public List<ItemResponse> getMyItems(Authentication auth) {
        User user = (User) auth.getPrincipal();
        List<Item> items = itemRepo.findByOwnerId(user.getUserId());
        return items.stream().map(this::toItemResponse).collect(Collectors.toList());
    }

    /**
     * Fetches item details by ID, or throws if not found.
     */
    public ItemResponse getItemDetails(String itemId) {
        Item item = itemRepo.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException("Item not found: " + itemId));
        return toItemResponse(item);
    }

    /**
     * Fetches all items by a specific owner.
     */
    public List<ItemResponse> getItemsByOwner(String userId) {
        List<Item> items = itemRepo.findByOwnerId(userId);
        return items.stream().map(this::toItemResponse).collect(Collectors.toList());
    }

    // -------------------------- PRIVATE HELPERS -------------------------- //

    private String saveFileToAssets(MultipartFile file) {
        try {
            File dir = new File(ASSETS_DIR);
            if (!dir.exists()) {
                if (!dir.mkdirs()) {
                    throw new IOException("Failed to create directory: " + ASSETS_DIR);
                }
            }

            String uniqueName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            File dest = new File(dir, uniqueName);
            file.transferTo(dest);
            return "Assets/" + uniqueName;

        } catch (IOException e) {
            throw new RuntimeException("Failed to save file: " + e.getMessage(), e);
        }
    }

    private ItemResponse toItemResponse(Item item) {
        List<String> imageUrls = item.getImages().stream()
                .map(img -> {
                    String fileName = img.getImagePath().replace("Assets/", "");
                    return "http://10.0.2.2:8080/api/items/image/" + fileName;
                })
                .collect(Collectors.toList());

        String createdAtStr = null;
        if (item.getCreatedAt() != null) {
            createdAtStr = item.getCreatedAt().toLocalDateTime()
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
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
                .build();
    }
}
