package org.flickit.assessment.kit.application.service.assessmentkit.update.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flickit.assessment.kit.application.domain.AssessmentKit;
import org.flickit.assessment.kit.application.domain.Subject;
import org.flickit.assessment.kit.application.domain.dsl.AssessmentKitDslModel;
import org.flickit.assessment.kit.application.domain.dsl.SubjectDslModel;
import org.flickit.assessment.kit.application.port.out.subject.UpdateSubjectPort;
import org.flickit.assessment.kit.application.service.assessmentkit.update.UpdateKitPersister;
import org.flickit.assessment.kit.application.service.assessmentkit.update.UpdateKitPersisterContext;
import org.flickit.assessment.kit.application.service.assessmentkit.update.UpdateKitPersisterResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toMap;
import static org.flickit.assessment.kit.application.service.assessmentkit.update.UpdateKitPersisterContext.KEY_SUBJECTS;


@Slf4j
@Service
@RequiredArgsConstructor
public class SubjectUpdateKitPersister implements UpdateKitPersister {

    private final UpdateSubjectPort updateSubjectPort;

    @Override
    public int order() {
        return 2;
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public UpdateKitPersisterResult persist(UpdateKitPersisterContext ctx, AssessmentKit savedKit, AssessmentKitDslModel dslKit) {
        Map<String, Subject> savedSubjectCodesMap = savedKit.getSubjects().stream().collect(toMap(Subject::getCode, i -> i));

        dslKit.getSubjects().forEach(dslSubject -> {
            Subject savedSubject = savedSubjectCodesMap.get(dslSubject.getCode());

            if (!savedSubject.getTitle().equals(dslSubject.getTitle()) ||
                savedSubject.getIndex() != dslSubject.getIndex() ||
                !savedSubject.getDescription().equals(dslSubject.getDescription())) {
                updateSubjectPort.update(toUpdateParam(savedSubject.getId(), dslSubject));
                log.debug("Subject[id={}, code={}] updated", savedSubject.getId(), savedSubject.getCode());
            }
        });

        Map<String, Long> subjectCodeToIdMap = savedSubjectCodesMap.entrySet().stream()
            .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().getId()));
        ctx.put(KEY_SUBJECTS, subjectCodeToIdMap);
        log.debug("Final subjects: {}", subjectCodeToIdMap);

        return new UpdateKitPersisterResult(false);
    }

    private UpdateSubjectPort.Param toUpdateParam(long id, SubjectDslModel dslSubject) {
        return new UpdateSubjectPort.Param(id,
            dslSubject.getTitle(),
            dslSubject.getIndex(),
            dslSubject.getDescription(),
            LocalDateTime.now()
        );
    }
}
