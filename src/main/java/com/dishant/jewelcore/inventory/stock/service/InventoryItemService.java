package com.dishant.jewelcore.inventory.stock.service;

import com.dishant.jewelcore.common.exception.ResourceNotFoundException;
import com.dishant.jewelcore.inventory.jewellery.entity.Jewellery;
import com.dishant.jewelcore.inventory.jewellery.repository.JewelleryRepository;
import com.dishant.jewelcore.inventory.stock.dto.InventoryItemCreateRequest;
import com.dishant.jewelcore.inventory.stock.dto.InventoryItemResponse;
import com.dishant.jewelcore.inventory.stock.entity.InventoryItem;
import com.dishant.jewelcore.inventory.stock.entity.InventoryItemStatus;
import com.dishant.jewelcore.inventory.stock.repository.InventoryItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional
public class InventoryItemService {

    private final InventoryItemRepository inventoryItemRepository;
    private final JewelleryRepository jewelleryRepository;

    public InventoryItemService(
            InventoryItemRepository inventoryItemRepository,
            JewelleryRepository jewelleryRepository) {

        this.inventoryItemRepository = inventoryItemRepository;
        this.jewelleryRepository = jewelleryRepository;
    }

    public InventoryItemResponse createItem(
            InventoryItemCreateRequest request) {

        if (inventoryItemRepository
                .findByItemCode(request.itemCode())
                .isPresent()) {

            throw new IllegalArgumentException(
                    "Inventory item code already exists: "
                            + request.itemCode()
            );
        }

        Jewellery jewellery =
                jewelleryRepository.findById(
                                request.jewelleryId()
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Jewellery not found with id: "
                                                + request.jewelleryId()
                                ));

        validateWeights(
                request.grossWeight(),
                request.stoneWeight(),
                request.netWeight()
        );

        InventoryItem item = new InventoryItem(
                request.itemCode(),
                jewellery,
                request.grossWeight(),
                request.stoneWeight(),
                request.netWeight(),
                request.purchaseCost(),
                request.location()
        );

        InventoryItem savedItem =
                inventoryItemRepository.save(item);

        return InventoryItemResponse.from(savedItem);
    }

    @Transactional(readOnly = true)
    public InventoryItemResponse getItemById(Long id) {

        InventoryItem item =
                inventoryItemRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Inventory item not found with id: "
                                                + id
                                ));

        return InventoryItemResponse.from(item);
    }

    @Transactional(readOnly = true)
    public List<InventoryItemResponse> getAllItems() {

        return inventoryItemRepository.findAll()
                .stream()
                .map(InventoryItemResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<InventoryItemResponse> getItemsByStatus(
            InventoryItemStatus status) {

        return inventoryItemRepository.findByStatus(status)
                .stream()
                .map(InventoryItemResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<InventoryItemResponse> getItemsByJewellery(
            Long jewelleryId) {

        if (!jewelleryRepository.existsById(jewelleryId)) {
            throw new ResourceNotFoundException(
                    "Jewellery not found with id: "
                            + jewelleryId
            );
        }

        return inventoryItemRepository
                .findByJewelleryId(jewelleryId)
                .stream()
                .map(InventoryItemResponse::from)
                .toList();
    }

    public InventoryItemResponse reserveItem(Long id) {

        InventoryItem item = findItem(id);

        item.reserve();

        return InventoryItemResponse.from(
                inventoryItemRepository.save(item)
        );
    }

    public InventoryItemResponse sellItem(Long id) {

        InventoryItem item = findItem(id);

        item.sell();

        return InventoryItemResponse.from(
                inventoryItemRepository.save(item)
        );
    }

    public InventoryItemResponse markItemDamaged(Long id) {

        InventoryItem item = findItem(id);

        item.markDamaged();

        return InventoryItemResponse.from(
                inventoryItemRepository.save(item)
        );
    }

    private InventoryItem findItem(Long id) {

        return inventoryItemRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Inventory item not found with id: "
                                        + id
                        ));
    }

    private void validateWeights(
            BigDecimal grossWeight,
            BigDecimal stoneWeight,
            BigDecimal netWeight) {

        if (stoneWeight.compareTo(grossWeight) > 0) {
            throw new IllegalArgumentException(
                    "Stone weight cannot be greater than gross weight"
            );
        }

        if (netWeight.compareTo(grossWeight) > 0) {
            throw new IllegalArgumentException(
                    "Net weight cannot be greater than gross weight"
            );
        }

        BigDecimal calculatedNetWeight =
                grossWeight.subtract(stoneWeight);

        if (netWeight.compareTo(calculatedNetWeight) != 0) {
            throw new IllegalArgumentException(
                    "Net weight must equal gross weight minus stone weight"
            );
        }
    }
}