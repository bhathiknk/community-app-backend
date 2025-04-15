package com.communityappbackend.model;

import jakarta.persistence.*;
import lombok.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Item {

    @Id
    @Column(name = "item_id", columnDefinition = "CHAR(36)")
    private String itemId;

    private String title;
    private String description;
    private Double price;

    private Long categoryId;
    private String ownerId; // user_id from JWT

    // New column: status
    @Builder.Default
    private String status = "ACTIVE"; // default is ACTIVE

    // New column: created_at from DB
    @Column(name = "created_at", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP",
            insertable = false, updatable = false)
    private Timestamp createdAt;

    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ItemImage> images = new ArrayList<>();

    @PrePersist
    public void generateItemId() {
        if (this.itemId == null) {
            this.itemId = UUID.randomUUID().toString();
        }
    }
}
