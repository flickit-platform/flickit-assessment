package org.flickit.assessment.kit.application.service.assessmentkit.update.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flickit.assessment.kit.application.domain.AssessmentKit;
import org.flickit.assessment.kit.application.domain.dsl.AssessmentKitDslModel;
import org.flickit.assessment.kit.application.domain.dsl.SubjectDslModel;
import org.flickit.assessment.kit.application.port.out.subject.UpdateSubjectPort;
import org.flickit.assessment.kit.application.service.assessmentkit.update.UpdateKitPersister;
import org.flickit.assessment.kit.application.service.assessmentkit.update.UpdateKitPersisterResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;


@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(propagation = Propagation.MANDATORY)
public class SubjectUpdateKitPersister implements UpdateKitPersister {

    private final UpdateSubjectPort updateSubjectPort;

    @Override
    public UpdateKitPersisterResult persist(AssessmentKit savedKit, AssessmentKitDslModel dslKit) {
        dslKit.getSubjects().forEach(s -> {
            updateSubjectPort.updateByCodeAndKitId(toUpdateParam(savedKit.getId(), s));
            log.debug("Subject with code [{}] and assessment kit id [{}] is updated.", s.getCode(), savedKit.getId());
        });
        return new UpdateKitPersisterResult(false);
    }

    private UpdateSubjectPort.Param toUpdateParam(long kitId, SubjectDslModel subject) {
        return new UpdateSubjectPort.Param(subject.getCode(),
            subject.getTitle(),
            subject.getIndex(),
            subject.getDescription(),
            LocalDateTime.now(),
            kitId
        );
    }
}
