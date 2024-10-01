package org.flickit.assessment.kit.application.port.out.subject;

import org.flickit.assessment.kit.application.port.in.subject.UpdateSubjectIndexUseCase.SubjectOrderParam;

import java.util.List;

public interface UpdateSubjectsIndexPort {

    void updateIndexes(long kitVersionId, List<SubjectOrderParam> subjectOrders);
}
