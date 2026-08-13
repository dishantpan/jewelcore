package com.dishant.jewelcore.inventory.jewellery.service;

import com.dishant.jewelcore.common.exception.ResourceNotFoundException;
import com.dishant.jewelcore.inventory.jewellery.dto.JewelleryCreateRequest;
import com.dishant.jewelcore.inventory.jewellery.dto.JewelleryUpdateRequest;
import com.dishant.jewelcore.inventory.jewellery.entity.Jewellery;
import com.dishant.jewelcore.inventory.jewellery.repository.JewelleryRepository;
import com.dishant.jewelcore.masterdata.category.entity.Category;
import com.dishant.jewelcore.masterdata.category.repository.CategoryRepository;
import com.dishant.jewelcore.masterdata.metal.entity.Metal;
import com.dishant.jewelcore.masterdata.metal.repository.MetalRepository;
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
class JewelleryServiceTest {

    @Mock
    private JewelleryRepository jewelleryRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private MetalRepository metalRepository;

    @Mock
    private PurityRepository purityRepository;

    @InjectMocks
    private JewelleryService jewelleryService;

    private Category category() {
        return new Category("Rings", "RING");
    }

    private Metal metal() {
        return new Metal("Gold", "GOLD");
    }

    private Purity purity() {
        return new Purity(
                "22 Karat Gold",
                "22K",
                new BigDecimal("91.60")
        );
    }

    private JewelleryCreateRequest createRequest() {
        return new JewelleryCreateRequest(
                "RG-001",
                "Classic Gold Ring",
                "Classic 22K gold ring",
                1L,
                1L,
                1L,
                new BigDecimal("5.200"),
                new BigDecimal("0.200"),
                new BigDecimal("5.000"),
                new BigDecimal("2500.00")
        );
    }

    @Test
    void shouldCreateJewellery() {

        Category category = category();
        Metal metal = metal();
        Purity purity = purity();

        when(jewelleryRepository.findBySku("RG-001"))
                .thenReturn(Optional.empty());

        when(categoryRepository.findById(1L))
                .thenReturn(Optional.of(category));

        when(metalRepository.findById(1L))
                .thenReturn(Optional.of(metal));

        when(purityRepository.findById(1L))
                .thenReturn(Optional.of(purity));

        Jewellery savedJewellery = new Jewellery(
                "RG-001",
                "Classic Gold Ring",
                "Classic 22K gold ring",
                category,
                metal,
                purity,
                new BigDecimal("5.200"),
                new BigDecimal("0.200"),
                new BigDecimal("5.000"),
                new BigDecimal("2500.00")
        );

        when(jewelleryRepository.save(any(Jewellery.class)))
                .thenReturn(savedJewellery);

        var result = jewelleryService.createJewellery(createRequest());

        assertThat(result).isNotNull();
        assertThat(result.sku()).isEqualTo("RG-001");
        assertThat(result.name()).isEqualTo("Classic Gold Ring");
        assertThat(result.categoryCode()).isEqualTo("RING");
        assertThat(result.metalCode()).isEqualTo("GOLD");
        assertThat(result.purityCode()).isEqualTo("22K");
        assertThat(result.netWeight())
                .isEqualByComparingTo("5.000");
        assertThat(result.active()).isTrue();

        verify(jewelleryRepository).findBySku("RG-001");
        verify(categoryRepository).findById(1L);
        verify(metalRepository).findById(1L);
        verify(purityRepository).findById(1L);
        verify(jewelleryRepository).save(any(Jewellery.class));
    }

    @Test
    void shouldRejectJewelleryWhenSkuAlreadyExists() {

        Jewellery existingJewellery = new Jewellery(
                "RG-001",
                "Existing Ring",
                null,
                category(),
                metal(),
                purity(),
                new BigDecimal("5.000"),
                new BigDecimal("0.000"),
                new BigDecimal("5.000"),
                new BigDecimal("2000.00")
        );

        when(jewelleryRepository.findBySku("RG-001"))
                .thenReturn(Optional.of(existingJewellery));

        assertThatThrownBy(() ->
                jewelleryService.createJewellery(createRequest())
        )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Jewellery SKU already exists: RG-001");

        verify(jewelleryRepository).findBySku("RG-001");
        verify(jewelleryRepository, never())
                .save(any(Jewellery.class));
    }

