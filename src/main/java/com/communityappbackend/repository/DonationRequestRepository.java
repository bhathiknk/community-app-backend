package com.communityappbackend.repository;

import com.communityappbackend.model.DonationRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface DonationRequestRepository extends JpaRepository<DonationRequest, String> {
    List<DonationRequest> findByDonationId(String donationId);
    List<DonationRequest> findByRequestedBy(String requestedBy);
}
