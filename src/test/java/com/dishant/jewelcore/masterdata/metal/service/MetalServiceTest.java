package com.dishant.jewelcore.masterdata.metal.service;

import com.dishant.jewelcore.masterdata.metal.entity.Metal;
import com.dishant.jewelcore.masterdata.metal.repository.MetalRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.dishant.jewelcore.masterdata.metal.dto.MetalCreateRequest;
import com.dishant.jewelcore.masterdata.metal.dto.MetalResponse;
import com.dishant.jewelcore.common.exception.ResourceNotFoundException;
import java.util.Optional;

import static org.mockito.Mockito.times;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MetalServiceTest {

    @Mock
    private MetalRepository metalRepository;

    @InjectMocks
    private MetalService metalService;

    @Test
    void shouldCreateMetalWhenCodeDoesNotExist() {

        when(metalRepository.findByCode("SIL"))
                .thenReturn(Optional.empty());

        Metal savedMetal = new Metal("Silver", "SIL");

        when(metalRepository.save(any(Metal.class)))
                .thenReturn(savedMetal);

        MetalResponse result = metalService.createMetal(
                new MetalCreateRequest("Silver", "SIL")
        );

        assertThat(result).isNotNull();
        assertThat(result.name()).isEqualTo("Silver");
        assertThat(result.code()).isEqualTo("SIL");

        verify(metalRepository).findByCode("SIL");
        verify(metalRepository).save(any(Metal.class));
    }

    @Test
    void shouldRejectMetalWhenCodeAlreadyExists() {

        Metal existingMetal = new Metal("Silver", "SIL");

        when(metalRepository.findByCode("SIL"))
                .thenReturn(Optional.of(existingMetal));

        assertThatThrownBy(() ->
                metalService.createMetal(
                        new MetalCreateRequest("Another Silver", "SIL")
                )
        )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Metal code already exists: SIL");

        verify(metalRepository).findByCode("SIL");
        verify(metalRepository, never()).save(any(Metal.class));
    }

    @Test
    void shouldDeactivateMetalWhenMetalExists() {

        Metal metal = new Metal("Silver", "SIL");

        when(metalRepository.findById(1L))
                .thenReturn(Optional.of(metal));

        metalService.deactivateMetal(1L);

        assertThat(metal.isActive()).isFalse();

        verify(metalRepository).findById(1L);
        verify(metalRepository).save(metal);
    }

    @Test
    void shouldRejectDeactivationWhenMetalDoesNotExist() {

        when(metalRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                metalService.deactivateMetal(99L)
        )
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Metal not found with id: 99");

        verify(metalRepository).findById(99L);
        verify(metalRepository, never()).save(any(Metal.class));
    }

    @Test
    void shouldRejectDeactivationWhenMetalIsAlreadyInactive() {

        Metal metal = new Metal("Silver", "SIL");
        metal.deactivate();

        when(metalRepository.findById(1L))
                .thenReturn(Optional.of(metal));

        assertThatThrownBy(() ->
                metalService.deactivateMetal(1L)
        )
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Metal is already inactive: 1");

        verify(metalRepository).findById(1L);
        verify(metalRepository, never()).save(any(Metal.class));
    }
}