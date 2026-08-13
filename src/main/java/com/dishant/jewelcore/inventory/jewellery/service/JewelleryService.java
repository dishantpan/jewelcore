package com.dishant.jewelcore.inventory.jewellery.service;

import com.dishant.jewelcore.common.exception.ResourceNotFoundException;
import com.dishant.jewelcore.inventory.jewellery.dto.JewelleryCreateRequest;
import com.dishant.jewelcore.inventory.jewellery.dto.JewelleryResponse;
import com.dishant.jewelcore.inventory.jewellery.dto.JewelleryUpdateRequest;
import com.dishant.jewelcore.inventory.jewellery.entity.Jewellery;
import com.dishant.jewelcore.inventory.jewellery.repository.JewelleryRepository;
import com.dishant.jewelcore.masterdata.category.entity.Category;
import com.dishant.jewelcore.masterdata.category.repository.CategoryRepository;
import com.dishant.jewelcore.masterdata.metal.entity.Metal;
import com.dishant.jewelcore.masterdata.metal.repository.MetalRepository;
import com.dishant.jewelcore.masterdata.purity.entity.Purity;
import com.dishant.jewelcore.masterdata.purity.repository.PurityRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class JewelleryService {

    private final JewelleryRepository jewelleryRepository;
    private final CategoryRepository categoryRepository;
    private final MetalRepository metalRepository;
    private final PurityRepository purityRepository;

    public JewelleryService(
            JewelleryRepository jewelleryRepository,
            CategoryRepository categoryRepository,
            MetalRepository metalRepository,
            PurityRepository purityRepository) {

        this.jewelleryRepository = jewelleryRepository;
        this.categoryRepository = categoryRepository;
        this.metalRepository = metalRepository;
        this.purityRepository = purityRepository;
    }

    public JewelleryResponse createJewellery(
            JewelleryCreateRequest request) {

        if (jewelleryRepository.findBySku(request.sku()).isPresent()) {
            throw new IllegalArgumentException(
                    "Jewellery SKU already exists: " + request.sku()
            );
        }

        Category category = categoryRepository.findById(
                        request.categoryId()
                )
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Category not found with id: "
                                + request.categoryId()
                ));

        Metal metal = metalRepository.findById(
                        request.metalId()
                )
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Metal not found with id: "
                                + request.metalId()
                ));

        Purity purity = purityRepository.findById(
                        request.purityId()
                )
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Purity not found with id: "
                                + request.purityId()
                ));

        validateWeights(
                request.grossWeight(),
                request.stoneWeight(),
                request.netWeight()
        );

        Jewellery jewellery = new Jewellery(
                request.sku(),
                request.name(),
                request.description(),
                category,
                metal,
                purity,
                request.grossWeight(),
                request.stoneWeight(),
                request.netWeight(),
                request.makingCharge()
        );

        Jewellery savedJewellery =
                jewelleryRepository.save(jewellery);

        return JewelleryResponse.from(savedJewellery);
    }

    @Transactional(readOnly = true)
    public List<JewelleryResponse> getAllJewellery() {

        return jewelleryRepository.findAll()
                .stream()
                .map(JewelleryResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public JewelleryResponse getJewelleryById(Long id) {

        Jewellery jewellery =
                jewelleryRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Jewellery not found with id: "
                                                + id
                                ));

        return JewelleryResponse.from(jewellery);
    }

    public JewelleryResponse updateJewellery(
            Long id,
            JewelleryUpdateRequest request) {

        Jewellery jewellery =
                jewelleryRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Jewellery not found with id: "
                                                + id
                                ));

        Category category = categoryRepository.findById(
                        request.categoryId()
                )
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Category not found with id: "
                                + request.categoryId()
                ));

        Metal metal = metalRepository.findById(
                        request.metalId()
                )
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Metal not found with id: "
                                + request.metalId()
                ));

        Purity purity = purityRepository.findById(
                        request.purityId()
                )
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Purity not found with id: "
                                + request.purityId()
                ));

        validateWeights(
                request.grossWeight(),
                request.stoneWeight(),
                request.netWeight()
        );

        jewellery.updateDetails(
                request.name(),
                request.description(),
                category,
                metal,
                purity,
                request.grossWeight(),
                request.stoneWeight(),
                request.netWeight(),
                request.makingCharge()
        );

        Jewellery updatedJewellery =
                jewelleryRepository.save(jewellery);

        return JewelleryResponse.from(updatedJewellery);
    }

    public void deactivateJewellery(Long id) {

        Jewellery jewellery =
                jewelleryRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Jewellery not found with id: "
                                                + id
                                ));

        if (!jewellery.isActive()) {
            throw new IllegalStateException(
                    "Jewellery is already inactive: " + id
            );
        }

        jewellery.deactivate();

        jewelleryRepository.save(jewellery);
    }

    private void validateWeights(
            java.math.BigDecimal grossWeight,
            java.math.BigDecimal stoneWeight,
            java.math.BigDecimal netWeight) {

        if (stoneWeight.compareTo(grossWeight) > 0) {
            throw new IllegalArgumentException(
                    "Stone weight cannot be greater than gross weight"
            );
        }

        if (netWeight.compareTo(grossWeight) > 0) {
            throw new IllegalArgumentException(
                    "Net weight cannot be greater than gross weight"
            );
        }

        java.math.BigDecimal calculatedNetWeight =
                grossWeight.subtract(stoneWeight);

        if (netWeight.compareTo(calculatedNetWeight) != 0) {
            throw new IllegalArgumentException(
                    "Net weight must equal gross weight minus stone weight"
            );
        }
    }
}