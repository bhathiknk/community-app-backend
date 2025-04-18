// src/main/java/com/communityappbackend/controller/RatingController.java
package com.communityappbackend.controller.RatingAPIs;

import com.communityappbackend.dto.RatingsHandleDTOs.RatingDTO;
import com.communityappbackend.dto.RatingsHandleDTOs.RatingDetailDTO;
import com.communityappbackend.dto.RatingsHandleDTOs.RatingReceivedDTO;
import com.communityappbackend.dto.RatingsHandleDTOs.RatingSummaryDTO;
import com.communityappbackend.model.Rating;
import com.communityappbackend.service.RatingsService.RatingService;
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

    @GetMapping("/me")
    public ResponseEntity<RatingSummaryDTO> getMySummary(Authentication auth) {
        User me = (User) auth.getPrincipal();
        RatingSummaryDTO summary = service.summaryForUser(me.getUserId());
        return ResponseEntity.ok(summary);
    }

    /** GET all the detailed “sent” ratings for the current user */
    @GetMapping("/me/detailed")
    public ResponseEntity<List<RatingDetailDTO>> mySentRatings(Authentication auth) {
        String me = ((User) auth.getPrincipal()).getUserId();
        return ResponseEntity.ok(service.detailsForRater(me));
    }

    @GetMapping("/me/received")
    public ResponseEntity<List<RatingReceivedDTO>> myReceivedRatings(Authentication auth) {
        String me = ((User)auth.getPrincipal()).getUserId();
        return ResponseEntity.ok(service.detailsForRatee(me));
    }
}