    @Test
    void shouldRejectWhenCategoryDoesNotExist() {

        when(jewelleryRepository.findBySku("RG-001"))
                .thenReturn(Optional.empty());

        when(categoryRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                jewelleryService.createJewellery(createRequest())
        )
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Category not found with id: 1");

        verify(categoryRepository).findById(1L);
        verify(jewelleryRepository, never())
                .save(any(Jewellery.class));
    }

    @Test
    void shouldRejectWhenMetalDoesNotExist() {

        when(jewelleryRepository.findBySku("RG-001"))
                .thenReturn(Optional.empty());

        when(categoryRepository.findById(1L))
                .thenReturn(Optional.of(category()));

        when(metalRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                jewelleryService.createJewellery(createRequest())
        )
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Metal not found with id: 1");

        verify(metalRepository).findById(1L);
        verify(jewelleryRepository, never())
                .save(any(Jewellery.class));
    }

    @Test
    void shouldRejectWhenPurityDoesNotExist() {

        when(jewelleryRepository.findBySku("RG-001"))
                .thenReturn(Optional.empty());

        when(categoryRepository.findById(1L))
                .thenReturn(Optional.of(category()));

        when(metalRepository.findById(1L))
                .thenReturn(Optional.of(metal()));

        when(purityRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                jewelleryService.createJewellery(createRequest())
        )
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Purity not found with id: 1");

        verify(purityRepository).findById(1L);
        verify(jewelleryRepository, never())
                .save(any(Jewellery.class));
    }

    @Test
    void shouldRejectWhenNetWeightIsIncorrect() {

        when(jewelleryRepository.findBySku("RG-001"))
                .thenReturn(Optional.empty());

        when(categoryRepository.findById(1L))
                .thenReturn(Optional.of(category()));

        when(metalRepository.findById(1L))
                .thenReturn(Optional.of(metal()));

        when(purityRepository.findById(1L))
                .thenReturn(Optional.of(purity()));

        JewelleryCreateRequest request =
                new JewelleryCreateRequest(
                        "RG-001",
                        "Classic Gold Ring",
                        null,
                        1L,
                        1L,
                        1L,
                        new BigDecimal("5.200"),
                        new BigDecimal("0.200"),
                        new BigDecimal("4.500"),
                        new BigDecimal("2500.00")
                );

        assertThatThrownBy(() ->
                jewelleryService.createJewellery(request)
        )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(
                        "Net weight must equal gross weight minus stone weight"
                );

        verify(jewelleryRepository, never())
                .save(any(Jewellery.class));
    }

    @Test
    void shouldGetJewelleryById() {

        Jewellery jewellery = new Jewellery(
                "RG-001",
                "Classic Gold Ring",
                null,
                category(),
                metal(),
                purity(),
                new BigDecimal("5.200"),
                new BigDecimal("0.200"),
                new BigDecimal("5.000"),
                new BigDecimal("2500.00")
        );

        when(jewelleryRepository.findById(1L))
                .thenReturn(Optional.of(jewellery));

        var result = jewelleryService.getJewelleryById(1L);

        assertThat(result).isNotNull();
        assertThat(result.sku()).isEqualTo("RG-001");
        assertThat(result.metalCode()).isEqualTo("GOLD");
        assertThat(result.purityCode()).isEqualTo("22K");

        verify(jewelleryRepository).findById(1L);
    }

    @Test
    void shouldRejectGetWhenJewelleryDoesNotExist() {

        when(jewelleryRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                jewelleryService.getJewelleryById(1L)
        )
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Jewellery not found with id: 1");

        verify(jewelleryRepository).findById(1L);
    }

