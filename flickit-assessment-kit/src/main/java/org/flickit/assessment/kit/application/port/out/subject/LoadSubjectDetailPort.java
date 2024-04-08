package org.flickit.assessment.kit.application.port.out.subject;

import org.flickit.assessment.kit.application.port.in.subject.GetSubjectDetailUseCase.Result;

public interface LoadSubjectDetailPort {

    Result loadByIdAndKitId(Long subjectId, Long kitId);
}
