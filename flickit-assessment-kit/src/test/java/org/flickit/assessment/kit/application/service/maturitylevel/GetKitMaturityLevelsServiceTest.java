package org.flickit.assessment.kit.application.service.maturitylevel;

import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.kit.application.domain.ExpertGroup;
import org.flickit.assessment.kit.application.domain.MaturityLevel;
import org.flickit.assessment.kit.application.port.in.maturitylevel.GetKitMaturityLevelsUseCase.Param;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadKitVersionExpertGroupPort;
import org.flickit.assessment.kit.application.port.out.expertgroupaccess.CheckExpertGroupAccessPort;
import org.flickit.assessment.kit.application.port.out.maturitylevel.LoadMaturityLevelsPort;
import org.flickit.assessment.kit.test.fixture.application.ExpertGroupMother;
import org.flickit.assessment.kit.test.fixture.application.MaturityLevelMother;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetKitMaturityLevelsServiceTest {

    @InjectMocks
    private GetKitMaturityLevelsService service;

    @Mock
    private LoadKitVersionExpertGroupPort loadKitVersionExpertGroupPort;

    @Mock
    private CheckExpertGroupAccessPort checkExpertGroupAccessPort;

    @Mock
    private LoadMaturityLevelsPort loadMaturityLevelsPort;

    @Test
    void testGetKitMaturityLevels_CurrentUserIsNotExpertGroupMember_AccessDenied() {
        Param param = new Param(12L, 10, 0, UUID.randomUUID());
        ExpertGroup expertGroup = ExpertGroupMother.createExpertGroup();

        when(loadKitVersionExpertGroupPort.loadKitVersionExpertGroup(param.getKitVersionId())).thenReturn(expertGroup);
        when(checkExpertGroupAccessPort.checkIsMember(expertGroup.getId(), param.getCurrentUserId())).thenReturn(false);

        var throwable = assertThrows(AccessDeniedException.class, () -> service.getKitMaturityLevels(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());

        verifyNoInteractions(loadMaturityLevelsPort);
    }

    @Test
    void testGetKitMaturityLevels_ValidParam_ReturnResult() {
        Param param = new Param(12L, 10, 0, UUID.randomUUID());
        ExpertGroup expertGroup = ExpertGroupMother.createExpertGroup();
        List<MaturityLevel> maturityLevels = MaturityLevelMother.allLevels();
        PaginatedResponse<MaturityLevel> paginatedResponse = new PaginatedResponse<>(maturityLevels, 0, 10, "index", "asc", maturityLevels.size());

        when(loadKitVersionExpertGroupPort.loadKitVersionExpertGroup(param.getKitVersionId())).thenReturn(expertGroup);
        when(checkExpertGroupAccessPort.checkIsMember(expertGroup.getId(), param.getCurrentUserId())).thenReturn(true);
        when(loadMaturityLevelsPort.loadByKitVersionId(param.getKitVersionId(), param.getSize(), param.getPage()))
            .thenReturn(paginatedResponse);

        var result = service.getKitMaturityLevels(param);
        var resultItems = result.getItems();
        assertEquals(maturityLevels.size(), resultItems.size());
        assertEquals(result.getPage(), paginatedResponse.getPage());
        assertEquals(result.getSize(), paginatedResponse.getSize());
        assertEquals(result.getSort(), paginatedResponse.getSort());
        assertEquals(result.getOrder(), paginatedResponse.getOrder());
        assertEquals(result.getTotal(), paginatedResponse.getTotal());

        var item = resultItems.get(1);
        var maturityLevel = maturityLevels.get(1);
        assertEquals(maturityLevel.getId(), item.id());
        assertEquals(maturityLevel.getTitle(), item.title());
        assertEquals(maturityLevel.getIndex(), item.index());
        assertEquals(maturityLevel.getValue(), item.value());

        var competence = maturityLevel.getCompetences().getFirst();
        var itemCompetence = item.competences().getFirst();
        assertEquals(itemCompetence.id(), competence.getId());
        assertEquals(itemCompetence.maturityLevelId(), competence.getEffectiveLevelId());
        assertEquals(itemCompetence.title(), competence.getEffectiveLevelTitle());
        assertEquals(itemCompetence.value(), competence.getValue());
    }
}