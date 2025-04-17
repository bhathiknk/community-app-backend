// src/main/java/com/communityappbackend/service/RatingService.java
package com.communityappbackend.service;

import com.communityappbackend.dto.RatingDTO;
import com.communityappbackend.model.DonationRequest;
import com.communityappbackend.model.DonationItem;
import com.communityappbackend.model.Rating;
import com.communityappbackend.repository.DonationItemRepository;
import com.communityappbackend.repository.DonationRequestRepository;
import com.communityappbackend.repository.RatingRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RatingService {
    private final RatingRepository ratingRepo;
    private final DonationRequestRepository requestRepo;
    private final DonationItemRepository itemRepo;

    public RatingService(
            RatingRepository ratingRepo,
            DonationRequestRepository requestRepo,
            DonationItemRepository itemRepo
    ) {
        this.ratingRepo = ratingRepo;
        this.requestRepo = requestRepo;
        this.itemRepo    = itemRepo;
    }

    /**
     * Save a new Rating, deriving both raterId (from token) and rateeId
     * (the owner of the donated item) automatically.
     */
    public Rating save(RatingDTO dto, String raterId) {
        // 1) Look up the request
        DonationRequest req = requestRepo.findById(dto.getDonationRequestId())
                .orElseThrow(() -> new IllegalArgumentException("Request not found"));

        // 2) Derive rateeId: the owner of the donated item
        DonationItem item = itemRepo.findById(req.getDonationId())
                .orElseThrow(() -> new IllegalArgumentException("Donation item not found"));

        String rateeId = item.getOwnerId();

        // 3) Build and save the Rating
        Rating rating = Rating.builder()
                .donationRequestId(dto.getDonationRequestId())
                .raterId(raterId)
                .rateeId(rateeId)
                .score(dto.getScore())
                .comment(dto.getComment())
                .build();

        return ratingRepo.save(rating);
    }

    public List<Rating> getForUser(String userId) {
        return ratingRepo.findByRateeId(userId);
    }
}
