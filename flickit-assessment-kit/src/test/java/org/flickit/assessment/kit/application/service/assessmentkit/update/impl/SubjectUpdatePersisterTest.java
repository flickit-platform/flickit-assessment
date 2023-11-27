package org.flickit.assessment.kit.application.service.assessmentkit.update.impl;

import lombok.SneakyThrows;
import org.flickit.assessment.kit.application.domain.AssessmentKit;
import org.flickit.assessment.kit.application.domain.dsl.AssessmentKitDslModel;
import org.flickit.assessment.kit.application.port.out.subject.UpdateSubjectsPort;
import org.flickit.assessment.kit.application.service.DslTranslator;
import org.flickit.assessment.kit.application.service.assessmentkit.update.UpdateKitPersisterResult;
import org.flickit.assessment.kit.test.fixture.application.AssessmentKitMother;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SubjectUpdatePersisterTest {

    public static final String FILE = "src/test/resources/dsl.json";

    @InjectMocks
    private SubjectUpdateKitPersister persister;

    @Mock
    private UpdateSubjectsPort updateSubjectsPort;

    private AssessmentKitDslModel dslKit;

    @BeforeEach
    @SneakyThrows
    void init() {
        if (dslKit == null) {
            String dslContent = new String(Files.readAllBytes(Paths.get(FILE)));
            dslKit = DslTranslator.parseJson(dslContent);
        }
    }

    @Test
    void testSubjectUpdatePersister_SameSizeWithSavedAndNotChangeCodes_ValidChange() {
        Long kitId = 1L;
        AssessmentKit savedKit = AssessmentKitMother.kitWithTwoSubject(kitId);

        doNothing().when(updateSubjectsPort).updateSubject(any());

        UpdateKitPersisterResult result = persister.persist(savedKit, dslKit);

        ArgumentCaptor<UpdateSubjectsPort.Param> param = ArgumentCaptor.forClass(UpdateSubjectsPort.Param.class);
        verify(updateSubjectsPort, times(2)).updateSubject(param.capture());

        List<UpdateSubjectsPort.Param> paramList = param.getAllValues();
        UpdateSubjectsPort.Param softwareSubject = paramList.get(0);
        UpdateSubjectsPort.Param teamSubject = paramList.get(1);

        assertEquals(1L, softwareSubject.kitId());
        assertEquals("Software", softwareSubject.code());
        assertEquals("Software title", softwareSubject.Title());
        assertEquals("Description for Software", softwareSubject.Description());
        assertEquals(1, softwareSubject.Index());

        assertEquals(1L, teamSubject.kitId());
        assertEquals("Team", teamSubject.code());
        assertEquals("Team title", teamSubject.Title());
        assertEquals("Description for Team", teamSubject.Description());
        assertEquals(2, teamSubject.Index());

        assertFalse(result.shouldInvalidateCalcResult());
    }
}
