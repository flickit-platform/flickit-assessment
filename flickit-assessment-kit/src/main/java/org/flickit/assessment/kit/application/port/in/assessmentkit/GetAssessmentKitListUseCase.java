package org.flickit.assessment.kit.application.port.in.assessmentkit;

import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;

import java.util.List;

public interface GetAssessmentKitListUseCase {

    PaginatedResponse<AssessmentKitListItem> getAssessmentKitList();

    record AssessmentKitListItem(
        Long id,
        String title,
        String summary,
        List<TagRecord> tags,
        ExpertGroupRecord expertGroup,
        Long likesNumber,
        Long numberOfAssessments,
        boolean isPrivate
    ) {}

    record TagRecord(Long id, String code, String title) {}

    record ExpertGroupRecord(Long id, String name, String picture) {}
}
