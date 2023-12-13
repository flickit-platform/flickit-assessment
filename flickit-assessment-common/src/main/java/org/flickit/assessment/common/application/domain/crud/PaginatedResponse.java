package org.flickit.assessment.common.application.domain.crud;

import lombok.NonNull;
import lombok.Value;

import java.util.List;

@Value
public class PaginatedResponse<T> {

    /**
     * The items in the requested page, can not be null.
     */
    @NonNull
    List<T> items;

    /**
     * The requested page number.
     */
    int page;

    /**
     * The requested page-size.
     */
    int size;

    /**
     * The sorted property, which can be determined by request or default sorting of the queried resource.
     */
    String sort;

    /**
     * The order of query, which can be determined by request or default order of the queried resource.
     */
    String order;

    /**
     * The total size of query, -1 means not provided, default: -1.
     */
    int total;

    @Override
    public String toString() {
        return "PaginateResponse[" +
            "#items=" + items.size() +
            ", page=" + page +
            ", size=" + size +
            ", sort='" + sort + '\'' +
            ", order='" + order + '\'' +
            ", total=" + total + ']';
    }
}
