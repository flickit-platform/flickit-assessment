package org.flickit.assessment.kit.application.port.in.assessmentkit;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.assessment.common.application.SelfValidating;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.flickit.assessment.kit.common.ErrorMessageKey.GET_PUBLISHED_KIT_KIT_ID_NOT_NULL;

public interface GetPublishedKitUseCase {

    Result getPublishedKit(Param param);

    @Value
    @EqualsAndHashCode(callSuper = true)
    class Param extends SelfValidating<Param> {

        @NotNull(message = GET_PUBLISHED_KIT_KIT_ID_NOT_NULL)
        Long kitId;

        UUID currentUserId;

        @Builder
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
                  Integer subjectsCount,
                  long expertGroupId,
                  List<MinimalSubject> subjects,
                  Metadata metadata,
                  List<Language> languages,
                  ExpertGroup expertGroup) {
        public record Language(String code,
                               String title) {
        }
    }

    record Like(int count, boolean liked) {
    }

    record MinimalSubject(Long id, String title, String description, List<MinimalAttribute> attributes) {
    }

    record MinimalAttribute(Long id, String title, String description) {
    }

    record Metadata(String goal, String context) {
    }

    record ExpertGroup(Long id, String title, String pictureLink) {
    }
}
