package org.flickit.assessment.advice.adapter.in.rest.advice;

import org.flickit.assessment.advice.application.port.in.advice.CalculateAdviceUseCase.AttributeLevelTarget;

import java.util.List;

public record CalculateAdviceRequestDto(List<AttributeLevelTarget> attributeLevelTargets) {
}
