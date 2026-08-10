package com.dishant.jewelcore.masterdata.metal.repository;

import com.dishant.jewelcore.masterdata.metal.entity.Metal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MetalRepository extends JpaRepository<Metal, Long> {

    Optional<Metal> findByCode(String code);
}