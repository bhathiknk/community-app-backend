package com.communityappbackend.Model;

import jakarta.persistence.*;
import lombok.*;
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
