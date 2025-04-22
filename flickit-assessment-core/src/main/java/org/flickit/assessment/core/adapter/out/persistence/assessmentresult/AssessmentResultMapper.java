package org.flickit.assessment.core.adapter.out.persistence.assessmentresult;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.flickit.assessment.common.application.domain.kit.KitLanguage;
import org.flickit.assessment.common.application.domain.kit.translation.MaturityLevelTranslation;
import org.flickit.assessment.common.util.JsonUtils;
import org.flickit.assessment.core.adapter.out.persistence.assessment.AssessmentMapper;
import org.flickit.assessment.core.adapter.out.persistence.kit.assessmentkit.AssessmentKitMapper;
import org.flickit.assessment.core.application.domain.AssessmentResult;
import org.flickit.assessment.core.application.domain.MaturityLevel;
import org.flickit.assessment.core.application.port.out.assessmentresult.CreateAssessmentResultPort;
import org.flickit.assessment.data.jpa.core.assessmentresult.AssessmentResultJpaEntity;
import org.flickit.assessment.data.jpa.kit.assessmentkit.AssessmentKitJpaEntity;
import org.flickit.assessment.data.jpa.kit.maturitylevel.MaturityLevelJpaEntity;

import java.util.ArrayList;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AssessmentResultMapper {

    public static AssessmentResultJpaEntity mapToJpaEntity(CreateAssessmentResultPort.Param param) {
        return new AssessmentResultJpaEntity(
            null,
            null,
            param.kitVersionId(),
            null,
            null,
            param.isCalculateValid(),
            param.isConfidenceValid(),
            param.langId(),
            param.lastModificationTime(),
            param.lastModificationTime(),
            param.lastModificationTime()
        );
    }

    public static AssessmentResult mapToDomainModel(AssessmentResultJpaEntity entity, MaturityLevelJpaEntity maturityLevelJpaEntity, AssessmentKitJpaEntity kitJpaEntity, KitLanguage language) {
        var translation = new MaturityLevelTranslation(null, null);
        if (language != null) {
            var translations = JsonUtils.fromJsonToMap(maturityLevelJpaEntity.getTranslations(), KitLanguage.class, MaturityLevelTranslation.class);
            translation = translations.getOrDefault(language, translation);
        }
        var kit = AssessmentKitMapper.mapToDomainModel(kitJpaEntity);
        var maturityLevel = new MaturityLevel(maturityLevelJpaEntity.getId(),
            translation.titleOrDefault(maturityLevelJpaEntity.getTitle()),
            maturityLevelJpaEntity.getIndex(),
            maturityLevelJpaEntity.getValue(),
            translation.descriptionOrDefault(maturityLevelJpaEntity.getDescription()));

        return new AssessmentResult(
            entity.getId(),
            AssessmentMapper.mapToDomainModel(entity.getAssessment(), kit, null),
            entity.getKitVersionId(),
            new ArrayList<>(),
            maturityLevel,
            entity.getConfidenceValue(),
            entity.getIsCalculateValid(),
            entity.getIsConfidenceValid(),
            KitLanguage.valueOfById(entity.getLangId()),
            entity.getLastModificationTime(),
            entity.getLastCalculationTime(),
            entity.getLastConfidenceCalculationTime()
        );
    }
}
