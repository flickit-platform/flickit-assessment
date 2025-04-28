package org.flickit.assessment.kit.adapter.out.persistence.assessmentkit;

import jakarta.annotation.Nullable;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.flickit.assessment.common.application.domain.kit.KitLanguage;
import org.flickit.assessment.common.application.domain.kit.translation.KitTranslation;
import org.flickit.assessment.common.util.JsonUtils;
import org.flickit.assessment.data.jpa.kit.assessmentkit.AssessmentKitJpaEntity;
import org.flickit.assessment.data.jpa.kit.assessmentkit.KitWithDraftVersionIdView;
import org.flickit.assessment.kit.application.domain.AssessmentKit;
import org.flickit.assessment.kit.application.domain.KitMetadata;
import org.flickit.assessment.kit.application.port.out.assessmentkit.CreateAssessmentKitPort;
import org.flickit.assessment.kit.application.port.out.assessmentkit.UpdateKitInfoPort;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;

import static org.apache.commons.lang3.ObjectUtils.isNotEmpty;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AssessmentKitMapper {

    public static AssessmentKitJpaEntity toJpaEntity(CreateAssessmentKitPort.Param param) {
        return new AssessmentKitJpaEntity(
            null,
            param.code(),
            param.title(),
            param.summary(),
            param.about(),
            param.published(),
            param.isPrivate(),
            param.expertGroupId(),
            param.lang().getId(),
            null,
            null,
            LocalDateTime.now(),
            LocalDateTime.now(),
            param.createdBy(),
            param.createdBy(),
            new HashSet<>(),
            LocalDateTime.now(),
            null
        );
    }

    public static AssessmentKitJpaEntity toJpaEntity(AssessmentKitJpaEntity entity, UpdateKitInfoPort.Param param) {
        var translations = param.isRemoveTranslations() ?
            null :
            isNotEmpty(param.translations()) ? JsonUtils.toJson(param.translations()) : entity.getTranslations();

        var metadata = param.isRemoveMetadata() ?
            null :
            metadataIsNotEmpty(param.metadata()) ? JsonUtils.toJson(param.metadata()) : entity.getMetadata();

        return new AssessmentKitJpaEntity(
            entity.getId(),
            param.code() != null ? param.code() : entity.getCode(),
            param.title() != null ? param.title() : entity.getTitle(),
            param.summary() != null ? param.summary() : entity.getSummary(),
            param.about() != null ? param.about() : entity.getAbout(),
            param.published() != null ? param.published() : entity.getPublished(),
            param.isPrivate() != null ? param.isPrivate() : entity.getIsPrivate(),
            entity.getExpertGroupId(),
            param.lang() != null ? param.lang().getId() : entity.getLanguageId(),
            translations,
            metadata,
            entity.getCreationTime(),
            param.lastModificationTime(),
            entity.getCreatedBy(),
            param.currentUserId(),
            entity.getAccessGrantedUsers(),
            entity.getLastMajorModificationTime(),
            entity.getKitVersionId()
        );
    }

    private static boolean metadataIsNotEmpty(KitMetadata metadata) {
        return metadata != null && (metadata.goal() != null || metadata.context() != null);
    }

    public static AssessmentKit mapToDomainModel(AssessmentKitJpaEntity entity) {
        return new AssessmentKit(
            entity.getId(),
            entity.getCode(),
            entity.getTitle(),
            entity.getSummary(),
            entity.getAbout(),
            KitLanguage.valueOfById(entity.getLanguageId()),
            entity.getCreationTime(),
            entity.getLastModificationTime(),
            entity.getPublished(),
            entity.getIsPrivate(),
            entity.getExpertGroupId(),
            JsonUtils.fromJsonToMap(entity.getTranslations(), KitLanguage.class, KitTranslation.class),
            null,
            null,
            null,
            null,
            null,
            entity.getKitVersionId(),
            entity.getMetadata() != null
                ? JsonUtils.fromJson(entity.getMetadata(), KitMetadata.class)
                : null,
            null);
    }

    public static AssessmentKit mapToDomainModel(KitWithDraftVersionIdView view) {
        AssessmentKitJpaEntity entity = view.getKit();
        var kit = new AssessmentKit(
            entity.getId(),
            entity.getCode(),
            entity.getTitle(),
            entity.getSummary(),
            entity.getAbout(),
            KitLanguage.valueOfById(entity.getLanguageId()),
            entity.getCreationTime(),
            entity.getLastModificationTime(),
            entity.getPublished(),
            entity.getIsPrivate(),
            entity.getExpertGroupId(),
            null,
            null,
            null,
            null,
            null,
            entity.getKitVersionId());
        kit.setDraftVersionId(view.getDraftVersionId());
        return kit;
    }

    public static AssessmentKit mapToDomainModel(AssessmentKitJpaEntity entity, KitLanguage language) {
        var translationLanguage = Objects.equals(language.getId(), entity.getLanguageId()) ? null : language;
        var translation = getTranslation(entity, translationLanguage);
        return new AssessmentKit(
            entity.getId(),
            entity.getCode(),
            translation.titleOrDefault(entity.getTitle()),
            translation.summaryOrDefault(entity.getSummary()),
            translation.aboutOrDefault(entity.getAbout()),
            KitLanguage.valueOfById(entity.getLanguageId()),
            entity.getCreationTime(),
            entity.getLastModificationTime(),
            entity.getPublished(),
            entity.getIsPrivate(),
            entity.getExpertGroupId(),
            null,
            null,
            null,
            null,
            null,
            null,
            entity.getKitVersionId(),
            null,
            null);
    }

    private static KitTranslation getTranslation(AssessmentKitJpaEntity assessmentKitEntity, @Nullable KitLanguage language) {
        var translation = new KitTranslation(null, null, null, null);
        if (language != null) {
            var translations = JsonUtils.fromJsonToMap(assessmentKitEntity.getTranslations(), KitLanguage.class, KitTranslation.class);
            translation = translations.getOrDefault(language, translation);
        }
        return translation;
    }
}
