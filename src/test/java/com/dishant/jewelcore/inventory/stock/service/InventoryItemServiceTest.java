package com.dishant.jewelcore.inventory.stock.service;

import com.dishant.jewelcore.common.exception.ResourceNotFoundException;
import com.dishant.jewelcore.inventory.jewellery.entity.Jewellery;
import com.dishant.jewelcore.inventory.jewellery.repository.JewelleryRepository;
import com.dishant.jewelcore.inventory.stock.dto.InventoryItemCreateRequest;
import com.dishant.jewelcore.inventory.stock.dto.InventoryItemResponse;
import com.dishant.jewelcore.inventory.stock.entity.InventoryItem;
import com.dishant.jewelcore.inventory.stock.entity.InventoryItemStatus;
import com.dishant.jewelcore.inventory.stock.repository.InventoryItemRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InventoryItemServiceTest {

    @Mock
    private InventoryItemRepository inventoryItemRepository;

    @Mock
    private JewelleryRepository jewelleryRepository;

    @InjectMocks
    private InventoryItemService inventoryItemService;

    private Jewellery jewellery() {

        return new Jewellery(
                "RG-001",
                "Classic Gold Ring",
                "Classic 22K gold ring",
                null,
                null,
                null,
                new BigDecimal("5.200"),
                new BigDecimal("0.200"),
                new BigDecimal("5.000"),
                new BigDecimal("2500.00")
        );
    }

    private InventoryItemCreateRequest createRequest() {

        return new InventoryItemCreateRequest(
                "ITEM-001",
                1L,
                new BigDecimal("5.200"),
                new BigDecimal("0.200"),
                new BigDecimal("5.000"),
                new BigDecimal("40000.00"),
                "SHOWCASE-A1"
        );
    }

    private InventoryItem inventoryItem() {

        return new InventoryItem(
                "ITEM-001",
                jewellery(),
                new BigDecimal("5.200"),
                new BigDecimal("0.200"),
                new BigDecimal("5.000"),
                new BigDecimal("40000.00"),
                "SHOWCASE-A1"
        );
    }

    @Test
    void shouldCreateInventoryItem() {

        when(inventoryItemRepository.findByItemCode("ITEM-001"))
                .thenReturn(Optional.empty());

        when(jewelleryRepository.findById(1L))
                .thenReturn(Optional.of(jewellery()));

        InventoryItem savedItem = inventoryItem();

        when(inventoryItemRepository.save(any(InventoryItem.class)))
                .thenReturn(savedItem);

        InventoryItemResponse result =
                inventoryItemService.createItem(createRequest());

        assertThat(result).isNotNull();
        assertThat(result.itemCode())
                .isEqualTo("ITEM-001");
        assertThat(result.jewellerySku())
                .isEqualTo("RG-001");
        assertThat(result.status())
                .isEqualTo(InventoryItemStatus.AVAILABLE);
        assertThat(result.netWeight())
                .isEqualByComparingTo("5.000");

        verify(inventoryItemRepository)
                .findByItemCode("ITEM-001");

        verify(jewelleryRepository)
                .findById(1L);

        verify(inventoryItemRepository)
                .save(any(InventoryItem.class));
    }

    @Test
    void shouldRejectDuplicateItemCode() {

        when(inventoryItemRepository.findByItemCode("ITEM-001"))
                .thenReturn(Optional.of(inventoryItem()));

        assertThatThrownBy(() ->
                inventoryItemService.createItem(createRequest())
        )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(
                        "Inventory item code already exists: ITEM-001"
                );

        verify(inventoryItemRepository)
                .findByItemCode("ITEM-001");

        verify(inventoryItemRepository, never())
                .save(any(InventoryItem.class));
    }

    @Test
    void shouldRejectWhenJewelleryDoesNotExist() {

        when(inventoryItemRepository.findByItemCode("ITEM-001"))
                .thenReturn(Optional.empty());

        when(jewelleryRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                inventoryItemService.createItem(createRequest())
        )
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Jewellery not found with id: 1");

        verify(jewelleryRepository)
                .findById(1L);

        verify(inventoryItemRepository, never())
                .save(any(InventoryItem.class));
    }

    @Test
    void shouldRejectInvalidNetWeight() {

        when(inventoryItemRepository.findByItemCode("ITEM-001"))
                .thenReturn(Optional.empty());

        when(jewelleryRepository.findById(1L))
                .thenReturn(Optional.of(jewellery()));

        InventoryItemCreateRequest request =
                new InventoryItemCreateRequest(
                        "ITEM-001",
                        1L,
                        new BigDecimal("5.200"),
                        new BigDecimal("0.200"),
                        new BigDecimal("4.500"),
                        new BigDecimal("40000.00"),
                        "SHOWCASE-A1"
                );

        assertThatThrownBy(() ->
                inventoryItemService.createItem(request)
        )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(
                        "Net weight must equal gross weight minus stone weight"
                );

        verify(inventoryItemRepository, never())
                .save(any(InventoryItem.class));
    }

    @Test
    void shouldGetInventoryItemById() {

        when(inventoryItemRepository.findById(1L))
                .thenReturn(Optional.of(inventoryItem()));

        InventoryItemResponse result =
                inventoryItemService.getItemById(1L);

        assertThat(result).isNotNull();
        assertThat(result.itemCode())
                .isEqualTo("ITEM-001");
        assertThat(result.status())
                .isEqualTo(InventoryItemStatus.AVAILABLE);

        verify(inventoryItemRepository)
                .findById(1L);
    }

    @Test
    void shouldRejectGetWhenItemDoesNotExist() {

        when(inventoryItemRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                inventoryItemService.getItemById(1L)
        )
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage(
                        "Inventory item not found with id: 1"
                );
    }

    @Test
    void shouldGetAllInventoryItems() {

        when(inventoryItemRepository.findAll())
                .thenReturn(List.of(inventoryItem()));

        List<InventoryItemResponse> result =
                inventoryItemService.getAllItems();

        assertThat(result)
                .hasSize(1);

        assertThat(result.get(0).itemCode())
                .isEqualTo("ITEM-001");

        verify(inventoryItemRepository)
                .findAll();
    }

    @Test
    void shouldGetItemsByStatus() {

        when(inventoryItemRepository.findByStatus(
                InventoryItemStatus.AVAILABLE
        ))
                .thenReturn(List.of(inventoryItem()));

        List<InventoryItemResponse> result =
                inventoryItemService.getItemsByStatus(
                        InventoryItemStatus.AVAILABLE
                );

        assertThat(result)
                .hasSize(1);

        assertThat(result.get(0).status())
                .isEqualTo(InventoryItemStatus.AVAILABLE);

        verify(inventoryItemRepository)
                .findByStatus(InventoryItemStatus.AVAILABLE);
    }

    @Test
    void shouldGetItemsByJewellery() {

        when(jewelleryRepository.existsById(1L))
                .thenReturn(true);

        when(inventoryItemRepository.findByJewelleryId(1L))
                .thenReturn(List.of(inventoryItem()));

        List<InventoryItemResponse> result =
                inventoryItemService.getItemsByJewellery(1L);

        assertThat(result)
                .hasSize(1);

        assertThat(result.get(0).jewellerySku())
                .isEqualTo("RG-001");

        verify(jewelleryRepository)
                .existsById(1L);

        verify(inventoryItemRepository)
                .findByJewelleryId(1L);
    }

    @Test
    void shouldRejectItemsByJewelleryWhenJewelleryDoesNotExist() {

        when(jewelleryRepository.existsById(1L))
                .thenReturn(false);

        assertThatThrownBy(() ->
                inventoryItemService.getItemsByJewellery(1L)
        )
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Jewellery not found with id: 1");

        verify(inventoryItemRepository, never())
                .findByJewelleryId(1L);
    }

    @Test
    void shouldReserveAvailableItem() {

        InventoryItem item = inventoryItem();

        when(inventoryItemRepository.findById(1L))
                .thenReturn(Optional.of(item));

        when(inventoryItemRepository.save(item))
                .thenReturn(item);

        InventoryItemResponse result =
                inventoryItemService.reserveItem(1L);

        assertThat(result.status())
                .isEqualTo(InventoryItemStatus.RESERVED);

        verify(inventoryItemRepository)
                .findById(1L);

        verify(inventoryItemRepository)
                .save(item);
    }

    @Test
    void shouldSellAvailableItem() {

        InventoryItem item = inventoryItem();

        when(inventoryItemRepository.findById(1L))
                .thenReturn(Optional.of(item));

        when(inventoryItemRepository.save(item))
                .thenReturn(item);

        InventoryItemResponse result =
                inventoryItemService.sellItem(1L);

        assertThat(result.status())
                .isEqualTo(InventoryItemStatus.SOLD);

        verify(inventoryItemRepository)
                .findById(1L);

        verify(inventoryItemRepository)
                .save(item);
    }

    @Test
    void shouldSellReservedItem() {

        InventoryItem item = inventoryItem();
        item.reserve();

        when(inventoryItemRepository.findById(1L))
                .thenReturn(Optional.of(item));

        when(inventoryItemRepository.save(item))
                .thenReturn(item);

        InventoryItemResponse result =
                inventoryItemService.sellItem(1L);

        assertThat(result.status())
                .isEqualTo(InventoryItemStatus.SOLD);
    }

    @Test
    void shouldRejectSellingDamagedItem() {

        InventoryItem item = inventoryItem();
        item.markDamaged();

        when(inventoryItemRepository.findById(1L))
                .thenReturn(Optional.of(item));

        assertThatThrownBy(() ->
                inventoryItemService.sellItem(1L)
        )
                .isInstanceOf(IllegalStateException.class)
                .hasMessage(
                        "Only available or reserved inventory items can be sold"
                );

        verify(inventoryItemRepository, never())
                .save(item);
    }

    @Test
    void shouldMarkAvailableItemAsDamaged() {

        InventoryItem item = inventoryItem();

        when(inventoryItemRepository.findById(1L))
                .thenReturn(Optional.of(item));

        when(inventoryItemRepository.save(item))
                .thenReturn(item);

        InventoryItemResponse result =
                inventoryItemService.markItemDamaged(1L);

        assertThat(result.status())
                .isEqualTo(InventoryItemStatus.DAMAGED);

        verify(inventoryItemRepository)
                .save(item);
    }

    @Test
    void shouldRejectDamagingSoldItem() {

        InventoryItem item = inventoryItem();
        item.sell();

        when(inventoryItemRepository.findById(1L))
                .thenReturn(Optional.of(item));

        assertThatThrownBy(() ->
                inventoryItemService.markItemDamaged(1L)
        )
                .isInstanceOf(IllegalStateException.class)
                .hasMessage(
                        "Sold inventory item cannot be marked as damaged"
                );

        verify(inventoryItemRepository, never())
                .save(item);
    }

    @Test
    void shouldRejectReservingNonAvailableItem() {

        InventoryItem item = inventoryItem();
        item.sell();

        when(inventoryItemRepository.findById(1L))
                .thenReturn(Optional.of(item));

        assertThatThrownBy(() ->
                inventoryItemService.reserveItem(1L)
        )
                .isInstanceOf(IllegalStateException.class)
                .hasMessage(
                        "Only available inventory items can be reserved"
                );

        verify(inventoryItemRepository, never())
                .save(item);
    }
}