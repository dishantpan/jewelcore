package com.dishant.jewelcore.masterdata.metal.service;

import com.dishant.jewelcore.masterdata.metal.dto.MetalCreateRequest;
import com.dishant.jewelcore.masterdata.metal.dto.MetalResponse;
import com.dishant.jewelcore.masterdata.metal.entity.Metal;
import com.dishant.jewelcore.masterdata.metal.repository.MetalRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.dishant.jewelcore.common.exception.ResourceNotFoundException;
import com.dishant.jewelcore.masterdata.metal.dto.MetalUpdateRequest;
import java.util.List;

@Service
@Transactional
public class MetalService {

    private final MetalRepository metalRepository;

    public MetalService(MetalRepository metalRepository) {
        this.metalRepository = metalRepository;
    }

    public MetalResponse createMetal(MetalCreateRequest request) {

        if (metalRepository.findByCode(request.code()).isPresent()) {
            throw new IllegalArgumentException(
                    "Metal code already exists: " + request.code()
            );
        }

        Metal metal = new Metal(
                request.name(),
                request.code()
        );

        Metal savedMetal = metalRepository.save(metal);

        return MetalResponse.from(savedMetal);
    }
    @Transactional(readOnly = true)
    public List<MetalResponse> getAllMetals() {

        return metalRepository.findAll()
                .stream()
                .map(MetalResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public MetalResponse getMetalById(Long id) {

        Metal metal = metalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Metal not found with id: " + id
                ));

        return MetalResponse.from(metal);
    }

    @Transactional
    public MetalResponse updateMetal(Long id, MetalUpdateRequest request) {

        Metal metal = metalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Metal not found with id: " + id
                ));

        metalRepository.findByCode(request.code())
                .filter(existingMetal -> !existingMetal.getId().equals(id))
                .ifPresent(existingMetal -> {
                    throw new IllegalArgumentException(
                            "Metal code already exists: " + request.code()
                    );
                });

        metal.updateDetails(request.name(), request.code());

        Metal updatedMetal = metalRepository.save(metal);

        return MetalResponse.from(updatedMetal);
    }

    @Transactional
    public void deactivateMetal(Long id) {

        Metal metal = metalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Metal not found with id: " + id
                ));

        if (!metal.isActive()) {
            throw new IllegalStateException(
                    "Metal is already inactive: " + id
            );
        }

        metal.deactivate();

        metalRepository.save(metal);
    }
}