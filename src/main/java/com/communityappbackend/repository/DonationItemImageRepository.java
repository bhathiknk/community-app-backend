package com.communityappbackend.repository;

import com.communityappbackend.model.DonationItemImage;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Manages persistence for DonationItemImage.
 */
public interface DonationItemImageRepository extends JpaRepository<DonationItemImage, Long> {
}
