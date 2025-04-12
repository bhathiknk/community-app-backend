package com.communityappbackend.DTO;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TradeRequestDTO {
    private String itemId;       // The requested item from the other user
    private String tradeType;    // "MONEY" or "ITEM"
    private Double moneyOffer;   // If "MONEY", how much
    private String message;      // optional note
}
