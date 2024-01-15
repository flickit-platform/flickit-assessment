package org.flickit.assessment.kit.application.service.assessmentkit.create.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flickit.assessment.kit.application.domain.Subject;
import org.flickit.assessment.kit.application.domain.dsl.AssessmentKitDslModel;
import org.flickit.assessment.kit.application.domain.dsl.SubjectDslModel;
import org.flickit.assessment.kit.application.port.out.subject.CreateSubjectPort;
import org.flickit.assessment.kit.application.service.assessmentkit.create.CreateKitPersister;
import org.flickit.assessment.kit.application.service.assessmentkit.create.CreateKitPersisterContext;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.flickit.assessment.kit.application.service.assessmentkit.update.UpdateKitPersisterContext.KEY_SUBJECTS;

@Slf4j
@Service
@RequiredArgsConstructor
public class SubjectCreateKitPersister implements CreateKitPersister {

    private final CreateSubjectPort createSubjectPort;

    @Override
    public int order() {
        return 2;
    }

    @Override
    public void persist(CreateKitPersisterContext ctx, AssessmentKitDslModel dslKit, Long kitId, UUID currentUserId) {
        List<SubjectDslModel> dslSubjects = dslKit.getSubjects();

        Map<String, Subject> savedSubjectCodesMap = new HashMap<>();
        dslSubjects.forEach(s -> {
            Subject createdSubject = createSubject(s, kitId, currentUserId);
            savedSubjectCodesMap.put(createdSubject.getCode(), createdSubject);
        });

        ctx.put(KEY_SUBJECTS, savedSubjectCodesMap);
        log.debug("Final subjects: {}", savedSubjectCodesMap);
    }

    private Subject createSubject(SubjectDslModel newSubject, Long kitId, UUID currentUserId) {
        CreateSubjectPort.Param param = new CreateSubjectPort.Param(
            newSubject.getCode(),
            newSubject.getTitle(),
            newSubject.getIndex(),
            newSubject.getWeight(),
            newSubject.getDescription(),
            kitId,
            currentUserId
        );

        Long persistedSubjectId = createSubjectPort.persist(param);
        log.debug("Subject[id={}, code={}] created.", persistedSubjectId, newSubject.getCode());

        return new Subject(
            persistedSubjectId,
            newSubject.getCode(),
            newSubject.getTitle(),
            newSubject.getIndex(),
            newSubject.getDescription(),
            LocalDateTime.now(),
            LocalDateTime.now()
        );
    }
}
