package org.flickit.assessment.core.adapter.in.rest.adviceitem;

import java.util.UUID;

public record CreateAdviceItemRequestDto(UUID assessmentId,
                                         String title,
                                         String description,
                                         String cost,
                                         String priority,
                                         String impact) {
}
