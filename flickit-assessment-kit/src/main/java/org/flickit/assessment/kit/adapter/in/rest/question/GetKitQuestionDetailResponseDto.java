package org.flickit.assessment.kit.adapter.in.rest.question;

import org.flickit.assessment.kit.application.port.in.question.GetKitQuestionDetailUseCase.Impact;
import org.flickit.assessment.kit.application.port.in.question.GetKitQuestionDetailUseCase.Option;

import java.util.List;

public record GetKitQuestionDetailResponseDto(String hint,
                                              List<Option> options,
                                              List<Impact> attributeImpacts) {
}
