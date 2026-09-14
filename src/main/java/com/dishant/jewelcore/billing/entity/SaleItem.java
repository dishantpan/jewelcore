package com.dishant.jewelcore.billing.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "sale_item")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SaleItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sale_id", nullable = false)
    private Sale sale;

    @Column(name = "inventory_item_id", nullable = false)
    private Long inventoryItemId;

    @Column(name = "jewellery_id", nullable = false)
    private Long jewelleryId;

    @Column(name = "jewellery_name", length = 255)
    private String jewelleryName;

    @Column(name = "metal_name", nullable = false, length = 100)
    private String metalName;

    @Column(name = "purity_name", nullable = false, length = 50)
    private String purityName;

    @Column(name = "purity_percentage", nullable = false, precision = 5, scale = 2)
    private BigDecimal purityPercentage;

    @Column(name = "gross_weight", nullable = false, precision = 19, scale = 4)
    private BigDecimal grossWeight;

    @Column(name = "stone_weight", nullable = false, precision = 19, scale = 4)
    @Builder.Default
    private BigDecimal stoneWeight = BigDecimal.ZERO;

    @Column(name = "net_weight", nullable = false, precision = 19, scale = 4)
    private BigDecimal netWeight;

    @Column(name = "metal_rate_per_gram", nullable = false, precision = 19, scale = 4)
    private BigDecimal metalRatePerGram;

    @Column(name = "pure_metal_rate_per_gram", nullable = false, precision = 19, scale = 4)
    private BigDecimal pureMetalRatePerGram;

    @Column(name = "metal_value", nullable = false, precision = 19, scale = 4)
    private BigDecimal metalValue;

    @Column(name = "making_charge", nullable = false, precision = 19, scale = 4)
    private BigDecimal makingCharge;

    @Column(name = "item_price", nullable = false, precision = 19, scale = 4)
    private BigDecimal itemPrice;

    @Column(name = "gst_rate", nullable = false, precision = 5, scale = 2)
    @Builder.Default
    private BigDecimal gstRate = new BigDecimal("3.00");

    @Column(name = "gst_amount", nullable = false, precision = 19, scale = 4)
    private BigDecimal gstAmount;

    @Column(name = "total_amount", nullable = false, precision = 19, scale = 4)
    private BigDecimal totalAmount;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private java.time.LocalDateTime createdAt;
}