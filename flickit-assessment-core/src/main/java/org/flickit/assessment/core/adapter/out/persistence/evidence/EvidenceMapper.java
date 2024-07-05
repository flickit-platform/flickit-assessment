package org.flickit.assessment.core.adapter.out.persistence.evidence;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.flickit.assessment.core.application.domain.Evidence;
import org.flickit.assessment.core.application.domain.EvidenceType;
import org.flickit.assessment.core.application.port.in.evidence.GetEvidenceListUseCase;
import org.flickit.assessment.core.application.port.in.evidence.GetEvidenceListUseCase.EvidenceListItem;
import org.flickit.assessment.core.application.port.out.evidence.CreateEvidencePort;
import org.flickit.assessment.core.application.port.out.evidence.LoadEvidencePort.*;
import org.flickit.assessment.data.jpa.core.evidence.EvidenceJpaEntity;
import org.flickit.assessment.data.jpa.core.evidence.EvidenceWithDetailsView;
import org.flickit.assessment.data.jpa.users.user.UserJpaEntity;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EvidenceMapper {

    public static EvidenceJpaEntity mapCreateParamToJpaEntity(CreateEvidencePort.Param param) {
        return new EvidenceJpaEntity(
            null,
            param.description(),
            param.creationTime(),
            param.lastModificationTime(),
            param.createdById(),
            param.createdById(),
            param.assessmentId(),
            param.questionId(),
            param.type(),
            false
        );
    }

    public static EvidenceListItem toEvidenceListItem(EvidenceJpaEntity entity, UserJpaEntity user) {
        return new EvidenceListItem(
            entity.getId(),
            entity.getDescription(),
            entity.getType() != null ? EvidenceType.values()[entity.getType()].getTitle() : null,
            entity.getLastModificationTime(),
            new GetEvidenceListUseCase.User(user.getId(), user.getDisplayName(), user.getPicture())
        );
    }

    public static Evidence mapToDomainModel(EvidenceJpaEntity entity) {
        return new Evidence(
            entity.getId(),
            entity.getDescription(),
            entity.getCreatedBy(),
            entity.getLastModifiedBy(),
            entity.getAssessmentId(),
            entity.getQuestionId(),
            entity.getType(),
            entity.getCreationTime(),
            entity.getLastModificationTime(),
            entity.isDeleted()
        );
    }

    public static Result mapJpaWithDetailsToPortResult(EvidenceWithDetailsView entity) {
        return new Result(entity.getId(),
            entity.getDescription(),
            entity.getAssessmentId(),
            new Questionnaire(entity.getQuestionnaire().getId(), entity.getQuestionnaire().getTitle()),
            new Question(entity.getQuestion().getId(), entity.getQuestion().getTitle(), entity.getQuestion().getIndex()),
            new Answer(new AnswerOption(entity.getAnswerOption().getId(), entity.getAnswerOption().getTitle(), entity.getAnswerOption().getIndex()),
                entity.getAnswer().getConfidenceLevelId(),
                entity.getAnswer().getIsNotApplicable()),
            entity.getCreatedBy(), entity.getCreationTime(), entity.getLastModificationTime());
    }
}
