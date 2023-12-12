package org.flickit.assessment.common.adapter.out.rest.api;

import java.util.List;

public record DataItemsDto<T>(List<T> items) {
}
