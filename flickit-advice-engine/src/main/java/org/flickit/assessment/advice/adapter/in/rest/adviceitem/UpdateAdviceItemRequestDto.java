package org.flickit.assessment.advice.adapter.in.rest.adviceitem;

public record UpdateAdviceItemRequestDto(String title,
                                         String description,
                                         String cost,
                                         String priority,
                                         String impact) {
}
