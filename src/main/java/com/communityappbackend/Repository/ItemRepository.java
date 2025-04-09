package com.communityappbackend.Repository;

import com.communityappbackend.Model.Item;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, String> {
    List<Item> findByOwnerId(String ownerId);
}
