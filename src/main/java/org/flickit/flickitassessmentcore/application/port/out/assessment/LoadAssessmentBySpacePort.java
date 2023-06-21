package org.flickit.flickitassessmentcore.application.port.out.assessment;

import org.flickit.flickitassessmentcore.domain.Assessment;

import java.util.List;

public interface LoadAssessmentBySpacePort {

    public List<Assessment> loadAssessmentBySpaceId(Long spaceId);
}
