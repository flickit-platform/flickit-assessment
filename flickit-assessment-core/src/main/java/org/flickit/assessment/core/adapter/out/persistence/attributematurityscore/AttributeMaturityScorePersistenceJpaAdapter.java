package org.flickit.assessment.core.adapter.out.persistence.attributematurityscore;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.kit.KitLanguage;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.adapter.out.persistence.kit.maturitylevel.MaturityLevelMapper;
import org.flickit.assessment.core.application.port.out.attributematurityscore.LoadAttributeMaturityScoresPort;
import org.flickit.assessment.data.jpa.core.assessmentresult.AssessmentResultJpaRepository;
import org.flickit.assessment.data.jpa.core.attributematurityscore.AttributeMaturityScoreJpaRepository;
import org.flickit.assessment.data.jpa.core.attributematurityscore.AttributeMaturityScoreView;
import org.flickit.assessment.data.jpa.kit.assessmentkit.AssessmentKitJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_ASSESSMENT_KIT_NOT_FOUND;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_ASSESSMENT_RESULT_NOT_FOUND;

@Component
@RequiredArgsConstructor
public class AttributeMaturityScorePersistenceJpaAdapter implements
    LoadAttributeMaturityScoresPort {

    private final AttributeMaturityScoreJpaRepository repository;
    private final AssessmentResultJpaRepository assessmentResultRepository;
    private final AssessmentKitJpaRepository assessmentKitRepository;

    @Override
    public Map<Long, List<LoadAttributeMaturityScoresPort.MaturityLevelScore>> loadAll(UUID assessmentResultId) {
        var views = repository.findByAssessmentResultId(assessmentResultId);
        var language = resolveLanguage(assessmentResultId);

        return views.stream()
            .collect(Collectors.groupingBy(
                AttributeMaturityScoreView::getAttributeId,
                Collectors.mapping(
                    view -> new LoadAttributeMaturityScoresPort.MaturityLevelScore(
                        MaturityLevelMapper.mapToDomainModel(view.getMaturityLevel(), language),
                        view.getScore()),
                    Collectors.toList()
                )
            ));
    }

    private KitLanguage resolveLanguage(UUID assessmentResultId) {
        var assessmentResult = assessmentResultRepository.findById(assessmentResultId)
            .orElseThrow(() -> new ResourceNotFoundException(COMMON_ASSESSMENT_RESULT_NOT_FOUND));
        var kit = assessmentKitRepository.findByKitVersionId(assessmentResult.getKitVersionId())
            .orElseThrow(() -> new ResourceNotFoundException(COMMON_ASSESSMENT_KIT_NOT_FOUND));
        return Objects.equals(assessmentResult.getLangId(), kit.getLanguageId()) ? null
            : KitLanguage.valueOfById(assessmentResult.getLangId());
    }
}
