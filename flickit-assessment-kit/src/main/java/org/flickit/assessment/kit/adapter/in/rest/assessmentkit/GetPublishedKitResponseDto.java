package org.flickit.assessment.kit.adapter.in.rest.assessmentkit;

import org.flickit.assessment.kit.application.port.in.assessmentkit.GetPublishedKitUseCase;

import java.time.LocalDateTime;
import java.util.List;

public record GetPublishedKitResponseDto(Long id,
                                         String title,
                                         String summary,
                                         String about,
                                         Boolean published,
                                         Boolean isPrivate,
                                         LocalDateTime creationTime,
                                         LocalDateTime lastModificationTime,
                                         GetPublishedKitUseCase.Like like,
                                         Integer assessmentsCount,
                                         Integer subjectsCount,
                                         Integer questionnairesCount,
                                         Long expertGroupId,
                                         List<GetPublishedKitUseCase.MinimalSubject> subjects,
                                         List<GetPublishedKitUseCase.MinimalQuestionnaire> questionnaires,
                                         List<GetPublishedKitUseCase.MinimalMaturityLevel> maturityLevels,
                                         List<GetPublishedKitUseCase.MinimalKitTag> tags) {
}
