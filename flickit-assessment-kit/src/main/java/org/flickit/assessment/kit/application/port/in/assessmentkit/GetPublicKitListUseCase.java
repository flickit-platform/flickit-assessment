package org.flickit.assessment.kit.application.port.in.assessmentkit;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.assessment.common.application.SelfValidating;
import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.common.application.domain.kit.KitLanguage;
import org.flickit.assessment.common.validation.EnumValue;

import java.util.List;
import java.util.Set;

import static org.flickit.assessment.kit.common.ErrorMessageKey.*;

public interface GetPublicKitListUseCase {

    PaginatedResponse<KitListItem> getPublicKitList(Param param);

    @Value
    @EqualsAndHashCode(callSuper = true)
    class Param extends SelfValidating<Param> {

        @EnumValue(enumClass = KitLanguage.class, message = GET_PUBLIC_KIT_LIST_LANGS_INVALID)
        Set<String> langs;

        @Min(value = 0, message = GET_PUBLIC_KIT_LIST_PAGE_MIN)
        int page;

        @Min(value = 1, message = GET_PUBLIC_KIT_LIST_SIZE_MIN)
        @Max(value = 100, message = GET_PUBLIC_KIT_LIST_SIZE_MAX)
        int size;

        @Builder
        public Param(Set<String> langs, int page, int size) {
            this.langs = langs;
            this.page = page;
            this.size = size;
            this.validateSelf();
        }
    }

    record KitListItem(
        long id,
        String title,
        String summary,
        int likes,
        int assessmentsCount,
        ExpertGroup expertGroup,
        List<String> languages,
        boolean isFree) {

        public record ExpertGroup(
            long id,
            String title,
            String picture) {
        }
    }
}
