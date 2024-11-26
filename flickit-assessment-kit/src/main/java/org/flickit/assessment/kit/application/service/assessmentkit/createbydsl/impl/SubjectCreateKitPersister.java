package org.flickit.assessment.kit.application.service.assessmentkit.createbydsl.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flickit.assessment.kit.application.domain.dsl.AssessmentKitDslModel;
import org.flickit.assessment.kit.application.domain.dsl.SubjectDslModel;
import org.flickit.assessment.kit.application.port.out.subject.CreateSubjectPort;
import org.flickit.assessment.kit.application.service.assessmentkit.createbydsl.CreateKitPersister;
import org.flickit.assessment.kit.application.service.assessmentkit.createbydsl.CreateKitPersisterContext;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.flickit.assessment.kit.application.service.assessmentkit.updatebydsl.UpdateKitPersisterContext.KEY_SUBJECTS;

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
    public void persist(CreateKitPersisterContext ctx, AssessmentKitDslModel dslKit, Long kitVersionId, UUID currentUserId) {
        List<SubjectDslModel> dslSubjects = dslKit.getSubjects();

        Map<String, Long> savedSubjectCodesMap = new HashMap<>();
        dslSubjects.forEach(s -> {
            Long persistedSubjectId = createSubject(s, kitVersionId, currentUserId);
            savedSubjectCodesMap.put(s.getCode(), persistedSubjectId);
        });

        ctx.put(KEY_SUBJECTS, savedSubjectCodesMap);
        log.debug("Final subjects: {}", savedSubjectCodesMap);
    }

    private Long createSubject(SubjectDslModel newSubject, Long kitVersionId, UUID currentUserId) {
        CreateSubjectPort.Param param = new CreateSubjectPort.Param(
            newSubject.getCode(),
            newSubject.getTitle(),
            newSubject.getIndex(),
            newSubject.getWeight(),
            newSubject.getDescription(),
            kitVersionId,
            currentUserId
        );

        Long persistedSubjectId = createSubjectPort.persist(param);
        log.debug("Subject[id={}, code={}] created.", persistedSubjectId, newSubject.getCode());

        return persistedSubjectId;
    }
}
