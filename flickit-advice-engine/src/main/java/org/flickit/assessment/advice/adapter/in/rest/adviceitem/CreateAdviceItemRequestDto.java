package org.flickit.assessment.advice.adapter.in.rest.adviceitem;

public record CreateAdviceItemRequestDto(String title,
                                         String description,
                                         String cost,
                                         String priority,
                                         String impact) {
}
