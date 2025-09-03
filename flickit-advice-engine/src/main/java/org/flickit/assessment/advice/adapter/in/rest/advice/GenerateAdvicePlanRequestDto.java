package org.flickit.assessment.advice.adapter.in.rest.advice;

import org.flickit.assessment.advice.application.domain.AttributeLevelTarget;

import java.util.List;

public record GenerateAdvicePlanRequestDto(List<AttributeLevelTarget> attributeLevelTargets) {
}
