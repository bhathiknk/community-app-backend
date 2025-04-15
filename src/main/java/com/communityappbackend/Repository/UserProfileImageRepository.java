// File: UserProfileImageRepository.java
package com.communityappbackend.Repository;

import com.communityappbackend.Model.UserProfileImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserProfileImageRepository extends JpaRepository<UserProfileImage, Long> {
    /**
     * Finds a UserProfileImage by the given userId (if exists),
     * wrapped in an Optional. Returns Optional.empty() if not found.
     */
    Optional<UserProfileImage> findByUserId(String userId);
}
