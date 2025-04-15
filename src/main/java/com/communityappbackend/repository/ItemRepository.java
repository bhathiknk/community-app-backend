package com.communityappbackend.repository;

import com.communityappbackend.model.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ItemRepository extends JpaRepository<Item, String> {

    List<Item> findByOwnerId(String ownerId);

    List<Item> findByStatusAndOwnerIdNot(String status, String ownerId);

    List<Item> findByStatusAndOwnerIdNotAndCategoryId(String status, String ownerId, Long categoryId);
}
