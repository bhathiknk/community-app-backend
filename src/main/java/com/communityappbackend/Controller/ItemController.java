package com.communityappbackend.Controller;

import com.communityappbackend.DTO.ItemRequest;
import com.communityappbackend.DTO.ItemResponse;
import com.communityappbackend.Service.ItemService;
import org.springframework.core.io.*;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.List;

@RestController
@RequestMapping("/api/items")
public class ItemController {

    private final ItemService itemService;

    // For local images
    private static final String ASSETS_DIR = "C:\\Projects\\Community App\\community-app-backend\\src\\main\\java\\com\\communityappbackend\\Assets";

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    // POST /api/items/add
    @PostMapping("/add")
    public ItemResponse addItem(
            @RequestPart("item") ItemRequest itemRequest,
            @RequestPart(value = "files", required = false) List<MultipartFile> files,
            Authentication auth
    ) {
        return itemService.addItem(itemRequest, files, auth);
    }

    // GET /api/items/my
    @GetMapping("/my")
    public List<ItemResponse> getMyItems(Authentication auth) {
        return itemService.getMyItems(auth);
    }

    // NEW: GET /api/items/image/{filename}
    // Serve actual image from the Assets folder
    @GetMapping("/image/{filename:.+}")
    public ResponseEntity<Resource> getImage(@PathVariable String filename) {
        try {
            File file = new File(ASSETS_DIR, filename);
            if (!file.exists()) {
                return ResponseEntity.notFound().build();
            }

            Path path = file.toPath();
            String contentType = Files.probeContentType(path);
            if (contentType == null) {
                contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE; // fallback
            }

            Resource resource = new FileSystemResource(file);
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .body(resource);

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // NEW: GET /api/items/user/{userId} => fetch that user's items
    @GetMapping("/user/{userId}")
    public List<ItemResponse> getItemsByUserId(@PathVariable String userId) {
        return itemService.getItemsByOwner(userId);
    }
}
