package com.dishant.jewelcore.billing.repository;

import com.dishant.jewelcore.billing.entity.Sale;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface SaleRepository extends JpaRepository<Sale, Long> {

    Optional<Sale> findBySaleNumber(String saleNumber);

    List<Sale> findByCustomerIdOrderBySaleDateDesc(Long customerId);

    List<Sale> findByCreatedByIdOrderBySaleDateDesc(Long userId);

    List<Sale> findBySaleDateBetween(LocalDateTime start, LocalDateTime end);

    List<Sale> findByPaymentStatus(String paymentStatus);

    boolean existsBySaleNumber(String saleNumber);

    List<Sale> findAllByOrderBySaleDateDesc();
}