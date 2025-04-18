// src/main/java/com/communityappbackend/repository/RatingRepository.java
package com.communityappbackend.repository;

import com.communityappbackend.model.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RatingRepository extends JpaRepository<Rating, String> {
    List<Rating> findByRateeId(String rateeId);
    List<Rating> findByRaterId(String raterId);
}
