package com.dishant.jewelcore.masterdata.metalprice.entity;

import com.dishant.jewelcore.masterdata.metal.entity.Metal;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "daily_metal_prices",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_daily_metal_price_metal_date",
                        columnNames = {"metal_id", "price_date"}
                )
        }
)
public class DailyMetalPrice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "metal_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_daily_metal_price_metal"
            )
    )
    private Metal metal;

    @Column(name = "price_date", nullable = false)
    private LocalDate priceDate;

    @Column(
            name = "price_per_gram",
            nullable = false,
            precision = 12,
            scale = 2
    )
    private BigDecimal pricePerGram;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    protected DailyMetalPrice() {
    }

    public DailyMetalPrice(
            Metal metal,
            LocalDate priceDate,
            BigDecimal pricePerGram) {

        this.metal = metal;
        this.priceDate = priceDate;
        this.pricePerGram = pricePerGram;
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

    public void updatePrice(BigDecimal pricePerGram) {
        this.pricePerGram = pricePerGram;
    }

    public Long getId() {
        return id;
    }

    public Metal getMetal() {
        return metal;
    }

    public LocalDate getPriceDate() {
        return priceDate;
    }

    public BigDecimal getPricePerGram() {
        return pricePerGram;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}