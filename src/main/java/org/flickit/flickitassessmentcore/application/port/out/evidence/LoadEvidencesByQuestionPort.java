package org.flickit.flickitassessmentcore.application.port.out.evidence;

import org.flickit.flickitassessmentcore.domain.Evidence;

import java.util.List;

public interface LoadEvidencesByQuestionPort {

    Result loadEvidencesByQuestionId(Param param);

    record Param(Long questionId) {}
    record Result(List<Evidence> evidences) {}
}