    @Test
    void shouldUpdateJewellery() {

        Jewellery jewellery = new Jewellery(
                "RG-001",
                "Classic Gold Ring",
                null,
                category(),
                metal(),
                purity(),
                new BigDecimal("5.200"),
                new BigDecimal("0.200"),
                new BigDecimal("5.000"),
                new BigDecimal("2500.00")
        );

        Category updatedCategory =
                new Category("Necklaces", "NECK");

        Metal updatedMetal =
                new Metal("Gold", "GOLD");

        Purity updatedPurity =
                new Purity(
                        "24 Karat Gold",
                        "24K",
                        new BigDecimal("99.90")
                );

        when(jewelleryRepository.findById(1L))
                .thenReturn(Optional.of(jewellery));

        when(categoryRepository.findById(2L))
                .thenReturn(Optional.of(updatedCategory));

        when(metalRepository.findById(1L))
                .thenReturn(Optional.of(updatedMetal));

        when(purityRepository.findById(2L))
                .thenReturn(Optional.of(updatedPurity));

        when(jewelleryRepository.save(jewellery))
                .thenReturn(jewellery);

        JewelleryUpdateRequest request =
                new JewelleryUpdateRequest(
                        "Updated Jewellery",
                        "Updated description",
                        2L,
                        1L,
                        2L,
                        new BigDecimal("6.200"),
                        new BigDecimal("0.200"),
                        new BigDecimal("6.000"),
                        new BigDecimal("3000.00")
                );

        var result =
                jewelleryService.updateJewellery(1L, request);

        assertThat(result.name())
                .isEqualTo("Updated Jewellery");

        assertThat(result.categoryCode())
                .isEqualTo("NECK");

        assertThat(result.purityCode())
                .isEqualTo("24K");

        assertThat(result.netWeight())
                .isEqualByComparingTo("6.000");

        verify(jewelleryRepository).findById(1L);
        verify(categoryRepository).findById(2L);
        verify(metalRepository).findById(1L);
        verify(purityRepository).findById(2L);
        verify(jewelleryRepository).save(jewellery);
    }

    @Test
    void shouldDeactivateActiveJewellery() {

        Jewellery jewellery = new Jewellery(
                "RG-001",
                "Classic Gold Ring",
                null,
                category(),
                metal(),
                purity(),
                new BigDecimal("5.200"),
                new BigDecimal("0.200"),
                new BigDecimal("5.000"),
                new BigDecimal("2500.00")
        );

        when(jewelleryRepository.findById(1L))
                .thenReturn(Optional.of(jewellery));

        jewelleryService.deactivateJewellery(1L);

        assertThat(jewellery.isActive()).isFalse();

        verify(jewelleryRepository).findById(1L);
        verify(jewelleryRepository).save(jewellery);
    }

    @Test
    void shouldRejectDeactivationWhenJewelleryDoesNotExist() {

        when(jewelleryRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                jewelleryService.deactivateJewellery(1L)
        )
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Jewellery not found with id: 1");

        verify(jewelleryRepository).findById(1L);
        verify(jewelleryRepository, never())
                .save(any(Jewellery.class));
    }

    @Test
    void shouldRejectDeactivationWhenAlreadyInactive() {

        Jewellery jewellery = new Jewellery(
                "RG-001",
                "Classic Gold Ring",
                null,
                category(),
                metal(),
                purity(),
                new BigDecimal("5.200"),
                new BigDecimal("0.200"),
                new BigDecimal("5.000"),
                new BigDecimal("2500.00")
        );

        jewellery.deactivate();

        when(jewelleryRepository.findById(1L))
                .thenReturn(Optional.of(jewellery));

        assertThatThrownBy(() ->
                jewelleryService.deactivateJewellery(1L)
        )
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Jewellery is already inactive: 1");

        verify(jewelleryRepository).findById(1L);
        verify(jewelleryRepository, never())
                .save(any(Jewellery.class));
    }
}