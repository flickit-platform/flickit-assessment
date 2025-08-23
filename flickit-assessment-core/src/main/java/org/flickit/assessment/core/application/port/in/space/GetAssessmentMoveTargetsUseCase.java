package org.flickit.assessment.core.application.port.in.space;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.assessment.common.application.SelfValidating;
import org.flickit.assessment.common.application.domain.space.SpaceType;

import java.util.List;
import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.core.common.ErrorMessageKey.GET_ASSESSMENT_MOVE_TARGETS_ASSESSMENT_ID_NOT_NULL;

public interface GetAssessmentMoveTargetsUseCase {

	Result getTargetSpaces(Param param);

    @Value
    @EqualsAndHashCode(callSuper = false)
    class Param extends SelfValidating<Param> {

        @NotNull(message = GET_ASSESSMENT_MOVE_TARGETS_ASSESSMENT_ID_NOT_NULL)
        UUID assessmentId;

        @NotNull(message = COMMON_CURRENT_USER_ID_NOT_NULL)
        UUID currentUserId;

        @Builder
        public Param(UUID assessmentId, UUID currentUserId) {
            this.assessmentId = assessmentId;
            this.currentUserId = currentUserId;
            this.validateSelf();
        }
    }

    record Result(List<SpaceListItem> items) {

        public record SpaceListItem(long id, String title, Type type, boolean selected, boolean isDefault) {

            public record Type(String code, String title) {

                public static Type of(SpaceType spaceType) {
                    return new Type(spaceType.getCode(), spaceType.getTitle());
                }
            }
        }
    }
}
