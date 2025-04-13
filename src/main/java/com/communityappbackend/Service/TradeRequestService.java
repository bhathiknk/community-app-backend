package com.communityappbackend.Service;

import com.communityappbackend.DTO.ItemResponse;
import com.communityappbackend.DTO.TradeRequestDTO;
import com.communityappbackend.DTO.TradeRequestDetailedDTO;
import com.communityappbackend.Model.*;
import com.communityappbackend.Repository.TradeRequestRepository;
import com.communityappbackend.Repository.ItemRepository;
import com.communityappbackend.Repository.UserRepository;
import com.communityappbackend.DTO.NotificationDTO;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class TradeRequestService {

    private final TradeRequestRepository tradeRequestRepo;
    private final ItemRepository itemRepo;
    private final UserRepository userRepo;
    private final NotificationService notificationService;

    public TradeRequestService(TradeRequestRepository tradeRequestRepo,
                               ItemRepository itemRepo,
                               UserRepository userRepo,
                               NotificationService notificationService) {
        this.tradeRequestRepo = tradeRequestRepo;
        this.itemRepo = itemRepo;
        this.userRepo = userRepo;
        this.notificationService = notificationService;
    }

    // 1) Create a trade request
    public TradeRequest createRequest(TradeRequestDTO dto, Authentication auth) {
        User user = (User) auth.getPrincipal();
        TradeRequest req = TradeRequest.builder()
                .itemId(dto.getItemId())
                .offeredBy(user.getUserId())
                .moneyOffer(dto.getMoneyOffer() != null ? dto.getMoneyOffer() : 0.0)
                .tradeType(dto.getTradeType() != null ? dto.getTradeType() : "MONEY")
                .status("PENDING")
                .build();
        return tradeRequestRepo.save(req);
    }

    // 2) Get all requests for items the current user owns
    public List<TradeRequest> getIncomingRequests(Authentication auth) {
        User user = (User) auth.getPrincipal();
        String currentUserId = user.getUserId();
        List<Item> myItems = itemRepo.findByOwnerId(currentUserId);
        Set<String> myItemIds = myItems.stream().map(Item::getItemId).collect(Collectors.toSet());
        List<TradeRequest> allRequests = tradeRequestRepo.findAll();
        return allRequests.stream()
                .filter(r -> myItemIds.contains(r.getItemId()))
                .collect(Collectors.toList());
    }

    // 3) Approve a request, then create a notification for the sender.
    public TradeRequest approveRequest(String requestId, String selectedItemId, Authentication auth) {
        Optional<TradeRequest> opt = tradeRequestRepo.findById(requestId);
        if (opt.isEmpty()) {
            throw new RuntimeException("Request not found");
        }
        TradeRequest req = opt.get();
        if (!"PENDING".equals(req.getStatus())) {
            throw new RuntimeException("Request is not pending");
        }
        req.setReceiverSelectedItemId(selectedItemId);
        req.setStatus("ACCEPTED");
        TradeRequest updated = tradeRequestRepo.save(req);
        User sender = userRepo.findById(req.getOfferedBy()).orElse(null);
        String senderName = (sender != null) ? sender.getFullName() : "Sender";
        String message = senderName + ", your trade request has been accepted!";
        notificationService.createNotification(new NotificationDTO(req.getOfferedBy(), message));
        return updated;
    }

    // 4) Reject a request, then create a notification for the sender.
    public TradeRequest rejectRequest(String requestId, Authentication auth) {
        Optional<TradeRequest> opt = tradeRequestRepo.findById(requestId);
        if (opt.isEmpty()) {
            throw new RuntimeException("Request not found");
        }
        TradeRequest req = opt.get();
        if (!"PENDING".equals(req.getStatus())) {
            throw new RuntimeException("Request is not pending");
        }
        req.setStatus("REJECTED");
        TradeRequest updated = tradeRequestRepo.save(req);
        User sender = userRepo.findById(req.getOfferedBy()).orElse(null);
        String senderName = (sender != null) ? sender.getFullName() : "Sender";
        String message = senderName + ", your trade request has been rejected.";
        notificationService.createNotification(new NotificationDTO(req.getOfferedBy(), message));
        return updated;
    }

    // 5) Get detailed incoming requests for items the current user owns with an optional status filter.
    public List<TradeRequestDetailedDTO> getIncomingRequestsDetailed(Authentication auth, String status) {
        User me = (User) auth.getPrincipal();
        List<Item> myItems = itemRepo.findByOwnerId(me.getUserId());
        Set<String> myItemIds = myItems.stream().map(Item::getItemId).collect(Collectors.toSet());
        List<TradeRequest> allRequests = tradeRequestRepo.findAll();
        List<TradeRequest> incomingForMe = allRequests.stream()
                .filter(r -> myItemIds.contains(r.getItemId()))
                .collect(Collectors.toList());
        if (status != null && !status.isEmpty()) {
            incomingForMe = incomingForMe.stream()
                    .filter(r -> r.getStatus().equalsIgnoreCase(status))
                    .collect(Collectors.toList());
        }
        return incomingForMe.stream().map(this::toDetailedDTO).collect(Collectors.toList());
    }

    private TradeRequestDetailedDTO toDetailedDTO(TradeRequest req) {
        User sender = userRepo.findById(req.getOfferedBy()).orElse(null);
        String offeredByName = (sender != null) ? sender.getFullName() : "Unknown User";

        Item requestedItem = itemRepo.findById(req.getItemId()).orElse(null);
        String requestedTitle = (requestedItem != null) ? requestedItem.getTitle() : "Unknown Item";
        String requestedDescription = (requestedItem != null) ? requestedItem.getDescription() : null;
        Double requestedPrice = (requestedItem != null) ? requestedItem.getPrice() : null;
        List<String> requestedImages = (requestedItem != null)
                ? mapImagesToPaths(requestedItem.getImages())
                : Collections.emptyList();

        Item offeredItem = null;
        String offeredItemTitle = null;
        String offeredItemDescription = null;
        Double offeredItemPrice = null;
        List<String> offeredItemImages = Collections.emptyList();
        if ("ITEM".equals(req.getTradeType()) && req.getReceiverSelectedItemId() != null) {
            offeredItem = itemRepo.findById(req.getReceiverSelectedItemId()).orElse(null);
            if (offeredItem != null) {
                offeredItemTitle = offeredItem.getTitle();
                offeredItemDescription = offeredItem.getDescription();
                offeredItemPrice = offeredItem.getPrice();
                offeredItemImages = mapImagesToPaths(offeredItem.getImages());
            }
        }

        return TradeRequestDetailedDTO.builder()
                .requestId(req.getRequestId())
                .status(req.getStatus())
                .moneyOffer(req.getMoneyOffer())
                .offeredByUserId(req.getOfferedBy())
                .offeredByUserName(offeredByName)
                .tradeType(req.getTradeType())
                .requestedItemId(req.getItemId())
                .requestedItemTitle(requestedTitle)
                .requestedItemDescription(requestedDescription)
                .requestedItemPrice(requestedPrice)
                .requestedItemImages(requestedImages)
                .offeredItemId(req.getReceiverSelectedItemId())
                .offeredItemTitle(offeredItemTitle)
                .offeredItemDescription(offeredItemDescription)
                .offeredItemPrice(offeredItemPrice)
                .offeredItemImages(offeredItemImages)
                .build();
    }

    // Map item images to full URLs.
    private List<String> mapImagesToPaths(List<ItemImage> images) {
        if (images == null) return Collections.emptyList();
        return images.stream()
                .map(img -> {
                    String fileName = img.getImagePath().replace("Assets/", "");
                    return "http://10.0.2.2:8080/api/items/image/" + fileName;
                })
                .collect(Collectors.toList());
    }

    // 6) Return items by owner.
    public List<ItemResponse> getItemsByOwner(String userId) {
        List<Item> items = itemRepo.findByOwnerId(userId);
        return items.stream().map(this::toItemResponse).collect(Collectors.toList());
    }

    private ItemResponse toItemResponse(Item item) {
        List<String> imagePaths = item.getImages().stream()
                .map(ItemImage::getImagePath)
                .map(path -> {
                    String fileName = path.replace("Assets/", "");
                    return "http://10.0.2.2:8080/api/items/image/" + fileName;
                })
                .collect(Collectors.toList());

        return ItemResponse.builder()
                .itemId(item.getItemId())
                .title(item.getTitle())
                .description(item.getDescription())
                .price(item.getPrice())
                .categoryId(item.getCategoryId())
                .images(imagePaths)
                .status(item.getStatus())
                .build();
    }
}
