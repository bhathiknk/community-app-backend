package com.communityappbackend.Service;

import com.communityappbackend.DTO.ItemResponse;
import com.communityappbackend.DTO.TradeRequestDTO;
import com.communityappbackend.DTO.TradeRequestDetailedDTO;
import com.communityappbackend.Model.*;
import com.communityappbackend.Repository.*;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class TradeRequestService {

    private final TradeRequestRepository tradeRequestRepo;
    private final ItemRepository itemRepo;
    private final UserRepository userRepo;

    public TradeRequestService(
            TradeRequestRepository tradeRequestRepo,
            ItemRepository itemRepo,
            UserRepository userRepo
    ) {
        this.tradeRequestRepo = tradeRequestRepo;
        this.itemRepo = itemRepo;
        this.userRepo = userRepo;
    }

    // 1) Create a trade request
    public TradeRequest createRequest(TradeRequestDTO dto, Authentication auth) {
        User user = (User) auth.getPrincipal();

        // If user picks tradeType="ITEM", moneyOffer could be null or zero
        TradeRequest req = TradeRequest.builder()
                .itemId(dto.getItemId())
                .offeredBy(user.getUserId())
                .moneyOffer(dto.getMoneyOffer() != null ? dto.getMoneyOffer() : 0.0)
                .tradeType(dto.getTradeType() != null ? dto.getTradeType() : "MONEY")
                .status("PENDING")
                .build();

        return tradeRequestRepo.save(req);
    }

    // 2) View all requests for items that I own
    public List<TradeRequest> getIncomingRequests(Authentication auth) {
        User user = (User) auth.getPrincipal();
        String currentUserId = user.getUserId();

        // find items where ownerId == currentUserId
        List<Item> myItems = itemRepo.findByOwnerId(currentUserId);
        Set<String> myItemIds = myItems.stream()
                .map(Item::getItemId)
                .collect(Collectors.toSet());

        // get all requests referencing my items
        List<TradeRequest> allRequests = tradeRequestRepo.findAll();
        return allRequests.stream()
                .filter(r -> myItemIds.contains(r.getItemId()))
                .collect(Collectors.toList());
    }

    // 3) Approve a request
    public TradeRequest approveRequest(String requestId, String selectedItemId, Authentication auth) {
        Optional<TradeRequest> opt = tradeRequestRepo.findById(requestId);
        if (opt.isEmpty()) {
            throw new RuntimeException("Request not found");
        }

        TradeRequest req = opt.get();
        if (!"PENDING".equals(req.getStatus())) {
            throw new RuntimeException("Request is not pending");
        }

        // The current user picks one of the sender's items
        req.setReceiverSelectedItemId(selectedItemId);
        req.setStatus("ACCEPTED");
        return tradeRequestRepo.save(req);
    }

    // 4) Reject request
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
        return tradeRequestRepo.save(req);
    }

    // Return a detailed list of incoming requests
    public List<TradeRequestDetailedDTO> getIncomingRequestsDetailed(Authentication auth) {
        User me = (User) auth.getPrincipal();
        List<Item> myItems = itemRepo.findByOwnerId(me.getUserId());
        Set<String> myItemIds = myItems.stream()
                .map(Item::getItemId)
                .collect(Collectors.toSet());

        List<TradeRequest> allRequests = tradeRequestRepo.findAll();
        List<TradeRequest> incomingForMe = allRequests.stream()
                .filter(r -> myItemIds.contains(r.getItemId()))
                .collect(Collectors.toList());

        return incomingForMe.stream()
                .map(this::toDetailedDTO)
                .collect(Collectors.toList());
    }

    private TradeRequestDetailedDTO toDetailedDTO(TradeRequest req) {
        // get the "offeredBy" user
        User sender = userRepo.findById(req.getOfferedBy()).orElse(null);
        String offeredByName = (sender != null) ? sender.getFullName() : "Unknown User";

        // find the requested item (the item that belongs to the current user)
        Item requestedItem = itemRepo.findById(req.getItemId()).orElse(null);
        String requestedTitle = (requestedItem != null) ? requestedItem.getTitle() : "Unknown Item";
        String requestedDescription = (requestedItem != null) ? requestedItem.getDescription() : null;
        Double requestedPrice = (requestedItem != null) ? requestedItem.getPrice() : null;
        List<String> requestedImages = (requestedItem != null)
                ? mapImagesToPaths(requestedItem.getImages())
                : Collections.emptyList();

        // If the trade type is "ITEM" and there's a chosen item (after approval),
        // we can retrieve full details of that offered item.
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

    // Reuse logic to map item images to full paths
    private List<String> mapImagesToPaths(List<ItemImage> images) {
        if (images == null) return Collections.emptyList();
        return images.stream()
                .map(img -> {
                    String fileName = img.getImagePath().replace("Assets/", "");
                    return "http://10.0.2.2:8080/api/items/image/" + fileName;
                })
                .collect(Collectors.toList());
    }

    // 6) NEW: Return all items (with images) by the userId (sender).
    public List<ItemResponse> getItemsByOwner(String userId) {
        List<Item> items = itemRepo.findByOwnerId(userId);
        return items.stream()
                .map(this::toItemResponse)
                .collect(Collectors.toList());
    }

    private ItemResponse toItemResponse(Item item) {
        // Convert each item to a DTO with images
        List<String> imagePaths = item.getImages().stream()
                .map(ItemImage::getImagePath)
                .map(path -> {
                    // remove "Assets/" and build full URL
                    String fileName = path.replace("Assets/", "");
                    return "http://10.0.2.2:8080/api/items/image/" + fileName;
                })
                .collect(Collectors.toList());

        return ItemResponse.builder()
                .itemId(item.getItemId())
                .title(item.getTitle())
                .description(item.getDescription())
                .price(item.getPrice())
                .images(imagePaths)
                .categoryId(item.getCategoryId())
                .status(item.getStatus())
                .build();
    }
}
