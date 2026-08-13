package com.dishant.jewelcore.masterdata.metal.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "metals",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_metals_name",
                        columnNames = "name"
                ),
                @UniqueConstraint(
                        name = "uk_metals_code",
                        columnNames = "code"
                )
        }
)
public class Metal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(
            nullable = false,
            length = 50
    )
    private String name;

    @Column(
            nullable = false,
            length = 20
    )
    private String code;

    @Column(
            nullable = false
    )
    private boolean active;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    protected Metal() {
    }

    public Metal(String name, String code) {
        this.name = name;
        this.code = code;
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
            String code) {

        this.name = name;
        this.code = code;
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