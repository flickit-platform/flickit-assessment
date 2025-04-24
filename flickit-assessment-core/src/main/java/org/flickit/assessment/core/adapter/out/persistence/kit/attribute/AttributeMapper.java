package org.flickit.assessment.core.adapter.out.persistence.kit.attribute;

import jakarta.annotation.Nullable;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.flickit.assessment.common.application.domain.kit.KitLanguage;
import org.flickit.assessment.common.application.domain.kit.translation.AttributeTranslation;
import org.flickit.assessment.common.util.JsonUtils;
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
            entity.getIndex(),
            entity.getTitle(),
            entity.getDescription(),
            entity.getWeight(),
            null
        );
    }

    public static Attribute mapToDomainModel(AttributeJpaEntity entity, @Nullable KitLanguage language) {
        var translation = getTranslation(entity, language);
        return new Attribute(
            entity.getId(),
            entity.getIndex(),
            translation.titleOrDefault(entity.getTitle()),
            translation.descriptionOrDefault(entity.getDescription()),
            entity.getWeight(),
            null
        );
    }

    public static Attribute mapToDomainWithQuestions(AttributeJpaEntity entity, List<Question> questions) {
        return new Attribute(
            entity.getId(),
            entity.getIndex(),
            entity.getTitle(),
            entity.getDescription(),
            entity.getWeight(),
            questions
        );
    }

    public static Attribute mapToDomainWithQuestions(AttributeJpaEntity entity, List<Question> questions, KitLanguage language) {
        var translation = getTranslation(entity, language);
        return new Attribute(
            entity.getId(),
            entity.getIndex(),
            translation.titleOrDefault(entity.getTitle()),
            translation.descriptionOrDefault(entity.getDescription()),
            entity.getWeight(),
            questions
        );
    }

    public static LoadAttributesPort.Result mapToResult(AttributeMaturityLevelSubjectView view, Integer weight) {
        return new LoadAttributesPort.Result(
            view.getAttribute().getId(),
            view.getAttribute().getTitle(),
            view.getAttribute().getDescription(),
            view.getAttribute().getIndex(),
            weight,
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

    private static AttributeTranslation getTranslation(AttributeJpaEntity entity, @Nullable KitLanguage language) {
        var translation = new AttributeTranslation(null, null);
        if (language != null) {
            var translations = JsonUtils.fromJsonToMap(entity.getTranslations(), KitLanguage.class, AttributeTranslation.class);
            translation = translations.getOrDefault(language, translation);
        }
        return translation;
    }
}
