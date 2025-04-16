package com.communityappbackend.model;

import jakarta.persistence.*;
import lombok.*;
import java.sql.Timestamp;
import java.util.UUID;

@Entity
@Table(name = "donation_requests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DonationRequest {

    @Id
    @Column(name = "request_id", columnDefinition = "CHAR(36)")
    private String requestId;  // We'll assign UUID manually

    @Column(name = "donation_id", columnDefinition = "CHAR(36)", nullable = false)
    private String donationId; // The donation item being requested

    @Column(name = "requested_by", columnDefinition = "CHAR(36)", nullable = false)
    private String requestedBy; // User ID of the requester

    @Column(name = "message", columnDefinition = "TEXT")
    private String message;  // Optional message

    @Builder.Default
    private String status = "PENDING"; // PENDING, ACCEPTED, or REJECTED

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
