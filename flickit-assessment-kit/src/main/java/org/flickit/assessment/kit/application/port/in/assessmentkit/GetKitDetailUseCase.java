package org.flickit.assessment.kit.application.port.in.assessmentkit;

import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.assessment.common.application.SelfValidating;

import java.util.List;

import static org.flickit.assessment.kit.common.ErrorMessageKey.GET_KIT_DETAIL_KIT_VERSION_ID_NOT_NULL;

public interface GetKitDetailUseCase {

    Result getKitDetail(Param param);

    @Value
    @EqualsAndHashCode(callSuper = false)
    class Param extends SelfValidating<GetKitDownloadLinkUseCase.Param> {

        @NotNull(message = GET_KIT_DETAIL_KIT_VERSION_ID_NOT_NULL)
        Long kitVersionId;

        public Param(Long kitVersionId) {
            this.kitVersionId = kitVersionId;
            this.validateSelf();
        }
    }

    record Result(
        List<KitDetailMaturityLevel> maturityLevels,
        List<KitDetailSubject> subjects,
        List<KitDetailQuestionnaire> questionnaires
    ) {
    }

    record KitDetailMaturityLevel(
        long id,
        String title,
        int index,
        List<Competences> competences
    ) {
    }

    record Competences(
        String title,
        int value,
        long maturityLevelId
    ) {
    }

    record KitDetailSubject(
        long id,
        String title,
        int index
    ) {
    }

    record KitDetailQuestionnaire(
        long id,
        String title,
        int index
    ) {
    }
}
