package org.flickit.assessment.advice.adapter.out.persistence.advicenarration;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.advice.application.domain.AdviceNarration;
import org.flickit.assessment.advice.application.port.out.advicenarration.CreateAdviceNarrationPort;
import org.flickit.assessment.advice.application.port.out.advicenarration.LoadAdviceNarrationPort;
import org.flickit.assessment.advice.application.port.out.advicenarration.UpdateAdviceNarrationPort;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.data.jpa.advice.advicenarration.AdviceNarrationJpaRepository;
import org.flickit.assessment.data.jpa.core.assessmentresult.AssessmentResultJpaRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

import static org.flickit.assessment.advice.common.ErrorMessageKey.APPROVE_ADVICE_NARRATION_ASSESSMENT_RESULT_NOT_FOUND;

@Component
@RequiredArgsConstructor
public class AdviceNarrationPersistenceJpaAdapter implements
    CreateAdviceNarrationPort,
    UpdateAdviceNarrationPort,
    LoadAdviceNarrationPort {

    private final AdviceNarrationJpaRepository repository;
    private final AssessmentResultJpaRepository assessmentResultRepository;

    @Override
    public void persist(AdviceNarration adviceNarration) {
        repository.save(AdviceNarrationMapper.toJpaEntity(adviceNarration));
    }

    @Override
    public void updateAssessorNarration(AssessorNarrationParam param) {
        repository.updateAssessorNarration(param.id(),
            param.narration(),
            param.approved(),
            param.narrationTime(),
            param.createdBy());
    }

    @Override
    public void approve(UUID assessmentId) {
        var assessmentResult = assessmentResultRepository.findFirstByAssessment_IdOrderByLastModificationTimeDesc(assessmentId)
            .orElseThrow(() -> new ResourceNotFoundException(APPROVE_ADVICE_NARRATION_ASSESSMENT_RESULT_NOT_FOUND));
        repository.approveByAssessmentResultId(assessmentResult.getId());
    }

    @Override
    public void updateAiNarration(AiNarrationParam param) {
        repository.updateAiNarration(param.id(),
            param.narration(),
            param.approved(),
            param.narrationTime());
    }

    @Override
    public Optional<AdviceNarration> loadByAssessmentResultId(UUID assessmentResultId) {
        return repository.findByAssessmentResultId(assessmentResultId)
            .map(AdviceNarrationMapper::toDomain);
    }

    @Override
    public boolean existsByAssessmentResultId(UUID assessmentResultId) {
        return repository.existsByAssessmentResultId(assessmentResultId);
    }
}
