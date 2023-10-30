package org.flickit.assessment.core.adapter.out.rest.api;

import java.util.List;

public record DataItemsDto<T>(List<T> items) {
}
