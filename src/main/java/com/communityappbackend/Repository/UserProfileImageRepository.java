// UserProfileImageRepository.java
package com.communityappbackend.Repository;

import com.communityappbackend.Model.UserProfileImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserProfileImageRepository extends JpaRepository<UserProfileImage, Long> {
    // Returns a UserProfileImage by userId or null if not found.
    UserProfileImage findByUserId(String userId);
}
