package com.dishant.jewelcore.inventory.stock.entity;

import com.dishant.jewelcore.inventory.jewellery.entity.Jewellery;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "inventory_items",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_inventory_item_code",
                        columnNames = "item_code"
                )
        }
)
public class InventoryItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(
            name = "item_code",
            nullable = false,
            length = 50
    )
    private String itemCode;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "jewellery_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_inventory_item_jewellery"
            )
    )
    private Jewellery jewellery;

    @Column(
            name = "gross_weight",
            nullable = false,
            precision = 10,
            scale = 3
    )
    private BigDecimal grossWeight;

    @Column(
            name = "stone_weight",
            nullable = false,
            precision = 10,
            scale = 3
    )
    private BigDecimal stoneWeight;

    @Column(
            name = "net_weight",
            nullable = false,
            precision = 10,
            scale = 3
    )
    private BigDecimal netWeight;

    @Column(
            name = "purchase_cost",
            nullable = false,
            precision = 12,
            scale = 2
    )
    private BigDecimal purchaseCost;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private InventoryItemStatus status;

    @Column(length = 100)
    private String location;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    protected InventoryItem() {
    }

    public InventoryItem(
            String itemCode,
            Jewellery jewellery,
            BigDecimal grossWeight,
            BigDecimal stoneWeight,
            BigDecimal netWeight,
            BigDecimal purchaseCost,
            String location) {

        this.itemCode = itemCode;
        this.jewellery = jewellery;
        this.grossWeight = grossWeight;
        this.stoneWeight = stoneWeight;
        this.netWeight = netWeight;
        this.purchaseCost = purchaseCost;
        this.location = location;
        this.status = InventoryItemStatus.AVAILABLE;
    }

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public void reserve() {

        if (status != InventoryItemStatus.AVAILABLE) {
            throw new IllegalStateException(
                    "Only available inventory items can be reserved"
            );
        }

        status = InventoryItemStatus.RESERVED;
    }

    public void sell() {

        if (status != InventoryItemStatus.AVAILABLE
                && status != InventoryItemStatus.RESERVED) {

            throw new IllegalStateException(
                    "Only available or reserved inventory items can be sold"
            );
        }

        status = InventoryItemStatus.SOLD;
    }

    public void markDamaged() {

        if (status == InventoryItemStatus.SOLD) {
            throw new IllegalStateException(
                    "Sold inventory item cannot be marked as damaged"
            );
        }

        status = InventoryItemStatus.DAMAGED;
    }

    public Long getId() {
        return id;
    }

    public String getItemCode() {
        return itemCode;
    }

    public Jewellery getJewellery() {
        return jewellery;
    }

    public BigDecimal getGrossWeight() {
        return grossWeight;
    }

    public BigDecimal getStoneWeight() {
        return stoneWeight;
    }

    public BigDecimal getNetWeight() {
        return netWeight;
    }

    public BigDecimal getPurchaseCost() {
        return purchaseCost;
    }

    public InventoryItemStatus getStatus() {
        return status;
    }

    public String getLocation() {
        return location;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}