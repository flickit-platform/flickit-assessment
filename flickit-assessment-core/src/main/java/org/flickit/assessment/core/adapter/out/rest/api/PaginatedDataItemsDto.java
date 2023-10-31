package org.flickit.assessment.core.adapter.out.rest.api;

import java.util.List;

public record PaginatedDataItemsDto<T>(int count, String next, String previous, List<T> items) {
}
