package org.flickit.assessment.core.adapter.out.persistence.kit.subject;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.flickit.assessment.common.application.domain.kit.KitLanguage;
import org.flickit.assessment.common.application.domain.kit.translation.SubjectTranslation;
import org.flickit.assessment.common.util.JsonUtils;
import org.flickit.assessment.core.application.domain.Attribute;
import org.flickit.assessment.core.application.domain.Subject;
import org.flickit.assessment.data.jpa.kit.subject.SubjectJpaEntity;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SubjectMapper {

    public static Subject mapToDomainModel(SubjectJpaEntity entity, List<Attribute> attributes, KitLanguage language) {
        var translation = new SubjectTranslation(null, null);
        if (language != null) {
            var translations = JsonUtils.fromJsonToMap(entity.getTranslations(), KitLanguage.class, SubjectTranslation.class);
            translation = translations.getOrDefault(language, translation);
        }

        return new Subject(
            entity.getId(),
            entity.getIndex(),
            translation.titleOrDefault(entity.getTitle()),
            translation.descriptionOrDefault(entity.getDescription()),
            entity.getWeight(),
            attributes
        );
    }

    public static Subject mapToDomainModel(SubjectJpaEntity entity, List<Attribute> attributes) {
        return new Subject(
            entity.getId(),
            entity.getIndex(),
            entity.getTitle(),
            entity.getDescription(),
            entity.getWeight(),
            attributes
        );
    }
}
