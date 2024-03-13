package org.flickit.assessment.kit.adapter.in.rest.assessmentkit;

import org.flickit.assessment.kit.application.port.in.assessmentkit.EditKitInfoUseCase;

import java.util.List;

public record EditKitInfoResponseDto(String title,
                                     String summary,
                                     Boolean isActive,
                                     Boolean isPrivate,
                                     Double price,
                                     String about,
                                     List<EditKitInfoUseCase.EditKitInfoTag> tags) {
}


