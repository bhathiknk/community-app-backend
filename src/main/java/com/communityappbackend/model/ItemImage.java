package com.communityappbackend.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "item_images")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long imageId;

    private String imagePath;

    @ManyToOne
    @JoinColumn(name = "item_id")
    private Item item;
}
