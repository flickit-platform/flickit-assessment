package org.flickit.assessment.kit.adapter.in.rest.assessmentkit;

import org.flickit.assessment.kit.application.port.in.assessmentkit.GetKitStatsUseCase;

import java.time.LocalDateTime;
import java.util.List;

public record GetKitStatsResponseDto(
    LocalDateTime creationTime,
    LocalDateTime lastModificationTime,
    Integer questionnairesCount,
    Integer attributesCount,
    Integer questionsCount,
    Integer maturityLevelsCount,
    Integer likes,
    Integer assessmentCounts,
    List<GetKitStatsUseCase.KitStatSubject> subjects,
    GetKitStatsUseCase.KitStatExpertGroup expertGroup
) {
}
