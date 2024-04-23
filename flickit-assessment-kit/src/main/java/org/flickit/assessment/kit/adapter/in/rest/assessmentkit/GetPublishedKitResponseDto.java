package org.flickit.assessment.kit.adapter.in.rest.assessmentkit;

import org.flickit.assessment.kit.application.port.in.assessmentkit.GetPublishedKitUseCase;

import java.time.LocalDateTime;
import java.util.List;

public record GetPublishedKitResponseDto(Long id,
                                         String code,
                                         String title,
                                         String summary,
                                         String about,
                                         Boolean published,
                                         Boolean isPrivate,
                                         LocalDateTime creationTime,
                                         LocalDateTime lastModificationTime,
                                         Integer likes,
                                         Integer assessmentsCount,
                                         Integer subjectsCount,
                                         Integer questionnairesCount,
                                         List<GetPublishedKitUseCase.Subject> subjects,
                                         List<GetPublishedKitUseCase.Questionnaire> questionnaires,
                                         List<GetPublishedKitUseCase.MaturityLevel> maturityLevels,
                                         List<GetPublishedKitUseCase.KitTag> tags,
                                         GetPublishedKitUseCase.ExpertGroup expertGroup) {
}
