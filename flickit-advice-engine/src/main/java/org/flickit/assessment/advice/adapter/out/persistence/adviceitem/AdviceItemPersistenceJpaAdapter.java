package org.flickit.assessment.advice.adapter.out.persistence.adviceitem;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.advice.application.port.out.adviceitem.CreateAdviceItemPort;
import org.flickit.assessment.advice.application.port.out.adviceitem.DeleteAdviceItemPort;
import org.flickit.assessment.advice.application.port.out.adviceitem.LoadAdviceItemPort;
import org.flickit.assessment.data.jpa.advice.adviceitem.AdviceItemJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AdviceItemPersistenceJpaAdapter implements
    CreateAdviceItemPort,
    LoadAdviceItemPort,
    DeleteAdviceItemPort {

    private final AdviceItemJpaRepository repository;

    @Override
    public void persistAll(List<CreateAdviceItemPort.Param> adviceItems, UUID assessmentResultId) {
        var entities = adviceItems.stream()
            .map(e -> AdviceItemMapper.toJpaEntity(e, assessmentResultId))
            .toList();
        repository.saveAll(entities);
    }

    @Override
    public boolean existsByAssessmentResultId(UUID assessmentResultId) {
        return repository.existsByAssessmentResultId(assessmentResultId);
    }

    @Override
    public void deleteAllAiGenerated(UUID assessmentResultId) {
        repository.deleteByAssessmentResultIdAndCreatedByIsNullAndLastModifiedByIsNull(assessmentResultId);
    }
}
