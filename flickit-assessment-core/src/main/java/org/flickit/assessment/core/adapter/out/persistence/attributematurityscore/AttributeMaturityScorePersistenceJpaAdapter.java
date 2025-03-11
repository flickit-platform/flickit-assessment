package org.flickit.assessment.core.adapter.out.persistence.attributematurityscore;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.core.adapter.out.persistence.kit.maturitylevel.MaturityLevelMapper;
import org.flickit.assessment.core.application.port.out.attributematurityscore.LoadAttributeMaturityScoresPort;
import org.flickit.assessment.data.jpa.core.attributematurityscore.AttributeMaturityScoreJpaRepository;
import org.flickit.assessment.data.jpa.core.attributematurityscore.AttributeMaturityScoreView;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class AttributeMaturityScorePersistenceJpaAdapter implements
    LoadAttributeMaturityScoresPort {

    private final AttributeMaturityScoreJpaRepository repository;

    @Override
    public Map<Long, List<LoadAttributeMaturityScoresPort.MaturityLevelScore>> loadAll(UUID assessmentResultId) {
        var views = repository.findByAssessmentResultId(assessmentResultId);

        return views.stream()
            .collect(Collectors.groupingBy(
                AttributeMaturityScoreView::getAttributeId,
                Collectors.mapping(
                    view -> new LoadAttributeMaturityScoresPort.MaturityLevelScore(
                        MaturityLevelMapper.mapToDomainModel(view.getMaturityLevel(), null),
                        view.getScore()),
                    Collectors.toList()
                )
            ));
    }
}
