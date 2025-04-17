package com.communityappbackend.service;

import com.communityappbackend.dto.*;
import com.communityappbackend.model.*;
import com.communityappbackend.model.DonationRequest;
import com.communityappbackend.repository.*;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
public class DonationRequestService {

    private final DonationRequestRepository donationRequestRepo;
    private final DonationItemRepository donationItemRepo;
    private final UserRepository userRepo;
    private final DonationItemImageRepository donationItemImageRepo;
    private final UserProfileImageRepository userProfileImageRepo;

    public DonationRequestService(
            DonationRequestRepository donationRequestRepo,
            DonationItemRepository donationItemRepo,
            UserRepository userRepo,
            DonationItemImageRepository donationItemImageRepo,
            UserProfileImageRepository userProfileImageRepo
    ) {
        this.donationRequestRepo = donationRequestRepo;
        this.donationItemRepo = donationItemRepo;
        this.userRepo = userRepo;
        this.donationItemImageRepo = donationItemImageRepo;
        this.userProfileImageRepo = userProfileImageRepo;
    }

    /**
     * Creates a donation request by the current user.
     */
    public DonationRequest createDonationRequest(DonationRequestDTO dto, Authentication auth) {
        User requester = (User) auth.getPrincipal();
        DonationItem donation = donationItemRepo.findById(dto.getDonationId())
                .orElseThrow(() -> new RuntimeException("Donation item not found"));
        if (donation.getOwnerId().equals(requester.getUserId())) {
            throw new RuntimeException("You cannot request your own donation.");
        }
        DonationRequest request = DonationRequest.builder()
                .donationId(dto.getDonationId())
                .requestedBy(requester.getUserId())
                .message(dto.getMessage())
                .status("PENDING")
                .build();
        return donationRequestRepo.save(request);
    }

    /**
     * Returns "merged" data for incoming donation requests: the donation info + the requesting user info.
     * ALGORITHM: O(n) linear, scanning each donation that belongs to current user.
     */
    public List<DonationRequestViewDTO> getIncomingDonationRequestsView(Authentication auth) {
        User currentUser = (User) auth.getPrincipal();

        // 1) Fetch all DonationItems owned by the current user
        List<DonationItem> myDonations = donationItemRepo.findByOwnerId(currentUser.getUserId());

        // 2) For each donation, fetch requests => flatten into a single list
        List<DonationRequest> allRequests = myDonations.stream()
                .flatMap(donation ->
                        donationRequestRepo.findByDonationId(donation.getDonationId()).stream()
                )
                .collect(Collectors.toList());

        // 3) Map each request into a DonationRequestViewDTO
        return allRequests.stream()
                .map(this::toViewDTO)
                .collect(Collectors.toList());
    }

    /**
     * Helper to convert a DonationRequest into DonationRequestViewDTO,
     * including donation item data + the requester user data.
     */
    private DonationRequestViewDTO toViewDTO(DonationRequest request) {
        // Fetch donation item
        DonationItem donation = donationItemRepo.findById(request.getDonationId())
                .orElse(null);

        // Donation data
        String donationTitle = (donation != null) ? donation.getTitle() : "Unknown item";
        String donationStatus = (donation != null) ? donation.getStatus() : "UNKNOWN";
        List<String> donationImages = new ArrayList<>();
        if (donation != null && donation.getImages() != null) {
            for (DonationItemImage img : donation.getImages()) {
                // Convert to a direct URL
                String imageFileName = img.getImagePath().replace("Donations/", "");
                String imageUrl = "http://10.0.2.2:8080/api/donations/image/" + imageFileName;
                donationImages.add(imageUrl);
            }
        }

        // Requester data
        User requester = (request.getRequestedBy() != null)
                ? userRepo.findById(request.getRequestedBy()).orElse(null)
                : null;
        String requesterFullName = (requester != null) ? requester.getFullName() : "Unknown";
        String requesterEmail = (requester != null) ? requester.getEmail() : "";
        String requesterPhone = (requester != null) ? requester.getPhone() : "";
        // Profile image if any
        String requesterProfile = "";
        if (requester != null) {
            Optional<UserProfileImage> upiOpt = userProfileImageRepo.findByUserId(requester.getUserId());
            if (upiOpt.isPresent()) {
                UserProfileImage upi = upiOpt.get();
                if (upi.getImagePath() != null && !upi.getImagePath().isEmpty()) {
                    requesterProfile = "http://10.0.2.2:8080/image/" + upi.getImagePath();
                }
            }
        }

        return DonationRequestViewDTO.builder()
                .requestId(request.getRequestId())
                .donationId(request.getDonationId())
                .message(request.getMessage())
                .status(request.getStatus())
                .createdAt(request.getCreatedAt())
                .donationTitle(donationTitle)
                .donationImages(donationImages)
                .donationStatus(donationStatus)
                .requestedBy(request.getRequestedBy())
                .requesterFullName(requesterFullName)
                .requesterEmail(requesterEmail)
                .requesterPhone(requesterPhone)
                .requesterProfile(requesterProfile)
                .build();
    }

