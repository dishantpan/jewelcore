package com.dishant.jewelcore.masterdata.purity.service;

import com.dishant.jewelcore.common.exception.ResourceNotFoundException;
import com.dishant.jewelcore.masterdata.purity.dto.PurityCreateRequest;
import com.dishant.jewelcore.masterdata.purity.dto.PurityResponse;
import com.dishant.jewelcore.masterdata.purity.dto.PurityUpdateRequest;
import com.dishant.jewelcore.masterdata.purity.entity.Purity;
import com.dishant.jewelcore.masterdata.purity.repository.PurityRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Objects;
import java.util.List;

@Service
@Transactional
public class PurityService {

    private final PurityRepository purityRepository;

    public PurityService(PurityRepository purityRepository) {
        this.purityRepository = purityRepository;
    }

    public PurityResponse createPurity(PurityCreateRequest request) {

        if (purityRepository.findByCode(request.code()).isPresent()) {
            throw new IllegalArgumentException(
                    "Purity code already exists: " + request.code()
            );
        }

        Purity purity = new Purity(
                request.name(),
                request.code(),
                request.percentage()
        );

        Purity savedPurity = purityRepository.save(purity);

        return PurityResponse.from(savedPurity);
    }

    @Transactional(readOnly = true)
    public List<PurityResponse> getAllPurities() {

        return purityRepository.findAll()
                .stream()
                .map(PurityResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public PurityResponse getPurityById(Long id) {

        Purity purity = purityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Purity not found with id: " + id
                ));

        return PurityResponse.from(purity);
    }

    public PurityResponse updatePurity(
            Long id,
            PurityUpdateRequest request) {

        Purity purity = purityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Purity not found with id: " + id
                ));

        purityRepository.findByCode(request.code())
                .filter(existingPurity ->
                        !Objects.equals(existingPurity.getId(), id))
                .ifPresent(existingPurity -> {
                    throw new IllegalArgumentException(
                            "Purity code already exists: "
                                    + request.code()
                    );
                });

        purity.updateDetails(
                request.name(),
                request.code(),
                request.percentage()
        );

        Purity updatedPurity = purityRepository.save(purity);

        return PurityResponse.from(updatedPurity);
    }

    public void deactivatePurity(Long id) {

        Purity purity = purityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Purity not found with id: " + id
                ));

        if (!purity.isActive()) {
            throw new IllegalStateException(
                    "Purity is already inactive: " + id
            );
        }

        purity.deactivate();

        purityRepository.save(purity);
    }
}