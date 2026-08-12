package com.dishant.jewelcore.masterdata.purity.service;

import com.dishant.jewelcore.common.exception.ResourceNotFoundException;
import com.dishant.jewelcore.masterdata.purity.dto.PurityCreateRequest;
import com.dishant.jewelcore.masterdata.purity.dto.PurityUpdateRequest;
import com.dishant.jewelcore.masterdata.purity.entity.Purity;
import com.dishant.jewelcore.masterdata.purity.repository.PurityRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PurityServiceTest {

    @Mock
    private PurityRepository purityRepository;

    @InjectMocks
    private PurityService purityService;

    @Test
    void shouldCreatePurityWhenCodeDoesNotExist() {

        PurityCreateRequest request = new PurityCreateRequest(
                "24 Karat Gold",
                "24K",
                new BigDecimal("99.90")
        );

        when(purityRepository.findByCode("24K"))
                .thenReturn(Optional.empty());

        Purity savedPurity = new Purity(
                "24 Karat Gold",
                "24K",
                new BigDecimal("99.90")
        );

        when(purityRepository.save(any(Purity.class)))
                .thenReturn(savedPurity);

        var result = purityService.createPurity(request);

        assertThat(result).isNotNull();
        assertThat(result.name()).isEqualTo("24 Karat Gold");
        assertThat(result.code()).isEqualTo("24K");
        assertThat(result.percentage())
                .isEqualByComparingTo("99.90");
        assertThat(result.active()).isTrue();

        verify(purityRepository).findByCode("24K");
        verify(purityRepository).save(any(Purity.class));
    }

    @Test
    void shouldRejectPurityWhenCodeAlreadyExists() {

        Purity existingPurity = new Purity(
                "24 Karat Gold",
                "24K",
                new BigDecimal("99.90")
        );

        when(purityRepository.findByCode("24K"))
                .thenReturn(Optional.of(existingPurity));

        PurityCreateRequest request = new PurityCreateRequest(
                "Another Purity",
                "24K",
                new BigDecimal("99.00")
        );

        assertThatThrownBy(() ->
                purityService.createPurity(request)
        )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Purity code already exists: 24K");

        verify(purityRepository).findByCode("24K");
        verify(purityRepository, never())
                .save(any(Purity.class));
    }

    @Test
    void shouldGetPurityById() {

        Purity purity = new Purity(
                "22 Karat Gold",
                "22K",
                new BigDecimal("91.60")
        );

        when(purityRepository.findById(1L))
                .thenReturn(Optional.of(purity));

        var result = purityService.getPurityById(1L);

        assertThat(result).isNotNull();
        assertThat(result.name()).isEqualTo("22 Karat Gold");
        assertThat(result.code()).isEqualTo("22K");
        assertThat(result.percentage())
                .isEqualByComparingTo("91.60");

        verify(purityRepository).findById(1L);
    }

    @Test
    void shouldRejectGetPurityWhenPurityDoesNotExist() {

        when(purityRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                purityService.getPurityById(1L)
        )
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Purity not found with id: 1");

        verify(purityRepository).findById(1L);
    }

    @Test
    void shouldUpdatePurity() {

        Purity purity = new Purity(
                "22 Karat Gold",
                "22K",
                new BigDecimal("91.60")
        );

        when(purityRepository.findById(1L))
                .thenReturn(Optional.of(purity));

        // Same code is allowed when updating the same purity.
        // The repository check is represented as no conflicting purity.
        when(purityRepository.findByCode("22K"))
                .thenReturn(Optional.empty());

        when(purityRepository.save(purity))
                .thenReturn(purity);

        PurityUpdateRequest request = new PurityUpdateRequest(
                "22K Gold",
                "22K",
                new BigDecimal("91.60")
        );

        var result = purityService.updatePurity(1L, request);

        assertThat(result.name())
                .isEqualTo("22K Gold");

        assertThat(result.code())
                .isEqualTo("22K");

        assertThat(result.percentage())
                .isEqualByComparingTo("91.60");

        verify(purityRepository).findById(1L);
        verify(purityRepository).findByCode("22K");
        verify(purityRepository).save(purity);
    }

    @Test
    void shouldRejectUpdateWhenNewCodeAlreadyBelongsToAnotherPurity() {

        Purity currentPurity = new Purity(
                "22 Karat Gold",
                "22K",
                new BigDecimal("91.60")
        );

        Purity existingPurity = org.mockito.Mockito.mock(Purity.class);

        when(existingPurity.getId())
                .thenReturn(2L);

        when(purityRepository.findById(1L))
                .thenReturn(Optional.of(currentPurity));

        when(purityRepository.findByCode("24K"))
                .thenReturn(Optional.of(existingPurity));

        PurityUpdateRequest request = new PurityUpdateRequest(
                "22K Gold",
                "24K",
                new BigDecimal("91.60")
        );

        assertThatThrownBy(() ->
                purityService.updatePurity(1L, request)
        )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Purity code already exists: 24K");

        verify(purityRepository).findById(1L);
        verify(purityRepository).findByCode("24K");

        verify(purityRepository, never())
                .save(any(Purity.class));
    }

    @Test
    void shouldDeactivateActivePurity() {

        Purity purity = new Purity(
                "22 Karat Gold",
                "22K",
                new BigDecimal("91.60")
        );

        when(purityRepository.findById(1L))
                .thenReturn(Optional.of(purity));

        purityService.deactivatePurity(1L);

        assertThat(purity.isActive()).isFalse();

        verify(purityRepository).findById(1L);
        verify(purityRepository).save(purity);
    }

    @Test
    void shouldRejectDeactivationWhenPurityDoesNotExist() {

        when(purityRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                purityService.deactivatePurity(1L)
        )
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Purity not found with id: 1");

        verify(purityRepository).findById(1L);
        verify(purityRepository, never())
                .save(any(Purity.class));
    }

    @Test
    void shouldRejectDeactivationWhenPurityIsAlreadyInactive() {

        Purity purity = new Purity(
                "22 Karat Gold",
                "22K",
                new BigDecimal("91.60")
        );

        purity.deactivate();

        when(purityRepository.findById(1L))
                .thenReturn(Optional.of(purity));

        assertThatThrownBy(() ->
                purityService.deactivatePurity(1L)
        )
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Purity is already inactive: 1");

        verify(purityRepository).findById(1L);
        verify(purityRepository, never())
                .save(any(Purity.class));
    }
}