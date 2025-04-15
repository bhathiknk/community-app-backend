package com.communityappbackend.model;

import jakarta.persistence.*;
import lombok.*;
import java.sql.Timestamp;
import java.util.UUID;

@Entity
@Table(name = "trade_requests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TradeRequest {

    @Id
    @Column(name = "request_id", columnDefinition = "CHAR(36)")
    private String requestId;

    @Column(name = "item_id", columnDefinition = "CHAR(36)", nullable = false)
    private String itemId;

    @Column(name = "offered_by", columnDefinition = "CHAR(36)", nullable = false)
    private String offeredBy;

    @Column(name = "money_offer", columnDefinition = "DECIMAL(10,2) DEFAULT 0.0")
    private Double moneyOffer;

    @Column(name = "receiver_selected_item_id", columnDefinition = "CHAR(36)")
    private String receiverSelectedItemId;

    @Builder.Default
    private String status = "PENDING";

    // NEW column for trade type
    @Builder.Default
    private String tradeType = "MONEY"; // Or "ITEM"

    @Column(name = "created_at", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP",
            insertable = false, updatable = false)
    private Timestamp createdAt;

    @PrePersist
    public void generateRequestId() {
        if (this.requestId == null) {
            this.requestId = UUID.randomUUID().toString();
        }
    }
}
