package org.flickit.assessment.core.adapter.in.rest.adviceitem;

public record UpdateAdviceItemRequestDto(String title,
                                         String description,
                                         String cost,
                                         String priority,
                                         String impact) {
}
