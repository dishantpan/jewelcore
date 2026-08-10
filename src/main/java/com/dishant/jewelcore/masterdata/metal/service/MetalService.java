package com.dishant.jewelcore.masterdata.metal.service;

import com.dishant.jewelcore.masterdata.metal.dto.MetalCreateRequest;
import com.dishant.jewelcore.masterdata.metal.dto.MetalResponse;
import com.dishant.jewelcore.masterdata.metal.entity.Metal;
import com.dishant.jewelcore.masterdata.metal.repository.MetalRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
}