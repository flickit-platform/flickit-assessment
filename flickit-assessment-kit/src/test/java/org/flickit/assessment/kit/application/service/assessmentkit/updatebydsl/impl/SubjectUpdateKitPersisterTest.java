package org.flickit.assessment.kit.application.service.assessmentkit.updatebydsl.impl;

import org.flickit.assessment.kit.application.domain.AssessmentKit;
import org.flickit.assessment.kit.application.domain.Subject;
import org.flickit.assessment.kit.application.domain.dsl.AssessmentKitDslModel;
import org.flickit.assessment.kit.application.domain.dsl.SubjectDslModel;
import org.flickit.assessment.kit.application.port.out.subject.UpdateSubjectByDslPort;
import org.flickit.assessment.kit.application.service.assessmentkit.updatebydsl.UpdateKitPersisterContext;
import org.flickit.assessment.kit.application.service.assessmentkit.updatebydsl.UpdateKitPersisterResult;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.flickit.assessment.kit.application.service.assessmentkit.updatebydsl.UpdateKitPersisterContext.KEY_SUBJECTS;
import static org.flickit.assessment.kit.test.fixture.application.AssessmentKitMother.kitWithSubjects;
import static org.flickit.assessment.kit.test.fixture.application.SubjectMother.subjectWithTitle;
import static org.flickit.assessment.kit.test.fixture.application.dsl.SubjectDslModelMother.domainToDslModel;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SubjectUpdateKitPersisterTest {

    @InjectMocks
    private SubjectUpdateKitPersister persister;

    @Mock
    private UpdateSubjectByDslPort updateSubjectByDslPort;

    @Test
    void testOrder() {
        assertEquals(2, persister.order());
    }

    @Test
    void testPersist_SameSubjectCodesWithDifferentFields_Update() {
        Subject subjectOne = subjectWithTitle("Software");
        Subject subjectTwo = subjectWithTitle("Team");
        AssessmentKit savedKit = kitWithSubjects(List.of(subjectOne, subjectTwo));

        SubjectDslModel dslSubjectOne = domainToDslModel(subjectOne, b -> b.description("new description"));
        SubjectDslModel dslSubjectTwo = domainToDslModel(subjectTwo, b -> b.title("new title"));
        AssessmentKitDslModel dslKit = AssessmentKitDslModel.builder()
            .subjects(List.of(dslSubjectOne, dslSubjectTwo))
            .build();

        doNothing().when(updateSubjectByDslPort).update(any());

        UpdateKitPersisterContext ctx = new UpdateKitPersisterContext();
        UpdateKitPersisterResult result = persister.persist(ctx, savedKit, dslKit, UUID.randomUUID());

        ArgumentCaptor<UpdateSubjectByDslPort.Param> param = ArgumentCaptor.forClass(UpdateSubjectByDslPort.Param.class);
        verify(updateSubjectByDslPort, times(2)).update(param.capture());

        List<UpdateSubjectByDslPort.Param> paramList = param.getAllValues();
        UpdateSubjectByDslPort.Param softwareSubject = paramList.getFirst();
        UpdateSubjectByDslPort.Param teamSubject = paramList.get(1);

        assertEquals(subjectOne.getId(), softwareSubject.id());
        assertEquals(dslSubjectOne.getTitle(), softwareSubject.title());
        assertEquals(savedKit.getActiveVersionId(), softwareSubject.kitVersionId());
        assertEquals(dslSubjectOne.getDescription(), softwareSubject.description());
        assertEquals(dslSubjectOne.getIndex(), softwareSubject.index());
        assertThat(softwareSubject.lastModificationTime(), lessThanOrEqualTo(LocalDateTime.now()));

        assertEquals(subjectTwo.getId(), teamSubject.id());
        assertEquals(savedKit.getActiveVersionId(), teamSubject.kitVersionId());
        assertEquals(dslSubjectTwo.getTitle(), teamSubject.title());
        assertEquals(dslSubjectTwo.getDescription(), teamSubject.description());
        assertEquals(dslSubjectTwo.getIndex(), teamSubject.index());
        assertThat(teamSubject.lastModificationTime(), lessThanOrEqualTo(LocalDateTime.now()));

        assertFalse(result.isMajorUpdate());
        Map<String, Long> codeToIdMap = ctx.get(KEY_SUBJECTS);
        assertNotNull(codeToIdMap);
        assertEquals(2, codeToIdMap.keySet().size());
    }
}
