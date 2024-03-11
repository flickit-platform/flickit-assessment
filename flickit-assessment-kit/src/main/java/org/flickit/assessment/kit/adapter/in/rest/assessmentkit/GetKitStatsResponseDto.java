package org.flickit.assessment.kit.adapter.in.rest.assessmentkit;

import org.flickit.assessment.kit.application.port.in.assessmentkit.GetKitStatsUseCase;

import java.time.LocalDateTime;

public record GetKitStatsResponseDto(
    LocalDateTime creationTime,
    LocalDateTime lastUpdateTime,
    Long questionnairesCount,
    Long attributesCount,
    Long questionsCount,
    Long maturityLevelsCount,
    Long likes,
    Long assessmentCounts,
    GetKitStatsUseCase.KitStatSubject subject,
    GetKitStatsUseCase.KitStatExpertGroup expertGroup
) {
}
