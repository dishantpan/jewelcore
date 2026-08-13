package com.dishant.jewelcore.masterdata.purity.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "purities",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_purities_code",
                        columnNames = "code"
                )
        }
)
public class Purity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(
            nullable = false,
            length = 100
    )
    private String name;

    @Column(
            nullable = false,
            length = 20
    )
    private String code;

    @Column(
            nullable = false,
            precision = 5,
            scale = 2
    )
    private BigDecimal percentage;

    @Column(
            nullable = false
    )
    private boolean active;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    protected Purity() {
    }

    public Purity(
            String name,
            String code,
            BigDecimal percentage) {

        this.name = name;
        this.code = code;
        this.percentage = percentage;
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
            String code,
            BigDecimal percentage) {

        this.name = name;
        this.code = code;
        this.percentage = percentage;
    }

    public void deactivate() {
        this.active = false;
    }

    public void activate() {
        this.active = true;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }

    public BigDecimal getPercentage() {
        return percentage;
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