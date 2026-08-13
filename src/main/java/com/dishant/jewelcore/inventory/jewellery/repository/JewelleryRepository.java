package com.dishant.jewelcore.inventory.jewellery.repository;

import com.dishant.jewelcore.inventory.jewellery.entity.Jewellery;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JewelleryRepository
        extends JpaRepository<Jewellery, Long> {

    Optional<Jewellery> findBySku(String sku);
}