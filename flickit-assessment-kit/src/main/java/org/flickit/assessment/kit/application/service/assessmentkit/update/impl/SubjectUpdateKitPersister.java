package org.flickit.assessment.kit.application.service.assessmentkit.update.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flickit.assessment.kit.application.domain.AssessmentKit;
import org.flickit.assessment.kit.application.domain.Subject;
import org.flickit.assessment.kit.application.domain.dsl.AssessmentKitDslModel;
import org.flickit.assessment.kit.application.domain.dsl.SubjectDslModel;
import org.flickit.assessment.kit.application.port.out.subject.UpdateSubjectPort;
import org.flickit.assessment.kit.application.service.assessmentkit.update.UpdateKitPersister;
import org.flickit.assessment.kit.application.service.assessmentkit.update.UpdateKitPersisterResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;


@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(propagation = Propagation.MANDATORY)
public class SubjectUpdateKitPersister implements UpdateKitPersister {

    private final UpdateSubjectPort updateSubjectPort;

    @Override
    public UpdateKitPersisterResult persist(AssessmentKit savedKit, AssessmentKitDslModel dslKit) {
        Map<String, Long> savedSubjectCodesMap = savedKit.getSubjects().stream().collect(Collectors.toMap(Subject::getCode, Subject::getId));
        dslKit.getSubjects().forEach(s -> {
            Long id = savedSubjectCodesMap.get(s.getCode());
            updateSubjectPort.update(toUpdateParam(id, s));
            log.debug("Subject with id [{}] is updated.", id);
        });
        return new UpdateKitPersisterResult(false);
    }

    private UpdateSubjectPort.Param toUpdateParam(long id, SubjectDslModel subject) {
        return new UpdateSubjectPort.Param(id,
            subject.getTitle(),
            subject.getIndex(),
            subject.getDescription(),
            LocalDateTime.now()
        );
    }
}
