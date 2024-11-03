package org.flickit.assessment.kit.application.service.levelcompetence;

import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.kit.application.domain.KitVersion;
import org.flickit.assessment.kit.application.domain.MaturityLevel;
import org.flickit.assessment.kit.application.port.in.levelcompetence.GetLevelCompetencesUseCase;
import org.flickit.assessment.kit.application.port.in.levelcompetence.GetLevelCompetencesUseCase.Param;
import org.flickit.assessment.kit.application.port.out.expertgroupaccess.CheckExpertGroupAccessPort;
import org.flickit.assessment.kit.application.port.out.kitversion.LoadKitVersionPort;
import org.flickit.assessment.kit.application.port.out.maturitylevel.LoadMaturityLevelsPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.kit.test.fixture.application.AssessmentKitMother.simpleKit;
import static org.flickit.assessment.kit.test.fixture.application.KitVersionMother.createKitVersion;
import static org.flickit.assessment.kit.test.fixture.application.MaturityLevelMother.allLevels;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetLevelCompetencesServiceTest {

    @InjectMocks
    private GetLevelCompetencesService service;

    @Mock
    private LoadKitVersionPort loadKitVersionPort;

    @Mock
    private CheckExpertGroupAccessPort checkExpertGroupAccessPort;

    @Mock
    private LoadMaturityLevelsPort loadMaturityLevelsPort;

    private final KitVersion kitVersion = createKitVersion(simpleKit());

    @Test
    void testGetLevelCompetences_CurrentUserIsNotExpertGroupMember_AccessDenied() {
        var param = createParam(GetLevelCompetencesUseCase.Param.ParamBuilder::build);

        when(loadKitVersionPort.load(param.getKitVersionId())).thenReturn(kitVersion);
        when(checkExpertGroupAccessPort.checkIsMember(kitVersion.getKit().getExpertGroupId(), param.getCurrentUserId())).thenReturn(false);

        var throwable = assertThrows(AccessDeniedException.class, () -> service.getLevelCompetences(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());

        verifyNoInteractions(loadMaturityLevelsPort);
    }

    @Test
    void testGetLevelCompetences_ValidParam_ReturnResult() {
        var param = createParam(GetLevelCompetencesUseCase.Param.ParamBuilder::build);
        List<MaturityLevel> maturityLevels = allLevels();

        when(loadKitVersionPort.load(param.getKitVersionId())).thenReturn(kitVersion);
        when(checkExpertGroupAccessPort.checkIsMember(kitVersion.getKit().getExpertGroupId(), param.getCurrentUserId())).thenReturn(true);
        when(loadMaturityLevelsPort.loadAllByKitVersionId(param.getKitVersionId()))
            .thenReturn(maturityLevels);

        var resultItems = service.getLevelCompetences(param);

        assertEquals(maturityLevels.size(), resultItems.size());

        var item = resultItems.get(1);
        var maturityLevel = maturityLevels.get(1);
        assertEquals(maturityLevel.getId(), item.id());
        assertEquals(maturityLevel.getTitle(), item.title());
        assertEquals(maturityLevel.getIndex(), item.index());
        assertEquals(maturityLevel.getCompetences().size(), item.competences().size());

        var itemCompetences = item.competences().getFirst();
        var competence = maturityLevel.getCompetences().getFirst();
        assertEquals(competence.getId(), itemCompetences.id());
        assertEquals(competence.getValue(), itemCompetences.value());
        assertEquals(competence.getEffectiveLevelId(), itemCompetences.maturityLevelId());
    }

    private GetLevelCompetencesUseCase.Param createParam(Consumer<Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        return paramBuilder.build();
    }

    private Param.ParamBuilder paramBuilder() {
        return Param.builder()
            .kitVersionId(kitVersion.getId())
            .currentUserId(UUID.randomUUID());
    }
}
