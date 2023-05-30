package org.flickit.flickitassessmentcore.adapter.out.persistence.AssessmentColor;

import lombok.RequiredArgsConstructor;
import org.flickit.flickitassessmentcore.adapter.out.persistence.entity.AssessmentColorEntity;
import org.flickit.flickitassessmentcore.application.port.in.Assessment.AssessmentColorDto;
import org.flickit.flickitassessmentcore.application.port.out.AssessmentColor.LoadAssessmentColorByIdPort;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AssessmentColorPersistence
    implements LoadAssessmentColorByIdPort {
    private final AssessmentColorJpaRepository assessmentColorJpaRepository;
    private final AssessmentColorMapper mapper;

    @Override
    public AssessmentColorDto loadById(Long id) {
        AssessmentColorEntity assessmentColorEntity = assessmentColorJpaRepository.findById(id).orElse(null);
        if (assessmentColorEntity == null)
            return null;
        return mapper.mapJpaEntityToColorDto(assessmentColorEntity);
    }
}
