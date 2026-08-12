package com.dishant.jewelcore.masterdata.purity.repository;

import com.dishant.jewelcore.masterdata.purity.entity.Purity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PurityRepository extends JpaRepository<Purity, Long> {

    Optional<Purity> findByCode(String code);
}