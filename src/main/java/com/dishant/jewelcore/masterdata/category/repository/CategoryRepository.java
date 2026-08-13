package com.dishant.jewelcore.masterdata.category.repository;

import com.dishant.jewelcore.masterdata.category.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategoryRepository
        extends JpaRepository<Category, Long> {

    Optional<Category> findByCode(String code);
}