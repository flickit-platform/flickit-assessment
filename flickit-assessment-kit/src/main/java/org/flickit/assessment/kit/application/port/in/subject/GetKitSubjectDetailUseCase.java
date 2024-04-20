package org.flickit.assessment.kit.application.port.in.subject;

import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.assessment.common.application.SelfValidating;

import java.util.List;
import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.kit.common.ErrorMessageKey.GET_KIT_SUBJECT_DETAIL_KIT_ID_NOT_NULL;
import static org.flickit.assessment.kit.common.ErrorMessageKey.GET_KIT_SUBJECT_DETAIL_SUBJECT_ID_NOT_NULL;

public interface GetKitSubjectDetailUseCase {

    Result getKitSubjectDetail(Param param);

    @Value
    @EqualsAndHashCode(callSuper = true)
    class Param extends SelfValidating<Param> {

        @NotNull(message = GET_KIT_SUBJECT_DETAIL_KIT_ID_NOT_NULL)
        Long kitId;

        @NotNull(message = GET_KIT_SUBJECT_DETAIL_SUBJECT_ID_NOT_NULL)
        Long subjectId;

        @NotNull(message = COMMON_CURRENT_USER_ID_NOT_NULL)
        UUID currentUserId;

        public Param(Long kitId, Long subjectId, UUID currentUserId) {
            this.kitId = kitId;
            this.subjectId = subjectId;
            this.currentUserId = currentUserId;
            this.validateSelf();
        }
    }

    record Result(int questionsCount, String description, List<Attribute> attributes) {
    }

    record Attribute(long id, int index, String title) {
    }
}
