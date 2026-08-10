package com.dishant.jewelcore.masterdata.metal.repository;

import com.dishant.jewelcore.masterdata.metal.entity.Metal;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class MetalRepositoryIntegrationTest {

    @Autowired
    private MetalRepository metalRepository;

    @Test
    void shouldSaveAndFindMetalByCode() {

        Metal metal = new Metal("Test Silver", "TEST-SIL");

        Metal savedMetal = metalRepository.save(metal);

        assertThat(savedMetal.getId()).isNotNull();
        assertThat(savedMetal.getName()).isEqualTo("Test Silver");
        assertThat(savedMetal.getCode()).isEqualTo("TEST-SIL");
        assertThat(savedMetal.isActive()).isTrue();

        Optional<Metal> foundMetal =
                metalRepository.findByCode("TEST-SIL");

        assertThat(foundMetal).isPresent();
        assertThat(foundMetal.get().getName()).isEqualTo("Test Silver");
    }
}