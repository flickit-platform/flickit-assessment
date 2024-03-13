package org.flickit.assessment.kit.adapter.in.rest.assessmentkit;

import org.flickit.assessment.kit.application.port.in.assessmentkit.GetKitEditableInfoUseCase;

import java.util.List;

public record GetKitEditableInfoResponseDto(Long id,
                                            String title,
                                            String summary,
                                            Boolean isActive,
                                            Double price,
                                            String about,
                                            List<GetKitEditableInfoUseCase.KitEditableInfoTag> tags) {
}
