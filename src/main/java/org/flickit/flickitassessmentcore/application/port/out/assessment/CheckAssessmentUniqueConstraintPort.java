package org.flickit.flickitassessmentcore.application.port.out.assessment;

public interface CheckAssessmentUniqueConstraintPort {
    boolean checkCodeAndSpaceIdUniqueConstraint(String code, Long spaceId);

    boolean checkTitleAndSpaceIdUniqueConstraint(String title, Long spaceId);
}
