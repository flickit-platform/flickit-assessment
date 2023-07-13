package org.flickit.flickitassessmentcore.application.port.out.subjectvalue;

import org.flickit.flickitassessmentcore.domain.SubjectValue;

import java.util.List;
import java.util.UUID;

public interface LoadSubjectValueByResultPort {

    Result loadSubjectValueByResultId(Param param);

    record Param(UUID resultId) {
    }

    record Result(List<SubjectValue> subjectValues) {
    }
}
