package com.communityappbackend.dto.TradeItemsHandleDTOs;

import lombok.*;
import java.util.List;

/** Detailed info about a trade request, including requested item & offered item. */
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

    private String requestedItemId;
    private String requestedItemTitle;
    private String requestedItemDescription;
    private Double requestedItemPrice;
    private List<String> requestedItemImages;

    private String offeredItemId;
    private String offeredItemTitle;
    private String offeredItemDescription;
    private Double offeredItemPrice;
    private List<String> offeredItemImages;

    private String senderEmail;
    private String senderPhone;
    private String senderAddress;

    private String receiverUserId;
    private String receiverFullName;
    private String receiverEmail;
    private String receiverPhone;
    private String receiverAddress;
}
