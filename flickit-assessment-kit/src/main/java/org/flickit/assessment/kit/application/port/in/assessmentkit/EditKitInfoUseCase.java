package org.flickit.assessment.kit.application.port.in.assessmentkit;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.assessment.common.application.SelfValidating;

import java.util.List;
import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.kit.common.ErrorMessageKey.*;

public interface EditKitInfoUseCase {

    Result editKitInfo(Param param);

    @Value
    @EqualsAndHashCode(callSuper = false)
    class Param extends SelfValidating<Param> {

        @NotNull(message = EDIT_KIT_INFO_KIT_ID_NOT_NULL)
        Long assessmentKitId;

        @Size(min = 3, message = EDIT_KIT_INFO_TITLE_SIZE_MIN)
        @Size(max = 50, message = EDIT_KIT_INFO_TITLE_SIZE_MAX)
        String title;

        @Size(min = 3, message = EDIT_KIT_INFO_SUMMARY_SIZE_MIN)
        @Size(max = 200, message = EDIT_KIT_INFO_SUMMARY_SIZE_MAX)
        String summary;

        Boolean isActive;

        Boolean isPrivate;

        Double price;

        @Size(min = 3, message = EDIT_KIT_INFO_ABOUT_SIZE_MIN)
        @Size(max = 1000, message = EDIT_KIT_INFO_ABOUT_SIZE_MAX)
        String about;

        List<Long> tags;

        @NotNull(message = COMMON_CURRENT_USER_ID_NOT_NULL)
        UUID currentUserId;

        public Param(Long assessmentKitId, String title, String summary, Boolean isActive, Boolean isPrivate, Double price, String about, List<Long> tags, UUID currentUserId) {
            this.assessmentKitId = assessmentKitId;
            this.currentUserId = currentUserId;
            this.title = title;
            this.summary = summary;
            this.isActive = isActive;
            this.isPrivate = isPrivate;
            this.price = price;
            this.about = about;
            this.tags = tags;
            this.validateSelf();
        }
    }

    record Result(String title,
                  String summary,
                  Boolean isActive,
                  Boolean isPrivate,
                  Double price,
                  String about,
                  List<EditKitInfoTag> tags) {
    }

    record EditKitInfoTag(Long id, String title) {}
}
