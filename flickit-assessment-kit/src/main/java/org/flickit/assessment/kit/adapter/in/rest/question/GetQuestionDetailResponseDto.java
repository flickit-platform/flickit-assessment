package org.flickit.assessment.kit.adapter.in.rest.question;

import org.flickit.assessment.kit.application.port.in.question.GetQuestionDetailUseCase.Impact;
import org.flickit.assessment.kit.application.port.in.question.GetQuestionDetailUseCase.Option;

import java.util.List;

public record GetQuestionDetailResponseDto(List<Option> options,
                                           List<Impact> attributeImpacts) {
}
