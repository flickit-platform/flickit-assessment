package org.flickit.assessment.kit.adapter.in.rest.levelcompetence;

public record CreateLevelCompetenceRequestDto(Long affectedLevelId,
                                              Long effectiveLevelId,
                                              Integer value) {
}
