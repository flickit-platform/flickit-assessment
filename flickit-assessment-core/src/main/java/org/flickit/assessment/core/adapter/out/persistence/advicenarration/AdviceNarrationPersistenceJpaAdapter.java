package org.flickit.assessment.core.adapter.out.persistence.advicenarration;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.domain.AdviceNarration;
import org.flickit.assessment.core.application.port.out.advicenarration.CreateAdviceNarrationPort;
import org.flickit.assessment.core.application.port.out.advicenarration.LoadAdviceNarrationPort;
import org.flickit.assessment.core.application.port.out.advicenarration.UpdateAdviceNarrationPort;
import org.flickit.assessment.data.jpa.advice.advicenarration.AdviceNarrationJpaRepository;
import org.flickit.assessment.data.jpa.core.assessmentresult.AssessmentResultJpaRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

import static org.flickit.assessment.core.common.ErrorMessageKey.APPROVE_ADVICE_NARRATION_ASSESSMENT_RESULT_NOT_FOUND;

@Component("coreAdviceNarrationPersistenceJpaAdapter")
@RequiredArgsConstructor
public class AdviceNarrationPersistenceJpaAdapter implements
    LoadAdviceNarrationPort,
    CreateAdviceNarrationPort,
    UpdateAdviceNarrationPort {

    private final AdviceNarrationJpaRepository repository;
    private final AssessmentResultJpaRepository assessmentResultRepository;

    @Override
    public String loadNarration(UUID assessmentResultId) {
        var narration = repository.findByAssessmentResultId(assessmentResultId).orElse(null);

        if (narration == null || (narration.getAiNarration() == null && narration.getAssessorNarration() == null))
            return null;
        if (narration.getAssessorNarration() == null ||
            narration.getAiNarrationTime() != null && narration.getAiNarrationTime().isAfter(narration.getAssessorNarrationTime())) {
            return narration.getAiNarration();
        }
        return narration.getAssessorNarration();
    }

    @Override
    public Optional<AdviceNarration> loadByAssessmentResultId(UUID assessmentResultId) {
        return repository.findByAssessmentResultId(assessmentResultId)
            .map(AdviceNarrationMapper::mapToDomainModel);
    }

    @Override
    public void persist(CreateAdviceNarrationPort.Param param, UUID assessmentResultId) {
        repository.save(AdviceNarrationMapper.toJpaEntity(param, assessmentResultId));
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
    public boolean existsByAssessmentResultId(UUID assessmentResultId) {
        return repository.existsByAssessmentResultId(assessmentResultId);
    }
}
