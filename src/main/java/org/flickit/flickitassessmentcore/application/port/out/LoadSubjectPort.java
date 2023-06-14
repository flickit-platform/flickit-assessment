package org.flickit.flickitassessmentcore.application.port.out;

import org.flickit.flickitassessmentcore.domain.AssessmentSubject;

public interface LoadSubjectPort {

    AssessmentSubject loadSubject(Long subId);
}
