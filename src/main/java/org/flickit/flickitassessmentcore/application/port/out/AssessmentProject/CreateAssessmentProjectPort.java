package org.flickit.flickitassessmentcore.application.port.out.AssessmentProject;

import org.flickit.flickitassessmentcore.application.port.in.AssessmentProject.CreateAssessmentProjectCommand;

import java.util.UUID;

public interface CreateAssessmentProjectPort {
    UUID persist(CreateAssessmentProjectCommand createAssessmentProjectCommand);
}
