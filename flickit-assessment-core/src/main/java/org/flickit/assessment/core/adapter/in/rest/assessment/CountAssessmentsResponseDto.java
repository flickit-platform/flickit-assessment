package org.flickit.assessment.core.adapter.in.rest.assessment;

public record CountAssessmentsResponseDto(Integer totalCount, Integer deletedCount, Integer notDeletedCount) {
}
