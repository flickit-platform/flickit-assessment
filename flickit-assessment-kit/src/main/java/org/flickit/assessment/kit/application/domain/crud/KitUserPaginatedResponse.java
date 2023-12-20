package org.flickit.assessment.kit.application.domain.crud;

import lombok.NonNull;
import lombok.Value;
import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;

@Value
public class KitUserPaginatedResponse {

    /**
     * The items in the requested page, can not be null.
     */
    @NonNull
    PaginatedResponse<UserListItem> result;

    /**
     * The kit that users have access on.
     */
    @NonNull
    Kit kit;

    /**
     * The expert group that kit belongs.
     */
    @NonNull
    ExpertGroup expertGroup;

    @Override
    public String toString() {
        return "PaginateResponse[" +
            "#items=" + result.getItems().size() +
            ", kit=" + kit.title +
            ", expert group=" + expertGroup.title +
            ", page=" + result.getPage() +
            ", size=" + result.getSize() +
            ", sort='" + result.getSort() + '\'' +
            ", order='" + result.getOrder() + '\'' +
            ", total=" + result.getTotal() + ']';
    }

    public record Kit(Long id, String title) {
    }

    public record ExpertGroup(Long id, String title) {
    }

    public record UserListItem(String name, String email) {
    }

}
