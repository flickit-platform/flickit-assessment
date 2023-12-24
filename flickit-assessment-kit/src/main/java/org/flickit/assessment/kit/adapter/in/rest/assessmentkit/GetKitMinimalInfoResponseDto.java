package org.flickit.assessment.kit.adapter.in.rest.assessmentkit;

import org.flickit.assessment.kit.application.port.in.assessmentkit.GetKitMinimalInfoUseCase.MinimalExpertGroup;

public record GetKitMinimalInfoResponseDto(Long id, String title, MinimalExpertGroup expertGroup) {
}
