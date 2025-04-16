package com.communityappbackend.repository;

import com.communityappbackend.model.DonationItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Manages persistence for DonationItem.
 */
public interface DonationItemRepository extends JpaRepository<DonationItem, String> {

    List<DonationItem> findByOwnerId(String ownerId);

    List<DonationItem> findByStatus(String status);
    // or more specialized queries if needed
}
