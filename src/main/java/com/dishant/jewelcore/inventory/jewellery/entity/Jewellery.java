package com.dishant.jewelcore.inventory.jewellery.entity;

import com.dishant.jewelcore.masterdata.category.entity.Category;
import com.dishant.jewelcore.masterdata.metal.entity.Metal;
import com.dishant.jewelcore.masterdata.purity.entity.Purity;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "jewellery",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_jewellery_sku",
                        columnNames = "sku"
                )
        }
)
public class Jewellery {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String sku;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(length = 500)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "category_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_jewellery_category"
            )
    )
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "metal_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_jewellery_metal"
            )
    )
    private Metal metal;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "purity_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_jewellery_purity"
            )
    )
    private Purity purity;

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
            name = "making_charge",
            nullable = false,
            precision = 12,
            scale = 2
    )
    private BigDecimal makingCharge;

    @Column(nullable = false)
    private boolean active = true;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    protected Jewellery() {
    }

    public Jewellery(
            String sku,
            String name,
            String description,
            Category category,
            Metal metal,
            Purity purity,
            BigDecimal grossWeight,
            BigDecimal stoneWeight,
            BigDecimal netWeight,
            BigDecimal makingCharge) {

        this.sku = sku;
        this.name = name;
        this.description = description;
        this.category = category;
        this.metal = metal;
        this.purity = purity;
        this.grossWeight = grossWeight;
        this.stoneWeight = stoneWeight;
        this.netWeight = netWeight;
        this.makingCharge = makingCharge;
        this.active = true;
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

    public void updateDetails(
            String name,
            String description,
            Category category,
            Metal metal,
            Purity purity,
            BigDecimal grossWeight,
            BigDecimal stoneWeight,
            BigDecimal netWeight,
            BigDecimal makingCharge) {

        this.name = name;
        this.description = description;
        this.category = category;
        this.metal = metal;
        this.purity = purity;
        this.grossWeight = grossWeight;
        this.stoneWeight = stoneWeight;
        this.netWeight = netWeight;
        this.makingCharge = makingCharge;
    }

    public void deactivate() {
        this.active = false;
    }

    public Long getId() {
        return id;
    }

    public String getSku() {
        return sku;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Category getCategory() {
        return category;
    }

    public Metal getMetal() {
        return metal;
    }

    public Purity getPurity() {
        return purity;
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

    public BigDecimal getMakingCharge() {
        return makingCharge;
    }

    public boolean isActive() {
        return active;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}