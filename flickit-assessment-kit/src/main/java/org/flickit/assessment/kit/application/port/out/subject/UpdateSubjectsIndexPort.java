package org.flickit.assessment.kit.application.port.out.subject;

import org.flickit.assessment.kit.application.port.in.subject.UpdateSubjectsOrderUseCase.SubjectParam;

import java.util.List;

public interface UpdateSubjectsIndexPort {

    void updateIndexes(long kitVersionId, List<SubjectParam> subjectOrders);
}
