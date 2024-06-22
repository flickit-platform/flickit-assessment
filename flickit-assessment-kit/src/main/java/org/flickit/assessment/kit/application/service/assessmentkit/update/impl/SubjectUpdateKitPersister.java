package org.flickit.assessment.kit.application.service.assessmentkit.update.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flickit.assessment.kit.application.domain.AssessmentKit;
import org.flickit.assessment.kit.application.domain.Subject;
import org.flickit.assessment.kit.application.domain.dsl.AssessmentKitDslModel;
import org.flickit.assessment.kit.application.domain.dsl.SubjectDslModel;
import org.flickit.assessment.kit.application.port.out.subject.CreateSubjectPort;
import org.flickit.assessment.kit.application.port.out.subject.UpdateSubjectPort;
import org.flickit.assessment.kit.application.service.assessmentkit.update.UpdateKitPersister;
import org.flickit.assessment.kit.application.service.assessmentkit.update.UpdateKitPersisterContext;
import org.flickit.assessment.kit.application.service.assessmentkit.update.UpdateKitPersisterResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toMap;
import static org.flickit.assessment.kit.application.service.assessmentkit.update.UpdateKitPersisterContext.KEY_SUBJECTS;

@Slf4j
@Service
@RequiredArgsConstructor
public class SubjectUpdateKitPersister implements UpdateKitPersister {

    private final UpdateSubjectPort updateSubjectPort;
    private final CreateSubjectPort createSubjectPort;

    @Override
    public int order() {
        return 2;
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public UpdateKitPersisterResult persist(UpdateKitPersisterContext ctx,
                                            AssessmentKit savedKit,
                                            AssessmentKitDslModel dslKit,
                                            UUID currentUserId) {
        Map<String, Subject> savedSubjectCodesMap = savedKit.getSubjects().stream().collect(toMap(Subject::getCode, i -> i));
        Map<String, Long> addedCodeToIdMap = new HashMap<>();

        dslKit.getSubjects().forEach(dslSubject -> {
            Subject savedSubject = savedSubjectCodesMap.get(dslSubject.getCode());

            if (savedSubject == null) {
                Long persistedSubjectId = createSubjectPort.persist(toCreateParam(dslSubject, savedKit.getKitVersionId(), currentUserId));
                addedCodeToIdMap.put(dslSubject.getCode(), persistedSubjectId);
                log.debug("Subject[id={}, code={}] created", persistedSubjectId, dslSubject.getCode());
            } else if (!savedSubject.getTitle().equals(dslSubject.getTitle()) ||
                savedSubject.getIndex() != dslSubject.getIndex() ||
                !savedSubject.getDescription().equals(dslSubject.getDescription())) {
                updateSubjectPort.update(toUpdateParam(savedSubject.getId(), savedKit.getKitVersionId(), dslSubject, currentUserId));
                log.debug("Subject[id={}, code={}] updated", savedSubject.getId(), savedSubject.getCode());
            }
        });

        Map<String, Long> updatedCodeToIdMap = savedSubjectCodesMap.entrySet().stream()
            .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().getId()));
        HashMap<String, Long> subjectCodeToIdMap = new HashMap<>(addedCodeToIdMap);
        subjectCodeToIdMap.putAll(updatedCodeToIdMap);

        ctx.put(KEY_SUBJECTS, subjectCodeToIdMap);
        log.debug("Final subjects: {}", subjectCodeToIdMap);

        return new UpdateKitPersisterResult(!addedCodeToIdMap.isEmpty());
    }

    private CreateSubjectPort.Param toCreateParam(SubjectDslModel dslSubject, long kitVersionId, UUID currentUserId) {
        return new CreateSubjectPort.Param(
            dslSubject.getCode(),
            dslSubject.getTitle(),
            dslSubject.getIndex(),
            dslSubject.getWeight(),
            dslSubject.getDescription(),
            kitVersionId,
            currentUserId
        );
    }

    private UpdateSubjectPort.Param toUpdateParam(long id, long kitVersionId, SubjectDslModel dslSubject, UUID currentUserId) {
        return new UpdateSubjectPort.Param(id,
            kitVersionId,
            dslSubject.getTitle(),
            dslSubject.getIndex(),
            dslSubject.getDescription(),
            LocalDateTime.now(),
            currentUserId
        );
    }
}
