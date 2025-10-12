package org.flickit.assessment.kit.adapter.in.rest.question;

import org.flickit.assessment.common.application.domain.kit.KitLanguage;
import org.flickit.assessment.common.application.domain.kit.translation.QuestionTranslation;
import org.flickit.assessment.kit.application.port.in.question.GetKitQuestionDetailUseCase.Impact;
import org.flickit.assessment.kit.application.port.in.question.GetKitQuestionDetailUseCase.Option;
import org.flickit.assessment.kit.application.port.in.question.GetKitQuestionDetailUseCase.QuestionDetailMeasure;

import java.util.List;
import java.util.Map;

import static org.flickit.assessment.kit.application.port.in.question.GetKitQuestionDetailUseCase.*;

public record GetKitQuestionDetailResponseDto(String hint,
                                              List<Option> options,
                                              List<Impact> attributeImpacts,
                                              QuestionDetailAnswerRange answerRange,
                                              QuestionDetailMeasure measure,
                                              Map<KitLanguage, QuestionTranslation> translations) {
}
