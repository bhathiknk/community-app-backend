package com.communityappbackend.dto.TradeItemsHandleDTOs;

import lombok.*;

/** Request object for creating a trade request. */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TradeRequestDTO {
    private String itemId;
    private String tradeType;   // "MONEY" or "ITEM"
    private Double moneyOffer;  // if "MONEY", how much
    private String message;     // optional note
}
