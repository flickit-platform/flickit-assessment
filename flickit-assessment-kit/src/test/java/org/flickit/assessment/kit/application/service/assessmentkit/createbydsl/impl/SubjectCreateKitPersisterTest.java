package org.flickit.assessment.kit.application.service.assessmentkit.createbydsl.impl;

import org.flickit.assessment.kit.application.domain.Subject;
import org.flickit.assessment.kit.application.domain.dsl.AssessmentKitDslModel;
import org.flickit.assessment.kit.application.domain.dsl.SubjectDslModel;
import org.flickit.assessment.kit.application.port.out.subject.CreateSubjectPort;
import org.flickit.assessment.kit.application.service.assessmentkit.createbydsl.CreateKitPersisterContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.flickit.assessment.kit.application.service.assessmentkit.createbydsl.CreateKitPersisterContext.KEY_SUBJECTS;
import static org.flickit.assessment.kit.test.fixture.application.SubjectMother.subjectWithTitle;
import static org.flickit.assessment.kit.test.fixture.application.dsl.SubjectDslModelMother.domainToDslModel;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SubjectCreateKitPersisterTest {

    private static final Long KIT_ID = 1L;
    private static final UUID CURRENT_USER_ID = UUID.randomUUID();
    @InjectMocks
    private SubjectCreateKitPersister persister;
    @Mock
    private CreateSubjectPort createSubjectPort;

    @Test
    void testOrder() {
        Assertions.assertEquals(2, persister.order());
    }

    @Test
    void testPersist_ValidInputs_SaveSubject() {
        Subject subjectOne = subjectWithTitle("Software");
        Subject subjectTwo = subjectWithTitle("Team");

        SubjectDslModel dslSubjectOne = domainToDslModel(subjectOne);
        SubjectDslModel dslSubjectTwo = domainToDslModel(subjectTwo);

        CreateKitPersisterContext context = new CreateKitPersisterContext();
        AssessmentKitDslModel dslModel = AssessmentKitDslModel.builder()
            .subjects(List.of(dslSubjectOne, dslSubjectTwo))
            .build();

        var subjectOneParam = new CreateSubjectPort.Param(subjectOne.getCode(), subjectOne.getTitle(), subjectOne.getIndex(), 1, subjectOne.getDescription(), KIT_ID, CURRENT_USER_ID);
        var subjectTwoParam = new CreateSubjectPort.Param(subjectTwo.getCode(), subjectTwo.getTitle(), subjectTwo.getIndex(), 1, subjectTwo.getDescription(), KIT_ID, CURRENT_USER_ID);
        when(createSubjectPort.persist(subjectOneParam)).thenReturn(subjectOne.getId());
        when(createSubjectPort.persist(subjectTwoParam)).thenReturn(subjectTwo.getId());

        persister.persist(context, dslModel, KIT_ID, CURRENT_USER_ID);

        Map<String, Long> subjects = context.get(KEY_SUBJECTS);
        assertEquals(2, subjects.size());
    }
}
