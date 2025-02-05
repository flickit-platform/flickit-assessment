package org.flickit.assessment.core.adapter.out.persistence.kit.attribute;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.flickit.assessment.core.application.domain.Attribute;
import org.flickit.assessment.core.application.domain.Question;
import org.flickit.assessment.core.application.port.out.attribute.LoadAttributesPort;
import org.flickit.assessment.data.jpa.core.attribute.AttributeMaturityLevelSubjectView;
import org.flickit.assessment.data.jpa.kit.attribute.AttributeJpaEntity;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AttributeMapper {

    public static Attribute mapToDomainModel(AttributeJpaEntity entity) {
        return new Attribute(
            entity.getId(),
            entity.getTitle(),
            entity.getDescription(),
            entity.getWeight(),
            null
        );
    }

    public static Attribute mapToDomainModel(AttributeJpaEntity entity, List<Question> questions) {
        return new Attribute(
            entity.getId(),
            entity.getTitle(),
            entity.getDescription(),
            entity.getWeight(),
            questions
        );
    }

    public static LoadAttributesPort.Result mapToResult(AttributeMaturityLevelSubjectView attributeMaturityLevelSubjectView) {
        return new LoadAttributesPort.Result(
            attributeMaturityLevelSubjectView.getAttribute().getId(),
            attributeMaturityLevelSubjectView.getAttribute().getTitle(),
            attributeMaturityLevelSubjectView.getAttribute().getDescription(),
            attributeMaturityLevelSubjectView.getAttribute().getIndex(),
            attributeMaturityLevelSubjectView.getAttribute().getWeight(),
            attributeMaturityLevelSubjectView.getAttributeValue().getConfidenceValue(),
            new LoadAttributesPort.MaturityLevel(
                attributeMaturityLevelSubjectView.getMaturityLevel().getId(),
                attributeMaturityLevelSubjectView.getMaturityLevel().getTitle(),
                attributeMaturityLevelSubjectView.getMaturityLevel().getDescription(),
                attributeMaturityLevelSubjectView.getMaturityLevel().getIndex(),
                attributeMaturityLevelSubjectView.getMaturityLevel().getValue()
            ),
            new LoadAttributesPort.Subject(
                attributeMaturityLevelSubjectView.getSubject().getId(),
                attributeMaturityLevelSubjectView.getSubject().getTitle()
            )
        );
    }
}
