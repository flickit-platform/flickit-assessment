package org.flickit.assessment.kit.application.port.in.assessmentkit;

import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.assessment.common.application.SelfValidating;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.kit.common.ErrorMessageKey.GET_PUBLISHED_KIT_KIT_ID_NOT_NULL;

public interface GetPublishedKitUseCase {

    Result getPublishedKit(Param param);

    @Value
    @EqualsAndHashCode(callSuper = true)
    class Param extends SelfValidating<Param> {

        @NotNull(message = GET_PUBLISHED_KIT_KIT_ID_NOT_NULL)
        Long kitId;

        @NotNull(message = COMMON_CURRENT_USER_ID_NOT_NULL)
        UUID currentUserId;

        public Param(Long kitId, UUID currentUserId) {
            this.kitId = kitId;
            this.currentUserId = currentUserId;
            this.validateSelf();
        }
    }

    record Result(Long id,
                  String title,
                  String summary,
                  String about,
                  Boolean published,
                  Boolean isPrivate,
                  LocalDateTime creationTime,
                  LocalDateTime lastModificationTime,
                  Like like,
                  Integer assessmentsCount,
                  Integer subjectsCount,
                  Integer questionnairesCount,
                  long expertGroupId,
                  List<MinimalSubject> subjects,
                  List<MinimalQuestionnaire> questionnaires,
                  List<MinimalMaturityLevel> maturityLevels,
                  List<MinimalKitTag> tags) {
    }

    record Like(int count, boolean liked) {
    }

    record MinimalSubject(Long id, String title, String description, List<MinimalAttribute> attributes) {
    }

    record MinimalAttribute(Long id, String title, String description) {
    }

    record MinimalQuestionnaire(Long id, String title, String description) {
    }

    record MinimalMaturityLevel(Long id, String title, Integer value, Integer index) {
    }

    record MinimalKitTag(Long id, String title) {
    }
}
