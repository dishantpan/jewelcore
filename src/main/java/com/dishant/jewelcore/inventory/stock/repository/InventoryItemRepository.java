package com.dishant.jewelcore.inventory.stock.repository;

import com.dishant.jewelcore.inventory.stock.entity.InventoryItem;
import com.dishant.jewelcore.inventory.stock.entity.InventoryItemStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface InventoryItemRepository
        extends JpaRepository<InventoryItem, Long> {

    Optional<InventoryItem> findByItemCode(String itemCode);

    List<InventoryItem> findByStatus(
            InventoryItemStatus status
    );

    List<InventoryItem> findByJewelleryId(Long jewelleryId);
}