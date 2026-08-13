package com.dishant.jewelcore.masterdata.metalprice.repository;

import com.dishant.jewelcore.masterdata.metalprice.entity.DailyMetalPrice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DailyMetalPriceRepository
        extends JpaRepository<DailyMetalPrice, Long> {

    Optional<DailyMetalPrice> findByMetalIdAndPriceDate(
            Long metalId,
            LocalDate priceDate
    );

    List<DailyMetalPrice> findByMetalIdOrderByPriceDateDesc(
            Long metalId
    );
}