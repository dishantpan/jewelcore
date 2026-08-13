package com.dishant.jewelcore.masterdata.metalprice.service;

import com.dishant.jewelcore.common.exception.ResourceNotFoundException;
import com.dishant.jewelcore.masterdata.metal.entity.Metal;
import com.dishant.jewelcore.masterdata.metal.repository.MetalRepository;
import com.dishant.jewelcore.masterdata.metalprice.dto.DailyMetalPriceCreateRequest;
import com.dishant.jewelcore.masterdata.metalprice.dto.DailyMetalPriceResponse;
import com.dishant.jewelcore.masterdata.metalprice.dto.DailyMetalPriceUpdateRequest;
import com.dishant.jewelcore.masterdata.metalprice.entity.DailyMetalPrice;
import com.dishant.jewelcore.masterdata.metalprice.repository.DailyMetalPriceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class DailyMetalPriceService {

    private final DailyMetalPriceRepository priceRepository;
    private final MetalRepository metalRepository;

    public DailyMetalPriceService(
            DailyMetalPriceRepository priceRepository,
            MetalRepository metalRepository) {

        this.priceRepository = priceRepository;
        this.metalRepository = metalRepository;
    }

    public DailyMetalPriceResponse createPrice(
            DailyMetalPriceCreateRequest request) {

        Metal metal = metalRepository.findById(request.metalId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Metal not found with id: " + request.metalId()
                ));

        if (priceRepository.findByMetalIdAndPriceDate(
                request.metalId(),
                request.priceDate()
        ).isPresent()) {

            throw new IllegalArgumentException(
                    "Price already exists for metal "
                            + request.metalId()
                            + " on "
                            + request.priceDate()
            );
        }

        DailyMetalPrice price = new DailyMetalPrice(
                metal,
                request.priceDate(),
                request.pricePerGram()
        );

        DailyMetalPrice savedPrice =
                priceRepository.save(price);

        return DailyMetalPriceResponse.from(savedPrice);
    }

    @Transactional(readOnly = true)
    public DailyMetalPriceResponse getPriceById(Long id) {

        DailyMetalPrice price = priceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Daily metal price not found with id: " + id
                ));

        return DailyMetalPriceResponse.from(price);
    }

    @Transactional(readOnly = true)
    public List<DailyMetalPriceResponse> getPricesByMetal(
            Long metalId) {

        if (!metalRepository.existsById(metalId)) {
            throw new ResourceNotFoundException(
                    "Metal not found with id: " + metalId
            );
        }

        return priceRepository
                .findByMetalIdOrderByPriceDateDesc(metalId)
                .stream()
                .map(DailyMetalPriceResponse::from)
                .toList();
    }

    public DailyMetalPriceResponse updatePrice(
            Long id,
            DailyMetalPriceUpdateRequest request) {

        DailyMetalPrice price = priceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Daily metal price not found with id: " + id
                ));

        price.updatePrice(request.pricePerGram());

        DailyMetalPrice updatedPrice =
                priceRepository.save(price);

        return DailyMetalPriceResponse.from(updatedPrice);
    }
}