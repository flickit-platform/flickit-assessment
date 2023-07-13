package org.flickit.flickitassessmentcore.adapter.out.persistence.subjectvalue;

import lombok.RequiredArgsConstructor;
import org.flickit.flickitassessmentcore.adapter.out.persistence.assessmentresult.AssessmentResultJpaEntity;
import org.flickit.flickitassessmentcore.adapter.out.persistence.assessmentresult.AssessmentResultJpaRepository;
import org.flickit.flickitassessmentcore.application.port.out.subjectvalue.SaveSubjectValuePort;
import org.flickit.flickitassessmentcore.application.port.out.subjectvalue.CreateSubjectValuePort;
import org.flickit.flickitassessmentcore.application.port.out.subjectvalue.LoadSubjectValueByResultPort;
import org.flickit.flickitassessmentcore.domain.SubjectValue;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class SubjectValuePersistenceJpaAdaptor implements
    CreateSubjectValuePort,
    SaveSubjectValuePort,
    LoadSubjectValueByResultPort {

    private final SubjectValueJpaRepository repository;
    private final AssessmentResultJpaRepository assessmentResultRepository;

    @Override
    public void persistAll(List<Long> subjectIds, UUID assessmentResultId) {
        AssessmentResultJpaEntity assessmentResult = assessmentResultRepository.findById(assessmentResultId).get();

        List<SubjectValueJpaEntity> entities = subjectIds.stream().map(subjectId -> {
            SubjectValueJpaEntity subjectValue = SubjectValueMapper.mapToJpaEntity(subjectId);
            subjectValue.setAssessmentResult(assessmentResult);
            return subjectValue;
        }).toList();

        repository.saveAll(entities);
    }

    @Override
    public void saveSubjectValue(SubjectValue subjectValue) {
        repository.save(SubjectValueMapper.mapToJpaEntity(subjectValue));
    }

    @Override
    public Result loadSubjectValueByResultId(Param param) {
        return new Result(repository.findByAssessmentResultId(param.resultId()).stream()
            .map(SubjectValueMapper::mapToDomainModel)
            .toList());
    }
}
