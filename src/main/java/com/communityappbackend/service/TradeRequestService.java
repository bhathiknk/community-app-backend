package com.communityappbackend.service;

import com.communityappbackend.dto.*;
import com.communityappbackend.exception.TradeRequestNotFoundException;
import com.communityappbackend.model.*;
import com.communityappbackend.repository.*;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Manages creation, approval, rejection, and details of trade requests.
 */
@Service
public class TradeRequestService {

    private final TradeRequestRepository tradeRequestRepo;
    private final ItemRepository itemRepo;
    private final UserRepository userRepo;
    private final NotificationService notificationService;

    public TradeRequestService(
            TradeRequestRepository tradeRequestRepo,
            ItemRepository itemRepo,
            UserRepository userRepo,
            NotificationService notificationService
    ) {
        this.tradeRequestRepo = tradeRequestRepo;
        this.itemRepo = itemRepo;
        this.userRepo = userRepo;
        this.notificationService = notificationService;
    }

    /**
     * Creates a trade request (money or item-based).
     */
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

    /**
     * Returns all incoming trade requests for items that the current user owns.
     */
    public List<TradeRequest> getIncomingRequests(Authentication auth) {
        User user = (User) auth.getPrincipal();
        List<Item> myItems = itemRepo.findByOwnerId(user.getUserId());
        Set<String> myItemIds = myItems.stream()
                .map(Item::getItemId)
                .collect(Collectors.toSet());

        return tradeRequestRepo.findAll().stream()
                .filter(r -> myItemIds.contains(r.getItemId()))
                .collect(Collectors.toList());
    }

    /**
     * Approves a request (with optional selected item from the sender).
     */
    public TradeRequest approveRequest(String requestId, String selectedItemId, Authentication auth) {
        TradeRequest req = tradeRequestRepo.findById(requestId)
                .orElseThrow(() -> new TradeRequestNotFoundException("Trade request not found: " + requestId));

        if (!"PENDING".equals(req.getStatus())) {
            throw new RuntimeException("Cannot approve a request that is not pending.");
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

    /**
     * Rejects a request and notifies the sender.
     */
    public TradeRequest rejectRequest(String requestId, Authentication auth) {
        TradeRequest req = tradeRequestRepo.findById(requestId)
                .orElseThrow(() -> new TradeRequestNotFoundException("Trade request not found: " + requestId));

        if (!"PENDING".equals(req.getStatus())) {
            throw new RuntimeException("Cannot reject a request that is not pending.");
        }
        req.setStatus("REJECTED");
        TradeRequest updated = tradeRequestRepo.save(req);

        User sender = userRepo.findById(req.getOfferedBy()).orElse(null);
        String senderName = (sender != null) ? sender.getFullName() : "Sender";
        String message = senderName + ", your trade request has been rejected.";
        notificationService.createNotification(new NotificationDTO(req.getOfferedBy(), message));

        return updated;
    }

    /**
     * Returns a detailed list of incoming requests for items owned by the current user,
     * optionally filtered by status.
     */
    public List<TradeRequestDetailedDTO> getIncomingRequestsDetailed(Authentication auth, String status) {
        User me = (User) auth.getPrincipal();
        List<Item> myItems = itemRepo.findByOwnerId(me.getUserId());
        Set<String> myItemIds = myItems.stream()
                .map(Item::getItemId)
                .collect(Collectors.toSet());

        List<TradeRequest> incomingForMe = tradeRequestRepo.findAll().stream()
                .filter(r -> myItemIds.contains(r.getItemId()))
                .collect(Collectors.toList());

        if (status != null && !status.isEmpty()) {
            incomingForMe = incomingForMe.stream()
                    .filter(r -> r.getStatus().equalsIgnoreCase(status))
                    .collect(Collectors.toList());
        }

        return incomingForMe.stream()
                .map(this::toDetailedDTO)
                .collect(Collectors.toList());
    }

    /**
     * Returns the publicly listed items of a sender (by userId).
     */
    public List<ItemResponse> getItemsByOwner(String userId) {
        List<Item> items = itemRepo.findByOwnerId(userId);
        return items.stream().map(this::toItemResponse).collect(Collectors.toList());
    }

    // -------------------------- PRIVATE HELPERS -------------------------- //

    private TradeRequestDetailedDTO toDetailedDTO(TradeRequest req) {
        User sender = userRepo.findById(req.getOfferedBy()).orElse(null);
        String offeredByName = (sender != null) ? sender.getFullName() : "Unknown User";
        String senderEmail = (sender != null) ? sender.getEmail() : "";
        String senderPhone = (sender != null) ? sender.getPhone() : "";
        String senderAddress = (sender != null) ? sender.getAddress() : "";

        Item requestedItem = itemRepo.findById(req.getItemId()).orElse(null);
        String requestedTitle = (requestedItem != null) ? requestedItem.getTitle() : "Unknown Item";
        String requestedDescription = (requestedItem != null) ? requestedItem.getDescription() : null;
        Double requestedPrice = (requestedItem != null) ? requestedItem.getPrice() : null;
        List<String> requestedImages = (requestedItem != null)
                ? mapImagesToPaths(requestedItem.getImages())
                : Collections.emptyList();

        // Retrieve the item owner
        User receiver = (requestedItem != null)
                ? userRepo.findById(requestedItem.getOwnerId()).orElse(null)
                : null;

        String receiverUserId = (receiver != null) ? receiver.getUserId() : "";
        String receiverFullName = (receiver != null) ? receiver.getFullName() : "";
        String receiverEmail = (receiver != null) ? receiver.getEmail() : "";
        String receiverPhone = (receiver != null) ? receiver.getPhone() : "";
        String receiverAddress = (receiver != null) ? receiver.getAddress() : "";

        // If it's an item-based trade and the request has a selected item from the sender
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
                .senderEmail(senderEmail)
                .senderPhone(senderPhone)
                .senderAddress(senderAddress)
                .receiverUserId(receiverUserId)
                .receiverFullName(receiverFullName)
                .receiverEmail(receiverEmail)
                .receiverPhone(receiverPhone)
                .receiverAddress(receiverAddress)
                .build();
    }

    private List<String> mapImagesToPaths(List<ItemImage> images) {
        if (images == null) return Collections.emptyList();
        return images.stream()
                .map(img -> {
                    String fileName = img.getImagePath().replace("Assets/", "");
                    return "http://10.0.2.2:8080/api/items/image/" + fileName;
                })
                .collect(Collectors.toList());
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
