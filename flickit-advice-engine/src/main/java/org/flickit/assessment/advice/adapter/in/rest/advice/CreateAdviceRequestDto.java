package org.flickit.assessment.advice.adapter.in.rest.advice;

import org.flickit.assessment.advice.application.port.in.CreateAdviceUseCase.AttributeLevelTarget;

import java.util.List;

public record CreateAdviceRequestDto(List<AttributeLevelTarget> attributeLevelTargets) {
}
