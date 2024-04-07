package org.flickit.assessment.kit.application.service.assessmentkit;

import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.kit.application.port.in.assessmentkit.GetKitStatsUseCase;
import org.flickit.assessment.kit.application.port.in.assessmentkit.GetKitStatsUseCase.Param;
import org.flickit.assessment.kit.application.port.out.assessmentkit.LoadKitExpertGroupPort;
import org.flickit.assessment.kit.application.port.out.assessmentkit.LoadKitStatsPort;
import org.flickit.assessment.kit.application.port.out.expertgroupaccess.CheckExpertGroupAccessPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.kit.common.ErrorMessageKey.EXPERT_GROUP_ID_NOT_FOUND;
import static org.flickit.assessment.kit.common.ErrorMessageKey.GET_KIT_STATS_KIT_ID_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetKitStatsServiceTest {

    private final static Long KIT_ID = 1L;
    private final static UUID CURRENT_USER_ID = UUID.randomUUID();
    private final static Long EXPERT_GROUP_ID = 25L;
    private final static Param param = new Param(KIT_ID, CURRENT_USER_ID);
    private final static String SUBJECT_TITLE = "subject title";
    private final static String EXPERT_GROUP_NAME = "expert group name";

    @InjectMocks
    private GetKitStatsService service;

    @Mock
    private LoadKitStatsPort loadKitStatsPort;

    @Mock
    private LoadKitExpertGroupPort loadKitExpertGroupPort;

    @Mock
    private CheckExpertGroupAccessPort checkExpertGroupAccessPort;

    @Test
    void testGetKitStats_KitNotFound_ErrorMessage() {
        when(loadKitExpertGroupPort.loadKitExpertGroupId(param.getAssessmentKitId())).thenReturn(EXPERT_GROUP_ID);
        when(checkExpertGroupAccessPort.checkIsMember(EXPERT_GROUP_ID, param.getCurrentUserId())).thenReturn(true);

        when(loadKitStatsPort.loadKitStats(KIT_ID)).thenThrow(new ResourceNotFoundException(GET_KIT_STATS_KIT_ID_NOT_FOUND));

        var throwable = assertThrows(ResourceNotFoundException.class,
            () -> service.getKitStats(param));
        assertThat(throwable).hasMessage(GET_KIT_STATS_KIT_ID_NOT_FOUND);
    }

    @Test
    void testGetKitStats_ValidInput_ValidResults() {
        when(loadKitExpertGroupPort.loadKitExpertGroupId(param.getAssessmentKitId())).thenReturn(EXPERT_GROUP_ID);
        when(checkExpertGroupAccessPort.checkIsMember(EXPERT_GROUP_ID, param.getCurrentUserId())).thenReturn(true);

        GetKitStatsUseCase.Result result = new GetKitStatsUseCase.Result(
            LocalDateTime.now(),
            LocalDateTime.now(),
            20,
            35,
            115,
            5,
            3,
            5,
            List.of(new GetKitStatsUseCase.KitStatSubject(SUBJECT_TITLE)),
            new GetKitStatsUseCase.KitStatExpertGroup(1L, EXPERT_GROUP_NAME)
        );

        when(loadKitStatsPort.loadKitStats(KIT_ID)).thenReturn(result);

        GetKitStatsUseCase.Result kitStats = service.getKitStats(param);

        assertEquals(result.creationTime(), kitStats.creationTime());
        assertEquals(result.lastUpdateTime(), kitStats.lastUpdateTime());
        assertEquals(result.questionnairesCount(), kitStats.questionnairesCount());
        assertEquals(result.attributesCount(), kitStats.attributesCount());
        assertEquals(result.questionsCount(), kitStats.questionsCount());
        assertEquals(result.maturityLevelsCount(), kitStats.maturityLevelsCount());
        assertEquals(result.likes(), kitStats.likes());
        assertEquals(result.assessmentCounts(), kitStats.assessmentCounts());
        assertEquals(result.subjects().size(), kitStats.subjects().size());
        assertEquals(result.expertGroup().id(), kitStats.expertGroup().id());
        assertEquals(result.expertGroup().name(), kitStats.expertGroup().name());
    }

    @Test
    void testGetKitStats_ExpertGroupNotFound_ErrorMessage() {
        when(loadKitExpertGroupPort.loadKitExpertGroupId(param.getAssessmentKitId())).thenReturn(EXPERT_GROUP_ID);
        when(checkExpertGroupAccessPort.checkIsMember(EXPERT_GROUP_ID, param.getCurrentUserId())).thenReturn(true);

        when(loadKitStatsPort.loadKitStats(KIT_ID)).thenThrow(new ResourceNotFoundException(EXPERT_GROUP_ID_NOT_FOUND));

        var throwable = assertThrows(ResourceNotFoundException.class,
            () -> service.getKitStats(param));
        assertThat(throwable).hasMessage(EXPERT_GROUP_ID_NOT_FOUND);
    }

    @Test
    void testGetKitStats_WhenUserIsNotMember_ThrowsException() {
        when(loadKitExpertGroupPort.loadKitExpertGroupId(param.getAssessmentKitId())).thenReturn(EXPERT_GROUP_ID);
        when(checkExpertGroupAccessPort.checkIsMember(EXPERT_GROUP_ID, param.getCurrentUserId())).thenReturn(false);

        AccessDeniedException exception = assertThrows(AccessDeniedException.class, () -> service.getKitStats(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, exception.getMessage());
    }
}
