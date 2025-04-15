package com.communityappbackend.dto;

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
    private String requestedItemDescription;
    private Double requestedItemPrice;
    private List<String> requestedItemImages;

    // Offered item details (if applicable)
    private String offeredItemId;
    private String offeredItemTitle;
    private String offeredItemDescription;
    private Double offeredItemPrice;
    private List<String> offeredItemImages;

    // NEW: Sender contact info
    private String senderEmail;
    private String senderPhone;
    private String senderAddress;

    // NEW: Receiver contact info
    private String receiverUserId;
    private String receiverFullName;
    private String receiverEmail;
    private String receiverPhone;
    private String receiverAddress;
}