    /**
     * Accept a request (set request.status=ACCEPTED and donation.status=RESERVED).
     */
    public DonationRequest acceptDonationRequest(String requestId, Authentication auth) {
        DonationRequest request = donationRequestRepo.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Donation request not found."));
        DonationItem donation = donationItemRepo.findById(request.getDonationId())
                .orElseThrow(() -> new RuntimeException("Donation item not found."));
        User owner = (User) auth.getPrincipal();
        if (!donation.getOwnerId().equals(owner.getUserId())) {
            throw new RuntimeException("Not authorized to accept this donation request.");
        }
        request.setStatus("ACCEPTED");
        donation.setStatus("RESERVED");
        donationItemRepo.save(donation);
        return donationRequestRepo.save(request);
    }

    /**
     * Reject a request (set request.status=REJECTED).
     */
    public DonationRequest rejectDonationRequest(String requestId, Authentication auth) {
        DonationRequest request = donationRequestRepo.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Donation request not found."));
        DonationItem donation = donationItemRepo.findById(request.getDonationId())
                .orElseThrow(() -> new RuntimeException("Donation item not found."));
        User owner = (User) auth.getPrincipal();
        if (!donation.getOwnerId().equals(owner.getUserId())) {
            throw new RuntimeException("Not authorized to reject this donation request.");
        }
        request.setStatus("REJECTED");
        return donationRequestRepo.save(request);
    }



    /**
     * Return all requests _sent_ by the current user, with donation + receiver info.
     */
    public List<DonationRequestSentViewDTO> getSentDonationRequestsView(Authentication auth) {
        User me = (User) auth.getPrincipal();
        List<DonationRequest> sent = donationRequestRepo.findByRequestedBy(me.getUserId());
        return sent.stream().map(this::toSentViewDTO).collect(Collectors.toList());
    }

    private DonationRequestSentViewDTO toSentViewDTO(DonationRequest req) {
        DonationItem donation = donationItemRepo.findById(req.getDonationId()).orElse(null);
        // build donation fields
        String title = donation != null ? donation.getTitle() : "Unknown";
        List<String> images = donation != null
                ? donation.getImages().stream()
                .map(img -> {
                    String fn = img.getImagePath().replace("Donations/", "");
                    return "http://10.0.2.2:8080/api/donations/image/"+fn;
                }).toList()
                : List.of();
        String dStatus = donation != null ? donation.getStatus() : "";
        // receiver = donation owner
        User owner = donation != null ? userRepo.findById(donation.getOwnerId()).orElse(null) : null;
        String rId = owner!=null?owner.getUserId():"";
        String rName=owner!=null?owner.getFullName():"";
        String rEmail=owner!=null?owner.getEmail():"";
        String rPhone=owner!=null?owner.getPhone():"";
        AtomicReference<String> rProf= new AtomicReference<>("");
        if(owner!=null){
            userProfileImageRepo.findByUserId(owner.getUserId()).ifPresent(upi->
                    rProf.set("http://10.0.2.2:8080/image/" + upi.getImagePath()));
        }
        return DonationRequestSentViewDTO.builder()
                .requestId(req.getRequestId())
                .donationId(req.getDonationId())
                .donationTitle(title)
                .donationImages(images)
                .donationStatus(dStatus)
                .message(req.getMessage())
                .status(req.getStatus())
                .createdAt(req.getCreatedAt())
                .receiverId(rId).receiverFullName(rName)
                .receiverEmail(rEmail).receiverPhone(rPhone)
                .receiverProfile(String.valueOf(rProf))
                .build();
    }

    // — Mark donation complete
    public DonationRequest completeDonation(String requestId, Authentication auth) {
        DonationRequest req = donationRequestRepo.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        String currentUser = ((User) auth.getPrincipal()).getUserId();
        if (!currentUser.equals(req.getRequestedBy())) {
            throw new RuntimeException("Not authorized");
        }

        // Fix: fetch the DonationItem via donationItemRepo, not the Image repo
        DonationItem item = donationItemRepo.findById(req.getDonationId())
                .orElseThrow(() -> new RuntimeException("Donation not found"));

        item.setStatus("DONATED");
        donationItemRepo.save(item);        // <-- save back to donationItemRepo

        req.setStatus("COMPLETED");
        return donationRequestRepo.save(req);
    }

    // — Fetch details for completion screen
    public DonationCompleteDTO getCompleteDetails(String requestId) {
        DonationRequest req = donationRequestRepo.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        // Likewise, fetch item via donationItemRepo
        DonationItem item = donationItemRepo.findById(req.getDonationId())
                .orElseThrow(() -> new RuntimeException("Donation not found"));

        User donor = userRepo.findById(item.getOwnerId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<String> images = item.getImages().stream()
                .map(img -> {
                    String fn = img.getImagePath().replace("Donations/","");
                    return "http://10.0.2.2:8080/api/donations/image/" + fn;
                }).collect(Collectors.toList());

        String profileUrl = userProfileImageRepo
                .findByUserId(donor.getUserId())
                .map(upi -> "http://10.0.2.2:8080/image/" + upi.getImagePath())
                .orElse("");

        return DonationCompleteDTO.builder()
                .donationId(item.getDonationId())
                .title(item.getTitle())
                .images(images)
                .donorId(donor.getUserId())
                .donorFullName(donor.getFullName())
                .donorEmail(donor.getEmail())
                .donorPhone(donor.getPhone())
                .donorProfileImage(profileUrl)
                .build();
    }
}
