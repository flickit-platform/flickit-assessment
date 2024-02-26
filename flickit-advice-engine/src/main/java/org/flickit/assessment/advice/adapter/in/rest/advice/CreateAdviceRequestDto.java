package org.flickit.assessment.advice.adapter.in.rest.advice;

import org.flickit.assessment.advice.application.domain.AttributeLevelTarget;
import org.flickit.assessment.advice.application.port.in.advice.CreateAdviceUseCase.AdviceQuestion;

import java.util.List;

public record CreateAdviceRequestDto(List<AttributeLevelTarget> attributeLevelTargets,
                                     List<AdviceQuestion> adviceQuestions) {
}
