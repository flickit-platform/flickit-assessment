package org.flickit.flickitassessmentcore.adapter.out.persistence.assessmentresult;

import lombok.RequiredArgsConstructor;
import org.flickit.flickitassessmentcore.adapter.out.persistence.assessmentresult.exception.AssessmentResultNotFound;
import org.flickit.flickitassessmentcore.application.port.out.assessmentresult.InvalidateAssessmentResultPort;
import org.flickit.flickitassessmentcore.application.port.out.assessmentresult.LoadAssessmentResultByAssessmentPort;
import org.flickit.flickitassessmentcore.application.port.out.assessmentresult.LoadAssessmentResultPort;
import org.flickit.flickitassessmentcore.application.port.out.assessmentresult.SaveAssessmentResultPort;
import org.flickit.flickitassessmentcore.domain.AssessmentResult;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;


@Component
@RequiredArgsConstructor
public class AssessmentResultPersistenceJpaAdaptor implements
    InvalidateAssessmentResultPort,
    SaveAssessmentResultPort,
    LoadAssessmentResultPort,
    LoadAssessmentResultByAssessmentPort {

    private final AssessmentResultJpaRepository repository;

    @Override
    public void invalidateById(UUID id) {
        repository.invalidateById(id);
    }

    @Override
    public AssessmentResult saveAssessmentResult(AssessmentResult assessmentResult) {
        return AssessmentResultMapper.mapToDomainModel(repository.save(AssessmentResultMapper.mapToJpaEntity(assessmentResult)));
    }

    @Override
    public AssessmentResult loadAssessmentResult(UUID resultId) {
        return AssessmentResultMapper.mapToDomainModel(
            repository.findById(resultId).orElseThrow(
                () -> new AssessmentResultNotFound("Assessment Result with id [" + resultId + "] not found!")));
    }

    @Override
    public Set<AssessmentResult> loadAssessmentResultByAssessmentId(UUID assessmentId) {
        return repository.findByAssessmentId(assessmentId).stream()
            .map(AssessmentResultMapper::mapToDomainModel)
            .collect(Collectors.toSet());
    }
}
