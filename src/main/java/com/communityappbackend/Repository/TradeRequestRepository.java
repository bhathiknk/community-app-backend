package com.communityappbackend.Repository;

import com.communityappbackend.Model.TradeRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TradeRequestRepository extends JpaRepository<TradeRequest, String> {

}
