package org.flickit.assessment.kit.adapter.out.persistence.questionimpact;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.flickit.assessment.data.jpa.kit.maturitylevel.MaturityLevelJpaEntity;
import org.flickit.assessment.data.jpa.kit.questionimpact.QuestionImpactJpaEntity;
import org.flickit.assessment.kit.adapter.out.persistence.answeroptionimpact.AnswerOptionImpactMapper;
import org.flickit.assessment.kit.adapter.out.persistence.maturitylevel.MaturityLevelMapper;
import org.flickit.assessment.kit.application.domain.QuestionImpact;
import org.flickit.assessment.kit.application.port.out.questionimpact.LoadQuestionImpactByQuestionPort;
import org.flickit.assessment.kit.application.port.out.questionimpact.LoadQuestionImpactByQuestionPort.AttributeImpact;

import java.util.List;


@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class QuestionImpactMapper {
    public static QuestionImpact mapToDomainModel(QuestionImpactJpaEntity entity) {
        return new QuestionImpact(
            entity.getId(),
            entity.getAttributeId(),
            entity.getMaturityLevel().getId(),
            entity.getWeight(),
            entity.getQuestionId(),
            entity.getCreationTime(),
            entity.getLastModificationTime(),
            entity.getCreatedBy(),
            entity.getLastModifiedBy()
        );
    }

    public static QuestionImpactJpaEntity mapToJpaEntityToPersist(QuestionImpact impact, MaturityLevelJpaEntity maturityLevelJpaEntity) {
        return new QuestionImpactJpaEntity(
            null,
            impact.getWeight(),
            impact.getQuestionId(),
            impact.getAttributeId(),
            maturityLevelJpaEntity,
            null,
            impact.getCreationTime(),
            impact.getLastModificationTime(),
            impact.getCreatedBy(),
            impact.getLastModifiedBy()
        );
    }

    public static AttributeImpact mapToQuestionDetailDomainModel(List<QuestionImpactJpaEntity> questionImpacts) {
        var affectedLevels = questionImpacts.stream().map(impact ->
                new LoadQuestionImpactByQuestionPort.AffectedLevel(
                    MaturityLevelMapper.mapToDomainModel(impact.getMaturityLevel()),
                    impact.getWeight(),
                    impact.getAnswerOptionImpacts().stream()
                        .map(AnswerOptionImpactMapper::mapToDomainModel)
                        .toList()))
            .toList();

        return new AttributeImpact(questionImpacts.get(0).getAttributeId(), affectedLevels);
    }
}
