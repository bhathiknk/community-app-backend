package com.communityappbackend.Repository;

import com.communityappbackend.Model.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ItemRepository extends JpaRepository<Item, String> {

    List<Item> findByOwnerId(String ownerId);

    // new: find all active items except logged user
    List<Item> findByStatusAndOwnerIdNot(String status, String ownerId);

    // new: same but with category
    List<Item> findByStatusAndOwnerIdNotAndCategoryId(String status, String ownerId, Long categoryId);
}
