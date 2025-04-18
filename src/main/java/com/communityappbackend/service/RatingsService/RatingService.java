// src/main/java/com/communityappbackend/service/RatingService.java
package com.communityappbackend.service.RatingsService;

import com.communityappbackend.dto.RatingsHandleDTOs.RatingDTO;
import com.communityappbackend.dto.RatingsHandleDTOs.RatingDetailDTO;
import com.communityappbackend.dto.RatingsHandleDTOs.RatingReceivedDTO;
import com.communityappbackend.dto.RatingsHandleDTOs.RatingSummaryDTO;
import com.communityappbackend.model.*;
import com.communityappbackend.repository.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RatingService {
    private final RatingRepository ratingRepo;
    private final DonationRequestRepository requestRepo;
    private final DonationItemRepository itemRepo;
    private final UserRepository userRepo;
    private final UserProfileImageRepository userProfileImageRepo;

    public RatingService(
            RatingRepository ratingRepo,
            DonationRequestRepository requestRepo,
            DonationItemRepository itemRepo,
            UserRepository userRepo,
            UserProfileImageRepository userProfileImageRepo
    ) {
        this.ratingRepo = ratingRepo;
        this.requestRepo = requestRepo;
        this.itemRepo    = itemRepo;
        this.userRepo     = userRepo;
        this.userProfileImageRepo = userProfileImageRepo;
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


    public RatingSummaryDTO summaryForUser(String userId) {
        List<Rating> all = ratingRepo.findByRateeId(userId);
        double avg = all.stream().mapToInt(Rating::getScore).average().orElse(0.0);
        return RatingSummaryDTO.builder()
                .average(Math.round(avg * 10) / 10.0)  // one decimal
                .count(all.size())
                .build();
    }


    /** Returns all ratings *given by* this user, with full details. */
    public List<RatingDetailDTO> detailsForRater(String raterId) {
        return ratingRepo.findByRaterId(raterId).stream().map(r -> {
            // 1) find the donation request & item
            DonationRequest req = requestRepo.findById(r.getDonationRequestId())
                    .orElseThrow(() -> new IllegalArgumentException("Request not found"));
            DonationItem item = itemRepo.findById(req.getDonationId())
                    .orElseThrow(() -> new IllegalArgumentException("Donation not found"));

            // 2) build the first image URL (if any)
            String itemImageUrl = item.getImages().stream()
                    .findFirst()
                    .map(img -> {
                        String fn = img.getImagePath().replace("Donations/", "");
                        return "http://10.0.2.2:8080/api/donations/image/" + fn;
                    })
                    .orElse("");

            // 3) look up the ratee (recipient) user
            String rateeId = r.getRateeId();
            User ratee = userRepo.findById(rateeId).orElse(null);
            String rateeName = ratee != null ? ratee.getFullName() : "";
            String rateeProfile = "";
            if (ratee != null) {
                Optional<UserProfileImage> upi = userProfileImageRepo.findByUserId(rateeId);
                if (upi.isPresent() && upi.get().getImagePath() != null) {
                    rateeProfile = "http://10.0.2.2:8080/image/" + upi.get().getImagePath();
                }
            }

            // 4) assemble the DTO
            return RatingDetailDTO.builder()
                    .ratingId(r.getRatingId())
                    .donationId(item.getDonationId())
                    .donationTitle(item.getTitle())
                    .donationImage(itemImageUrl)
                    .rateeId(rateeId)
                    .rateeFullName(rateeName)
                    .rateeProfileImage(rateeProfile)
                    .score(r.getScore())
                    .comment(r.getComment())
                    .createdAt(r.getCreatedAt())
                    .build();
        }).collect(Collectors.toList());
    }
    // src/main/java/com/communityappbackend/service/RatingsService/RatingService.java
    public List<RatingReceivedDTO> detailsForRatee(String rateeId) {
        return ratingRepo.findByRateeId(rateeId).stream().map(r -> {
            DonationItem item = itemRepo.findById(
                    requestRepo.findById(r.getDonationRequestId())
                            .orElseThrow().getDonationId()
            ).orElseThrow();
            // build first image URL
            String imgUrl = item.getImages().stream()
                    .findFirst()
                    .map(i->"http://10.0.2.2:8080/api/donations/image/"+i.getImagePath().replace("Donations/",""))
                    .orElse("");
            // fetch rater info
            User rater = userRepo.findById(r.getRaterId()).orElseThrow();
            String profile = userProfileImageRepo.findByUserId(rater.getUserId())
                    .map(upi->"http://10.0.2.2:8080/image/"+upi.getImagePath())
                    .orElse("");
            return RatingReceivedDTO.builder()
                    .ratingId(r.getRatingId())
                    .donationId(item.getDonationId())
                    .donationTitle(item.getTitle())
                    .donationImage(imgUrl)
                    .raterId(rater.getUserId())
                    .raterFullName(rater.getFullName())
                    .raterProfileImage(profile)
                    .score(r.getScore())
                    .comment(r.getComment())
                    .createdAt(r.getCreatedAt())
                    .build();
        }).collect(Collectors.toList());
    }


}
