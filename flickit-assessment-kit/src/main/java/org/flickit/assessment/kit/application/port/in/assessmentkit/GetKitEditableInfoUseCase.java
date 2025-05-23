package org.flickit.assessment.kit.application.port.in.assessmentkit;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.assessment.common.application.SelfValidating;
import org.flickit.assessment.common.application.domain.kit.KitLanguage;
import org.flickit.assessment.common.application.domain.kit.translation.KitTranslation;
import org.flickit.assessment.kit.application.domain.KitTag;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.kit.common.ErrorMessageKey.GET_KIT_EDITABLE_INFO_KIT_ID_NOT_NULL;

public interface GetKitEditableInfoUseCase {

    KitEditableInfo getKitEditableInfo(Param param);

    @Value
    @EqualsAndHashCode(callSuper = false)
    class Param extends SelfValidating<Param> {

        @NotNull(message = GET_KIT_EDITABLE_INFO_KIT_ID_NOT_NULL)
        Long kitId;

        @NotNull(message = COMMON_CURRENT_USER_ID_NOT_NULL)
        UUID currentUserId;

        @Builder
        public Param(Long kitId, UUID currentUserId) {
            this.kitId = kitId;
            this.currentUserId = currentUserId;
            this.validateSelf();
        }
    }

    record KitEditableInfo(
        Long id,
        String title,
        String summary,
        Language mainLanguage,
        Boolean published,
        Boolean isPrivate,
        Double price,
        String about,
        List<KitTag> tags,
        boolean editable,
        boolean hasActiveVersion,
        Map<KitLanguage, KitTranslation> translations,
        List<Language> languages,
        Metadata metadata) {
        public record Language(String code,
                               String title) {
        }

        @JsonInclude(JsonInclude.Include.NON_NULL)
        public record Metadata(String goal,
                               String context) {

        }
    }
}
