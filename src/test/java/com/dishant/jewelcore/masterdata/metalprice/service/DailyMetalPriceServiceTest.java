package com.dishant.jewelcore.masterdata.metalprice.service;

import com.dishant.jewelcore.common.exception.ResourceNotFoundException;
import com.dishant.jewelcore.masterdata.metal.entity.Metal;
import com.dishant.jewelcore.masterdata.metal.repository.MetalRepository;
import com.dishant.jewelcore.masterdata.metalprice.dto.DailyMetalPriceCreateRequest;
import com.dishant.jewelcore.masterdata.metalprice.dto.DailyMetalPriceUpdateRequest;
import com.dishant.jewelcore.masterdata.metalprice.entity.DailyMetalPrice;
import com.dishant.jewelcore.masterdata.metalprice.repository.DailyMetalPriceRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DailyMetalPriceServiceTest {

    @Mock
    private DailyMetalPriceRepository priceRepository;

    @Mock
    private MetalRepository metalRepository;

    @InjectMocks
    private DailyMetalPriceService priceService;

    @Test
    void shouldCreateDailyMetalPrice() {

        Metal metal = new Metal("Gold", "GOLD");

        when(metalRepository.findById(1L))
                .thenReturn(Optional.of(metal));

        when(priceRepository.findByMetalIdAndPriceDate(
                1L,
                LocalDate.of(2026, 8, 13)
        )).thenReturn(Optional.empty());

        DailyMetalPrice savedPrice = new DailyMetalPrice(
                metal,
                LocalDate.of(2026, 8, 13),
                new BigDecimal("7250.00")
        );

        when(priceRepository.save(any(DailyMetalPrice.class)))
                .thenReturn(savedPrice);

        DailyMetalPriceCreateRequest request =
                new DailyMetalPriceCreateRequest(
                        1L,
                        LocalDate.of(2026, 8, 13),
                        new BigDecimal("7250.00")
                );

        var result = priceService.createPrice(request);

        assertThat(result).isNotNull();
        assertThat(result.metalName()).isEqualTo("Gold");
        assertThat(result.metalCode()).isEqualTo("GOLD");
        assertThat(result.priceDate())
                .isEqualTo(LocalDate.of(2026, 8, 13));
        assertThat(result.pricePerGram())
                .isEqualByComparingTo("7250.00");

        verify(metalRepository).findById(1L);
        verify(priceRepository)
                .findByMetalIdAndPriceDate(
                        1L,
                        LocalDate.of(2026, 8, 13)
                );
        verify(priceRepository).save(any(DailyMetalPrice.class));
    }

    @Test
    void shouldRejectPriceWhenMetalDoesNotExist() {

        when(metalRepository.findById(1L))
                .thenReturn(Optional.empty());

        DailyMetalPriceCreateRequest request =
                new DailyMetalPriceCreateRequest(
                        1L,
                        LocalDate.of(2026, 8, 13),
                        new BigDecimal("7250.00")
                );

        assertThatThrownBy(() ->
                priceService.createPrice(request)
        )
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Metal not found with id: 1");

        verify(metalRepository).findById(1L);
        verify(priceRepository, never())
                .save(any(DailyMetalPrice.class));
    }

    @Test
    void shouldRejectDuplicatePriceForSameMetalAndDate() {

        Metal metal = new Metal("Gold", "GOLD");

        DailyMetalPrice existingPrice =
                new DailyMetalPrice(
                        metal,
                        LocalDate.of(2026, 8, 13),
                        new BigDecimal("7250.00")
                );

        when(metalRepository.findById(1L))
                .thenReturn(Optional.of(metal));

        when(priceRepository.findByMetalIdAndPriceDate(
                1L,
                LocalDate.of(2026, 8, 13)
        )).thenReturn(Optional.of(existingPrice));

        DailyMetalPriceCreateRequest request =
                new DailyMetalPriceCreateRequest(
                        1L,
                        LocalDate.of(2026, 8, 13),
                        new BigDecimal("7300.00")
                );

        assertThatThrownBy(() ->
                priceService.createPrice(request)
        )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(
                        "Price already exists for metal 1 on 2026-08-13"
                );

        verify(priceRepository, never())
                .save(any(DailyMetalPrice.class));
    }

    @Test
    void shouldGetPriceById() {

        Metal metal = new Metal("Gold", "GOLD");

        DailyMetalPrice price = new DailyMetalPrice(
                metal,
                LocalDate.of(2026, 8, 13),
                new BigDecimal("7250.00")
        );

        when(priceRepository.findById(1L))
                .thenReturn(Optional.of(price));

        var result = priceService.getPriceById(1L);

        assertThat(result).isNotNull();
        assertThat(result.metalName()).isEqualTo("Gold");
        assertThat(result.pricePerGram())
                .isEqualByComparingTo("7250.00");

        verify(priceRepository).findById(1L);
    }

    @Test
    void shouldRejectGetPriceWhenPriceDoesNotExist() {

        when(priceRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                priceService.getPriceById(1L)
        )
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage(
                        "Daily metal price not found with id: 1"
                );

        verify(priceRepository).findById(1L);
    }

    @Test
    void shouldGetPricesByMetal() {

        Metal metal = new Metal("Gold", "GOLD");

        DailyMetalPrice price1 = new DailyMetalPrice(
                metal,
                LocalDate.of(2026, 8, 13),
                new BigDecimal("7250.00")
        );

        DailyMetalPrice price2 = new DailyMetalPrice(
                metal,
                LocalDate.of(2026, 8, 12),
                new BigDecimal("7200.00")
        );

        when(metalRepository.existsById(1L))
                .thenReturn(true);

        when(priceRepository.findByMetalIdOrderByPriceDateDesc(1L))
                .thenReturn(List.of(price1, price2));

        var result = priceService.getPricesByMetal(1L);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).pricePerGram())
                .isEqualByComparingTo("7250.00");
        assertThat(result.get(1).pricePerGram())
                .isEqualByComparingTo("7200.00");

        verify(metalRepository).existsById(1L);
        verify(priceRepository)
                .findByMetalIdOrderByPriceDateDesc(1L);
    }

    @Test
    void shouldRejectGetPricesWhenMetalDoesNotExist() {

        when(metalRepository.existsById(1L))
                .thenReturn(false);

        assertThatThrownBy(() ->
                priceService.getPricesByMetal(1L)
        )
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Metal not found with id: 1");

        verify(metalRepository).existsById(1L);
        verify(priceRepository, never())
                .findByMetalIdOrderByPriceDateDesc(1L);
    }

    @Test
    void shouldUpdatePrice() {

        Metal metal = new Metal("Gold", "GOLD");

        DailyMetalPrice price = new DailyMetalPrice(
                metal,
                LocalDate.of(2026, 8, 13),
                new BigDecimal("7250.00")
        );

        when(priceRepository.findById(1L))
                .thenReturn(Optional.of(price));

        when(priceRepository.save(price))
                .thenReturn(price);

        DailyMetalPriceUpdateRequest request =
                new DailyMetalPriceUpdateRequest(
                        new BigDecimal("7300.00")
                );

        var result = priceService.updatePrice(1L, request);

        assertThat(result.pricePerGram())
                .isEqualByComparingTo("7300.00");

        verify(priceRepository).findById(1L);
        verify(priceRepository).save(price);
    }

    @Test
    void shouldRejectUpdateWhenPriceDoesNotExist() {

        when(priceRepository.findById(1L))
                .thenReturn(Optional.empty());

        DailyMetalPriceUpdateRequest request =
                new DailyMetalPriceUpdateRequest(
                        new BigDecimal("7300.00")
                );

        assertThatThrownBy(() ->
                priceService.updatePrice(1L, request)
        )
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage(
                        "Daily metal price not found with id: 1"
                );

        verify(priceRepository).findById(1L);
        verify(priceRepository, never())
                .save(any(DailyMetalPrice.class));
    }
}