package org.flickit.assessment.kit.application.service.maturitylevel;

import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.kit.application.domain.KitVersion;
import org.flickit.assessment.kit.application.domain.MaturityLevel;
import org.flickit.assessment.kit.application.port.in.maturitylevel.GetKitMaturityLevelsUseCase;
import org.flickit.assessment.kit.application.port.in.maturitylevel.GetKitMaturityLevelsUseCase.Param;
import org.flickit.assessment.kit.application.port.out.expertgroupaccess.CheckExpertGroupAccessPort;
import org.flickit.assessment.kit.application.port.out.kitversion.LoadKitVersionPort;
import org.flickit.assessment.kit.application.port.out.maturitylevel.LoadMaturityLevelsPort;
import org.flickit.assessment.kit.test.fixture.application.AssessmentKitMother;
import org.flickit.assessment.kit.test.fixture.application.KitVersionMother;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.kit.test.fixture.application.MaturityLevelMother.allLevels;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetKitMaturityLevelsServiceTest {

    @InjectMocks
    private GetKitMaturityLevelsService service;

    @Mock
    private LoadKitVersionPort loadKitVersionPort;

    @Mock
    private CheckExpertGroupAccessPort checkExpertGroupAccessPort;

    @Mock
    private LoadMaturityLevelsPort loadMaturityLevelsPort;

    @Test
    void testGetKitMaturityLevels_CurrentUserIsNotExpertGroupMember_AccessDenied() {
        KitVersion kitVersion = KitVersionMother.createKitVersion(AssessmentKitMother.simpleKit());
        Param param = createParam(b -> b.kitVersionId(kitVersion.getId()));

        when(loadKitVersionPort.load(param.getKitVersionId())).thenReturn(kitVersion);
        when(checkExpertGroupAccessPort.checkIsMember(kitVersion.getKit().getExpertGroupId(), param.getCurrentUserId())).thenReturn(false);

        var throwable = assertThrows(AccessDeniedException.class, () -> service.getKitMaturityLevels(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());

        verifyNoInteractions(loadMaturityLevelsPort);
    }

    @Test
    void testGetKitMaturityLevels_ValidParam_ReturnResult() {
        KitVersion kitVersion = KitVersionMother.createKitVersion(AssessmentKitMother.simpleKit());
        Param param = createParam(b -> b.kitVersionId(kitVersion.getId()));
        List<MaturityLevel> maturityLevels = allLevels();

        var paginatedResponse = new PaginatedResponse<>(maturityLevels, param.getPage(),
            param.getSize(), "index", "asc", maturityLevels.size());

        when(loadKitVersionPort.load(param.getKitVersionId())).thenReturn(kitVersion);
        when(checkExpertGroupAccessPort.checkIsMember(kitVersion.getKit().getExpertGroupId(), param.getCurrentUserId())).thenReturn(true);
        when(loadMaturityLevelsPort.loadByKitVersionId(param.getKitVersionId(), param.getSize(), param.getPage()))
            .thenReturn(paginatedResponse);

        var result = service.getKitMaturityLevels(param);

        var resultItems = result.getItems();
        assertEquals(maturityLevels.size(), resultItems.size());
        assertEquals(paginatedResponse.getPage(), result.getPage());
        assertEquals(paginatedResponse.getSize(), result.getSize());
        assertEquals(paginatedResponse.getSort(), result.getSort());
        assertEquals(paginatedResponse.getOrder(), result.getOrder());
        assertEquals(paginatedResponse.getTotal(), result.getTotal());

        var item = resultItems.get(1);
        var maturityLevel = maturityLevels.get(1);
        assertEquals(maturityLevel.getId(), item.id());
        assertEquals(maturityLevel.getTitle(), item.title());
        assertEquals(maturityLevel.getDescription(), item.description());
        assertEquals(maturityLevel.getIndex(), item.index());
        assertEquals(maturityLevel.getValue(), item.value());
    }

    private GetKitMaturityLevelsUseCase.Param createParam(Consumer<GetKitMaturityLevelsUseCase.Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        return paramBuilder.build();
    }

    private Param.ParamBuilder paramBuilder() {
        return Param.builder()
            .kitVersionId(1L)
            .size(10)
            .page(2)
            .currentUserId(UUID.randomUUID());
    }
}
