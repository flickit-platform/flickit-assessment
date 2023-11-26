package org.flickit.assessment.kit.application.service.assessmentkit.update.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flickit.assessment.kit.application.domain.AssessmentKit;
import org.flickit.assessment.kit.application.domain.dsl.AssessmentKitDslModel;
import org.flickit.assessment.kit.application.domain.dsl.SubjectDslModel;
import org.flickit.assessment.kit.application.port.out.subject.UpdateSubjectsPort;
import org.flickit.assessment.kit.application.service.assessmentkit.update.UpdateKitPersister;
import org.flickit.assessment.kit.application.service.assessmentkit.update.UpdateKitPersisterResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;


@Slf4j
@Transactional(propagation = Propagation.MANDATORY)
@Service
@RequiredArgsConstructor
public class SubjectUpdateKitPersister implements UpdateKitPersister {

    private final UpdateSubjectsPort updateSubjectsPort;

    @Override
    public UpdateKitPersisterResult persist(AssessmentKit savedKit, AssessmentKitDslModel dslKit) {
        dslKit.getSubjects().forEach(s -> {
            updateSubjectsPort.updateSubject(toUpdateParam(savedKit, s));
            log.debug("Subject with code [{}] and assessment kit id [{}] is updated.", s.getCode(), savedKit.getId());
        });
        return new UpdateKitPersisterResult(false);
    }

    private UpdateSubjectsPort.Param toUpdateParam(AssessmentKit savedKit, SubjectDslModel subject) {
        return new UpdateSubjectsPort.Param(savedKit.getId(),
            subject.getCode(),
            subject.getTitle(),
            subject.getDescription(),
            subject.getIndex(),
            LocalDateTime.now()
        );
    }
}
