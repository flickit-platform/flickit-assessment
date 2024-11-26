package org.flickit.assessment.advice.adapter.out.persistence.assessmentresult;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.advice.application.domain.AssessmentResult;
import org.flickit.assessment.advice.application.port.out.assessmentresult.LoadAssessmentResultPort;
import org.flickit.assessment.common.application.domain.ID;
import org.flickit.assessment.data.jpa.core.assessmentresult.AssessmentResultJpaRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component("adviceAssessmentResultPersistenceJpaAdapter")
@RequiredArgsConstructor
public class AssessmentResultPersistenceJpaAdapter implements LoadAssessmentResultPort {

    private final AssessmentResultJpaRepository repository;

    @Override
    public Optional<AssessmentResult> loadByAssessmentId(ID assessmentId) {
        var entity = repository.findFirstByAssessment_IdOrderByLastModificationTimeDesc(ID.fromDomain(assessmentId));
        return entity.map(AssessmentResultMapper::mapToDomain);
    }
}
