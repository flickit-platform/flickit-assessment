package org.flickit.assessment.kit.adapter.in.rest.question;

import java.util.List;

public record UpdateQuestionsOrderRequestDto(List<QuestionOrder> questionOrders) {

    public record QuestionOrder(Long questionId, Integer index) {}
}
