package org.flickit.assessment.kit.adapter.in.rest.questionnaire;

import java.util.List;

public record UpdateQuestionnaireOrdersRequestDto(List<QuestionnaireOrderDto> orders) {
    record QuestionnaireOrderDto(Long id, Integer index) {
    }
}
