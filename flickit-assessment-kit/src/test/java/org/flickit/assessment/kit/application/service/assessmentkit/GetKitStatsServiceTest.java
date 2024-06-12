package org.flickit.assessment.kit.application.service.assessmentkit;

import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.kit.application.domain.AssessmentKit;
import org.flickit.assessment.kit.application.domain.ExpertGroup;
import org.flickit.assessment.kit.application.domain.Subject;
import org.flickit.assessment.kit.application.port.in.assessmentkit.GetKitStatsUseCase;
import org.flickit.assessment.kit.application.port.in.assessmentkit.GetKitStatsUseCase.Param;
import org.flickit.assessment.kit.application.port.out.assessmentkit.CountKitStatsPort;
import org.flickit.assessment.kit.application.port.out.assessmentkit.LoadAssessmentKitPort;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadKitExpertGroupPort;
import org.flickit.assessment.kit.application.port.out.expertgroupaccess.CheckExpertGroupAccessPort;
import org.flickit.assessment.kit.application.port.out.subject.LoadSubjectsPort;
import org.flickit.assessment.kit.test.fixture.application.AssessmentKitMother;
import org.flickit.assessment.kit.test.fixture.application.SubjectMother;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.kit.common.ErrorMessageKey.EXPERT_GROUP_ID_NOT_FOUND;
import static org.flickit.assessment.kit.common.ErrorMessageKey.KIT_ID_NOT_FOUND;
import static org.flickit.assessment.kit.test.fixture.application.ExpertGroupMother.createExpertGroup;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetKitStatsServiceTest {

    @InjectMocks
    private GetKitStatsService service;

    @Mock
    private CountKitStatsPort countKitStatsPort;

    @Mock
    private LoadKitExpertGroupPort loadKitExpertGroupPort;

    @Mock
    private CheckExpertGroupAccessPort checkExpertGroupAccessPort;

    @Mock
    private LoadAssessmentKitPort loadAssessmentKitPort;

    @Mock
    private LoadSubjectsPort loadSubjectsPort;

    @Test
    void testGetKitStats_KitNotFound_ErrorMessage() {
        long kitId = 1L;
        Param param = new Param(kitId, UUID.randomUUID());

        when(loadKitExpertGroupPort.loadKitExpertGroup(param.getKitId()))
            .thenThrow(new ResourceNotFoundException(KIT_ID_NOT_FOUND));

        var throwable = assertThrows(ResourceNotFoundException.class, () -> service.getKitStats(param));
        assertThat(throwable).hasMessage(KIT_ID_NOT_FOUND);
    }

    @Test
    void testGetKitStats_ValidInput_ValidResults() {
        ExpertGroup expertGroup = createExpertGroup();
        AssessmentKit assessmentKit = AssessmentKitMother.simpleKit();
        Param param = new Param(assessmentKit.getId(), UUID.randomUUID());
        List<Subject> subjects = List.of(SubjectMother.subjectWithTitle("title"));

        when(loadKitExpertGroupPort.loadKitExpertGroup(param.getKitId())).thenReturn(expertGroup);
        when(checkExpertGroupAccessPort.checkIsMember(expertGroup.getId(), param.getCurrentUserId())).thenReturn(true);

        CountKitStatsPort.Result counts = new CountKitStatsPort.Result(20, 35, 115,
            5, 3, 5);

        when(countKitStatsPort.countKitStats(assessmentKit.getId())).thenReturn(counts);
        when(loadAssessmentKitPort.load(assessmentKit.getId())).thenReturn(assessmentKit);
        when(loadSubjectsPort.loadByKitVersionId(anyLong())).thenReturn(subjects);

        GetKitStatsUseCase.Result kitStats = service.getKitStats(param);

        assertEquals(assessmentKit.getCreationTime(), kitStats.creationTime());
        assertEquals(assessmentKit.getLastModificationTime(), kitStats.lastModificationTime());
        assertEquals(counts.questionnairesCount(), kitStats.questionnairesCount());
        assertEquals(counts.attributesCount(), kitStats.attributesCount());
        assertEquals(counts.questionsCount(), kitStats.questionsCount());
        assertEquals(counts.maturityLevelsCount(), kitStats.maturityLevelsCount());
        assertEquals(counts.likes(), kitStats.likes());
        assertEquals(counts.assessmentCounts(), kitStats.assessmentCounts());
        assertEquals(subjects.size(), kitStats.subjects().size());
        assertEquals(expertGroup.getId(), kitStats.expertGroup().id());
        assertEquals(expertGroup.getTitle(), kitStats.expertGroup().title());
    }

    @Test
    void testGetKitStats_ExpertGroupNotFound_ErrorMessage() {
        ExpertGroup expertGroup = createExpertGroup();
        long kitId = 1L;
        Param param = new Param(kitId, UUID.randomUUID());

        when(loadKitExpertGroupPort.loadKitExpertGroup(param.getKitId())).thenReturn(expertGroup);
        when(checkExpertGroupAccessPort.checkIsMember(expertGroup.getId(), param.getCurrentUserId())).thenReturn(true);

        when(countKitStatsPort.countKitStats(kitId)).thenThrow(new ResourceNotFoundException(EXPERT_GROUP_ID_NOT_FOUND));

        var throwable = assertThrows(ResourceNotFoundException.class,
            () -> service.getKitStats(param));
        assertThat(throwable).hasMessage(EXPERT_GROUP_ID_NOT_FOUND);
    }

    @Test
    void testGetKitStats_WhenUserIsNotMember_ThrowsException() {
        ExpertGroup expertGroup = createExpertGroup();
        long kitId = 1L;
        Param param = new Param(kitId, UUID.randomUUID());

        when(loadKitExpertGroupPort.loadKitExpertGroup(param.getKitId())).thenReturn(expertGroup);
        when(checkExpertGroupAccessPort.checkIsMember(expertGroup.getId(), param.getCurrentUserId())).thenReturn(false);

        AccessDeniedException exception = assertThrows(AccessDeniedException.class, () -> service.getKitStats(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, exception.getMessage());
    }
}
