package org.flickit.assessment.kit.application.service.assessmentkit.create.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flickit.assessment.kit.application.domain.Subject;
import org.flickit.assessment.kit.application.domain.dsl.AssessmentKitDslModel;
import org.flickit.assessment.kit.application.domain.dsl.BaseDslModel;
import org.flickit.assessment.kit.application.domain.dsl.SubjectDslModel;
import org.flickit.assessment.kit.application.port.out.subject.CreateSubjectPort;
import org.flickit.assessment.kit.application.service.assessmentkit.create.CreateKitPersister;
import org.flickit.assessment.kit.application.service.assessmentkit.create.CreateKitPersisterContext;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toMap;
import static org.flickit.assessment.kit.application.service.assessmentkit.update.UpdateKitPersisterContext.KEY_SUBJECTS;

@Slf4j
@Service
@RequiredArgsConstructor
public class SubjectCreateKitPersister implements CreateKitPersister {

    private final CreateSubjectPort createSubjectPort; // TODO: implement

    @Override
    public int order() {
        return 2;
    }

    @Override
    public void persist(CreateKitPersisterContext ctx, AssessmentKitDslModel dslKit, Long kitId) {
        List<SubjectDslModel> dslSubjects = dslKit.getSubjects();
        Map<String, SubjectDslModel> subjectDslCodesMap = dslSubjects.stream().collect(toMap(BaseDslModel::getCode, i -> i));
        Map<String, Subject> savedSubjectCodesMap = new HashMap<>();
        dslSubjects.forEach(s -> {
            Subject createdSubject = createSubject(subjectDslCodesMap.get(s.getCode()), kitId);
            savedSubjectCodesMap.put(createdSubject.getCode(), createdSubject);
        });

        ctx.put(KEY_SUBJECTS, savedSubjectCodesMap);
        log.debug("Final subjects: {}", savedSubjectCodesMap);
    }

    private Subject createSubject(SubjectDslModel newSubject, Long kitId) {
        Subject newDomainSubject = new Subject(
            null,
            newSubject.getCode(),
            newSubject.getTitle(),
            newSubject.getIndex(),
            newSubject.getDescription(),
            LocalDateTime.now(),
            LocalDateTime.now()
        );

        Long persistedSubjectId = createSubjectPort.persist(newDomainSubject, kitId);
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
