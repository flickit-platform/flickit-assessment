package org.flickit.assessment.advice.adapter.in.rest.adviceitem;

import java.util.UUID;

public record CreateAdviceItemRequestDto(String title,
                                         UUID assessmentResultId,
                                         String description,
                                         String cost,
                                         String priority,
                                         String impact) {
}
