package org.flickit.assessment.kit.application.service.kitcustom;

import org.assertj.core.api.Assertions;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.kit.application.domain.AssessmentKit;
import org.flickit.assessment.kit.application.domain.KitCustom;
import org.flickit.assessment.kit.application.domain.KitCustomData;
import org.flickit.assessment.kit.application.port.in.kitcustom.GetKitCustomUseCase;
import org.flickit.assessment.kit.application.port.out.assessmentkit.LoadAssessmentKitPort;
import org.flickit.assessment.kit.application.port.out.kitcustom.LoadKitCustomPort;
import org.flickit.assessment.kit.application.port.out.kituseraccess.CheckKitUserAccessPort;
import org.flickit.assessment.kit.test.fixture.application.KitCustomMother;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.kit.test.fixture.application.AssessmentKitMother.privateKit;
import static org.flickit.assessment.kit.test.fixture.application.AssessmentKitMother.simpleKit;
import static org.flickit.assessment.kit.test.fixture.application.KitCustomMother.simpleKitCustom;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetKitCustomServiceTest {

    @InjectMocks
    private GetKitCustomService service;

    @Mock
    private LoadAssessmentKitPort loadAssessmentKitPort;

    @Mock
    private CheckKitUserAccessPort checkKitUserAccessPort;

    @Mock
    private LoadKitCustomPort loadKitCustomPort;

    @Test
    void testGetKitCustom_WhenKitIsPrivateAndCurrentUserHasNoAccessToKit_ThenThrowAccessDeniedException() {
        var param = createParam(GetKitCustomUseCase.Param.ParamBuilder::build);
        AssessmentKit kit = privateKit();
        var kitCustom = KitCustomMother.simpleKitCustom(kit.getId(), null);

        when(loadKitCustomPort.load(param.getKitCustomId())).thenReturn(kitCustom);
        when(loadAssessmentKitPort.load(kit.getId())).thenReturn(kit);
        when(checkKitUserAccessPort.hasAccess(kit.getId(), param.getCurrentUserId())).thenReturn(false);

        var accessDeniedException = assertThrows(AccessDeniedException.class, () -> service.getKitCustom(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, accessDeniedException.getMessage());
    }

    @Test
    void testGetKitCustom_WhenKitIsPrivateAndCurrentUserHasAccessToKit_ThenGetKitCustom() {
        var param = createParam(GetKitCustomUseCase.Param.ParamBuilder::build);
        AssessmentKit kit = privateKit();

        var customSubjects = List.of(
            new KitCustomData.Subject(123, 5),
            new KitCustomData.Subject(124, 2));
        var customAttributes = List.of(
            new KitCustomData.Attribute(222, 2),
            new KitCustomData.Attribute(223, 4));
        var customData = new KitCustomData(customSubjects, customAttributes);
        var kitCustom = simpleKitCustom(kit.getId(), customData);

        when(loadKitCustomPort.load(param.getKitCustomId())).thenReturn(kitCustom);
        when(loadAssessmentKitPort.load(kit.getId())).thenReturn(kit);
        when(checkKitUserAccessPort.hasAccess(kit.getId(), param.getCurrentUserId())).thenReturn(true);

        var result = service.getKitCustom(param);

        assertServiceResult(result, kitCustom);
    }

    @Test
    void testGetKitCustom_WhenKitIsPublic_ThenGetKitCustom() {
        var param = createParam(GetKitCustomUseCase.Param.ParamBuilder::build);
        AssessmentKit kit = simpleKit();

        var customSubjects = List.of(
            new KitCustomData.Subject(123, 5),
            new KitCustomData.Subject(124, 2));
        var customAttributes = List.of(
            new KitCustomData.Attribute(222, 2),
            new KitCustomData.Attribute(223, 4));
        var customData = new KitCustomData(customSubjects, customAttributes);
        var kitCustom = simpleKitCustom(kit.getId(), customData);

        when(loadKitCustomPort.load(param.getKitCustomId())).thenReturn(kitCustom);
        when(loadAssessmentKitPort.load(kit.getId())).thenReturn(kit);

        var result = service.getKitCustom(param);

        assertServiceResult(result, kitCustom);

        verifyNoInteractions(checkKitUserAccessPort);
    }

    private void assertServiceResult(GetKitCustomUseCase.Result result, KitCustom kitCustom) {
        assertNotNull(result);

        var expectedSubjects = kitCustom.getCustomData().subjects();
        var actualSubjects = result.customData().subjects();
        assertNotNull(actualSubjects);
        assertEquals(expectedSubjects.size(), actualSubjects.size());
        Assertions.assertThat(actualSubjects)
            .zipSatisfy(expectedSubjects, (actual, expected) -> {
                assertEquals(expected.id(), actual.id());
                assertEquals(expected.weight(), actual.weight());
            });

        var expectedAttributes = kitCustom.getCustomData().attributes();
        var actualAttributes = result.customData().attributes();
        assertNotNull(actualAttributes);
        assertEquals(expectedAttributes.size(), actualAttributes.size());
        Assertions.assertThat(actualAttributes)
            .zipSatisfy(expectedAttributes, (actual, expected) -> {
                assertEquals(expected.id(), actual.id());
                assertEquals(expected.weight(), actual.weight());
            });
    }

    private GetKitCustomUseCase.Param createParam(Consumer<GetKitCustomUseCase.Param.ParamBuilder> changer) {
        var param = paramBuilder();
        changer.accept(param);
        return param.build();
    }

    private GetKitCustomUseCase.Param.ParamBuilder paramBuilder() {
        return GetKitCustomUseCase.Param.builder()
            .kitCustomId(2L)
            .currentUserId(UUID.randomUUID());
    }
}
