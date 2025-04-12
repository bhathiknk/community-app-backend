package com.communityappbackend.DTO;

import lombok.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TradeRequestDetailedDTO {

    private String requestId;
    private String status;
    private Double moneyOffer;
    private String offeredByUserId;
    private String offeredByUserName;
    private String tradeType;

    // The item that belongs to the current user (the requested item)
    private String requestedItemId;
    private String requestedItemTitle;
    private String requestedItemDescription;  // NEW
    private Double requestedItemPrice;        // NEW
    private List<String> requestedItemImages; // short version of item images

    // If user offered an item instead of money, details of that item:
    // This is typically stored in `receiverSelectedItemId` after approval.
    private String offeredItemId;
    private String offeredItemTitle;
    private String offeredItemDescription;   // NEW
    private Double offeredItemPrice;         // NEW
    private List<String> offeredItemImages;  // NEW
}
