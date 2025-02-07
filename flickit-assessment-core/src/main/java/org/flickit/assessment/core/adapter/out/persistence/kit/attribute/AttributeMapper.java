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

    public static LoadAttributesPort.Result mapToResult(AttributeMaturityLevelSubjectView view, Integer customWeight) {
        return new LoadAttributesPort.Result(
            view.getAttribute().getId(),
            view.getAttribute().getTitle(),
            view.getAttribute().getDescription(),
            view.getAttribute().getIndex(),
            customWeight != null ? customWeight : view.getAttribute().getWeight(),
            view.getAttributeValue().getConfidenceValue(),
            new LoadAttributesPort.MaturityLevel(
                view.getMaturityLevel().getId(),
                view.getMaturityLevel().getTitle(),
                view.getMaturityLevel().getDescription(),
                view.getMaturityLevel().getIndex(),
                view.getMaturityLevel().getValue()
            ),
            new LoadAttributesPort.Subject(
                view.getSubject().getId(),
                view.getSubject().getTitle()
            )
        );
    }
}
