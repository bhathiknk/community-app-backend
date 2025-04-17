// src/main/java/com/communityappbackend/controller/RatingController.java
package com.communityappbackend.controller;

import com.communityappbackend.dto.RatingDTO;
import com.communityappbackend.model.Rating;
import com.communityappbackend.service.RatingService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.communityappbackend.model.User;

import java.util.List;

@RestController
@RequestMapping("/api/ratings")
public class RatingController {

    private final RatingService service;

    public RatingController(RatingService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Rating> submit(
            @RequestBody RatingDTO dto,
            Authentication auth
    ) {
        // Extract the current user's ID from the Authentication principal
        User currentUser = (User) auth.getPrincipal();
        Rating saved = service.save(dto, currentUser.getUserId());
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Rating>> getForUser(@PathVariable String userId) {
        return ResponseEntity.ok(service.getForUser(userId));
    }
}
