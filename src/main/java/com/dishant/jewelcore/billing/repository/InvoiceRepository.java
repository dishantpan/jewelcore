package com.dishant.jewelcore.billing.repository;

import com.dishant.jewelcore.billing.entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    Optional<Invoice> findByInvoiceNumber(String invoiceNumber);

    Optional<Invoice> findBySaleId(Long saleId);

    boolean existsByInvoiceNumber(String invoiceNumber);
}