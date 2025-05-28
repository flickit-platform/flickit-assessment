package org.flickit.assessment.core.adapter.in.rest.assessment;

public record CalculateAssessmentResponseDto(MaturityLevelDto maturityLevel, boolean resultAffected) {

    public record MaturityLevelDto(long id, String title, int value, int index) {
    }
}
